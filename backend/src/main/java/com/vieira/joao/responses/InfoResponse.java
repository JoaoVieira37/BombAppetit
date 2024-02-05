package com.vieira.joao.responses;


public class InfoResponse {

    private Integer id;
    private String name;

    public InfoResponse(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }
    public String getName() { return name; }
}
