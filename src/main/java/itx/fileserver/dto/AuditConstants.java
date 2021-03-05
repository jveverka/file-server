package itx.fileserver.dto;

public class AuditConstants {

    public static final CategoryUserAccess USER_ACCESS = new CategoryUserAccess();
    public static final CategoryFileAccess FILE_ACCESS = new CategoryFileAccess();
    public static final CategoryAdminAccess ADMIN_ACCESS = new CategoryAdminAccess();

    private AuditConstants() {
    }

    public interface Category {
    }

    public static class CategoryUserAccess implements Category {
        private CategoryUserAccess() {}
        public static final String NAME = "USER_ACCESS";
        public static final String LOGIN = "LOGIN";
        public static final String LOGOUT = "LOGOUT";
    }

    public static class CategoryFileAccess implements Category {
        private CategoryFileAccess() {}
        public static final String NAME = "FILE_ACCESS";
        public static final String LIST_DIR = "LIST_DIR";
        public static final String DOWNLOAD = "DOWNLOAD";
        public static final String UPLOAD = "UPLOAD";
        public static final String DELETE = "DELETE";
        public static final String CREATE_DIR = "CREATE_DIR";
        public static final String MOVE = "MOVE";
    }

    public static class CategoryAdminAccess implements Category {
        private CategoryAdminAccess() {}
        public static final String NAME = "ADMIN_ACCESS";
        public static final String GET_USERS = "GET_USERS";
        public static final String CREATE_USER = "CREATE_USER";
        public static final String DELETE_USER = "DELETE_USER";
        public static final String GET_ACCESS_FILTERS = "GET_ACCESS_FILTERS";
        public static final String CREATE_ACCESS_FILTER = "CREATE_ACCESS_FILTER";
        public static final String DELETE_ACCESS_FILTER = "DELETE_ACCESS_FILTER";
    }

}
