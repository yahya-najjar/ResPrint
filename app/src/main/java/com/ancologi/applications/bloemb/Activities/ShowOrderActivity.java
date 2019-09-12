package com.ancologi.applications.bloemb.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ancologi.applications.bloemb.Adapters.ProductsAdapter;
import com.ancologi.applications.bloemb.Api.CallBackListener;
import com.ancologi.applications.bloemb.Api.SendGetJsonApi;
import com.ancologi.applications.bloemb.Masters.MasterActivity;
import com.ancologi.applications.bloemb.Models.Order;
import com.ancologi.applications.bloemb.Models.Product;
import com.ancologi.applications.bloemb.R;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ShowOrderActivity extends MasterActivity implements ProductsAdapter.ItemClickListener{

    Order order;
    TextView order_date, order_num, customer_name, customer_city, customer_street, customer_code, customer_landmark, customer_email, customer_phone, payment_type, delivery_date, payment_type2;
    TextView now, min_30, min_45, min_60;
    RecyclerView productsRecyclerView;
    ArrayList<Product> products;
    private LinearLayoutManager mLayoutManager;
    private ProductsAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_show_order);
        super.onCreate(savedInstanceState);

        assignUIReferences();
        assignActions();
        getData();

    }

    @Override
    protected void getData() {
        Order order = (Order) getIntent().getSerializableExtra("order");
        int order_id =  getIntent().getIntExtra("order_id",-1);
        Log.d("my_order_id",String.valueOf(order_id));
        if (order != null){
            this.order = order;
            this.products = order.getProducts();
            showData();
        }
        else{
            Log.d("else_my_order_id",String.valueOf(order_id));
            JSONObject jsonParam = new JSONObject();
            try {
                jsonParam.put("limit", 10);
                jsonParam.put("order_id", order_id);
            }
            catch (Exception e) {}
            new SendGetJsonApi(this, "getOrder", jsonParam,new CallBackListener() {
                @Override
                public void onFinish(String response) {
                    String result="";
                    // Create the root JSONObject from the JSON string.
                    try {
                        JSONObject jsonin = new JSONObject(response);
                        Log.d("content", String.valueOf(jsonin));

                        result = jsonin.optString("result");
                        Log.d("all_data",String.valueOf(jsonin.optJSONObject("content")));
                        if (result.equals("success")){
                            Order temp = new Order();
                            Gson json = new Gson();
                            temp = json.fromJson(jsonin.optJSONObject("content").toString(),Order.class);
                            ShowOrderActivity.this.setOrder(temp);
                            showData();

                        }
                        else {

                            showSnackBarMessage(getResources().getString(R.string.order_not_found));
                            startActivity(new Intent(ShowOrderActivity.this,HomeActivity.class));
                            int error_code = jsonin.optInt("error_code");
                            String error_des = jsonin.optString("error_des");

                            switch (error_code){
                                case 1:
                                    if(!error_des.equals("")){
                                        Log.d("content", error_des);
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.order_not_found) , Toast.LENGTH_LONG).show();
                                        Intent home_intent = new Intent(getBaseContext(),HomeActivity.class);
                                        finish();
                                        startActivity(home_intent);
                                    }else {
                                        Log.d("content", "not success");
                                    }
                                    break;
                                case -1:
                                    Log.d("content", "not auth");

                                    break;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onProgress(int process) {}
            }).Execute();
        }
    }

    public void setOrder(Order my_order){
        this.order = my_order;
        this.products = my_order.getProducts();
    }

    @Override
    protected void showData() {
        order_date.setText(order.getOrder_date());
        order_num.setText(String.valueOf(order.getOrder_id()));
        customer_name.setText(order.getCustomer_name());
        customer_city.setText(order.getCity());
        customer_street.setText(order.getStreet());
        customer_code.setText(order.getPincode());
        customer_landmark.setText(order.getLandmark());
        customer_email.setText(order.getEmail());
        customer_phone.setText(order.getPhone());
        payment_type.setText(order.getPayment_type());
        payment_type2.setText(order.getPayment_type());
        delivery_date.setText(order.getDatedelver());


        mLayoutManager = new LinearLayoutManager(this);
        productsRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ProductsAdapter(this,products);
        mAdapter.setClickListener(ShowOrderActivity.this);
        productsRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void assignUIReferences() {
        order_date = findViewById(R.id.order_date) ;
        order_num = findViewById(R.id.order_num);
        customer_name = findViewById(R.id.customer_name);
        customer_city = findViewById(R.id.customer_city);
        customer_street = findViewById(R.id.customer_street);
        customer_code = findViewById(R.id.customer_code);
        customer_landmark = findViewById(R.id.customer_landmark);
        customer_email = findViewById(R.id.customer_email);
        customer_phone = findViewById(R.id.customer_phone);
        payment_type = findViewById(R.id.payment_type);
        payment_type2  = findViewById(R.id.payment_type2);
        delivery_date = findViewById(R.id.delivery_date);

        now = findViewById(R.id.now);
        min_30 = findViewById(R.id.min_30);
        min_45 = findViewById(R.id.min_45);
        min_60 = findViewById(R.id.min_60);

        productsRecyclerView = findViewById(R.id.items_recyclerView);
    }

    @Override
    protected void assignActions() {
       Toolbar toolbar = set_toolbar(true, "");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("content","pressed");
                onBackPressed();
            }
        });

        min_30.setOnClickListener(v -> {
            sendTimeShift(30);
            min_30.setBackground(getResources().getDrawable(R.drawable.rounded_reg_input_on));
            min_30.setTextColor(getResources().getColor(R.color.white));
            min_45.setEnabled(false);
            min_60.setEnabled(false);
            now.setEnabled(false);

        });

        min_45.setOnClickListener(v -> {
            sendTimeShift(45);
            min_45.setBackground(getResources().getDrawable(R.drawable.rounded_reg_input_on));
            min_30.setEnabled(false);
            min_60.setEnabled(false);
            now.setEnabled(false);

        });

        min_60.setOnClickListener(v -> {
            sendTimeShift(60);
            min_60.setBackground(getResources().getDrawable(R.drawable.rounded_reg_input_on));
            min_45.setEnabled(false);
            min_30.setEnabled(false);
            now.setEnabled(false);

        });

        now.setOnClickListener(v -> {
            // we don't need to send it .. just print
            now.setBackground(getResources().getDrawable(R.drawable.rounded_reg_input_on));
            min_45.setEnabled(false);
            min_60.setEnabled(false);
            min_30.setEnabled(false);

        });
    }

    public void sendTimeShift(int time_shift){

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.share_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = order.getCustomer_name();
        String shareTitle = order.getCustomer_name();
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareTitle);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.print)));
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(View view, int position) {

    }
}
