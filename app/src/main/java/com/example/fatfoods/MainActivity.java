package com.example.fatfoods;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collections;
import java.util.Hashtable;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    Hashtable<String, Integer> hashTableFoods = new Hashtable<String, Integer>();
    EditText inputGram;
    TextView inputResult;
    TextView inputFood;
    String selectedFood;
    int setGram;
    ArrayAdapter<String> adapterItems;
    OkHttpClient client = new OkHttpClient();
    Call lastCall;
    private static final String BASE_URL_SERVICE = "https://csrusader.tk/search-food";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inputGram = findViewById(R.id.idGram);
        inputResult = findViewById(R.id.idResult);
        inputFood = findViewById(R.id.idFood);
        inputGram.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                setGram = 0;
                if (editable.length() > 0) {
                    setGram = Integer.parseInt(editable.toString());
                }
                calcCalories();
            }
        });
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_searchable_spinner);
        dialog.getWindow().setLayout(650, 800);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        inputFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                EditText dialogEditText = dialog.findViewById(R.id.idQueryFood);
                ListView dialogListView = dialog.findViewById(R.id.idListFoods);
                adapterItems = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, Collections.list(hashTableFoods.keys()));
                dialogListView.setAdapter(adapterItems);
                dialogEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (editable.length() >= 2) {
                            searchFood(editable.toString());
                        } else {
                            clearQueryResult();
                        }
                    }
                });
                dialogListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        selectedFood = adapterItems.getItem(i);
                        inputFood.setText(selectedFood);
                        dialog.dismiss();
                        calcCalories();
                    }
                });
            }
        });
    }

    private void searchFood(String query) {
        Request request = new Request.Builder()
                .url(BASE_URL_SERVICE + "?query=" + query)
                .build();
        if (lastCall != null) {
            lastCall.cancel();
        }
        lastCall = client.newCall(request);
        lastCall.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        clearQueryResult();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        clearQueryResult();
                        JSONArray foods = new JSONArray();
                        try {
                            String data = response.body().string();
                            System.out.print(data);
                            foods = new JSONArray(data);
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                        if (foods.length() == 0) {
                            return;
                        }
                        for (int j = 0; j < foods.length(); j++) {
                            try {
                                JSONObject food = foods.getJSONObject(j);
                                hashTableFoods.put(food.optString("name"), food.optInt("calories"));
                            } catch (JSONException e) {
                                return;
                            }
                        }
                        adapterItems.addAll(Collections.list(hashTableFoods.keys()));
                        adapterItems.getFilter().filter(query);
                    }
                });
            }
        });
    }

    private void calcCalories() {
        if (selectedFood == null) {
            return;
        }
        Integer foodValue = hashTableFoods.get(selectedFood);
        if (foodValue != null) {
            inputResult.setText(
                String.valueOf(
                    Math.round((double)setGram * ((double)foodValue / 100))
                )
            );
        }
    }

    private void clearQueryResult() {
        hashTableFoods.clear();
        adapterItems.clear();
    }
}