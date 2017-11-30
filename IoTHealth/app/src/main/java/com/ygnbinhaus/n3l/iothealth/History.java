package com.ygnbinhaus.n3l.iothealth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class History extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        ArrayList<Record> recordArrayList = new ArrayList<Record>();
        final RecordAdapter recordAdapter = new RecordAdapter(this,recordArrayList);
        ListView listView = (ListView)findViewById(R.id.historylistview);
        listView.setAdapter(recordAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Record record = (Record) recordAdapter.getItem(position);
                Intent showDetailIntent = new Intent(getApplicationContext(),ShowDetailMap.class);
                showDetailIntent.putExtra("EXTRA_DATE",record.getDate());
                showDetailIntent.putExtra("EXTRA_HEARTRATE",record.getHeartrate().toString());
                showDetailIntent.putExtra("EXTRA_OXYGEN",record.getOxygen().toString());
                showDetailIntent.putExtra("EXTRA_TEMPERATURE",record.getTemperature().toString());
                showDetailIntent.putExtra("EXTRA_HUMIDITY",record.getHumidity().toString());
                showDetailIntent.putExtra("EXTRA_LATITUDE",record.getLatitude().toString());
                showDetailIntent.putExtra("EXTRA_LONGITUDE",record.getLongitude().toString());
                showDetailIntent.putExtra("EXTRA_COMMENT",record.getComment());
                startActivity(showDetailIntent);
            }
        });
        HttpPostRequest request = new HttpPostRequest();
        JSONObject requestJSON = new JSONObject();
        String dataString;
        try {
            requestJSON.put("nrc",preferences.getString("nrc",""));
            requestJSON.put("key",preferences.getString("key",""));
            dataString = request.execute(String.format("%s/records", preferences.getString("server_address", "")),requestJSON.toString()).get();
            JSONArray records = parseRecords(dataString);
            for(int i=0;i<records.length();i++){
                recordArrayList.add(createRecord(records.getJSONObject(i)));
            }
            recordAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private Record createRecord(JSONObject recordJSON) throws JSONException {
        return new Record(recordJSON.getString("date"),recordJSON.getDouble("heartrate"),recordJSON.getDouble("oxygen"),recordJSON.getDouble("temperature"),recordJSON.getDouble("humidity"),recordJSON.getJSONObject("location").getDouble("latitude"),recordJSON.getJSONObject("location").getDouble("longitude"), recordJSON.getString("comment"));
    }

    private JSONArray parseRecords(String jsonString) throws JSONException {
        return new JSONArray(jsonString);
    }

}
