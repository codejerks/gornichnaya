package org.example.domain;

public class Users {
    private long id;
    private String username;

    public Users(long id, String username) {
        this.id = id;
        this.username = username;
    }
    public long getId() { return id; }
    public String getUsername() { return username; }
}