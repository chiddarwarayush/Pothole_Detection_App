package com.ayushchiddarwar.phdnew;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import static android.text.TextUtils.substring;


public class Start extends AppCompatActivity implements SensorEventListener, LocationListener, View.OnClickListener {
    TextView time;
    private float lastX, lastY, lastZ;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private float deltaXMax = 0;
    private float deltaYMax = 0;
    private float deltaZMax = 0;

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;

    private float vibrateThreshold = 0;

    private TextView maxX, maxY, maxZ;
    private EditText currentX, currentY, currentZ;

    public Vibrator v;


String intensity;



    final String TAG = "GPS";
    private final static int ALL_PERMISSIONS_RESULT = 101;


    TextView tvLatitude, tvLongitude, tvTime;
    LocationManager locationManager;
    Location loc;
    ArrayList<String> permissions = new ArrayList<>();
    ArrayList<String> permissionsToRequest;
    ArrayList<String> permissionsRejected = new ArrayList<>();
    boolean isGPS = false;
    boolean isNetwork = false;
    boolean canGetLocation = true;
    String ltt,lont;
    String lttold="111",lontold="111";
String userid;
Boolean status;
    String time1=null,time2=null;
    long tm2,tm1;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String userIDs = "userIDs";
    SharedPreferences sharedpreferences;
    Button stop;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
stop=findViewById(R.id.stop);
stop.setOnClickListener(this);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        userid=sharedpreferences.getString(userIDs,"");
        if(userid.length()<=0 || userid.isEmpty()){
            Toast.makeText(getApplicationContext(),"Please Login First!",Toast.LENGTH_SHORT).show();

            Intent i=new Intent(getApplicationContext(),Login.class);
           startActivity(i);
            finish();
        }

CheckConnection ch = new CheckConnection();
        status = ch.checkInternetConnection(getApplicationContext());

        time=findViewById(R.id.timer);

        initializeViews();
        try {
            ImageView imageView = findViewById(R.id.imageView);
            Glide.with(this).load(R.drawable.source).into(imageView);
        } catch (Exception e) {

        }

        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsToRequest = findUnAskedPermissions(permissions);

        if (!isGPS && !isNetwork) {
            Toast.makeText(getApplicationContext(),"Please Enable GPS.",Toast.LENGTH_SHORT).show();

        } else {
            Log.d(TAG, "Connection on");
            // check permissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permissionsToRequest.size() > 0) {
                    requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                            ALL_PERMISSIONS_RESULT);
                    Log.d(TAG, "Permission requests");
                    canGetLocation = false;
                }
            }

            getLocation();
        }




        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer

            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            vibrateThreshold = accelerometer.getMaximumRange() / 35;
        } else {
            // fai! we dont have an accelerometer!
        }

        //initialize vibration
        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        CountUpTimer timer = new CountUpTimer(30000000) {
            public void onTick(int second) {

                Date d = new Date(second * 1000L);
                SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss"); // HH for 0-23
                df.setTimeZone(TimeZone.getTimeZone("GMT"));
                String hms = df.format(d);

                time.setText(hms);
            }
        };

        timer.start();

    }

    public void initializeViews() {
        currentX = (EditText) findViewById(R.id.currentX);
        currentY = (EditText) findViewById(R.id.currentY);
        currentZ = (EditText) findViewById(R.id.currentZ);

        maxX = (TextView) findViewById(R.id.maxX);
        maxY = (TextView) findViewById(R.id.maxY);
        maxZ = (TextView) findViewById(R.id.maxZ);
    }

    //onResume() register the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d(TAG, "1");
        // clean current values
        displayCleanValues();
        // display the current x,y,z accelerometer values
        displayCurrentValues();
        // display the max x,y,z accelerometer values
        displayMaxValues();

        // get the change of the x,y,z values of the accelerometer
        deltaX = Math.abs(lastX - event.values[0]);
        deltaY = Math.abs(lastY - event.values[1]);
        deltaZ = Math.abs(lastZ - event.values[2]);

        // if the change is below 2, it is just plain noise
        if (deltaX < 2){
            deltaX = 0;
        }

        if (deltaY < 2){
            deltaY = 0;
        }

