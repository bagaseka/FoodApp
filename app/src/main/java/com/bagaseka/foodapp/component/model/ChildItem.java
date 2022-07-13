package com.bagaseka.foodapp.component.model;

import java.util.Objects;

public class ChildItem {
    public String nama;
    public String foodID;
    public String image;
    public int harga;
    public int counter;
    public boolean reviewStatus;
    public boolean progress;
    public String orderId;

    public ChildItem() {
    }

    public boolean isProgress() {
        return progress;
    }

    public void setProgress(boolean progress) {
        this.progress = progress;
    }

    public String getOrderId() {
        return orderId;
    }

    public boolean isReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(boolean reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getFoodID() {
        return foodID;
    }

    public void setFoodID(String foodID) {
        this.foodID = foodID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChildItem childItem = (ChildItem) o;
        return harga == childItem.harga && counter == childItem.counter && reviewStatus == childItem.reviewStatus && progress == childItem.progress && Objects.equals(nama, childItem.nama) && Objects.equals(foodID, childItem.foodID) && Objects.equals(image, childItem.image) && Objects.equals(orderId, childItem.orderId);
    }

    public boolean checkId(ChildItem data) {
        return this.getFoodID().equals(data.foodID) && this.getOrderId().equals(data.getOrderId());
    }

    public void update(ChildItem newData) {
        nama = newData.getNama();
        foodID = newData.getFoodID();
        image = newData.getImage();
        harga = newData.getHarga();
        counter = newData.counter;
        reviewStatus = newData.reviewStatus;
        progress = newData.progress;
        orderId = newData.getOrderId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(nama, foodID, image, harga, counter, reviewStatus, progress, orderId);
    }
}
