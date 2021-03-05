package itx.fileserver.services.data.base;

import itx.fileserver.services.data.FileAccessManagerService;
import itx.fileserver.dto.AccessType;
import itx.fileserver.dto.FileAccessFilter;
import itx.fileserver.dto.FilterConfig;
import itx.fileserver.dto.RoleId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class FileAccessManagerServiceImpl implements FileAccessManagerService {

    private static final Logger LOG = LoggerFactory.getLogger(FileAccessManagerServiceImpl.class);

    protected Map<RoleId, List<FileAccessFilter>> filters;
    protected Set<FilterConfig> filterConfigs;

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
        persist();
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
            return Collections.emptyList();
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
        persist();
    }

    public abstract void persist();

}
