package itx.fileserver.dto;

import java.util.HashMap;
import java.util.Map;

public class ResourceAccessInfo {

    private final Map<String, Integer> counters;

    public ResourceAccessInfo() {
        this.counters = new HashMap<>();
    }

    public void incrementCounter(String action) {
        Integer counter = this.counters.get(action);
        if (counter == null) {
            this.counters.put(action, 1);
        } else {
            this.counters.put(action, counter + 1);
        }
    }

    public Map<String, Integer> getCounters() {
        return this.counters;
    }

}
