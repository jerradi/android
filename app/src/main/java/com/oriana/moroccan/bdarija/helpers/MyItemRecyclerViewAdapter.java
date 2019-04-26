package com.oriana.moroccan.bdarija.helpers;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.oriana.moroccan.bdarija.ItemFragment.OnListFragmentInteractionListener;

import com.oriana.moroccan.bdarija.R;
import com.oriana.moroccan.bdarija.model.Item;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> implements Filterable {

    private int type = -1;
    private List<Item> mValues;
    private List<Item> allValues;
    private final OnListFragmentInteractionListener mListener;
    private ValueFilter valueFilter;
    private View view;

    public MyItemRecyclerViewAdapter(List<Item> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        allValues = items;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getId() + "");
        holder.mContentView.setText(mValues.get(position).getDescription());
        holder.mContentView.setAlpha(holder.mItem.isSeen()? 0.4f:1f);
        holder.tvBeen.setText(Utils.sdf.format(mValues.get(position).getDateCreated()));
        holder.tvMore.setVisibility(moreTextToSee(holder) ? View.VISIBLE : View.GONE);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem, type);
                }
            }
        });
    }

    private boolean moreTextToSee(ViewHolder holder) {
        holder.mContentView.getPaint().measureText(holder.mItem.getDescription());
        TextView a = (TextView) view.findViewById(R.id.content);
        TextPaint paint = a.getPaint();
        Rect rect = new Rect();
        String text = String.valueOf(a.getText());
        paint.getTextBounds(text, 0, text.length(), rect);
        Log.i("id:" + holder.mItem.getId() + " text nbLines:" + a.getLineCount() + " char count :" + holder.mItem.getDescription().length() + " a getHeight :" + a.getHeight() + "  rect.height :" + rect.height(), (holder.mItem.getDescription().length() > 120 || ((a.getHeight() + a.getWidth() > 0) && (a.getLineCount() > 6 || rect.height() > a.getHeight()))) + "");

        return holder.mItem.getDescription().length() > 120 || ((a.getHeight() + a.getWidth() > 0) && (a.getLineCount() > 6 || rect.height() > a.getHeight()));

    }


    public int getItemCount() {
        if (mValues == null) return 0;
        return mValues.size();
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {

            valueFilter = new ValueFilter();
        }

        return valueFilter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final TextView tvMore;
        public final TextView tvBeen;
        public Item mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            tvBeen = (TextView) view.findViewById(R.id.tv_been);
            tvMore = (TextView) view.findViewById(R.id.tvMore);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
 private class ValueFilter extends Filter {

        // Invoked in a worker thread to filter the data according to the
        // constraint.
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                List<Item> filterList = new ArrayList<Item>();
                try {

                    QueryBuilder qb = getHelper(view).getDao().queryBuilder();
                   qb.where().like("description", "%" + constraint + "%").query();
                    if (type >= 0)
                        qb.where().like("description", "%" + constraint + "%").and().eq("type", type);
                    if (type == Item.LIKES)
                        qb.where().like("description", "%" + constraint + "%").and().eq("like", true);
                    filterList = qb.query();
                    Log.i("filterList size ", filterList.size() + " : " + type);
                    results.count = filterList.size();
                    results.values = filterList;
                } catch (SQLException e) {
                    e.printStackTrace();

                }
 } else {
                results.count = allValues.size();
                results.values = allValues;
            }
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            mValues = (ArrayList<Item>) results.values;
            notifyDataSetChanged();
        }
    }
    private ItemOpenDatabaseHelper databaseHelper = null;
    public ItemOpenDatabaseHelper getHelper(View view) {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(view.getContext(), ItemOpenDatabaseHelper.class);
        }
        return databaseHelper;
    }
}