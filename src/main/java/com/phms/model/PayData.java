package com.phms.model;

/**
 * @Author Sijie He
 * @Date 2022/4/22 14:44
 * @Version 1.0
 */
public class PayData {
    private String id;
    private Integer price;
    private String title;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
