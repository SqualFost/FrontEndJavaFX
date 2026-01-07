package com.example.projetjavafx.model;

import java.util.List;

public class ClientOrder {
    private String id;
    private List<String> items;
    private String status;
    private String time;

    public ClientOrder(String id, List<String> items, String status, String time) {
        this.id = id;
        this.items = items;
        this.status = status;
        this.time = time;
    }

    public String getId() { return id; }
    public List<String> getItems() { return items; }
    public String getStatus() { return status; }
    public String getTime() { return time; }
}