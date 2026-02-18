package cc.ai.context;



public class UserContext {


    private String userId;

    private String name;


    public UserContext(String userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public UserContext(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
