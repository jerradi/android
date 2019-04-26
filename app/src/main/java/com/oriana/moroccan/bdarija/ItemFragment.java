package com.oriana.moroccan.bdarija;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.MediaRouteButton;
import android.app.SearchManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.oriana.moroccan.bdarija.helpers.ItemOpenDatabaseHelper;
import com.oriana.moroccan.bdarija.helpers.MyItemRecyclerViewAdapter;
import com.oriana.moroccan.bdarija.model.Item;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ItemFragment extends Fragment {
    public Integer type = -1;
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 2;
    private OnListFragmentInteractionListener mListener;
    private MyItemRecyclerViewAdapter adapter;
    private SearchView searchView;
    private Menu menuu;
    private List<Item> items = new ArrayList<>();
    private View rootView;
    private RecyclerView recyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {
    }

    public void setType(int typ) {
        type = typ;
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ItemFragment newInstance(int columnCount) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        Log.i("Access to menu", "menu_content");
        this.menuu = menu;
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();

        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();

        inflater.inflate(R.menu.menu_content, menu);

        SearchManager searchManager = (SearchManager) getActivity()
                .getSystemService(Context.SEARCH_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            searchView = (SearchView) menu.findItem(R.id.action_search)
                    .getActionView();


            searchView.setSearchableInfo(searchManager
                    .getSearchableInfo(getActivity().getComponentName()));
            searchView.setIconifiedByDefault(false);

            try {
                SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextChange(String newText) {

                        adapter.getFilter().filter(  newText  );
                        Log.e("JAM", "on text chnge text: " + newText);
                        return true;
                    }

                    @Override
                    public boolean onQueryTextSubmit(String newText) {
                        // this is your adapter that will be filtered

                        Log.e("JAM", "on query submit: " + newText);
                        return true;
                    }
                };
                searchView.setOnQueryTextListener(textChangeListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_item_list, container, false);
        View view = rootView.findViewById(R.id.list);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            adapter = (MyItemRecyclerViewAdapter) recyclerView.getAdapter();


            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            adapter = new MyItemRecyclerViewAdapter(items, mListener);
            recyclerView.setAdapter(adapter);
            refreshData();
        }
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.i("Scrool", newState + "");
               final  TextView tvCounter =((TextView) rootView.findViewById(R.id.tvCounter));
                if (newState <= RecyclerView.SCROLL_STATE_IDLE) {
                    tvCounter.setVisibility(View.VISIBLE);
                    tvCounter.animate()

                            .translationY(0)
                            .alpha(1.0f)
                            .setDuration(500).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            tvCounter.setAlpha(1.0f);
                        }
                    });
                    ;
                } else tvCounter.animate()
                        .translationY(0 - tvCounter.getHeight())
                        .alpha(0.0f)
                        .setDuration(100)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                tvCounter.setVisibility(View.GONE);
                            }
                        });
            }
        });
        return rootView;
    }


    private ItemOpenDatabaseHelper databaseHelper = null;

    public ItemOpenDatabaseHelper getHelper(View view) {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(view.getContext(), ItemOpenDatabaseHelper.class);
        }
        return databaseHelper;
    }

    private List<Item> getItems(View view) {
        List<Item> arrayOfUsers = new ArrayList<Item>();

        QueryBuilder<Item, Long> queryBuilder;
        try {
            queryBuilder = getHelper(view).getDao().queryBuilder();

            if (type >= 0) queryBuilder.where().eq("type", type);
            else {
                if (type == Item.LIKES) queryBuilder.where().eq("like", true);
            }
            queryBuilder.orderBy("seen",true).orderBy("dbId", false);
            arrayOfUsers = getHelper(view).getDao().query(queryBuilder.prepare());

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return arrayOfUsers;
    }

    private void print(List<Item> arrayOfUsers) {
        for (Item item : arrayOfUsers
                ) {
            Log.i("Print Items ", "" + item.getDescription());
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public String getNameOfType() {
        String nameOfType = getString(R.string.all);

        switch (type) {
            case Item.EXPERIENCES:
                nameOfType = getResources().getString(R.string.experiences);
                break;
            case Item.HISTORIES:
                nameOfType = getResources().getString(R.string.histories);
                break;
            case Item.JOKES:
                nameOfType = getResources().getString(R.string.jokes);
                break;
            case Item.PROBLEMS:
                nameOfType = getResources().getString(R.string.problems);
                break;
            case Item.LIKES:
                nameOfType = getResources().getString(R.string.favs);
                break;
            default:
                break;
        }

        return nameOfType;
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.i("Frag restarted", "haha");


        refreshData();
    }

    private String getStats(){
        QueryBuilder<Item, Long> queryBuilder;
        String seen="";
        try {
            queryBuilder = getHelper(null).getDao().queryBuilder();
            seen=getString(  R.string.stats  , ""+(queryBuilder.countOf()), ""+(queryBuilder.where().eq("seen", true).countOf()) );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  seen;
    }

    public void refreshData() {

        adapter.setType(type);
        if (type == Item.ALL) ((GridLayoutManager) recyclerView.getLayoutManager()).setSpanCount(1);
        else ((GridLayoutManager) recyclerView.getLayoutManager()).setSpanCount(2);
        items.clear();
        items.addAll(getItems(rootView));
        adapter.notifyDataSetChanged();
        Log.i("DATA CHANGED", "Size" + items.size() + " TYPE " + type);
        ((TextView) rootView.findViewById(R.id.tvCounter)).setText(getStats());
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Item item, int type);
    }
}
