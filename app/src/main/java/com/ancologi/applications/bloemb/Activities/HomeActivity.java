package com.ancologi.applications.bloemb.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.ancologi.applications.bloemb.Adapters.PaginationAdapter;
import com.ancologi.applications.bloemb.Api.CallBackListener;
import com.ancologi.applications.bloemb.Api.SendGetJsonApi;
import com.ancologi.applications.bloemb.Masters.MasterActivity;
import com.ancologi.applications.bloemb.Models.Order;
import com.ancologi.applications.bloemb.R;
import com.ancologi.applications.bloemb.Utils.PaginationScrollListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;



public class HomeActivity extends MasterActivity implements PaginationAdapter.ItemClickListener{

    private static final String TAG = "Yahya_Ancologi";
    NavigationView navigationView;
    DrawerLayout drawer;
    Toolbar toolbar;
    Context context;
    private View header;

    private ArrayList<Order> orders = new ArrayList<>();

    private RecyclerView mRecyclerView;
    //    private LoadingDialog mLoadingDialog;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView errorTextView;
    private LinearLayoutManager mLayoutManager;

    ProgressBar progressBar;

    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    // limiting to 5 for this tutorial, since total pages in actual API is very large. Feel free to modify.
    private int TOTAL_PAGES = 5;
    private int currentPage = PAGE_START;
    PaginationAdapter adapter;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        context = this;
        setContentView(R.layout.activity_home);
        super.onCreate(savedInstanceState);
        adapter = new PaginationAdapter(context);

        assignUIReferences();
        assignActions();
        loadFirstPage();

