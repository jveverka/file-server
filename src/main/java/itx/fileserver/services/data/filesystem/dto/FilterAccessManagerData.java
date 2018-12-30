package itx.fileserver.services.data.filesystem.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import itx.fileserver.services.dto.FilterConfig;

import java.util.Collection;

public class FilterAccessManagerData {

    private final Collection<FilterConfig> filterConfigs;

    @JsonCreator
    public FilterAccessManagerData(@JsonProperty("filterConfigs") Collection<FilterConfig> filterConfigs) {
        this.filterConfigs = filterConfigs;
    }

    public Collection<FilterConfig> getFilterConfigs() {
        return filterConfigs;
    }

}
