package itx.examples.springboot.fileserver.services;

import itx.examples.springboot.fileserver.config.FileServerConfig;
import itx.examples.springboot.fileserver.services.dto.AccessType;
import itx.examples.springboot.fileserver.services.dto.FileAccessFilter;
import itx.examples.springboot.fileserver.services.dto.RoleId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class FileAccessServiceImpl implements FileAccessService {

    private static final Logger LOG = LoggerFactory.getLogger(FileAccessServiceImpl.class);

    private final Map<RoleId, List<FileAccessFilter>> filters;

    @Autowired
    public FileAccessServiceImpl(FileServerConfig fileServerConfig) {
        this.filters = new HashMap<>();
        fileServerConfig.getFilters().forEach(f -> {
            FileAccessFilter fileAccessFilter = new FileAccessFilter(f.getPath(), AccessType.valueOf(f.getAccess()));
            f.getRoles().forEach(r -> {
                RoleId roleId = new RoleId(r);
                List<FileAccessFilter> fileAccessFilters = filters.get(roleId);
                if (fileAccessFilters == null) {
                    fileAccessFilters = new ArrayList<>();
                    filters.put(roleId, fileAccessFilters);
                }
                LOG.info("Filter: role={} path={} {}", roleId.getId(), fileAccessFilter.getPath(), fileAccessFilter.getAccessType());
                fileAccessFilters.add(fileAccessFilter);
            });
        });
    }

    @Override
    public boolean canRead(Set<RoleId> roles, Path path) {
        return checkAccess(roles, path, AccessType.READ);
    }

    @Override
    public boolean canReadAndWrite(Set<RoleId> roles, Path path) {
        return checkAccess(roles, path, AccessType.READ_WRITE);
    }

    private boolean checkAccess(Set<RoleId> roles, Path path, AccessType expectedAccessType) {
        for (RoleId role : roles) {
            if (checkAccess(role, path, expectedAccessType)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkAccess(RoleId role, Path path, AccessType expectedAccessType) {
        Set<AccessType> result = getAccessTypes(role, path, expectedAccessType);
        return checkAccessUseMostRestrictive(result, expectedAccessType);
    }

    private Set<AccessType> getAccessTypes(RoleId role, Path path, AccessType expectedAccessType) {
        Set<AccessType> result = new HashSet<>();
        List<FileAccessFilter> fileAccessFilters = filters.get(role);
        String strPath = path.toString();
        if (fileAccessFilters != null) {
            for (FileAccessFilter filter: fileAccessFilters) {
                LOG.info("checkAccess: {} {} {} {}/{}", role.getId(), strPath, filter.getPath(), expectedAccessType, filter.getAccessType());
                boolean match = FileUtils.wildcardMatch(strPath, filter.getPath());
                if (match) {
                    result.add(filter.getAccessType());
                }
            }
        }
        return result;
    }

    public static boolean checkAccessUseMostRestrictive(Set<AccessType> accessTypes, AccessType expectedAccessType) {
        if (accessTypes.contains(AccessType.NONE)) {
            return false;
        }
        if (accessTypes.contains(AccessType.READ) && AccessType.READ_WRITE.equals(expectedAccessType)) {
            return false;
        }
        if (accessTypes.contains(AccessType.READ) && AccessType.READ.equals(expectedAccessType)) {
            return true;
        }
        if (accessTypes.contains(AccessType.READ_WRITE) && (AccessType.READ_WRITE.equals(expectedAccessType) || AccessType.READ.equals(expectedAccessType))) {
            return true;
        }
        return false;
    }

}