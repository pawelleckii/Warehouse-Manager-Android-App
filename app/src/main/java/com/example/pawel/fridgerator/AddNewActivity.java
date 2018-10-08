package com.example.pawel.fridgerator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.Serializable;
import java.math.BigDecimal;

public class AddNewActivity extends AppCompatActivity {

    Toolbar mToolbar;
    EditText nameText;
    EditText priceText;
    EditText storeText;
    EditText amountText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);

        mToolbar = (Toolbar) findViewById(R.id.toolbarNew);
        nameText = (EditText) findViewById(R.id.nameTextId);
        priceText = (EditText) findViewById(R.id.priceTextId);
        storeText = (EditText) findViewById(R.id.storeTextId);
        amountText = (EditText) findViewById(R.id.amountTextId);
        mToolbar.setTitle("New Item");

        final Button buttonCreate = (Button) findViewById(R.id.buttonCreateId);

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!editTextsEmpty()) {
                    Item newItem = new Item(nameText.getText().toString(),
                            storeText.getText().toString(),
                            new BigDecimal(priceText.getText().toString()),
                            Integer.parseInt(amountText.getText().toString()),
                            0,
                            0,
                            Item.status.CREATED
                    );

                    Intent intent = new Intent(AddNewActivity.this, BasicActivity.class);
                    intent.putExtra("newItem", newItem);
                    intent.putExtra("user", getIntent().getSerializableExtra("user"));
                    startActivity(intent);
                } else {
                    buttonCreate.setError("Fields are empty!");
                }
            }


        });


    }

    private boolean editTextsEmpty() {
        if (nameText.getText().toString().isEmpty() ||
                storeText.getText().toString().isEmpty() ||
                priceText.getText().toString().isEmpty() ||
                amountText.getText().toString().isEmpty()
                ) return true;
        else return false;
    }

}
