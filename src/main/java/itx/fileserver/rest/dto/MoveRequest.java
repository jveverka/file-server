package itx.fileserver.rest.dto;

public class MoveRequest {

    private String destinationPath;

    public MoveRequest() {
    }

    public String getDestinationPath() {
        return destinationPath;
    }

    public void setDestinationPath(String destinationPath) {
        this.destinationPath = destinationPath;
    }

}
