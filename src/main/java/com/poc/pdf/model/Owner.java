package com.poc.pdf.model;

public class Owner extends ToString {
    private static final long serialVersionUID = 5107025885404117844L;

    private String name;

    public Owner(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
