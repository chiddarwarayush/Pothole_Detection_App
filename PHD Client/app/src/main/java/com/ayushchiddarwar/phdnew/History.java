package com.ayushchiddarwar.phdnew;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class History extends AppCompatActivity {
WebView webView;
String userid;

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String userIDs = "userIDs";
    SharedPreferences sharedpreferences;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        Home.CheckConnection ch = new Home.CheckConnection();
        Boolean status = ch.checkInternetConnection(getApplicationContext());

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        userid=sharedpreferences.getString(userIDs,"");

        if(userid.length()<=0 || userid.isEmpty()){
            Toast.makeText(getApplicationContext(),"Please Login First!",Toast.LENGTH_SHORT).show();

            Intent i=new Intent(getApplicationContext(),Login.class);
            startActivity(i);
            finish();
        }else{
            if(status){

                webView=findViewById(R.id.webView);
                WebSettings webSettings = webView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                webView.setWebViewClient(new MyWebViewClient());
                webView.loadUrl("http://www.potholedetection.tech/PHD/get_history.php?userid="+userid+"");

            }else{

                Toast.makeText(getApplicationContext(),"No Internet Connection!",Toast.LENGTH_SHORT).show();

            }
        }


    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if ("http://symphonixitservices.in/PHD/".equals(Uri.parse(url).getHost())) {
                // This is my website, so do not override; let my WebView load the page
                return false;
            }
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
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
