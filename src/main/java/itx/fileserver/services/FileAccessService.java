package itx.fileserver.services;

import itx.fileserver.dto.RoleId;

import java.nio.file.Path;
import java.util.Set;

/**
 * Verify access for given paths.
 */
public interface FileAccessService {

    /**
     * Check if given path has read access, based on provided roles.
     * @param roles set of roles provided for this check.
     * @param path path in question for access verification.
     * @return true if read access is permitted, false otherwise.
     */
    boolean canRead(Set<RoleId> roles, Path path);

    /**
     * Check if given path has read and write access, based on provided roles.
     * @param roles set of roles provided for this check.
     * @param path path in question for access verification.
     * @return true if read and write access is permitted, false otherwise.
     */
    boolean canReadAndWrite(Set<RoleId> roles, Path path);

}
