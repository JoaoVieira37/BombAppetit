package sirs.a14.bombappetit.responses;

import java.util.List;

public class InfoResponseWrapper {

    private List<InfoResponse> restaurants;

    public InfoResponseWrapper(List<InfoResponse> infoResponses) {
        this.restaurants = infoResponses;
    }

}
