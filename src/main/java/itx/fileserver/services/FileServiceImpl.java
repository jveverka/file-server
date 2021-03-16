package itx.fileserver.services;

import itx.fileserver.config.FileServerConfig;
import itx.fileserver.services.data.AuditService;
import itx.fileserver.dto.AuditQuery;
import itx.fileserver.dto.AuditRecord;
import itx.fileserver.dto.DirectoryInfo;
import itx.fileserver.dto.FileInfo;
import itx.fileserver.dto.FileList;
import itx.fileserver.dto.FileStorageInfo;
import itx.fileserver.dto.ResourceAccessInfo;
import itx.fileserver.dto.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Collection;
import java.util.stream.Stream;

import static itx.fileserver.dto.AuditConstants.FILE_ACCESS;

@Service
public class FileServiceImpl implements FileService {

    private static final Logger LOG = LoggerFactory.getLogger(FileServiceImpl.class);

    private final Path fileStorageLocation;
    private final FileAccessService fileAccessService;
    private final AuditService auditService;

    @Autowired
    public FileServiceImpl(FileServerConfig fileServerConfig, FileAccessService fileAccessService,
                           AuditService auditService) {
        LOG.info("fileStorageLocation={}", fileServerConfig.getHome());
        this.fileAccessService = fileAccessService;
        this.fileStorageLocation = Paths.get(fileServerConfig.getHome())
                .toAbsolutePath().normalize();
        this.auditService = auditService;
    }

    @Override
    public FileStorageInfo getFileStorageInfo() {
        File home = fileStorageLocation.toFile();
        return new FileStorageInfo(fileStorageLocation, home.getFreeSpace(), home.getTotalSpace());
    }

    @Override
    public ResourceAccessInfo getResourceAccessInfo(UserData userData, Path filePath) throws OperationNotAllowedException {
        LOG.info("getResourceAccessInfo: {}", filePath);
        verifyReadAccess(userData, filePath);
        AuditQuery auditQuery = AuditQuery.newBuilder()
                .withResourcePattern(filePath.toString())
                .withCategory(FILE_ACCESS.NAME)
                .build();
        Collection<AuditRecord> audits = auditService.getAudits(auditQuery);
        ResourceAccessInfo resourceAccessInfo = new ResourceAccessInfo();
        audits.forEach(a -> resourceAccessInfo.incrementCounter(a.getAction()));
        return resourceAccessInfo;
    }

    @Override
    public Resource loadFileAsResource(UserData userData, Path filePath) throws FileNotFoundException, OperationNotAllowedException {
        LOG.info("loadFileAsResource: {}", filePath);
        try {
            verifyReadAccess(userData, filePath);
            Path resolvedFilePath = this.fileStorageLocation.resolve(filePath).normalize();
            Resource resource = new UrlResource(resolvedFilePath.toUri());
            if(resource.exists()) {
                createDownloadFileAuditRecord(userData, filePath);
                return resource;
            } else {
                throw new FileNotFoundException("File not found " + filePath);
            }
        } catch (MalformedURLException ex) {
            throw new FileNotFoundException("File not found " + filePath);
        }
    }

    @Override
    public FileList getFilesInfo(UserData userData, Path filePath) throws IOException, OperationNotAllowedException {
        LOG.info("getFilesInfo: {}", filePath);
        verifyReadAccess(userData, filePath.resolve("*"));
        FileList fileList = new FileList(filePath.toString());
        Path resolvedFilePath = this.fileStorageLocation.resolve(filePath).normalize();
        try (Stream<Path> filesWalk = Files.walk(resolvedFilePath, 1)) {
            filesWalk.forEach(fw -> {
                Path pathToCheck = Paths.get(filePath.toString(), fw.getFileName().toString());
                if (fileAccessService.canRead(userData.getRoles(), pathToCheck)) {
                    if (resolvedFilePath.endsWith(fw)) {
                        //skip parent directory
                    } else if (Files.isDirectory(fw)) {
                        File file = fw.toFile();
                        fileList.add(new DirectoryInfo(fw.getFileName().toString(), file.lastModified()));
                    } else if (Files.isRegularFile(fw)) {
                        File file = fw.toFile();
                        fileList.add(new FileInfo(fw.getFileName().toString(), file.length(), file.lastModified()));
                    } else {
                        LOG.error("getFilesInfo skipped: {} is not regular file nor directory !", filePath);
                    }
                }
            });
        }
        createListDirectoryAuditRecord(userData, filePath);
        return fileList;
    }

    @Override
    public void saveFile(UserData userData, Path filePath, InputStream inputStream) throws IOException, OperationNotAllowedException {
        LOG.info("saveFile: {}", filePath);
        verifyReadAndWriteAccess(userData, filePath);
        Path resolvedFilePath = this.fileStorageLocation.resolve(filePath).normalize();
        byte[] buffer = new byte[inputStream.available()];
        inputStream.read(buffer);
        File targetFile = resolvedFilePath.toFile();
        OutputStream outStream = new FileOutputStream(targetFile);
        outStream.write(buffer);
        createUploadFileAuditRecord(userData, filePath);
    }

