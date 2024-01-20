package sirs.a14.bombappetit.responses;


public class UserResponse {

    private Integer id;
    private String name;

    public UserResponse(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }
    public String getName() { return name; }

}