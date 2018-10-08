package com.example.pawel.fridgerator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pawel on 10-Nov-17.
 */

public class ItemList {

    private List<Item> items;

    public ItemList() {
        this.items = new ArrayList<>();
    }

    public ItemList(List<Item> list) {
        this.items = list;
    }

    public ItemList(String jsonBody) {
        try {
            JSONObject jsonMain = new JSONObject(jsonBody);
            JSONArray itemsArray = jsonMain.getJSONArray("items");
            items = new ArrayList<>();

            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject object = itemsArray.getJSONObject(i);
                String name = object.getString("name");
                String store = object.getString("store");

                BigDecimal price;
                Object priceObj = object.get("price");
                if(priceObj instanceof BigDecimal)
                    price = ((BigDecimal) priceObj);
                else
                    price = new BigDecimal((Double) priceObj);

                int amount = object.getInt("amount");
                Item item = new Item(name, store, price, amount, 0, 0, Item.status.ON_SERVER);

                items.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getItemNames() {
        List<String> ret = new ArrayList<>(items.size());

        for (Item item : items) {
            ret.add(item.getName());
        }
        return ret;
    }

//    public void createInitialData() {
//        List<Item> itemList = new ArrayList<>();
//        itemList.add(new Item("milk", "Biedronka", new BigDecimal(126.54), 3));
//        itemList.add(new Item("egg", "Biedronka", (BigDecimal.valueOf(12.34)), 1));
//        itemList.add(new Item("ham", "Lidl", (BigDecimal.valueOf(4.56)), 6));
//        itemList.add(new Item("cheese", "Supersam", (BigDecimal.valueOf(7.12)), 0));
//        itemList.add(new Item("sour", "Biedronka", (BigDecimal.valueOf(4.57)), 12));
//        itemList.add(new Item("dog", "Biedronka", (BigDecimal.valueOf(123.67)), 4));
//        itemList.add(new Item("chicken leg", "Biedronka", (BigDecimal.valueOf(672.37)), 2));
//        itemList.add(new Item("papaya", "Grosz", (BigDecimal.valueOf(1235.63)), 3));
//        itemList.add(new Item("tomato", "Biedronka", (BigDecimal.valueOf(34.78)), 0));
//        itemList.add(new Item("potato", "Lidl", (BigDecimal.valueOf(3.10)), 1));
//        itemList.add(new Item("endonia", "Biedronka", (BigDecimal.valueOf(15.63)), 77));
//        items = itemList;
//    }


    /**
     * @return removed element
     */
    public Item remove(int position) {
        return items.remove(position);
    }

    public void setItemStatus(int position, Item.status status){
        Item item = items.get(position);
        item.setStatus(status);
        this.items.set(position, item);
    }

    public List<Item> getItems() {
        return items;
    }

    public void addItem(Item newItem) {
        boolean isCollision = false;
        for (Item item : items) {
            if (item.getName().equals(newItem.getName())){
                item.setItem(newItem);
                isCollision = true;
            }
        }
        if (!isCollision) items.add(newItem);
    }

    public void editItem(Item myItem) {
        for (Item item : items) {
            if (item.getName().equals(myItem.getName())){
                item.setAmount(myItem.getAmount());
                item.setDeltaAmount(myItem.getDeltaAmount());
                item.setDeltaAmountSent(myItem.getDeltaAmountSent());
            }
        }
    }

    public List<Item> getNonDeletedItems() {
        List<Item> ret = new ArrayList<>();
        for (Item item : items) {
            if(item.getStatus() != Item.status.DELETED){
                ret.add(item);
            }
        }
        return ret;
    }

    public Item getItem(int position) {
        return items.get(position);
    }

    public Item getNonDeletedItem(int position) {
        List<Item> nonDeletedItems = getNonDeletedItems();
        return nonDeletedItems.get(position);
    }

}
