package com.ancologi.applications.bloemb.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.ancologi.applications.bloemb.Masters.MasterActivity;
import com.ancologi.applications.bloemb.R;
import com.mazenrashed.printooth.Printooth;
import com.mazenrashed.printooth.data.printable.ImagePrintable;
import com.mazenrashed.printooth.data.printable.Printable;
import com.mazenrashed.printooth.data.printable.RawPrintable;
import com.mazenrashed.printooth.data.printable.TextPrintable;
import com.mazenrashed.printooth.data.printer.DefaultPrinter;
import com.mazenrashed.printooth.ui.ScanningActivity;
import com.mazenrashed.printooth.utilities.Printing;
import com.mazenrashed.printooth.utilities.PrintingCallback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;


import java.util.ArrayList;

public class BluetoothPrintActivity extends MasterActivity implements PrintingCallback {

    Button btn_unpair_pair, btn_print, btn_print_image;
    Printing printing;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        setContentView(R.layout.bluetooth_print_activity);
        super.onCreate(savedInstanceState);

        assignUIReferences();
        initPrinting();
        assignActions();

    }

    @Override
    protected void getData() {

    }

    @Override
    protected void showData() {

    }

    @Override
    protected void assignUIReferences() {
        btn_print = findViewById(R.id.btnPrint);
        btn_print_image= findViewById(R.id.btnPrintImage);
        btn_unpair_pair = findViewById(R.id.btnPairUnpair);


        if (printing != null){
            printing.setPrintingCallback(this);
        }
    }

    @Override
    protected void assignActions() {

        btn_unpair_pair.setOnClickListener(v -> {
            if (Printooth.INSTANCE.hasPairedPrinter())
                Printooth.INSTANCE.removeCurrentPrinter();
            else {
                startActivityForResult(new Intent(BluetoothPrintActivity.this, ScanningActivity.class),ScanningActivity.SCANNING_FOR_PRINTER);
                changePairAndUnpair();
            }
        });

        btn_print_image.setOnClickListener(v -> {
            if (!Printooth.INSTANCE.hasPairedPrinter())
                startActivityForResult(new Intent(BluetoothPrintActivity.this,ScanningActivity.class),ScanningActivity.SCANNING_FOR_PRINTER);
            else
                printImages();
        });

        btn_print.setOnClickListener(v -> {
            if (!Printooth.INSTANCE.hasPairedPrinter())
                startActivityForResult(new Intent(BluetoothPrintActivity.this,ScanningActivity.class),ScanningActivity.SCANNING_FOR_PRINTER);
            else
                printText();
        });

        changePairAndUnpair();
    }

    private void printText() {

        ArrayList<Printable> printables = new ArrayList<>();
        printables.add(new RawPrintable.Builder(new byte[]{27,100,4}).build());

        // Add Text
        printables.add(new TextPrintable.Builder()
        .setText("Hello Ancologi : ")
        .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
        .setNewLinesAfter(1)
        .build());


        // Custom Text
        printables.add(new TextPrintable.Builder()
        .setText("Hello Ancologi")
        .setLineSpacing(DefaultPrinter.Companion.getLINE_SPACING_60())
        .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
        .setEmphasizedMode(DefaultPrinter.Companion.getEMPHASIZED_MODE_BOLD())
        .setUnderlined(DefaultPrinter.Companion.getUNDERLINED_MODE_ON())
        .setNewLinesAfter(1)
        .build());

        Log.d("yahya",String.valueOf(printables));
        Log.d("yahyaa",String.valueOf(printing));

        printing.print(printables);
    }

    private void printImages() {

        ArrayList<Printable> printables = new ArrayList<>();

        Picasso.get()
                .load("http://bloemb.ancologi.nl/uploads/logo/site_logo.png")
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        printables.add(new ImagePrintable.Builder(bitmap).build());

                        printing.print(printables);
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
    }

    private void changePairAndUnpair() {
        if (Printooth.INSTANCE.hasPairedPrinter())
            btn_unpair_pair.setText(new StringBuilder("Unpair")
                    .append(Printooth.INSTANCE.getPairedPrinter().getName()).toString());
        else
            btn_unpair_pair.setText("Pair with printer");
    }

    @Override
    public void connectingWithPrinter() {

        Toast.makeText(this,"Connecting to printer",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void connectionFailed(String s) {
        Toast.makeText(this,"Failed: " +s,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(String s) {
        Toast.makeText(this,"Error: " +s,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMessage(String s) {
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();

    }

    @Override
    public void printingOrderSentSuccessfully() {
        showSnackBarMessage("Order Sent to printer");
    }


    // Ctrl + O



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ScanningActivity.SCANNING_FOR_PRINTER && resultCode == Activity.RESULT_OK)
            initPrinting();
        changePairAndUnpair();
    }

    private void initPrinting() {

        if(!Printooth.INSTANCE.hasPairedPrinter())
            printing = Printooth.INSTANCE.printer();
        if (printing != null)
            printing.setPrintingCallback(this);
    }
}