if ((deltaY > vibrateThreshold)) {
    Log.d(TAG, "2");
    intensity=String.valueOf(deltaY);
    if (!isGPS && !isNetwork) {
        Log.d(TAG, "3");
        Toast.makeText(getApplicationContext(),"Please Enable GPS.",Toast.LENGTH_SHORT).show();

    } else {
        Log.d(TAG, "4");

            Log.d(TAG, "10");
            v.vibrate(50);

            if (loc != null){

                if(time1 != null){
                    tm2= System.currentTimeMillis();

                   time2= String.valueOf(tm2);
                   long vart1=tm1/1000;
                    Log.d(TAG, "time1: ="+vart1);
                   long vart2=tm2/1000;
                    Log.d(TAG, "time2: ="+vart2);
                   long resultdiff=vart2-vart1;
                    Log.d(TAG, "Diffrence: ="+resultdiff);
                   if(resultdiff>4){

                       tm1= System.currentTimeMillis();
                       time1= String.valueOf(tm1);
                       updateUI(loc);
                   }
                }else{
                    tm1= System.currentTimeMillis();
                    time1= String.valueOf(tm1);
                    updateUI(loc);
                }



            }else{
                Log.d(TAG, "Empty Loc values!");
                Toast.makeText(getApplicationContext(),"Please Turn Off GPS!",Toast.LENGTH_SHORT).show();

            }






    }

        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void displayCleanValues() {
        currentX.setText("0.0");
        currentY.setText("0.0");
        currentZ.setText("0.0");
    }

    // display the current x,y,z accelerometer values
    public void displayCurrentValues() {
        currentX.setText(Float.toString(deltaX));
        currentY.setText(Float.toString(deltaY));
        currentZ.setText(Float.toString(deltaZ));
    }

    // display the max x,y,z accelerometer values
    public void displayMaxValues() {
        if (deltaX > deltaXMax) {
            deltaXMax = deltaX;
           // maxX.setText(Float.toString(deltaXMax));
        }
        if (deltaY > deltaYMax) {
            deltaYMax = deltaY;
           // maxY.setText(Float.toString(deltaYMax));
        }
        if (deltaZ > deltaZMax) {
            deltaZMax = deltaZ;
           // maxZ.setText(Float.toString(deltaZMax));
        }
    }

    @Override
    public void onClick(View view) {
        if(view==stop) {
            Intent i = new Intent(getApplicationContext(), Home.class);
            startActivity(i);
            finish();
        }
    }

    public abstract class CountUpTimer extends CountDownTimer {
        private static final long INTERVAL_MS = 1000;
        private final long duration;

        protected CountUpTimer(long durationMs) {
            super(durationMs, INTERVAL_MS);
            this.duration = durationMs;
        }

        public abstract void onTick(int second);

        @Override
        public void onTick(long msUntilFinished) {
            int second = (int) ((duration - msUntilFinished) / 1000);
            onTick(second);
        }

        @Override
        public void onFinish() {
            onTick(duration / 1000);
        }
    }



    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");
       // updateUI(location);
    }


    public void onStatusChanged(String s, int i, Bundle bundle) {}


    public void onProviderEnabled(String s) {
        getLocation();
    }


    public void onProviderDisabled(String s) {
        if (locationManager != null) {
            locationManager.removeUpdates((LocationListener) this);
        }
    }

    public void getLocation() {
        try {
            if (canGetLocation) {
                Log.d(TAG, "Can get location");
                if (isGPS) {
                    // from GPS
                    Log.d(TAG, "GPS on");
locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,this);

                    if (locationManager != null) {
                        while (true) {
                            loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (loc == null){
                                continue;

                            }
                            else {
                                Log.d(TAG, "FOUND GPS LOC");
                                break;
                            }
                        }


                    }else{
Toast.makeText(getApplicationContext(),"Empty LM GPS",Toast.LENGTH_SHORT).show();

                    }
                } else if (isNetwork) {
                    // from Network Provider
                    Log.d(TAG, "NETWORK_PROVIDER on");
locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000,0,this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        Log.d(TAG, "FOUND NETWORK LOC");
                    }else{
Toast.makeText(getApplicationContext(),"Empty LM NETWORK",Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Log.d(TAG, "no loc");
                  //  loc.setLatitude(0);
                  //  loc.setLongitude(0);
                 //   updateUI(loc);
                }
            } else {
                Log.d(TAG, "Can't get location");
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void getLastLocation() {
        try {
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, false);
            Location location = locationManager.getLastKnownLocation(provider);
            Log.d(TAG, provider);
            Log.d(TAG, location == null ? "NO LastLocation" : location.toString());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canAskPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canAskPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                Log.d(TAG, "onRequestPermissionsResult");
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
Toast.makeText(getApplicationContext(),"These permissions are mandatory for the application. Please allow access.",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                } else {
                    Log.d(TAG, "No rejected permissions.");
                    canGetLocation = true;
                    getLocation();
                }
                break;
        }
    }


    /*private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(SecondActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }*/

    private void updateUI(Location loc) {
        Log.d(TAG, "updateUI");
        ltt=Double.toString(loc.getLatitude());
        lont=Double.toString(loc.getLongitude());
        if(status){
            if(intensity.length()>=0 || !intensity.isEmpty()){
                Log.d(TAG, "calling background task");
                new BackgroundTaskProcess().execute();
            }else{
                Log.d(TAG, "Intensity length<=0 || empty");
            }

        }else{
            Toast.makeText(getApplicationContext(),"No Internet Connection!",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates((LocationListener) this);
        }
    }


    public static class CheckConnection {
        public boolean checkInternetConnection(Context context) {
            ConnectivityManager con_manager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            if (con_manager.getActiveNetworkInfo() != null
                    && con_manager.getActiveNetworkInfo().isAvailable()
                    && con_manager.getActiveNetworkInfo().isConnected()) {

                return true;
            } else {

                return false;
            }
        }
    }


    public class BackgroundTaskProcess extends AsyncTask<String,Void,String> {
        public String data = "";
        public String dataParsed = "";
        public String datauser_id = "";
        public String singleParsed = "";
        public String statuscode = "NONE";

        private ProgressDialog Dialog = new ProgressDialog(Start.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Dialog.setMessage("Please Wait..");
            Dialog.setCanceledOnTouchOutside(false);
            Dialog.show();
        }

        @Override
        protected String doInBackground(String... voids) {

            try {
                ltt= URLEncoder.encode(ltt, "UTF-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

            try {
                lont= URLEncoder.encode(lont, "UTF-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

            try {
                intensity= URLEncoder.encode(intensity, "UTF-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

            try {
                URL url = new URL("http://www.potholedetection.tech/PHD/process_phdata.php?user_id="+userid+"&latitude="+ltt+"&longitude="+lont+"&intensity="+intensity+"");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                StringBuilder sb = new StringBuilder();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String line = "";
                while((line = bufferedReader.readLine()) != null){
                    sb.append(line+ "\n");
                }
                data = sb.toString().trim();
                bufferedReader.close();

            } catch (java.io.IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            try {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONArray jsonArray = null;
                jsonArray = jsonObject.getJSONArray("server_response");

                for (int i = 0; i< jsonArray.length(); i++){
                    JSONObject JO = null;
                    JO = jsonArray.getJSONObject(i);
                    statuscode = JO.getString("status");
                    dataParsed = JO.getString("msg");
                }


            } catch (JSONException e) {
                Dialog.dismiss();
                Toast.makeText(getApplicationContext(),"Something Went Wrong, Please Try Again!",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            Dialog.dismiss();
            if(statuscode.equalsIgnoreCase("success")){

                Toast.makeText(getApplicationContext(),"Data Uploaded Successfully!",Toast.LENGTH_SHORT).show();

            }else if(statuscode.equalsIgnoreCase("error")){
                Toast.makeText(getApplicationContext(),dataParsed,Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }

    }

}
