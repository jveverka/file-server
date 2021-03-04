package itx.fileserver.dto;

public class FileInfo {

    private final String filePath;
    private final long size;
    private final long lastModified;

    public FileInfo(String filePath, long size, long lastModified) {
        this.filePath = filePath;
        this.size = size;
        this.lastModified = lastModified;
    }

    public String getFilePath() {
        return filePath;
    }

    public long getSize() {
        return size;
    }

    public long getLastModified() {
        return lastModified;
    }
}
