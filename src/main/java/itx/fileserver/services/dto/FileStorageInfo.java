package itx.fileserver.services.dto;

import java.nio.file.Path;

public class FileStorageInfo {

    private final Path basePath;
    private final long bytesFree;
    private final long bytesTotal;

    public FileStorageInfo(Path basePath, long bytesFree, long bytesTotal) {
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
