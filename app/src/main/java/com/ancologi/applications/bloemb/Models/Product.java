package com.ancologi.applications.bloemb.Models;

import java.io.Serializable;

public class Product implements Serializable {

    int op_id;
    int is_deleted;
    int order_id;
    int item_id;
    String item_name;
    String item_image_name;
    double final_cost;
    int item_qty;
    double item_cost;


    public Product() {
    }

    public Product(int op_id, int is_deleted, int order_id, int item_id, String item_name, String item_image_name, double final_cost, int item_qty, double item_cost) {
        this.op_id = op_id;
        this.is_deleted = is_deleted;
        this.order_id = order_id;
        this.item_id = item_id;
        this.item_name = item_name;
        this.item_image_name = item_image_name;
        this.final_cost = final_cost;
        this.item_qty = item_qty;
        this.item_cost = item_cost;
    }

    public int getOp_id() {
        return op_id;
    }

    public void setOp_id(int op_id) {
        this.op_id = op_id;
    }

    public int getIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(int is_deleted) {
        this.is_deleted = is_deleted;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getItem_image_name() {
        return item_image_name;
    }

    public void setItem_image_name(String item_image_name) {
        this.item_image_name = item_image_name;
    }

    public double getFinal_cost() {
        return final_cost;
    }

    public void setFinal_cost(double final_cost) {
        this.final_cost = final_cost;
    }

    public int getItem_qty() {
        return item_qty;
    }

    public void setItem_qty(int item_qty) {
        this.item_qty = item_qty;
    }

    public double getItem_cost() {
        return item_cost;
    }

    public void setItem_cost(double item_cost) {
        this.item_cost = item_cost;
    }
}
