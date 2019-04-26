package com.oriana.moroccan.bdarija;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.oriana.moroccan.bdarija.helpers.ItemOpenDatabaseHelper;
import com.oriana.moroccan.bdarija.helpers.Utils;
import com.oriana.moroccan.bdarija.model.Item;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class ContentActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private  Item currentInstance;
    private Item mainItem;
    private int type = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_content);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mainItem = (Item) getIntent().getSerializableExtra("item");
        currentInstance = mainItem;
        type = getIntent().getIntExtra("type", Item.ALL);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        currentInstance.setSeen(true);
        try {
            getHelper(null).getDao().update(currentInstance);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // Here's your instance

                 currentInstance =((PlaceholderFragment)mSectionsPagerAdapter.getRegisteredFragment(position)).item;
                currentInstance.setSeen(true);
                try {
                    getHelper(null).getDao().update(currentInstance);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        FloatingActionButton fabLike = (FloatingActionButton) findViewById(R.id.fab);
        fabLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(currentInstance.toString()+"NOOO ERROR UPDATE", "============================");
                currentInstance.setLike(!currentInstance.isLike());
                if(type==Item.LIKES) {mSectionsPagerAdapter.notifyDataSetChanged();}
                try {
                    getHelper(null).getDao().update(currentInstance);
                 /*   UpdateBuilder<Item, Long> updateBuilder =
                            getHelper(null).getDao().updateBuilder();
                    updateBuilder.updateColumnValue("like", !currentInstance.isLike());*/
// only update the rows where password is null
             /*       updateBuilder.where().eq("id",currentInstance.getId() );
                  updateBuilder.update(); */



                    Snackbar.make(view, currentInstance.isLike()?  "إعجاب تمت اضافته الى المفضلات \uD83D\uDE42": "تم الحذف من قائمة المفضلات", Snackbar.LENGTH_SHORT )
                            .setAction("Action", null).show();
                    if(type==Item.LIKES) {mSectionsPagerAdapter.notifyDataSetChanged();}

                } catch (SQLException e) {
                    e.printStackTrace();Log.e("ERROR UPDATE", e.getMessage());
                }


            }
        });
        FloatingActionButton fabShare = (FloatingActionButton) findViewById(R.id.fab2);
        fabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(
                        android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, currentInstance.getDescription()

                        + " \n\nحمل التطبيق للمزيد :https://goo.gl/YKb9LJ");
                 startActivity(sharingIntent);


            }
        });



        AdView adView = (AdView) findViewById(R.id.adViewContent);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("291361B6853EA3C74FB32BE3AB84F735")
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);
        showRating(60000);
    }

    private void showRating(final int in) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            private void ddismiss(AlertDialog alertDialog) {
                alertDialog.dismiss();
            }
            AlertDialog alertDialog;

            @Override
            public void run() {

                try {
                    if(active) {
                        SharedPreferences prefs = getSharedPreferences("voted",
                                MODE_PRIVATE);
                        boolean voted = prefs.getBoolean("voted", false);
                        final SharedPreferences.Editor editor = prefs.edit();

                        if (!voted) {
                            AlertDialog.Builder builder;


                            LayoutInflater inflater = (LayoutInflater)
                                    ContentActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
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

                            builder = new AlertDialog.Builder(new ContextThemeWrapper(ContentActivity.this, R.style.myDialog));

                            builder.setView(layout);
                            alertDialog = builder.create();
                            alertDialog.show();
                            Log.i("edit firstRun to ", "" + voted);  //Do something after 100ms
                            showRating(  60000);

                        }
                    }
            }catch (Exception e) {}
            }
        }, in);
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
    static boolean active = false;

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private Item item;


        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, Item item) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.item = item;
            fragment.setArguments(args);

            return fragment;
        }



        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_content, container, false);


            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            TextView tvID = (TextView) rootView.findViewById(R.id.tvId);
            tvID.setText(item.getId() + "");
            TextView tvDate = (TextView) rootView.findViewById(R.id.tvDate);
            tvDate.setText(Utils.sdf.format(item.getDateCreated()));

            textView.setText(item.getDescription());



            return rootView;
        }



    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1, getItem(null, position + 1));
        }

        private int count = -1;

        @Override
        public int getCount() {


            try {
                if (type == Item.ALL) {
                    count = (int) getHelper(null).getDao().countOf();

                } else {
                    QueryBuilder<Item, Long> queryBuilder;
                    queryBuilder = getHelper(null).getDao().queryBuilder();
                    queryBuilder.setCountOf(true);
                    if (type == Item.LIKES) {

                        queryBuilder.where().ge("id", mainItem.getId()).and().eq("like", true);
                        count = (int) getHelper(null).getDao().countOf(queryBuilder.prepare());
                        return count;
                    } else {


                        queryBuilder.where().ge("id", mainItem.getId()).and().eq("type", type);
                        count = (int) getHelper(null).getDao().countOf(queryBuilder.prepare());

                        return count;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }


            return Integer.MAX_VALUE;
        }
        SparseArray<Fragment> registeredFragments = new SparseArray<>();
        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }




        private Item getItem(View view, int position) {
            List<Item> arrayOfUsers = new ArrayList<Item>();

            QueryBuilder<Item, Long> queryBuilder;
            try {
                queryBuilder = getHelper(view).getDao().queryBuilder();
                if (type == Item.ALL) {
                    queryBuilder.where().eq("id", (mainItem.getId() + (position - 2)) % (count) + 1);
                    arrayOfUsers = getHelper(view).getDao().query(queryBuilder.prepare());
                }


                else {
                    if (type == Item.LIKES) {
                        queryBuilder.where().ge("id", mainItem.getId()).and().eq("like",true);
                        arrayOfUsers = getHelper(view).getDao().query(queryBuilder.orderBy("id", true).offset(position - 1).limit(1).prepare());
                        Log.i("count" , count+"");
                        return arrayOfUsers.get(0);
                    } else {
                        queryBuilder.where().ge("id", mainItem.getId()).and().eq("type", type);
                        arrayOfUsers = getHelper(view).getDao().query(queryBuilder.orderBy("id", true).offset(position - 1).limit(1).prepare());
                        return arrayOfUsers.get(0);
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (arrayOfUsers.size() < 1) return getItem(view, position++ % (count + 1));

            return arrayOfUsers.get(0);
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return "Section";
        }
    }
    private ItemOpenDatabaseHelper databaseHelper = null;

    public ItemOpenDatabaseHelper getHelper(View view) {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(ContentActivity.this, ItemOpenDatabaseHelper.class);
        }
        return databaseHelper;
    }
}

