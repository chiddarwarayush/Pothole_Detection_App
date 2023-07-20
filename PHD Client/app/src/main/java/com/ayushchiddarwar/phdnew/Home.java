package com.ayushchiddarwar.phdnew;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Home extends AppCompatActivity implements View.OnClickListener {
    Button start, history, about,logout;
    String userid;
    Boolean status;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String userIDs = "userIDs";
    SharedPreferences sharedpreferences;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                setContentView(R.layout.home);
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

       start=findViewById(R.id.start);
       history=findViewById(R.id.history);
       about=findViewById(R.id.about);
       logout=findViewById(R.id.logout);

       start.setOnClickListener(this);
       history.setOnClickListener(this);
       about.setOnClickListener(this);
       logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view==start){
            if(status){
                Intent i = new Intent(Home.this,Start.class);
                startActivity(i);
            }else{
                Toast.makeText(getApplicationContext(),"No Internet Connection!",Toast.LENGTH_SHORT).show();

            }

        }else if(view==history){
            if(status){
                Intent i = new Intent(Home.this,History.class);
                startActivity(i);
            }else{
                Toast.makeText(getApplicationContext(),"No Internet Connection!",Toast.LENGTH_SHORT).show();

            }

        }else if(view==about){
            Intent i = new Intent(Home.this,About.class);
            startActivity(i);
        }else if(view==logout){

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(userIDs, null);
            editor.commit();

            Intent i = new Intent(Home.this,MainActivity.class);
            startActivity(i);
            finish();
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
}
