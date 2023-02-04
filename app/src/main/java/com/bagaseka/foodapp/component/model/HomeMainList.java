package com.bagaseka.foodapp.component.model;

import java.util.List;

public class HomeMainList{
    public String nama;
    public String harga;
    public String image;
    public String foodID;
    public String countOrder;
    public String itemCount;
    public List<String> menuID;

    public HomeMainList() {}

    public HomeMainList(String nama, String harga, String image, String foodID, String countOrder) {
        this.nama = nama;
        this.harga = harga;
        this.image = image;
        this.foodID = foodID;
        this.countOrder = countOrder;
    }

    public List<String> getMenuID() {
        return menuID;
    }

    public void setMenuID(List<String> menuID) {
        this.menuID = menuID;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFoodID() {
        return foodID;
    }

    public void setFoodID(String foodID) {
        this.foodID = foodID;
    }

    public String getItemCount() {
        return itemCount;
    }

    public void setItemCount(String itemCount) {
        this.itemCount = itemCount;
    }

    public String getCountOrder() {
        return countOrder;
    }

    public void setCountOrder(String countOrder) {
        this.countOrder = countOrder;
    }

}
