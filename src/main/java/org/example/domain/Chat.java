package org.example.domain;

public class Chat {
    private long id;
    private long userId;
    private String name;

    public Chat(long id, long userId, String name) {
        this.id = id;
        this.userId = userId;
        this.name = name;
    }

    public void setUserId(long userId) {this.userId = userId;}

    public void setId(long id) {this.id = id;}

    public void setName(String name) {this.name = name;}

    public long getId() { return id; }
    public long getUserId() { return userId; }
    public String getName() { return name; }
}