package com.example.pawel.fridgerator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.List;

public class BasicActivity extends AppCompatActivity {

    Integer communicationCounter;
    String deviceId;

    ListView listView;
    ItemList items;
    User user;
    List<Item> nonDeletedItems;
    ArrayAdapter<Item> myAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarBasic);
        toolbar.setTitle("Fridgerator");
        setSupportActionBar(toolbar);
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        Serializable userBundle = getIntent().getSerializableExtra("user");
        if (userBundle != null) {
            user = (User) userBundle;
        }

        items = getStoredData(user.getUsername());
        if (items == null) items = new ItemList();

        communicationCounter = getStoredCounter(user.getUsername());
        if(communicationCounter == null) communicationCounter = 1;
        //toolbar.setTitle("" + communicationCounter);


        Serializable newItemBundle = getIntent().getSerializableExtra("newItem");
        if (newItemBundle != null) {
            items.addItem((Item) newItemBundle);
        }

        Serializable editItemBundle = getIntent().getSerializableExtra("editItem");
        if (editItemBundle != null) {
            items.editItem((Item) editItemBundle);
        }
        saveStoredData(user, items);

        //sync goes here

        listView = (ListView) findViewById(R.id.itemList);
        //if(items == null) items = new ItemList();
        nonDeletedItems = items.getNonDeletedItems();
        myAdapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, nonDeletedItems);

        listView.setAdapter(myAdapter);
        registerForContextMenu(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position,
                                    long id) {
                Intent intent = new Intent(BasicActivity.this, ItemActivity.class);
                intent.putExtra("myItem", items.getItems().get(position));
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        final Button buttonSync = (Button) findViewById(R.id.buttonSyncId);
        buttonSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if sent items are null just gets data from server
                System.out.println("---------------------------");
                System.out.println("STORED: " + items.getItems());
                ItemList receivedList = ServletCommunication.sendPost(user, items, deviceId, communicationCounter);
                // Data is not up-to-date
                if (receivedList != null) {
                    items = receivedList;
                    nonDeletedItems.clear();
                    nonDeletedItems.addAll(items.getNonDeletedItems());
                }
                System.out.println("RECEIVED: " + items.getItems());
                saveStoredData(user, items);
                communicationCounter++;
                saveStoredCounter(user, communicationCounter);
                myAdapter.notifyDataSetChanged();
                buttonSync.setText("SYNC:" + communicationCounter);
            }
        });

        Button buttonAdd = (Button) findViewById(R.id.buttonAddId);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BasicActivity.this, AddNewActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        Button buttonLogOut = (Button) findViewById(R.id.buttonLogOutId);
        buttonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BasicActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case (R.id.delete_id):
                //Item removed = items.remove(info.position);
                //items.setItemStatus(info.position, Item.status.DELETED);
                myAdapter.remove(items.getNonDeletedItem(info.position));
                //items.getNonDeletedItems().get(info.position).setStatus(Item.status.DELETED);
                items.getNonDeletedItem(info.position).setStatus(Item.status.DELETED);
                //ServletCommunication.sendPost(user, items);
                saveStoredData(user, items);
                System.out.println("STORED: " + items.getItems());
                //myAdapter.remove(removed);
                myAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }

    }

    private void saveStoredData(User user, ItemList items) {
        SharedPreferences sharedPref = getSharedPreferences(user.getUsername(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(items);
        editor.putString("items", json);
        editor.apply();
    }

    private ItemList getStoredData(String username) {
        SharedPreferences sharedPref = getSharedPreferences(username, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPref.getString("items", null);
        return gson.fromJson(json, ItemList.class);
    }

    private void saveStoredCounter(User user, int communicationCounter) {
        SharedPreferences sharedPref = getSharedPreferences(user.getUsername() + "_counter", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(communicationCounter);
        editor.putString("counter", json);
        editor.apply();
    }

    private Integer getStoredCounter(String username){
        SharedPreferences sharedPref = getSharedPreferences(username + "_counter", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPref.getString("counter", null);
        if (json==null) return null;
        return gson.fromJson(json, Integer.class);
    }


}