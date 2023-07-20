package com.ayushchiddarwar.phdnew;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
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

public class Register extends AppCompatActivity implements View.OnClickListener {
    Button reg,login;
    EditText name,mobile,username,pwd;
    String un,var_name,var_mobile,var_pwd;
    boolean status;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        CheckConnection ch = new CheckConnection();
        status = ch.checkInternetConnection(getApplicationContext());

        reg=findViewById(R.id.process_register);
        login=findViewById(R.id.nav_login);
        name=findViewById(R.id.fullname);
        mobile=findViewById(R.id.mobilenumber);
        username=findViewById(R.id.username);
        pwd=findViewById(R.id.password);

        login.setOnClickListener(this);
        reg.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == login){
            if(status){
                Intent i=new Intent(Register.this,Login.class);
                startActivity(i);
            }else{
Toast.makeText(getApplicationContext(),"No Internet Connection!",Toast.LENGTH_LONG).show();

            }

        }else if(view == reg){

            if(status){

             un=username.getText().toString();
             var_name=name.getText().toString();
             var_mobile=mobile.getText().toString();
             var_pwd=pwd.getText().toString();
            if(var_name.length()<=0 || var_name.isEmpty()){
                Toast.makeText(getApplicationContext(),"Full Name Should Not Be Empty!",Toast.LENGTH_SHORT).show();
            }else if(var_mobile.length()<=0 || var_mobile.isEmpty()){
                Toast.makeText(getApplicationContext(),"Mobile Number Should Not Be Empty!",Toast.LENGTH_SHORT).show();
            }else if(var_mobile.length()<10 || var_mobile.length()>10){
                Toast.makeText(getApplicationContext(),"Invalid Mobile Number!",Toast.LENGTH_SHORT).show();
            }else if(un.length()<=0 || un.isEmpty()){
                Toast.makeText(getApplicationContext(),"Username Should Not Be Empty!",Toast.LENGTH_SHORT).show();
            }else if(var_pwd.length()<=0 || var_pwd.isEmpty()){
                Toast.makeText(getApplicationContext(),"Password Should Not Be Empty!",Toast.LENGTH_LONG).show();
            }else{

                new BackgroundTaskProcess().execute();

            }

            }else{
                Toast.makeText(getApplicationContext(),"No Internet Connection!",Toast.LENGTH_LONG).show();

            }



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
        public String singleParsed = "";
        public String statuscode = "NONE";

        private ProgressDialog Dialog = new ProgressDialog(Register.this);

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
                var_name= URLEncoder.encode(var_name, "UTF-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            try {
                var_mobile= URLEncoder.encode(var_mobile, "UTF-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

            try {
                var_pwd= URLEncoder.encode(var_pwd, "UTF-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

            try {
                URL url = new URL("http://www.potholedetection.tech/PHD/process_register.php?fullname="+var_name+"&mobilenum="+var_mobile+"&username="+un+"&password="+var_pwd+"");
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
                Toast.makeText(Register.this,"Something Went Wrong, Please Try Again!",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            Dialog.dismiss();
            if(statuscode.equalsIgnoreCase("success")){

                Toast.makeText(Register.this,"User Registered Successfully!",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Register.this, Login.class);
                startActivity(i);
                finish();

            }else if(statuscode.equalsIgnoreCase("error")){
                Toast.makeText(Register.this,dataParsed,Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }

    }




}