package itx.fileserver.services.data;

import itx.fileserver.services.dto.FileAccessFilter;
import itx.fileserver.services.dto.FilterConfig;
import itx.fileserver.services.dto.RoleId;

import java.util.Collection;

public interface FileAccessManagerService {

    void addFilter(FilterConfig filterConfig);

    Collection<FilterConfig> getFilters();

    void removeFilter(FilterConfig filterConfig);

    Collection<FileAccessFilter> getFilters(RoleId roleId);

}
