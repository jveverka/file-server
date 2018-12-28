package itx.fileserver.services;

import itx.fileserver.services.dto.FileList;
import itx.fileserver.services.dto.FileStorageInfo;
import itx.fileserver.services.dto.RoleId;
import org.springframework.core.io.Resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Set;

/**
 * Service for manipulating files on the file system.
 */
public interface FileService {

    /**
     * Get info about file storage.
     * @return
     */
    FileStorageInfo getFileStorageInfo();

    /**
     * Create {@link Resource} for given file.
     * @param filePath relative path to file.
     * @param roles set of roles accessing this file
     * @return {@link Resource} representing the file.
     * @throws FileNotFoundException
     * @throws OperationNotAllowedException
     */
    Resource loadFileAsResource(Set<RoleId> roles, Path filePath) throws FileNotFoundException, OperationNotAllowedException;

    /**
     * Get information about file or list content of directory.
     * @param filePath relative path to file or directory.
     * @param roles set of roles accessing this file or directory.
     * @return meta data about file or directory content list.
     * @throws IOException
     * @throws OperationNotAllowedException
     */
    FileList getFilesInfo(Set<RoleId> roles, Path filePath) throws IOException, OperationNotAllowedException;

    /**
     * Writes data in {@link InputStream} into file specified by relative path.
     * @param filePath relative path to file.
     * @param roles set of roles accessing writing into target directory.
     * @param inputStream data to be written into that file.
     * @throws IOException
     * @throws OperationNotAllowedException
     */
    void saveFile(Set<RoleId> roles, Path filePath, InputStream inputStream) throws IOException, OperationNotAllowedException;

    /**
     * Deletes file or directory. Directories are deleted even when not empty.
     * @param filePath relative path to file or directory.
     * @param roles set of roles accessing this file or directory.
     * @throws IOException
     * @throws OperationNotAllowedException
     */
    void delete(Set<RoleId> roles, Path filePath) throws IOException, OperationNotAllowedException;

    /**
     * Creates new empty directory.
     * @param filePath relative path to directory.
     * @param roles set of roles accessing this directory.
     * @throws IOException
     * @throws OperationNotAllowedException
     */
    void createDirectory(Set<RoleId> roles, Path filePath) throws IOException, OperationNotAllowedException;

    /**
     * Move file or directory from source to destination.
     * @param roles set of roles accessing source and destination.
     * @param sourcePath relative path to source file or directory.
     * @param destinationPath relative path to destination file or directory.
     * @throws IOException
     * @throws OperationNotAllowedException
     */
    void move(Set<RoleId> roles, Path sourcePath, Path destinationPath) throws IOException, OperationNotAllowedException;

}