        setMenuTextColor(this,toolbar,R.id.language_title,R.color.custom_blue);
    }



    @Override
    protected void getData() {

    }

    @Override
    protected void showData() {

    }

    @Override
    protected void assignUIReferences() {
        navigationView = findViewById(R.id.nav_view);
        header = navigationView.getHeaderView(0);
        drawer = findViewById(R.id.drawer_layout);


        progressBar =  findViewById(R.id.main_progress);

        mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        mRecyclerView = findViewById(R.id.orders_recyclerView);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(adapter);
        errorTextView = findViewById(R.id.error_textView);
        mSwipeRefreshLayout = findViewById(R.id.mSwipeRefreshLayout);

    }





    @Override
    protected void assignActions() {
        toolbar = set_toolbar(false, "");

        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            if (!isLoading) {
                mSwipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.VISIBLE);
                currentPage = PAGE_START;
                isLastPage = false;
                adapter.clear();
                loadFirstPage();
            }
        });

        mRecyclerView.addOnScrollListener(new PaginationScrollListener(mLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                // mocking network delay for API call
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadNextPage();
                    }
                }, 1000);
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(context, ShowOrderActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("record",adapter.getItem(position));
        startActivity(intent);
    }

    private void loadFirstPage() {
        Log.d(TAG, "loadFirstPage: ");
        mSwipeRefreshLayout.setEnabled(false);

        JSONObject jsonParam = new JSONObject();
        ArrayList<Order> results = new ArrayList<>();
        try {
            jsonParam.put("limit", 10);

        }
        catch (Exception e) {}
        Log.d("jsonin",String.valueOf(jsonParam));
        new SendGetJsonApi(context, "orders?page="+currentPage, jsonParam,new CallBackListener() {
            @Override
            public void onFinish(String response) {
                String result="";

                // Create the root JSONObject from the JSON string.
                try {
                    JSONObject jsonin = new JSONObject(response);
                    result = jsonin.optString("result");
                    Log.d("jsonin",String.valueOf(jsonin));
                    if (result.equals("success")){
                        JSONObject paginator = jsonin.optJSONObject("paginator");
                        if (paginator != null)
                        TOTAL_PAGES = paginator.optInt("total_page");
                        JSONArray jArray = jsonin.getJSONArray("content");
                        if (jArray != null) {
                            Log.d("content", "0:");
                            results.clear();
                            for (int i=0;i<jArray.length();i++){
                                Order temp = new Order();
                                Gson json = new Gson();
                                temp = json.fromJson(jArray.get(i).toString(),Order.class);

                                results.add(temp);
                            }
                        }

                        if (results.size() <= 0){
                            errorTextView.setText(R.string.no_records_yet);
                            errorTextView.setVisibility(View.VISIBLE);
                        }else
                            errorTextView.setVisibility(View.INVISIBLE);

                    }
                    else {
                        showSnackBarMessage(getResources().getString(R.string.network_error));
                        int error_code = jsonin.optInt("error_code");
                        String error_des = jsonin.optString("error_des");

                        switch (error_code){
                            case 1:
                                if(!error_des.equals("")){
                                    Log.d("content", error_des);
                                    Toast.makeText(context, error_des , Toast.LENGTH_LONG).show();
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
                progressBar.setVisibility(View.GONE);

                adapter.addAll(results);

                if (currentPage < TOTAL_PAGES) adapter.addLoadingFooter();
                else isLastPage = true;

                mSwipeRefreshLayout.setEnabled(true);

            }
            @Override
            public void onProgress(int process) {}
        }).Execute();



    }

    private void loadNextPage() {
        Log.d(TAG, "loadNextPage: " + currentPage);

        Log.d(TAG, "loadFirstPage: ");
        mSwipeRefreshLayout.setEnabled(false);


        JSONObject jsonParam = new JSONObject();
        ArrayList<Order> results = new ArrayList<>();
        try {
            jsonParam.put("limit", 10);

        }
        catch (Exception e) {}
        Log.d("jsonin",String.valueOf(jsonParam));
        new SendGetJsonApi(context, "orders?page="+currentPage, jsonParam,new CallBackListener() {
            @Override
            public void onFinish(String response) {
                String result="";

                // Create the root JSONObject from the JSON string.
                try {
                    Log.d("jsonin",String.valueOf(response));

                    JSONObject jsonin = new JSONObject(response);
                    result = jsonin.optString("result");
                    Log.d("jsonin",String.valueOf(jsonin));
//                    mLoadingDialog.closeDialog();
                    if (result.equals("success")){
                        JSONArray jArray = jsonin.getJSONArray("content");
                        if (jArray != null) {
                            Log.d("content", "0:");
                            results.clear();
                            for (int i=0;i<jArray.length();i++){
                                Order temp = new Order();
                                Gson json = new Gson();
                                temp = json.fromJson(jArray.get(i).toString(),Order.class);

                                results.add(temp);
                            }
                        }

                        if (results.size() <= 0){
                            errorTextView.setText(R.string.no_records_yet);
                            errorTextView.setVisibility(View.VISIBLE);
                        }
                    }
                    else {
                        showSnackBarMessage(getResources().getString(R.string.network_error));
                        int error_code = jsonin.optInt("error_code");
                        String error_des = jsonin.optString("error_des");

                        switch (error_code){
                            case 1:
                                if(!error_des.equals("")){
                                    Log.d("content", error_des);
                                    Toast.makeText(context, error_des , Toast.LENGTH_LONG).show();
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

                adapter.removeLoadingFooter();
                isLoading = false;
                adapter.addAll(results);

                if (currentPage != TOTAL_PAGES) adapter.addLoadingFooter();
                else isLastPage = true;

                mSwipeRefreshLayout.setEnabled(true);

            }
            @Override
            public void onProgress(int process) {}
        }).Execute();

    }





    // Main Functions
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.english:
                ChangeLanguage("en");
                break;
            case R.id.arabic:
                ChangeLanguage("ar");
                break;
            case R.id.dutch:
                ChangeLanguage("nl");
                break;
//            case R.id.action_search:
//                startActivity(new Intent(this, SearchActivity.class));
//                break;
        }
        return super.onOptionsItemSelected(item);
    }






}
