package com.example.foodmanager.model;

import java.util.ArrayList;

public class Receipt {
    private String id;
    private ArrayList<ProductToOrder> listPoduct;
    private Double monney;
    private String time;
    public Receipt() {
    }

    public Receipt(String id, ArrayList<ProductToOrder> listPoduct, Double monney, String time) {
        this.id = id;
        this.listPoduct = listPoduct;
        this.monney = monney;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<ProductToOrder> getListPoduct() {
        return listPoduct;
    }

    public void setListPoduct(ArrayList<ProductToOrder> listPoduct) {
        this.listPoduct = listPoduct;
    }

    public Double getMonney() {
        return monney;
    }

    public void setMonney(Double monney) {
        this.monney = monney;
    }
}
