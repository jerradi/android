package com.oriana.moroccan.bdarija;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.oriana.moroccan.bdarija.helpers.GetDataJSON;
import com.oriana.moroccan.bdarija.helpers.ItemOpenDatabaseHelper;
import com.oriana.moroccan.bdarija.model.Item;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class LuncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_luncher);
        final Intent in = new Intent(this, MainActivity.class);
        SharedPreferences prefs = getSharedPreferences("darija",
                MODE_PRIVATE);
        boolean firstRun = prefs.getBoolean("firstRun", true);

        Log.i("firstRun", "" + firstRun);
        if (firstRun  ) {
            try {
               /* *//**//*new GetDataJSON(getHelper(this) , LuncherActivity.this, null,null).execute();
*/
                pupulateDB();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("firstRun", false);
                editor.commit();
                Log.i("edit firstRun to ", "" + false);

                View tv = findViewById(R.id.tvvv);
                Animation inout = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
                tv.setVisibility(View.VISIBLE);
                tv.setAnimation(inout);

                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(in);
                        LuncherActivity.this.finish();
                    }
                }, 6000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

            startActivity(in);
            LuncherActivity.this.finish();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }
    private void pupulateDB() {


        AssetManager assetManager = this.getAssets();
        try {
            InputStream inputStream = assetManager.open("jokes.csv");
              inputStream = assetManager.open("out.png");
            InputStreamReader streamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(streamReader);
            String line;
            String[] values;
            while ((line = bufferedReader.readLine()) != null) {
                try {
                    line=  new String(Base64.decode(line, Base64.NO_WRAP),"UTF-8" ) ;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    line= "";
                }



                Item item = new Item(line.replace("]]", "\n") , Item.JOKES);
                getHelper(this).getDao().create(item);
            }

        } catch (Exception e) {
            Log.e("TBCAE", "Failed to open data input file");
            e.printStackTrace();
        }
    }


    private ItemOpenDatabaseHelper databaseHelper = null;

    public ItemOpenDatabaseHelper getHelper(Context context) {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(context, ItemOpenDatabaseHelper.class);
        }
        return databaseHelper;
    }





}
