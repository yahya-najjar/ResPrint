package com.ancologi.applications.bloemb.Activities;

import com.ancologi.applications.bloemb.Masters.MasterActivity;
import com.ancologi.applications.bloemb.R;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

// Import neccessor namespace
import android.bluetooth.BluetoothAdapter;
import  android.bluetooth.BluetoothDevice;
import  android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.Set;
import java.util.UUID;
import android.os.Handler;


public class BluetoothPrinterActivity extends MasterActivity {


    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket;
    BluetoothDevice bluetoothDevice;

    OutputStream outputStream;
    InputStream inputStream;
    Thread thread;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;

    TextView lblPrinterName;
    EditText textBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.bluetooth_printer_activity);
        super.onCreate(savedInstanceState);

        // Create object of controls
        Button btnConnect = (Button) findViewById(R.id.btnConnect);
        Button btnDisconnect = (Button) findViewById(R.id.btnDisconnect);
        Button btnPrint = (Button) findViewById(R.id.btnPrint);

        textBox = (EditText) findViewById(R.id.txtText);

        lblPrinterName = (TextView) findViewById(R.id.lblPrinterName);

        getData();

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    FindBluetoothDevice();
                    openBluetoothPrinter();

                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });
        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    disconnectBT();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    printData();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });

    }

    void FindBluetoothDevice(){

        try{

            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if(bluetoothAdapter==null){
                lblPrinterName.setText("No Bluetooth Adapter found");
            }
            if(bluetoothAdapter.isEnabled()){
                Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBT,0);
            }

            Set<BluetoothDevice> pairedDevice = bluetoothAdapter.getBondedDevices();

            if(pairedDevice.size()>0){
                for(BluetoothDevice pairedDev:pairedDevice){
                    showSnackBarMessage(pairedDev.getName());
                    // My Bluetoth printer name is BTP_F09F1A
                    if(pairedDev.getName().equals("TSP100-C0132") || pairedDev.getName().equals("TSP1OO-CO132") || pairedDev.getName().equals("TSP100-CO132") || pairedDev.getName().equals("TSP1OO-C0132") || pairedDev.getName().equals("TSP10O-CO132")  || pairedDev.getName().contains("TSP")){
                        bluetoothDevice=pairedDev;
                        lblPrinterName.setText("Bluetooth Printer Attached: "+pairedDev.getName());
                        break;
                    }
                }
            }

            lblPrinterName.setText("Bluetooth Printer Attached");
        }catch(Exception ex){
            ex.printStackTrace();
        }

    }

    // Open Bluetooth Printer

    void openBluetoothPrinter() throws IOException{
        try{

            //Standard uuid from string //
            UUID uuidSting = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            bluetoothSocket=bluetoothDevice.createRfcommSocketToServiceRecord(uuidSting);
            bluetoothSocket.connect();
            outputStream=bluetoothSocket.getOutputStream();
            inputStream=bluetoothSocket.getInputStream();

            beginListenData();

        }catch (Exception ex){

        }
    }

    void beginListenData(){
        try{

            final Handler handler =new Handler();
            final byte delimiter=10;
            stopWorker =false;
            readBufferPosition=0;
            readBuffer = new byte[1024];

            thread=new Thread(new Runnable() {
                @Override
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorker){
                        try{
                            int byteAvailable = inputStream.available();
                            if(byteAvailable>0){
                                byte[] packetByte = new byte[byteAvailable];
                                inputStream.read(packetByte);

                                for(int i=0; i<byteAvailable; i++){
                                    byte b = packetByte[i];
                                    if(b==delimiter){
                                        byte[] encodedByte = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer,0,
                                                encodedByte,0,
                                                encodedByte.length
                                        );
                                        final String data = new String(encodedByte,"US-ASCII");
                                        readBufferPosition=0;
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                lblPrinterName.setText(data);
                                            }
                                        });
                                    }else{
                                        readBuffer[readBufferPosition++]=b;
                                    }
                                }
                            }
                        }catch(Exception ex){
                            stopWorker=true;
                        }
                    }

                }
            });

            thread.start();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    // Printing Text to Bluetooth Printer //
    void printData() throws  IOException{
        try{
            String msg = textBox.getText().toString();
            msg+="\n";
            outputStream.write(msg.getBytes());
            lblPrinterName.setText("Printing Text...");
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    // Disconnect Printer //
    void disconnectBT() throws IOException{
        try {
            stopWorker=true;
            outputStream.close();
            inputStream.close();
            bluetoothSocket.close();
            lblPrinterName.setText("Printer Disconnected.");
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }




    @Override
    protected void getData() {

    }

    @Override
    protected void showData() {

    }

    @Override
    protected void assignUIReferences() {

    }

    @Override
    protected void assignActions() {

    }
}
