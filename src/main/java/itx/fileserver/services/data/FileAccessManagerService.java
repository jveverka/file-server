package itx.fileserver.services.data;

import itx.fileserver.dto.FileAccessFilter;
import itx.fileserver.dto.FilterConfig;
import itx.fileserver.dto.RoleId;

import java.util.Collection;

public interface FileAccessManagerService {

    void addFilter(FilterConfig filterConfig);

    Collection<FilterConfig> getFilters();

    void removeFilter(FilterConfig filterConfig);

    Collection<FileAccessFilter> getFilters(RoleId roleId);

}
