package com.example.pawel.fridgerator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;

public class Item implements Serializable {

    private String name;
    private String store;
    private BigDecimal price;
    private int amount;
    private int deltaAmount; //difference between server and android amount status
    private int deltaAmountSent;
    private status status;

    public int getDeltaAmountSent() {
        return deltaAmountSent;
    }

    public void setDeltaAmountSent(int deltaAmountSent) {
        this.deltaAmountSent = deltaAmountSent;
    }

    public enum status{
        ON_SERVER,
        CREATED,
        DELETED,
    }

    public Item.status getStatus() {
        return status;
    }

    public int getDeltaAmount() {
        return deltaAmount;
    }

    public Item(String name, String store, BigDecimal price, int amount, int deltaAmount, int deltaAmountSent, Item.status status) {
        this.name = name;
        this.store = store;
        this.price = price;
        this.amount = amount;
        this.deltaAmount = deltaAmount;
        this.deltaAmountSent = deltaAmountSent;
        this.status = status;
    }

    public JSONObject createJson() throws JSONException {
        JSONObject jObject = new JSONObject();

        jObject.put("name", name);
        jObject.put("store", store);
        jObject.put("price", price);
        jObject.put("amount", amount);
        jObject.put("deltaAmount", deltaAmount);
        jObject.put("deltaAmountSent", deltaAmountSent);
        jObject.put("status", status);

        return jObject;
    }

    public void setItem(Item newItem){
        name = newItem.name;
        price = newItem.price;
        amount = newItem.amount;
        store = newItem.store;
        deltaAmount = newItem.deltaAmount;
        deltaAmountSent = newItem.deltaAmountSent;
        status = newItem.status;

    }

    @Override
    public String toString() {
        String ret = name;
        //for(int i =0 ; i < 40 - name.length(); i++) ret += " ";
        //return  ret + " (" +amount + ")";
        return name + " " + amount + " " + status.toString() + " D:" + deltaAmount;
    }

    public void setDeltaAmount(int deltaAmount) {
        this.deltaAmount = deltaAmount;
    }



    //Getters and setters
    public String getName() {
        return name;
    }

    public void setStatus(Item.status status) {
        this.status = status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
