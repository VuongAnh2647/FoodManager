package com.example.foodmanager.model;

public class ProductToOrder {
    private String idBill;
    private String id ;
    private String nameProduct;
    private double price;
    private int soLuong;
    private String typeProduct;
    private String imgProduct;
    private String note;
    private String time;


    public ProductToOrder() {
    }

    public ProductToOrder(String id, String nameProduct, double price, int soLuong, String typeProduct, String imgProduct, String note, String time) {
        this.id = id;
        this.nameProduct = nameProduct;
        this.price = price;
        this.soLuong = soLuong;
        this.typeProduct = typeProduct;
        this.imgProduct = imgProduct;
        this.note = note;
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

    public String getNameProduct() {
        return nameProduct;
    }

    public void setNameProduct(String nameProduct) {
        this.nameProduct = nameProduct;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public String getTypeProduct() {
        return typeProduct;
    }

    public void setTypeProduct(String typeProduct) {
        this.typeProduct = typeProduct;
    }

    public String getImgProduct() {
        return imgProduct;
    }

    public void setImgProduct(String imgProduct) {
        this.imgProduct = imgProduct;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
