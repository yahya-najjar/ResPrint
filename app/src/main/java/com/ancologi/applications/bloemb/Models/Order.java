package com.ancologi.applications.bloemb.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class Order implements Serializable {

    int order_id;
    String order_date;
    String order_time;
    double total_cost;
    double delivery_fee;
    String customer_name;
    String phone;
    String house_no;
    String street;
    String landmark;
    String locality;
    String city;
    String pincode;
    String payment_type;
    int no_of_items;
    String email;
    double payment_cost;
    String payment_currency;
    String datedelver;
    String timedelver;
    int is_print;
    private ArrayList<Product> order_products;


    public Order() {
    }

    public Order(int order_id, String order_date, String order_time, double total_cost, double delivery_fee, String customer_name, String phone, String house_no, String street, String landmark, String locality, String city, String pincode, String payment_type, int no_of_items, String email, double payment_cost, String payment_currency, String datedelver, String timedelver, int is_print,ArrayList<Product> order_products) {
        this.order_id = order_id;
        this.order_date = order_date;
        this.order_time = order_time;
        this.total_cost = total_cost;
        this.delivery_fee = delivery_fee;
        this.customer_name = customer_name;
        this.phone = phone;
        this.house_no = house_no;
        this.street = street;
        this.landmark = landmark;
        this.locality = locality;
        this.city = city;
        this.pincode = pincode;
        this.payment_type = payment_type;
        this.no_of_items = no_of_items;
        this.email = email;
        this.payment_cost = payment_cost;
        this.payment_currency = payment_currency;
        this.datedelver = datedelver;
        this.timedelver = timedelver;
        this.is_print = is_print;
        this.order_products = order_products;
    }


    public ArrayList<Product> getProducts() {
        return order_products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.order_products = products;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getOrder_time() {
        return order_time;
    }

    public void setOrder_time(String order_time) {
        this.order_time = order_time;
    }

    public double getTotal_cost() {
        return total_cost;
    }

    public void setTotal_cost(double total_cost) {
        this.total_cost = total_cost;
    }

    public double getDelivery_fee() {
        return delivery_fee;
    }

    public void setDelivery_fee(double delivery_fee) {
        this.delivery_fee = delivery_fee;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getHouse_no() {
        return house_no;
    }

    public void setHouse_no(String house_no) {
        this.house_no = house_no;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public int getNo_of_items() {
        return no_of_items;
    }

    public void setNo_of_items(int no_of_items) {
        this.no_of_items = no_of_items;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getPayment_cost() {
        return payment_cost;
    }

    public void setPayment_cost(double payment_cost) {
        this.payment_cost = payment_cost;
    }

    public String getPayment_currency() {
        return payment_currency;
    }

    public void setPayment_currency(String payment_currency) {
        this.payment_currency = payment_currency;
    }

    public String getDatedelver() {
        return datedelver;
    }

    public void setDatedelver(String datedelver) {
        this.datedelver = datedelver;
    }

    public String getTimedelver() {
        return timedelver;
    }

    public void setTimedelver(String timedelver) {
        this.timedelver = timedelver;
    }

    public int getIs_print() {
        return is_print;
    }

    public void setIs_print(int is_print) {
        this.is_print = is_print;
    }
}
