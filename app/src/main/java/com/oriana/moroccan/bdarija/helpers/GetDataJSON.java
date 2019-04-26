package com.oriana.moroccan.bdarija.helpers;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Base64;
import android.util.Log;

import com.j256.ormlite.stmt.QueryBuilder;
import com.nispok.snackbar.Snackbar;
import com.oriana.moroccan.bdarija.ItemFragment;
import com.oriana.moroccan.bdarija.LuncherActivity;
import com.oriana.moroccan.bdarija.R;
import com.oriana.moroccan.bdarija.model.Item;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * Created by dc on 11/28/2017.
 */

public class GetDataJSON extends AsyncTask<String, Void, String> {

    private ItemOpenDatabaseHelper helper;
    private int count;
    private Activity ctx;
    private SwipeRefreshLayout swiperefreshlayout;
    ItemFragment fragment;

    public GetDataJSON(ItemOpenDatabaseHelper helper, Activity ctx, SwipeRefreshLayout swiperefreshlayout, ItemFragment fragment) {
        super();

        this.fragment = fragment;
        this.helper = helper;
        this.swiperefreshlayout = swiperefreshlayout;
        this.ctx = ctx;
    }
    public int getLastInsertId()   {
        try {
            QueryBuilder<Item,Long> qb = helper.getDao().queryBuilder();
            qb.orderBy("dbId", false); // descending sort
            // get the top one result
            qb.limit(1);
            Item result = qb.queryForFirst();
            return result.getDbId();
        }catch (Exception e){
            return  -1;
        }

    }
    @Override
    protected String doInBackground(String... params) {
        DefaultHttpClient httpclient = new DefaultHttpClient(
                new BasicHttpParams());

        HttpGet httppost = new HttpGet(
                ctx.getString(R.string.server) + "?op=get&id="+ getLastInsertId());


        Log.w("url" , ctx.getString(R.string.server) + "?op=get&id="+ getLastInsertId() );
        // Depends on your web service
        httppost.setHeader("Content-type", "application/json");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        InputStream inputStream = null;
        String result = null;
        try {
            HttpResponse
                    response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();

            inputStream = entity.getContent();
            // json is UTF-8 by default
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
                Log.e("sb", line);
            }
            result = sb.toString();
        } catch (Exception e) {
            // Oops
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (Exception squish) {
                squish.printStackTrace();
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {

        try {
            JSONObject jsonObj = new JSONObject(result);
            JSONArray newquotes = jsonObj.getJSONArray("result");
            Log.e("JSON RECEIVED", result);
            count = newquotes.length();
            for (int i = 0; i < count; i++) {
                JSONObject c = newquotes.getJSONObject(i);
                int cat = c.getInt(Item.TAG_DB_CAT);
                Date date = null;

                String text = c.getString(Item.TAG_DB_TEXT);
                int db_id = c.getInt(Item.TAG_DB_ID);

                try {
                    text=  new String(Base64.decode(text, Base64.NO_WRAP),"UTF-8" ) ;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                   text= "";
                }

                Item q = new Item(  text    ,cat );



                q.setDbId(db_id);
                helper.getDao().create(q);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Snackbar.with(ctx)
                // context
                .text(count +" "+ctx.getString(R.string.newquote)) // text to display
                .show(ctx);
        if(swiperefreshlayout!=null) swiperefreshlayout.setRefreshing(false);
        if(fragment!=null) fragment.refreshData();
    }
}