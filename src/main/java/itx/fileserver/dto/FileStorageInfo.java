package itx.fileserver.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.nio.file.Path;

public class FileStorageInfo {

    private final Path basePath;
    private final long bytesFree;
    private final long bytesTotal;

    @JsonCreator
    public FileStorageInfo(@JsonProperty("basePath") Path basePath,
                           @JsonProperty("bytesFree") long bytesFree,
                           @JsonProperty("bytesTotal") long bytesTotal) {
        this.basePath = basePath;
        this.bytesFree = bytesFree;
        this.bytesTotal = bytesTotal;
    }

    public Path getBasePath() {
        return basePath;
    }

    public long getBytesFree() {
        return bytesFree;
    }

    public long getBytesTotal() {
        return bytesTotal;
    }

}
