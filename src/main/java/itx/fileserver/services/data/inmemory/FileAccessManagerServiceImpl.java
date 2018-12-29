package itx.fileserver.services.data.inmemory;

import itx.fileserver.config.FileServerConfig;
import itx.fileserver.services.data.FileAccessManagerService;
import itx.fileserver.services.dto.AccessType;
import itx.fileserver.services.dto.FileAccessFilter;
import itx.fileserver.services.dto.FilterConfig;
import itx.fileserver.services.dto.RoleId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class FileAccessManagerServiceImpl implements FileAccessManagerService {

    private static final Logger LOG = LoggerFactory.getLogger(FileAccessManagerServiceImpl.class);

    private final Map<RoleId, List<FileAccessFilter>> filters;
    private final Set<FilterConfig> filterConfigs;

    public FileAccessManagerServiceImpl(FileServerConfig fileServerConfig) {
        this.filters = new ConcurrentHashMap<>();
        this.filterConfigs = new HashSet<>();
        fileServerConfig.getFilters().forEach(f -> {
            addFilter(f);
        });
    }


    @Override
    public void addFilter(FilterConfig filterConfig) {
        this.filterConfigs.add(filterConfig);
        FileAccessFilter fileAccessFilter = new FileAccessFilter(filterConfig.getPath(), AccessType.valueOf(filterConfig.getAccess()));
        filterConfig.getRoles().forEach(r -> {
            RoleId roleId = new RoleId(r);
            List<FileAccessFilter> fileAccessFilters = filters.get(roleId);
            if (fileAccessFilters == null) {
                fileAccessFilters = new ArrayList<>();
                filters.put(roleId, fileAccessFilters);
            }
            LOG.info("Filter: role={} path={} {}", roleId.getId(), fileAccessFilter.getPath(), fileAccessFilter.getAccessType());
            fileAccessFilters.add(fileAccessFilter);
        });
    }

    @Override
    public Collection<FilterConfig> getFilters() {
        return Collections.unmodifiableList(new ArrayList<>(filterConfigs));
    }

    @Override
    public Collection<FileAccessFilter> getFilters(RoleId roleId) {
        List<FileAccessFilter> fileAccessFilters = filters.get(roleId);
        if (fileAccessFilters != null) {
            return Collections.unmodifiableList(filters.get(roleId));
        } else {
            return Collections.unmodifiableList(Collections.EMPTY_LIST);
        }
    }

    @Override
    public void removeFilter(FilterConfig filterConfig) {
        filterConfigs.remove(filterConfig);
        filterConfig.getRoles().forEach(r->{
            RoleId roleId = new RoleId(r);
            List<FileAccessFilter> fileAccessFilters = filters.get(roleId);
            if (fileAccessFilters != null) {
                List<FileAccessFilter> collected =
                        fileAccessFilters.stream().filter(fa -> !fa.getPath().equals(filterConfig.getPath())).collect(Collectors.toList());
                if (collected.size() > 0) {
                    filters.replace(roleId, collected);
                } else {
                    filters.remove(roleId);
                }
            }
        });
    }

}
