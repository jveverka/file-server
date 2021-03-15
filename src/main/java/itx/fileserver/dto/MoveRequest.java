package itx.fileserver.dto;

public class MoveRequest {

    private String destinationPath;

    public MoveRequest() {
    }

    public MoveRequest(String destinationPath) {
        this.destinationPath = destinationPath;
    }

    public String getDestinationPath() {
        return destinationPath;
    }

    public void setDestinationPath(String destinationPath) {
        this.destinationPath = destinationPath;
    }

}
