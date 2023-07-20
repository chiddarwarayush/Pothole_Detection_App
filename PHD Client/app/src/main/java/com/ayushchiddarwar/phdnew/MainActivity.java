package com.ayushchiddarwar.phdnew;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button login,register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login=findViewById(R.id.login);
        register=findViewById(R.id.register);
        login.setOnClickListener(this);
        register.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if(view == login){
            Intent i = new Intent(MainActivity.this, Login.class);
            startActivity(i);
        }else if(view == register){
            Intent i = new Intent(MainActivity.this, Register.class);
            startActivity(i);
        }
    }
}
