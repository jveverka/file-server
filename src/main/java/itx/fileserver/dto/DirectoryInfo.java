package itx.fileserver.dto;

public class DirectoryInfo {

    private final String filePath;
    private final long lastModified;

    public DirectoryInfo(String filePath, long lastModified) {
        this.filePath = filePath;
        this.lastModified = lastModified;
    }

    public String getFilePath() {
        return filePath;
    }

    public long getLastModified() {
        return lastModified;
    }

}
