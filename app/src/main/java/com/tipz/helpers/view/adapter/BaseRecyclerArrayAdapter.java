package com.tipz.helpers.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * This boilerplate code helps manage the RecyclerView adapter code
 * It is designed to work when you:
 * <ul>
 * <li>Using ArrayList to manage your data</li>
 * <li>Have a single type of data in the adapter</li>
 * </ul>
 *
 * @param <VIEW_HOLDER> - The ViewHolder Type that references the views in a single item
 * @param <DATA>        - The Data Type that will be saved in the ArrayList
 */
public abstract class BaseRecyclerArrayAdapter<VIEW_HOLDER extends BaseRecyclerViewHolder, DATA>
        extends RecyclerView.Adapter<VIEW_HOLDER> {

    protected final ArrayList<DATA> mData;

    /**
     * Construct an adapter with data in it
     *
     * @param data the data for the adapter to display
     */
    public BaseRecyclerArrayAdapter(ArrayList<DATA> data) {
        // Note this will be used internally by the adapter.
        // This is passed by reference, and by that is subject to changes from
        // outside the adapter.
        mData = data;
    }

    @Override
    public VIEW_HOLDER onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(getSingleItemLayoutRes(), null);
        return (VIEW_HOLDER) BaseRecyclerViewHolder.create(getViewHolderClass(), v);
    }

    @Override
    public void onBindViewHolder(VIEW_HOLDER viewHolder, int position) {
        onBindViewHolderToData(viewHolder, mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    protected abstract Class getViewHolderClass();

    protected abstract int getSingleItemLayoutRes();

    protected abstract void onBindViewHolderToData(VIEW_HOLDER viewHolder, DATA data);
}
