package com.example.pawel.fridgerator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.Serializable;
import java.math.RoundingMode;

public class ItemActivity extends AppCompatActivity {

    Toolbar mToolbar;
    Button buttonPlus;
    Button buttonMinus;
    Button buttonOk;
    Item myItem;
    EditText editText;
    TextView amountText;
    TextView amountValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        mToolbar = (Toolbar) findViewById(R.id.toolbarNew);
        TextView priceText = (TextView) findViewById(R.id.priceId);
        TextView storeText = (TextView) findViewById(R.id.storeId);
        amountText = (TextView) findViewById(R.id.amountId);
        amountValue = (TextView) findViewById(R.id.amountValueId);
        buttonPlus = (Button) findViewById(R.id.buttonPlusId);
        buttonMinus = (Button) findViewById(R.id.buttonMinusId);
        buttonOk = (Button) findViewById(R.id.buttonOkId);
        editText = (EditText) findViewById(R.id.editTextId);

        final User user = ((User) getIntent().getSerializableExtra("user"));

        Serializable bundle = getIntent().getSerializableExtra("myItem");
        if (bundle != null) {
            myItem = (Item) bundle;
            mToolbar.setTitle(myItem.getName());

            priceText.setText(" PRICE : " + myItem.getPrice().setScale(2, RoundingMode.HALF_UP) + " zl");
            amountValue.setText(Integer.toString(myItem.getAmount()));
            storeText.setText(" STORE : " + myItem.getStore());
        }

        buttonPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editText.getText().toString().isEmpty()) {
                    int currentValue = getIntValue(amountValue);
                    if (currentValue < Integer.MAX_VALUE - getIntValue(editText)) {
                        currentValue += getIntValue(editText);
                    }
                    amountValue.setText(Integer.toString(currentValue));
                }
            }
        });

        buttonMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editText.getText().toString().isEmpty()) {
                    int currentValue = getIntValue(amountValue);
                    currentValue -= getIntValue(editText);
                    if (currentValue < 0) {
                        currentValue = 0;
                    }
                    amountValue.setText(Integer.toString(currentValue));
                }
            }
        });

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myItem.setDeltaAmount(myItem.getDeltaAmount() + getIntValue(amountValue) - myItem.getAmount());
                myItem.setDeltaAmountSent(myItem.getDeltaAmountSent() + getIntValue(amountValue) - myItem.getAmount());
                myItem.setAmount(getIntValue(amountValue));
                Intent intent = new Intent(ItemActivity.this, BasicActivity.class);
                intent.putExtra("editItem", myItem);
                intent.putExtra("user", user);
                startActivity(intent);

            }
        });

    }

    private int getIntValue(TextView textView) {
        String str = textView.getText().toString();
        float f = Float.parseFloat(str);
        return Math.abs(f) < Integer.MAX_VALUE ? (int) f : 0;
    }

}
