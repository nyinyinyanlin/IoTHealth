package com.ygnbinhaus.n3l.iothealth;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.ref.WeakReference;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class Measure extends AppCompatActivity {

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    Toast.makeText(context, "USB Ready", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    Toast.makeText(context, "USB Permission not granted", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                    Toast.makeText(context, "No USB connected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    Toast.makeText(context, "USB device not supported", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private UsbService usbService;
    private MyHandler mHandler;
    private JSONObject recordJSON;
    private Button sendBtn;
    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mHandler = new MyHandler(this);
        Intent intent = getIntent();
        TextView locationTv = (TextView) findViewById(R.id.location);
        locationTv.setText(intent.getDoubleExtra("EXTRA_LATITUDE",0.0)+" : "+intent.getDoubleExtra("EXTRA_LONGITUDE",0.0));
        sendBtn = (Button) findViewById(R.id.send);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendBtn.setEnabled(false);
                HttpPostRequest request = new HttpPostRequest();
                String data = "null";
                EditText comment = (EditText) findViewById(R.id.comment);
                try {
                    recordJSON.put("comment",comment.getText().toString());
                    recordJSON.put("key",preferences.getString("key",""));
                    recordJSON.put("nrc",preferences.getString("nrc",""));
                    data = request.execute(String.format("%s/insertrecord", preferences.getString("server_address", "")),recordJSON.toString()).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    Toast toast = Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setFilters();  // Start listening notifications from UsbService
        startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mUsbReceiver);
        unbindService(usbConnection);
    }

    public boolean populateData(String dataString) {
        Double heartrate, oxygen, humidity, temperature;
        JSONObject dataObj;
        int index = dataString.lastIndexOf("{");
        if(index < 0) return false;
        try {
            dataString = dataString.substring(index);
            dataObj = new JSONObject(dataString);
            heartrate = dataObj.getDouble("heartrate");
            oxygen = dataObj.getDouble("oxygen");
            humidity = dataObj.getDouble("humidity");
            temperature = dataObj.getDouble("temperature");
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        JSONObject location = new JSONObject();
        try {
            location.put("latitude",getIntent().getDoubleExtra("EXTRA_LATITUDE",0.0));
            location.put("longitude",getIntent().getDoubleExtra("EXTRA_LONGITUDE",0.0));
            dataObj.put("location",location);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        TextView heartbeatTv = (TextView) findViewById(R.id.heartbeat);
        TextView oxygenTv = (TextView) findViewById(R.id.oxygen);
        TextView humidityTv = (TextView) findViewById(R.id.humidity);
        TextView temperatureTv = (TextView) findViewById(R.id.temperature);
        heartbeatTv.setText(heartrate + "BPM");
        oxygenTv.setText(oxygen + "%");
        humidityTv.setText(humidity + "%");
        temperatureTv.setText(temperature + "'F");
        recordJSON = dataObj;
        return true;
    }

    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(this, service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            startService(startService);
        }
        Intent bindingIntent = new Intent(this, service);
        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        registerReceiver(mUsbReceiver, filter);
    }

    /*
     * This handler will be passed to UsbService. Data received from serial port is displayed through this handler
     */

    private static class MyHandler extends Handler {
        private final WeakReference<Measure> mActivity;
        public StringBuilder msgString = new StringBuilder();
        public MyHandler(Measure activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UsbService.MESSAGE_FROM_SERIAL_PORT:
                    String data = (String) msg.obj;
                    if(data != null && data.equals("}")) {
                        msgString.append(data);
                        if (mActivity.get().populateData(msgString.toString())) {
                            mActivity.get().sendBtn.setEnabled(true);
                            Toast.makeText(mActivity.get(), "DATA RECEIVED", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mActivity.get(), "ERROR PARSING DATA", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        if(data != null) msgString.append(data);
                    }
                    break;
                case UsbService.CTS_CHANGE:
                    Toast.makeText(mActivity.get(), "CTS_CHANGE",Toast.LENGTH_LONG).show();
                    break;
                case UsbService.DSR_CHANGE:
                    Toast.makeText(mActivity.get(), "DSR_CHANGE",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }
}