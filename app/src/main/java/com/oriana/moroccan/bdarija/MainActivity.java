package com.oriana.moroccan.bdarija;




import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.oriana.moroccan.bdarija.helpers.GetDataJSON;
import com.oriana.moroccan.bdarija.helpers.ItemOpenDatabaseHelper;
import com.oriana.moroccan.bdarija.model.Item;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ItemFragment.OnListFragmentInteractionListener {
    private Toolbar toolbar;
    private TextView tvSlidJoke;
    ItemFragment fragment;
    Handler handler1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, NewItemActivity.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                toggle.onDrawerSlide(drawerView, slideOffset);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                toggle.onDrawerOpened(drawerView);
                handler1 = new Handler();
                showMsg(0);

            }

            private void showMsg(final int n) {
             /*   if (handler1 == null) return;
                final int nm = n;
                final Animation fadein = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.bounce);
                final TextView tv = ((TextView) findViewById(R.id.tvSlidJoke));
                final Item i = getNextItem(tvSlidJoke, nm);
                if (i != null) {
                    handler1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tv.setVisibility(View.GONE);

                            tv.setText(" " + i.getDescription().replace("\n", " ").replace("\r", " "));
                            tv.setAnimation(fadein);
                            tv.setVisibility(View.VISIBLE);
          *//*  tv.animate();*//*
                            showMsg(nm + 1);

                        }
                    }, 3000);
                } else

                {
                    showMsg(0);
                }*/
            }

            @Override
            public void onDrawerClosed(View onDrawerClosed) {
                toggle.onDrawerClosed(onDrawerClosed);
                handler1 = null;
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                toggle.onDrawerStateChanged(newState);
            }
        });

        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        AdView adView = (AdView) findViewById(R.id.adViewMain);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("291361B6853EA3C74FB32BE3AB84F735")
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);

        final SwipeRefreshLayout swiperefreshlayout = ((SwipeRefreshLayout) findViewById(R.id.swiperefresh));

        swiperefreshlayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {

                    @Override
                    public void onRefresh() {
                       loadData(swiperefreshlayout);
                    }
                }

        );
        if(swiperefreshlayout!=null) swiperefreshlayout.setRefreshing(true);
        loadData(swiperefreshlayout);
    }

    private void loadData(SwipeRefreshLayout swiperefreshlayout) {

        new GetDataJSON(getHelper(toolbar), MainActivity.this, swiperefreshlayout, (ItemFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment)).execute();

    }

    private void showRating() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            private void ddismiss(AlertDialog alertDialog) {
                alertDialog.dismiss();
            }
            AlertDialog alertDialog;

            @Override
            public void run() {
                try {
                    SharedPreferences prefs = getSharedPreferences("voted",
                            MODE_PRIVATE);
                    boolean voted = prefs.getBoolean("voted", false);
                    final SharedPreferences.Editor editor = prefs.edit();

                    if (!voted) {

                        AlertDialog.Builder builder;


                        LayoutInflater inflater = (LayoutInflater)
                                MainActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
                        View layout = inflater.inflate(R.layout.dialog,
                                (ViewGroup) findViewById(R.id.hall));

                        Button btContact = (Button) layout.findViewById(R.id.btContact);
                        Button btShare = (Button) layout.findViewById(R.id.btShare);

                        btShare.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                shareApp();
                            }
                        });
                        btContact.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                contactUs();
                            }
                        });


                        RatingBar text = (RatingBar) layout.findViewById(R.id.ratingBar);
                        text.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                            @Override
                            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                if (v > 3) {

                                    try {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                    } catch (android.content.ActivityNotFoundException anfe) {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                                    }
                                    editor.putBoolean("voted", true);
                                    editor.commit();
                                }
                                ddismiss(alertDialog);
                            }


                        });

                        builder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.myDialog));

                        builder.setView(layout);
                        alertDialog = builder.create();
                        alertDialog.show();
                        Log.i("edit firstRun to ", "" + voted);  //Do something after 100ms

                        showRating();
                    }
                }catch (Exception e) {}
            }
        }, 60000);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.i("Menu item selected", item.getTitle() + "");
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_share) {
       return  shareApp();
        }
        if (id == R.id.nav_send) {

         return  contactUs();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragment = (ItemFragment) fragmentManager.findFragmentById(R.id.main_fragment);

        fragment.setType(Item.ALL);
        if (id == R.id.nav_camera) {

            fragment.setType(Item.HISTORIES);

        } else if (id == R.id.nav_gallery) {

            fragment.setType(Item.EXPERIENCES);

        } else if (id == R.id.nav_slideshow) {

            fragment.setType(Item.JOKES);

        } else if (id == R.id.nav_manage) {

            fragment.setType(Item.PROBLEMS);


        } else if (id == R.id.favs) {

            fragment.setType(Item.LIKES);

        }

        fragment.refreshData();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        try {
            toolbar.setTitle(fragment.getNameOfType());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    private boolean contactUs() {
        String uriText =
                "mailto:" + getString(R.string.our_mail) +
                        "?subject=" + Uri.encode(getString(R.string.sender_title) + " " + getString(R.string.app_name)) +
                        "&body=" + Uri.encode("some text here");
        Uri uri = Uri.parse(uriText);
        Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
        sendIntent.setData(uri);
        startActivity(Intent.createChooser(sendIntent, getString(R.string.contact)));


        return false;
    }

    private boolean shareApp() {
        Intent sharingIntent = new Intent(
                android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.share_string));
        startActivity(sharingIntent);
        return false;
    }

    @Override
    public void onListFragmentInteraction(Item item, int type) {
        Intent i = new Intent(this, ContentActivity.class);
        i.putExtra("item", item);
        i.putExtra("type", type);
        startActivity(i);
    }


    private ItemOpenDatabaseHelper databaseHelper = null;

    public ItemOpenDatabaseHelper getHelper(View view) {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(getApplicationContext(), ItemOpenDatabaseHelper.class);
        }
        return databaseHelper;
    }

    private Item getNextItem(View view, int n) {
        List<Item> arrayOfUsers = new ArrayList<>();
        QueryBuilder<Item, Long> queryBuilder;
        try {
            queryBuilder = getHelper(view).getDao().queryBuilder().limit(1).offset(n);
            queryBuilder.where().eq("type", Item.JOKES);

            arrayOfUsers = getHelper(view).getDao().query(queryBuilder.prepare());

        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (arrayOfUsers.size() > 0)
            return arrayOfUsers.get(0);
        return null;
    }
}