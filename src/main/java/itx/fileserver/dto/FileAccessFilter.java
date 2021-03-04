package itx.fileserver.dto;

public class FileAccessFilter {

    private final String path;
    private final AccessType accessType;

    public FileAccessFilter(String path, AccessType accessType) {
        this.path = path;
        this.accessType = accessType;
    }

    public String getPath() {
        return path;
    }

    public AccessType getAccessType() {
        return accessType;
    }

}
