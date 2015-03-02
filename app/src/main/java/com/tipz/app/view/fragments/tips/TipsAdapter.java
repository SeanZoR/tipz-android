package com.tipz.app.view.fragments.tips;

import android.view.View;
import android.widget.TextView;

import com.tipz.app.R;
import com.tipz.app.model.entities.TipEntity;
import com.tipz.helpers.view.adapter.BaseRecyclerArrayAdapter;
import com.tipz.helpers.view.adapter.BaseRecyclerViewHolder;
import com.tipz.helpers.view.adapter.ViewHolderBinder;

import java.util.ArrayList;

public class TipsAdapter extends BaseRecyclerArrayAdapter<TipsAdapter.TipViewHolder, TipEntity> {

    /**
     * Construct an adapter with data in it
     *
     * @param data the data for the adapter to display
     */
    public TipsAdapter(ArrayList<TipEntity> data) {
        super(data);
    }

    @Override
    protected Class getViewHolderClass() {
        return TipViewHolder.class;
    }

    @Override
    protected int getSingleItemLayoutRes() {
        return R.layout.item_tips;
    }

    @Override
    public void onBindViewHolderToData(TipViewHolder holder, TipEntity data) {
        // Bind the data to the view holder
        holder.title.setText(data.title);
    }


    public static class TipViewHolder extends BaseRecyclerViewHolder {

        public TipViewHolder(View itemView) {
            super(itemView);
        }

        @ViewHolderBinder(resId = R.id.item_tips_title)
        public TextView title;

    }
}