package sirs.a14.bombappetit.responses;

import java.util.List;

public class UserResponseWrapper {

    private List<UserResponse> users;

    public UserResponseWrapper(List<UserResponse> userResponses) {
        this.users = userResponses;
    }

}
