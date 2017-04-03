package com.example.varunrao.wifisignalstrength;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.content.*;
import android.net.wifi.*;
import java.util.*;
import android.widget.*;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class MainActivity extends AppCompatActivity implements Runnable{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Thread t=new Thread(this);
        t.start();



    }
    static int i=0;
    @Override
    public void run() {
        Button next=(Button)findViewById(R.id.goToNext);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,RSSIDetected.class);

                startActivity(i);
            }
        });
        try {
            GraphView graph = (GraphView) findViewById(R.id.graph);
            final LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
            series.setTitle("Variation in RSSI");
            graph.addSeries(series);
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setMinX(0);
            graph.getViewport().setMaxX(60);
            graph.getViewport().setYAxisBoundsManual(true);
            graph.getViewport().setMaxY(0);
            graph.getViewport().setMinY(-100);
            graph.setTitle("Variations in RSSI");
            final SQLiteDatabase sqlDB=openOrCreateDatabase("db123#4",MODE_PRIVATE,null);
            sqlDB.execSQL("CREATE TABLE IF NOT EXISTS RSSIs(EventNo int AUTO_INCREMENT,rssiVal int,timeMeasured VARCHAR)");
            sqlDB.execSQL("DELETE FROM RSSIs");
            final Vector<String> RSSIDetails=new <String>Vector();
            while(i<20)
            {
                //arrayAdapter.clear();
                //arrayAdapter.notifyDataSetChanged();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final TextView signalDisplayer=(TextView)findViewById(R.id.signalDisplay);
                        boolean startAhead=false;
                        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        if(wifiManager.getWifiState()==WifiManager.WIFI_STATE_ENABLED)
                        {
                            // Level of current connection
                            int rssi = wifiManager.getConnectionInfo().getRssi();
                            signalDisplayer.setText("Signal Strength is " + rssi + " dbM");
                            System.out.println(rssi);
                            RSSIDetails.add(0,rssi+"dbMs@"+Calendar.getInstance().getTime());
                            if(i>60)
                                startAhead=true;
                            series.appendData(new DataPoint(i,rssi),startAhead,60);
                            //arrayAdapter.notifyDataSetChanged();
                            sqlDB.execSQL("INSERT INTO RSSIs(rssiVal,timeMeasured) VALUES("+rssi+",'"+Calendar.getInstance().getTime()+"')");
                        }
                        else
                        {
                            signalDisplayer.setText("Wifi not enabled!");
                        }
                        i++;
                    }
                });
                Thread.sleep(500);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println(e);
        }
    }
}