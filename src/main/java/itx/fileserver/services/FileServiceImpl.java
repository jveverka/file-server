package itx.fileserver.services;

import itx.fileserver.config.FileServerConfig;
import itx.fileserver.services.dto.DirectoryInfo;
import itx.fileserver.services.dto.FileInfo;
import itx.fileserver.services.dto.FileList;
import itx.fileserver.services.dto.RoleId;
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
import java.util.Set;
import java.util.stream.Stream;

@Service
public class FileServiceImpl implements FileService {

    private static final Logger LOG = LoggerFactory.getLogger(FileServiceImpl.class);

    private final Path fileStorageLocation;
    private final FileAccessService fileAccessService;

    @Autowired
    public FileServiceImpl(FileServerConfig fileServerConfig, FileAccessService fileAccessService) {
        LOG.info("fileStorageLocation={}", fileServerConfig.getHome());
        this.fileAccessService = fileAccessService;
        this.fileStorageLocation = Paths.get(fileServerConfig.getHome())
                .toAbsolutePath().normalize();
    }

    @Override
    public Path getBasePath() {
        return fileStorageLocation;
    }

    @Override
    public Resource loadFileAsResource(Set<RoleId> roles, Path filePath) throws FileNotFoundException, OperationNotAllowedException {
        LOG.info("loadFileAsResource: {}", filePath);
        try {
            verifyReadAccess(roles, filePath);
            Path resolvedFilePath = this.fileStorageLocation.resolve(filePath).normalize();
            Resource resource = new UrlResource(resolvedFilePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException("File not found " + filePath);
            }
        } catch (MalformedURLException ex) {
            throw new FileNotFoundException("File not found " + filePath);
        }
    }

    @Override
    public FileList getFilesInfo(Set<RoleId> roles, Path filePath) throws IOException, OperationNotAllowedException {
        LOG.info("getFilesInfo: {}", filePath);
        FileList fileList = new FileList(filePath.toString());
        Path resolvedFilePath = this.fileStorageLocation.resolve(filePath).normalize();
        try (Stream<Path> filesWalk = Files.walk(resolvedFilePath, 1)) {
            filesWalk.forEach(fw -> {
                Path pathToCheck = Paths.get(filePath.toString(), fw.getFileName().toString());
                if (fileAccessService.canRead(roles, pathToCheck)) {
                    if (resolvedFilePath.endsWith(fw)) {
                        //skip parent directory
                    } else if (Files.isDirectory(fw)) {
                        fileList.add(new DirectoryInfo(fw.getFileName().toString()));
                    } else {
                        fileList.add(new FileInfo(fw.getFileName().toString()));
                    }
                }
            });
        }
        return fileList;
    }

    @Override
    public void saveFile(Set<RoleId> roles, Path filePath, InputStream inputStream) throws IOException, OperationNotAllowedException {
        LOG.info("saveFile: {}", filePath);
        verifyReadAndWriteAccess(roles, filePath);
        Path resolvedFilePath = this.fileStorageLocation.resolve(filePath).normalize();
        byte[] buffer = new byte[inputStream.available()];
        inputStream.read(buffer);
        File targetFile = resolvedFilePath.toFile();
        OutputStream outStream = new FileOutputStream(targetFile);
        outStream.write(buffer);
    }

    @Override
    public void delete(Set<RoleId> roles, Path filePath) throws IOException, OperationNotAllowedException {
        LOG.info("delete: {}", filePath);
        verifyReadAndWriteAccess(roles, filePath);
        Path resolvedFilePath = this.fileStorageLocation.resolve(filePath).normalize();
        LOG.info("deleting: {}", resolvedFilePath.toString());
        if (Files.isDirectory(resolvedFilePath)) {
            FileSystemUtils.deleteRecursively(resolvedFilePath);
        } else {
            Files.delete(resolvedFilePath);
        }
    }

    @Override
    public void createDirectory(Set<RoleId> roles, Path filePath) throws IOException, OperationNotAllowedException {
        LOG.info("createDirectory: {}", filePath);
        verifyReadAndWriteAccess(roles, filePath);
        Path resolvedFilePath = this.fileStorageLocation.resolve(filePath).normalize();
        Files.createDirectories(resolvedFilePath);
    }

    private void verifyReadAccess(Set<RoleId> roles, Path filePath) throws OperationNotAllowedException {
        if (!fileAccessService.canRead(roles, filePath)) {
            throw new OperationNotAllowedException();
        }
    }

    private void verifyReadAndWriteAccess(Set<RoleId> roles, Path filePath) throws OperationNotAllowedException {
        if (!fileAccessService.canReadAndWrite(roles, filePath)) {
            throw new OperationNotAllowedException();
        }
    }

}
