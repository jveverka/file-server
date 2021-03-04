package itx.fileserver.services;

import itx.fileserver.dto.FileList;
import itx.fileserver.dto.FileStorageInfo;
import itx.fileserver.dto.ResourceAccessInfo;
import itx.fileserver.dto.UserData;
import org.springframework.core.io.Resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

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
     * Get info about resource access.
     * @param userData users's data accessing this resource.
     * @param filePath relative path to file or directory.
     * @return {@link ResourceAccessInfo} access info about this resource.
     * @throws OperationNotAllowedException
     */
    ResourceAccessInfo getResourceAccessInfo(UserData userData, Path filePath) throws OperationNotAllowedException;

    /**
     * Create {@link Resource} for given file.
     * @param filePath relative path to file.
     * @param userData users's data accessing this file
     * @return {@link Resource} representing the file.
     * @throws FileNotFoundException
     * @throws OperationNotAllowedException
     */
    Resource loadFileAsResource(UserData userData, Path filePath) throws FileNotFoundException, OperationNotAllowedException;

    /**
     * Get information about file or list content of directory.
     * @param filePath relative path to file or directory.
     * @param userData users's data accessing this file or directory.
     * @return meta data about file or directory content list.
     * @throws IOException
     * @throws OperationNotAllowedException
     */
    FileList getFilesInfo(UserData userData, Path filePath) throws IOException, OperationNotAllowedException;

    /**
     * Writes data in {@link InputStream} into file specified by relative path.
     * @param filePath relative path to file.
     * @param userData users's data writing into target directory.
     * @param inputStream data to be written into that file.
     * @throws IOException
     * @throws OperationNotAllowedException
     */
    void saveFile(UserData userData, Path filePath, InputStream inputStream) throws IOException, OperationNotAllowedException;

    /**
     * Deletes file or directory. Directories are deleted even when not empty.
     * @param filePath relative path to file or directory.
     * @param userData users's data accessing this file or directory.
     * @throws IOException
     * @throws OperationNotAllowedException
     */
    void delete(UserData userData, Path filePath) throws IOException, OperationNotAllowedException;

    /**
     * Creates new empty directory.
     * @param filePath relative path to directory.
     * @param userData users's data accessing this directory.
     * @throws IOException
     * @throws OperationNotAllowedException
     */
    void createDirectory(UserData userData, Path filePath) throws IOException, OperationNotAllowedException;

    /**
     * Move file or directory from source to destination.
     * @param userData users's data accessing source and destination.
     * @param sourcePath relative path to source file or directory.
     * @param destinationPath relative path to destination file or directory.
     * @throws IOException
     * @throws OperationNotAllowedException
     */
    void move(UserData userData, Path sourcePath, Path destinationPath) throws IOException, OperationNotAllowedException;

}
