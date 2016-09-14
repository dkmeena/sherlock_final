package com.example.dinesh.sherlock_final;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends Activity implements View.OnClickListener {

    public TextView text;
    public Button start, stop, restart;
    public String cellid;
    public int rssi = 0;
    public String operatorName;
    private LocationManager locationManagerNET;
    private LocationManager locationManagerGPS;
    public double gpslat, gpslon, gpsacc, netlat, netlon, netacc;
    public int flagstrt = 0, flagstop = 0, flagrestrt = 0;
    public TelephonyManager tm;
    public MyPhoneStateListener MyListener;
    public String sfile = "";
    public EditText fname;
    public File file;
    AlarmManager am;
    Intent intent;
    PendingIntent pendingIntent;
    PendingIntent pIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = (TextView) findViewById(R.id.text);
        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.stop);
        restart = (Button) findViewById(R.id.restart);
        fname = (EditText) findViewById(R.id.fname);

        start.setOnClickListener(this);
        stop.setOnClickListener(this);
        restart.setOnClickListener(this);


        am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(this, Alarm.class);

        pIntent = PendingIntent.getBroadcast(this, 0,
                intent, 0);

        locationManagerNET = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManagerGPS = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


    }


    LocationListener locationListenerNET = new LocationListener() {
        public void onLocationChanged(Location location) {

//            Toast.makeText(MainActivity.this, "location changed", Toast.LENGTH_SHORT).show();
             //Log.d("Listener", "Location changed");

            Calendar c = Calendar.getInstance();
            int hr = c.get(Calendar.HOUR);
            int mn = c.get(Calendar.MINUTE);
            int sec = c.get(Calendar.SECOND);
            SimpleDateFormat mdformat = new SimpleDateFormat("yyyy / MM / dd ");
            String strDate = mdformat.format(c.getTime());

            GsmCellLocation loc = (GsmCellLocation) tm.getCellLocation();
            cellid = String.valueOf(loc.getCid() & 0xffff);
            operatorName = tm.getSimOperatorName();

            netlat = location.getLatitude();
            netlon = location.getLongitude();
            netacc = location.getAccuracy();
            netacc = Math.round(netacc * 100);
            netacc = netacc / 100.0;

            sfile = sfile + strDate + " || " + hr + "::" + mn + "::" + sec + " || " + gpslat + " || " + gpslon + " || " + gpsacc + " || " + netlat + " || " + netlon + " || " + netacc + " || " + cellid + " || " + operatorName + " || " + rssi + "\n";
            text.setText(strDate + " || " + hr + "::" + mn + "::" + sec + " || " + gpslat + " || " + gpslon + " || " + gpsacc + " || " + netlat + " || " + netlon + " || " + netacc + " || " + cellid + " || " + operatorName + " || " + rssi);

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };

    LocationListener locationListenerGPS = new LocationListener() {
        public void onLocationChanged(Location location) {

//            Toast.makeText(MainActivity.this, "location changed", Toast.LENGTH_SHORT).show();
            //Log.d("Listener", "Location changed");

            Calendar c = Calendar.getInstance();
            int hr = c.get(Calendar.HOUR);
            int mn = c.get(Calendar.MINUTE);
            int sec = c.get(Calendar.SECOND);

            SimpleDateFormat mdformat = new SimpleDateFormat("yyyy / MM / dd ");
            String strDate = mdformat.format(c.getTime());

            GsmCellLocation loc = (GsmCellLocation) tm.getCellLocation();
            cellid = String.valueOf(loc.getCid() & 0xffff);
            operatorName = tm.getSimOperatorName();

            gpslat = location.getLatitude();
            gpslon = location.getLongitude();
            gpsacc = location.getAccuracy();
            gpsacc = Math.round(gpsacc * 100);
            gpsacc = gpsacc / 100.0;

            sfile = sfile + strDate + " || " + hr + "::" + mn + "::" + sec + " || " + gpslat + " || " + gpslon + " || " + gpsacc + " || " + netlat + " || " + netlon + " || " + netacc + " || " + cellid + " || " + operatorName + " || " + rssi + "\n";
            text.setText(strDate + " || " + hr + "::" + mn + "::" + sec + " || " + gpslat + " || " + gpslon + " || " + gpsacc + " || " + netlat + " || " + netlon + " || " + netacc + " || " + cellid + " || " + operatorName + " || " + rssi);

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.start && flagstrt == 0 && flagstop == 0) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        0);

                return;

            }

            if (!locationManagerGPS.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Please Enable GPS")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

                return;

            }


            locationManagerNET = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationManagerNET.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNET);
            locationManagerGPS = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationManagerGPS.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGPS);


            flagstrt = 1;

            // Alarm

            pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            int ALARM_TYPE = AlarmManager.RTC_WAKEUP;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                am.setExactAndAllowWhileIdle(ALARM_TYPE, System.currentTimeMillis() + 30000, pendingIntent);
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                am.setExact(ALARM_TYPE, System.currentTimeMillis() + 30000, pendingIntent);
            else
                am.set(ALARM_TYPE, System.currentTimeMillis() + 30000, pendingIntent);


            Toast.makeText(getApplicationContext(), "Data Collection Started !!!", Toast.LENGTH_SHORT).show();

            tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            MyListener = new MyPhoneStateListener();
            tm.listen(MyListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);


        } else if (v.getId() == R.id.stop && flagstrt == 1 && flagstop == 0) {

            flagstop = 1;

            if (locationManagerNET != null) {
                locationManagerNET.removeUpdates(locationListenerNET);
                locationManagerNET = null;
            }

            if (locationManagerGPS != null) {
                locationManagerGPS.removeUpdates(locationListenerGPS);
                locationManagerGPS = null;
            }

            if (tm != null)
                tm.listen(MyListener, PhoneStateListener.LISTEN_NONE);

            text.setText("DONE!!!");
            writeToFile(sfile);

            // Intent intent = new Intent(this, Alarm.class);
//            final PendingIntent pIntent = PendingIntent.getBroadcast(this, 0,
//                    intent, 0);

            //AlarmManager alarm = (AlarmManager) this.getSystemService(this.ALARM_SERVICE);
            am.cancel(pIntent);

            Toast.makeText(MainActivity.this, " Data Collection Stopped ", Toast.LENGTH_SHORT).show();

            // ----- send to server ---------//

            new Thread(new Runnable() {
                public void run() {

                    try{
                        URL url = new URL("http://10.129.28.209:8080/sherlock_server/Main");
                        URLConnection connection = url.openConnection();

                        Log.d("inputString", sfile);

                        connection.setDoOutput(true);
                        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                        out.write(sfile);
                        out.close();

                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                        final String returnString = in.readLine();

                        in.close();


                        runOnUiThread(new Runnable() {
                            public void run() {

                                Toast.makeText(getApplicationContext(), returnString , Toast.LENGTH_SHORT).show();

                            }
                        });

                    }catch(Exception e)
                    {
                        Log.d("Exception",e.toString());
                    }

                }
            }).start();

            // ------------------- //



        } else if (v.getId() == R.id.restart) {

            if (locationManagerNET != null) {
                locationManagerNET.removeUpdates(locationListenerNET);
                locationManagerNET = null;
            }

            if (locationManagerGPS != null) {
                locationManagerGPS.removeUpdates(locationListenerGPS);
                locationManagerGPS = null;
            }

            if (tm != null)
                tm.listen(MyListener, PhoneStateListener.LISTEN_NONE);

            // Intent intent = new Intent(this, Alarm.class);
//            final PendingIntent pIntent = PendingIntent.getBroadcast(this, 0,
//                    intent, 0);
            //AlarmManager alarm = (AlarmManager) this.getSystemService(this.ALARM_SERVICE);
            am.cancel(pIntent);

            Intent i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage(getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(i);

        }
    }

    private class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            //Log.d("signal","signal changed");
            int val = -113 + 2 * signalStrength.getGsmSignalStrength();
            rssi = val;
            GsmCellLocation loc = (GsmCellLocation) tm.getCellLocation();

            cellid = String.valueOf(loc.getCid() & 0xffff);
            operatorName = tm.getSimOperatorName();

            Calendar c = Calendar.getInstance();
            int hr = c.get(Calendar.HOUR);
            int mn = c.get(Calendar.MINUTE);
            int sec = c.get(Calendar.SECOND);

            SimpleDateFormat mdformat = new SimpleDateFormat("yyyy / MM / dd ");
            String strDate = mdformat.format(c.getTime());

            sfile = sfile + strDate + " || " + hr + "::" + mn + "::" + sec + " || " + gpslat + " || " + gpslon + " || " + gpsacc + " || " + netlat + " || " + netlon + " || " + netacc + " || " + cellid + " || " + operatorName + " || " + rssi + "\n";
            text.setText(strDate + " || " + hr + "::" + mn + "::" + sec + " || " + gpslat + " || " + gpslon + " || " + gpsacc + " || " + netlat + " || " + netlon + " || " + netacc + " || " + cellid + " || " + operatorName + " || " + rssi);


        }
    }

    public boolean writeToFile(String data) {
        try {
            Toast.makeText(getApplicationContext(), "writing to file", Toast.LENGTH_SHORT).show();
            File file = new File(getExternalFilesDir(null).toString());
            file.mkdirs();
            File f = new File(file, fname.getText().toString() + ".txt");
            //Log.d("nkn", String.valueOf(fname));
            FileWriter fw = new FileWriter(f, true);
            BufferedWriter out = new BufferedWriter(fw);
            out.append(data);
            out.close();
            return true;
        } catch (FileNotFoundException f) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        // AlarmManager alarm = (AlarmManager) this.getSystemService(this.ALARM_SERVICE);
        am.cancel(pIntent);

        stopService(new Intent(this, Alarm.class));

        if (locationManagerNET != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManagerNET.removeUpdates(locationListenerNET);
            locationManagerNET = null;
        }

        if (locationManagerGPS != null) {
            locationManagerGPS.removeUpdates(locationListenerGPS);
            locationManagerGPS = null;
        }

        if (tm != null)
            tm.listen(MyListener, PhoneStateListener.LISTEN_NONE);

    }

}
