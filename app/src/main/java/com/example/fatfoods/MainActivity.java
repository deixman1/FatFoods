package com.example.fatfoods;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.Hashtable;

public class MainActivity extends AppCompatActivity {

    Hashtable<String, Integer> foods = new Hashtable<String, Integer>();
    AutoCompleteTextView autoCompleteTextView;
    EditText input;
    TextView inputResult;
    String selectedItem;
    ArrayAdapter<String> adapterItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        foods.put("Apple", 52);
        foods.put("Banana", 89);
        foods.put("Cucumber", 15);
        autoCompleteTextView = findViewById(R.id.food_types);
        input = findViewById(R.id.count);
        inputResult = findViewById(R.id.result);
        adapterItems = new ArrayAdapter<>(this, R.layout.list_items, Collections.list(foods.keys()));
        autoCompleteTextView.setAdapter(adapterItems);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedItem = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(MainActivity.this, selectedItem, Toast.LENGTH_SHORT).show();
            }
        });
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    Integer inputValue = Integer.parseInt(editable.toString());
                    Integer foodValue = foods.get(selectedItem);
                    if (foodValue != null) {
                        inputResult.setText(String.valueOf((double)inputValue * ((double)foodValue / 100)));
                    }
                }
            }
        });
    }
}