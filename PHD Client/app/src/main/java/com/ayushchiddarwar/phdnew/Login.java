package com.ayushchiddarwar.phdnew;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Login extends AppCompatActivity implements View.OnClickListener {
    Button login,reg;
    EditText username,pwd;
    String un,chkpwd;
    GlobleClass gl;
    CheckConnection ch;
    boolean istatus;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String userIDs = "userIDs";
    SharedPreferences sharedpreferences;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        login=findViewById(R.id.process_login);
        reg=findViewById(R.id.nav_reg);

        username=findViewById(R.id.username);
        pwd=findViewById(R.id.password);

        login.setOnClickListener(this);
        reg.setOnClickListener(this);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

       String userid=sharedpreferences.getString(userIDs,"");
        if(userid.length()<=0 || userid.isEmpty()){

        }else{
            Intent i=new Intent(getApplicationContext(),Home.class);
            startActivity(i);
            finish();
        }


        ch=new CheckConnection();
        istatus=ch.checkInternetConnection(getApplicationContext());
    }

    @Override
    public void onClick(View view) {
        if(view == login){

            if(istatus){
                un=username.getText().toString();
                chkpwd=pwd.getText().toString();
                if(un.length()<=0 || un.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Username Should Not Be Empty!",Toast.LENGTH_SHORT).show();
                }else if(chkpwd.length()<=0 || chkpwd.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Password Should Not Be Empty!",Toast.LENGTH_SHORT).show();
                }else{

                    new BackgroundTaskProcess().execute();

                }
            }else{
                Toast.makeText(getApplicationContext(),"No Internet Connection!",Toast.LENGTH_SHORT).show();

            }


        }else if(view == reg){
            Intent i = new Intent(Login.this,Register.class);
            startActivity(i);
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

        private ProgressDialog Dialog = new ProgressDialog(Login.this);

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
                un= URLEncoder.encode(un, "UTF-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

            try {
                chkpwd= URLEncoder.encode(chkpwd, "UTF-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

            try {
                URL url = new URL("http://www.potholedetection.tech/PHD/process_login.php?username="+un+"&password="+chkpwd+"");
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
                    datauser_id = JO.getString("user_id");
                }


            } catch (JSONException e) {
                Dialog.dismiss();
                Toast.makeText(getApplicationContext(),"Something Went Wrong, Please Try Again!",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            Dialog.dismiss();
            if(statuscode.equalsIgnoreCase("success")){

                SharedPreferences.Editor editor = sharedpreferences.edit();

                editor.putString(userIDs, datauser_id);

                editor.commit();

                Toast.makeText(getApplicationContext(),"User Logged In Successfully!",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), Home.class);
                startActivity(i);
                finish();

            }else if(statuscode.equalsIgnoreCase("error")){
                Toast.makeText(getApplicationContext(),dataParsed,Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }

    }
}