    @Override
    public void delete(UserData userData, Path filePath) throws IOException, OperationNotAllowedException {
        LOG.info("delete: {}", filePath);
        verifyReadAndWriteAccess(userData, filePath);
        Path resolvedFilePath = this.fileStorageLocation.resolve(filePath).normalize();
        LOG.info("deleting: {}", resolvedFilePath);
        if (Files.isDirectory(resolvedFilePath)) {
            FileSystemUtils.deleteRecursively(resolvedFilePath);
        } else {
            Files.delete(resolvedFilePath);
        }
        createDeleteAuditRecord(userData, filePath);
    }

    @Override
    public void createDirectory(UserData userData, Path filePath) throws IOException, OperationNotAllowedException {
        LOG.info("createDirectory: {}", filePath);
        verifyReadAndWriteAccess(userData, filePath);
        Path resolvedFilePath = this.fileStorageLocation.resolve(filePath).normalize();
        Files.createDirectories(resolvedFilePath);
        createCreateDirectoryAuditRecord(userData, filePath);
    }

    @Override
    public void move(UserData userData, Path sourcePath, Path destinationPath) throws IOException, OperationNotAllowedException {
        LOG.info("move: {}->{}", sourcePath, destinationPath);
        verifyReadAndWriteAccess(userData, sourcePath);
        verifyReadAndWriteAccess(userData, destinationPath);
        Path resolvedSourcePath = this.fileStorageLocation.resolve(sourcePath).normalize();
        Path resolvedDestinationPath = this.fileStorageLocation.resolve(destinationPath).normalize();
        if (Files.isRegularFile(resolvedSourcePath)) {
            LOG.info("moving file {}->{}", sourcePath, destinationPath);
            Files.move(resolvedSourcePath, resolvedDestinationPath);
        } else if (Files.isDirectory(resolvedSourcePath)) {
            LOG.info("moving directory {}->{}", sourcePath, destinationPath);
            Files.move(resolvedSourcePath, resolvedDestinationPath);
        } else {
            LOG.error("source must be both file or directory");
            throw new OperationNotAllowedException();
        }
        createMoveAuditRecord(userData, sourcePath, destinationPath);
    }

    private void verifyReadAccess(UserData userData, Path filePath) throws OperationNotAllowedException {
        if (!fileAccessService.canRead(userData.getRoles(), filePath)) {
            throw new OperationNotAllowedException();
        }
    }

    private void verifyReadAndWriteAccess(UserData userData, Path filePath) throws OperationNotAllowedException {
        if (!fileAccessService.canReadAndWrite(userData.getRoles(), filePath)) {
            throw new OperationNotAllowedException();
        }
    }

    /* AUDIT METHODS */

    private void createDownloadFileAuditRecord(UserData userData, Path filePath) {
        AuditRecord auditRecord = new AuditRecord(Instant.now().getEpochSecond(), FILE_ACCESS.NAME,
                FILE_ACCESS.DOWNLOAD, userData.getId().getId(), filePath.toString(), "OK", "");
        auditService.storeAudit(auditRecord);
    }

    private void createListDirectoryAuditRecord(UserData userData, Path filePath) {
        AuditRecord auditRecord = new AuditRecord(Instant.now().getEpochSecond(), FILE_ACCESS.NAME,
                FILE_ACCESS.LIST_DIR, userData.getId().getId(), filePath.toString(), "OK", "");
        auditService.storeAudit(auditRecord);
    }

    private void createUploadFileAuditRecord(UserData userData, Path filePath) {
        AuditRecord auditRecord = new AuditRecord(Instant.now().getEpochSecond(), FILE_ACCESS.NAME,
                FILE_ACCESS.UPLOAD, userData.getId().getId(), filePath.toString(), "OK", "");
        auditService.storeAudit(auditRecord);
    }

    private void createDeleteAuditRecord(UserData userData, Path filePath) {
        AuditRecord auditRecord = new AuditRecord(Instant.now().getEpochSecond(), FILE_ACCESS.NAME,
                FILE_ACCESS.DELETE, userData.getId().getId(), filePath.toString(), "OK", "");
        auditService.storeAudit(auditRecord);
    }

    private void createCreateDirectoryAuditRecord(UserData userData, Path filePath) {
        AuditRecord auditRecord = new AuditRecord(Instant.now().getEpochSecond(), FILE_ACCESS.NAME,
                FILE_ACCESS.CREATE_DIR, userData.getId().getId(), filePath.toString(), "OK", "");
        auditService.storeAudit(auditRecord);
    }

    private void createMoveAuditRecord(UserData userData, Path sourcePath, Path destinationPath) {
        AuditRecord auditRecord = new AuditRecord(Instant.now().getEpochSecond(), FILE_ACCESS.NAME,
                FILE_ACCESS.MOVE, userData.getId().getId(), sourcePath.toString(), "OK", destinationPath.toString());
        auditService.storeAudit(auditRecord);
    }

}
