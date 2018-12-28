package itx.examples.springboot.fileserver.services;

import itx.examples.springboot.fileserver.services.dto.FileList;
import itx.examples.springboot.fileserver.services.dto.RoleId;
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
     * Get base path used by this service for all file system operations.
     * @return absolute path to base directory.
     */
    Path getBasePath();

    /**
     * Create {@link Resource} for given file.
     * @param filePath relative path to file.
     * @param roles set of roles accessing this file
     * @return {@link Resource} representing the file.
     * @throws FileNotFoundException
     */
    Resource loadFileAsResource(Set<RoleId> roles, Path filePath) throws FileNotFoundException, OperationNotAllowedException;

    /**
     * Get information about file or list content of directory.
     * @param filePath relative path to file or directory.
     * @param roles set of roles accessing this file or directory.
     * @return meta data about file or directory content list.
     * @throws IOException
     */
    FileList getFilesInfo(Set<RoleId> roles, Path filePath) throws IOException, OperationNotAllowedException;

    /**
     * Writes data in {@link InputStream} into file specified by relative path.
     * @param filePath relative path to file.
     * @param roles set of roles accessing writing into target directory.
     * @param inputStream data to be written into that file.
     * @throws IOException
     */
    void saveFile(Set<RoleId> roles, Path filePath, InputStream inputStream) throws IOException, OperationNotAllowedException;

    /**
     * Deletes file or directory. Directories are deleted even when not empty.
     * @param filePath relative path to file or directory.
     * @param roles set of roles accessing this file or directory.
     * @throws IOException
     */
    void delete(Set<RoleId> roles, Path filePath) throws IOException, OperationNotAllowedException;

    /**
     * Creates new empty directory.
     * @param filePath relative path to directory.
     * @param roles set of roles accessing this directory
     * @throws IOException
     */
    void createDirectory(Set<RoleId> roles, Path filePath) throws IOException, OperationNotAllowedException;

}
