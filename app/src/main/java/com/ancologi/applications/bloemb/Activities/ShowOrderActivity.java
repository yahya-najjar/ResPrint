package com.ancologi.applications.bloemb.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ancologi.applications.bloemb.Adapters.ProductsAdapter;
import com.ancologi.applications.bloemb.Adapters.pdfDocumentAdapter;
import com.ancologi.applications.bloemb.Api.CallBackListener;
import com.ancologi.applications.bloemb.Api.SendGetJsonApi;
import com.ancologi.applications.bloemb.Masters.MasterActivity;
import com.ancologi.applications.bloemb.Models.Order;
import com.ancologi.applications.bloemb.Models.Product;
import com.ancologi.applications.bloemb.R;
import com.ancologi.applications.bloemb.Utils.Common;
import com.google.gson.Gson;
//import com.itextpdf.text.BaseColor;
//import com.itextpdf.text.Chunk;
//import com.itextpdf.text.Document;
//import com.itextpdf.text.DocumentException;
//import com.itextpdf.text.Element;
//import com.itextpdf.text.Font;
//import com.itextpdf.text.PageSize;
//import com.itextpdf.text.Paragraph;
//import com.itextpdf.text.RectangleReadOnly;
//import com.itextpdf.text.pdf.BaseFont;
//import com.itextpdf.text.pdf.PdfWriter;
//import com.itextpdf.text.pdf.draw.LineSeparator;
//import com.itextpdf.text.pdf.draw.VerticalPositionMark;
//import com.karumi.dexter.Dexter;
//import com.karumi.dexter.PermissionToken;
//import com.karumi.dexter.listener.PermissionDeniedResponse;
//import com.karumi.dexter.listener.PermissionGrantedResponse;
//import com.karumi.dexter.listener.PermissionRequest;
//import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class ShowOrderActivity extends MasterActivity implements ProductsAdapter.ItemClickListener{

    Order order;
    TextView order_date, order_num, customer_name, customer_city, customer_street, customer_code, customer_landmark, customer_email, customer_phone, payment_type, delivery_date, payment_type2;
    TextView now, min_30, min_45, min_60;
    RecyclerView productsRecyclerView;
    ArrayList<Product> products;
    private LinearLayoutManager mLayoutManager;
    private ProductsAdapter mAdapter;
    int delayTime = -1;

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
            min_30.setBackground(getResources().getDrawable(R.drawable.rounded_reg_input_on));

            min_45.setBackground(getResources().getDrawable(R.drawable.rounded_reg_input));
            min_60.setBackground(getResources().getDrawable(R.drawable.rounded_reg_input));
            now.setBackground(getResources().getDrawable(R.drawable.rounded_reg_input));
            delayTime = 30;

        });

        min_45.setOnClickListener(v -> {
            min_45.setBackground(getResources().getDrawable(R.drawable.rounded_reg_input_on));
            min_30.setBackground(getResources().getDrawable(R.drawable.rounded_reg_input));
            min_60.setBackground(getResources().getDrawable(R.drawable.rounded_reg_input));
            now.setBackground(getResources().getDrawable(R.drawable.rounded_reg_input));
            delayTime = 45;

        });

        min_60.setOnClickListener(v -> {
            min_60.setBackground(getResources().getDrawable(R.drawable.rounded_reg_input_on));
            min_45.setBackground(getResources().getDrawable(R.drawable.rounded_reg_input));
            min_30.setBackground(getResources().getDrawable(R.drawable.rounded_reg_input));
            now.setBackground(getResources().getDrawable(R.drawable.rounded_reg_input));
            delayTime = 60;

        });

        now.setOnClickListener(v -> {
            // we don't need to send it .. just print
            now.setBackground(getResources().getDrawable(R.drawable.rounded_reg_input_on));
            min_45.setBackground(getResources().getDrawable(R.drawable.rounded_reg_input));
            min_60.setBackground(getResources().getDrawable(R.drawable.rounded_reg_input));
            min_30.setBackground(getResources().getDrawable(R.drawable.rounded_reg_input));
            delayTime = 0;

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

        if (item.getItemId() == R.id.action_print){
            if (delayTime == -1){
                showSnackBarMessage(getResources().getString(R.string.pick_delay_time));
            }else
                Log.d("test","wifi");
//                Dexter.withActivity(this)
//                        .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                        .withListener(new PermissionListener() {
//                            @Override
//                            public void onPermissionGranted(PermissionGrantedResponse response) {
//                                sendTimeShift(delayTime);
////                                createPDFFile(Common.getAppPath(ShowOrderActivity.this)+"test_pdf.pdf");
//                            }
//
//                            @Override
//                            public void onPermissionDenied(PermissionDeniedResponse response) {
//
//                            }
//
//                            @Override
//                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
//
//                            }
//                        })
//                        .check();
        }else {

            startActivity(new Intent(ShowOrderActivity.this,BluetoothPrintActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

//    private void createPDFFile(String path) {
//
//        if(new File(path).exists())
//            new File(path).delete();
//
//        try {
//            Document document = new Document();
//
//            RectangleReadOnly pageSize = new RectangleReadOnly(80,4500);
//            // Save
//            PdfWriter.getInstance(document,new FileOutputStream(path));
//
//
//            // Open to write
//            document.open();
//
//
//            // Setting
//            document.setPageSize(PageSize.A4);
//            document.addCreationDate();
//            document.addAuthor("Ancologi.nl");
//            document.addCreator("Bloem van damascus");
//
//
//            // Font Setting
//            BaseColor colorAccent = new BaseColor(0,0,0,200);
//            float fontSize = 20.0f;
//
//
//            // Custom Font
//            BaseFont fontName = BaseFont.createFont("assets/fonts/brandon_medium.otf","UTF-8",BaseFont.EMBEDDED);
//            BaseFont boldFont = BaseFont.createFont("assets/fonts/brandon_medium_bold.otf","UTF-8",BaseFont.EMBEDDED);
//
//
//            // Create title of document
//            Font titleFont = new Font(fontName, 30.0f, Font.NORMAL, BaseColor.BLACK);
//            Font boldtitleFont = new Font(boldFont, 20.0f, Font.NORMAL, BaseColor.BLACK);
//
//            addNewItem(document,"bloemvandamascus.nl", Element.ALIGN_CENTER,titleFont);
//
//
//            // Add Empty Lines
//            addLineSpace(document);
//            addLineSpace(document);
//
//            // Add Restaurant Address
//            Font orderFont  = new Font(fontName,fontSize,Font.NORMAL,colorAccent);
//            addNewItem(document,getResources().getString(R.string.app_name),Element.ALIGN_LEFT,orderFont);
//            addNewItem(document,getResources().getString(R.string.address_label),Element.ALIGN_LEFT,orderFont);
//            addNewItem(document,getResources().getString(R.string.street_label),Element.ALIGN_LEFT,orderFont);
//            addNewItem(document,getResources().getString(R.string.street_code_label),Element.ALIGN_LEFT,orderFont);
//
//            // Add Empty Lines
//            addLineSpace(document);
//            addLineSpace(document);
//
//            // Add Order Date & Order Num
//            addNewItem(document,getResources().getString(R.string.order_date) + " " + order.getOrder_date() + "   " + order.getOrder_time(),Element.ALIGN_LEFT,orderFont);
//            addNewItem(document,getResources().getString(R.string.order_num) + " " + order.getOrder_id(),Element.ALIGN_LEFT,orderFont);
//
//            // Add Empty Lines
//            addLineSpace(document);
//            addLineSpace(document);
//
//            // Add Customer Information
//            addNewItem(document,order.getCustomer_name(),Element.ALIGN_LEFT,orderFont);
//            addNewItem(document,order.getCity(),Element.ALIGN_LEFT,orderFont);
//            addNewItem(document,order.getStreet(),Element.ALIGN_LEFT,orderFont);
//            addNewItem(document,order.getPincode(),Element.ALIGN_LEFT,orderFont);
//            addNewItem(document,order.getLandmark(),Element.ALIGN_LEFT,orderFont);
//            addNewItem(document,order.getEmail(),Element.ALIGN_LEFT,orderFont);
//            addNewItem(document,order.getPhone(),Element.ALIGN_LEFT,orderFont);
//
//            addLineSeparator(document);
//
//            // Add Payment Method & Delivery Date
//            addNewItem(document,getResources().getString(R.string.let_op), Element.ALIGN_CENTER,boldtitleFont);
//            addNewItem(document,getResources().getString(R.string.bestelling_is) + " " + paymentType(order.getPayment_type()), Element.ALIGN_CENTER,boldtitleFont);
//
//            addLineSeparatorWithoutSpaces(document);
//
//            addNewItem(document,getResources().getString(R.string.bezorgen_zsm), Element.ALIGN_CENTER,boldtitleFont);
//            addNewItem(document,order.getDatedelver() + "  " + order.getTimedelver(), Element.ALIGN_CENTER,boldtitleFont);
//
//            addLineSeparatorWithoutSpaces(document);
//
//
//            // Add Order Items
//            for (Iterator<Product> i = products.iterator(); i.hasNext();) {
//                Product product = i.next();
//                addNewItemWithLeftAndRight(document,product.getItem_qty() + " " + product.getItem_name(),product.getFinal_cost(),orderFont); // 2 pizza        30$
//                addLineSeparatorWithoutSpaces(document);
//            }
//
//
//            // Add Total
//            addLineSpace(document);
//            addNewItemWithLeftAndRight(document,getResources().getString(R.string.total),order.getTotal_cost(),orderFont);
//
//            addLineSeparatorWithoutSpaces(document);
//
//            // Add GoodBye
//            addNewItem(document,getResources().getString(R.string.betalingswijze) + " " + paymentType(order.getPayment_type()), Element.ALIGN_CENTER,titleFont);
//            addNewItem(document,getResources().getString(R.string.tot_ziens), Element.ALIGN_CENTER,titleFont);
//
//
//            document.close();
//
//            Toast.makeText(this,"Success",Toast.LENGTH_LONG).show();
//
//            printPDF();
//
//
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (DocumentException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }

    private void printPDF() {

        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);

        try {
            PrintDocumentAdapter printDocumentAdapter = new pdfDocumentAdapter(ShowOrderActivity.this,Common.getAppPath(ShowOrderActivity.this) + "test_pdf.pdf");
            printManager.print("Document",printDocumentAdapter,new PrintAttributes.Builder().build());
        }catch (Exception e){
            Log.d("Yahya","" + e.getMessage());
        }
    }

//    private void addNewItemWithLeftAndRight(Document document, String item_name, double final_cost, Font font) throws DocumentException {
//        Chunk chunkTextLeft = new Chunk(item_name, font);
//        Chunk chunkTextRight = new Chunk(String.valueOf(final_cost),font);
//        Paragraph paragraph = new Paragraph(chunkTextLeft);
//        paragraph.add(new Chunk(new VerticalPositionMark()));
//        paragraph.add(chunkTextRight);
//        document.add(paragraph);
//    }

    private String paymentType(String payment_type) {

        if (payment_type.equals("online"))
            return getResources().getString(R.string.online_betaald);
        else if (payment_type.equals("cash"))
            return getResources().getString(R.string.cach);
        else
            return getResources().getString(R.string.cach); // change later
    }


//    private void addLineSeparator(Document document) throws DocumentException {
//
//        LineSeparator lineSeparator = new LineSeparator();
//        lineSeparator.setLineColor(new BaseColor(0,0,0,68));
//        addLineSpace(document);
//        document.add(new Chunk(lineSeparator));
//        addLineSpace(document);
//
//    }
//
//    private void addLineSeparatorWithoutSpaces(Document document) throws DocumentException {
//
//        LineSeparator lineSeparator = new LineSeparator();
//        lineSeparator.setLineColor(new BaseColor(0,0,0,68));
//        document.add(new Chunk(lineSeparator));
//
//    }
//
//    private void addLineSpace(Document document) throws DocumentException {
//
//        document.add(new Paragraph(" "));
//    }
//
//    private void addNewItem(Document document, String text, int align, Font font) throws DocumentException {
//
//        Chunk chunk = new Chunk(text,font);
//        Paragraph paragraph = new Paragraph(chunk);
//        paragraph.setAlignment(align);
//        document.add(paragraph);
//    }

    @Override
    public void onItemClick(View view, int position) {

    }
}
