package com.tipz.app.view.fragments.tips;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.tipz.app.R;
import com.tipz.app.TipzApplication;
import com.tipz.app.model.entities.TipEntity;
import com.tipz.helpers.view.BaseDataFragment;

import java.util.ArrayList;

/**
 * A fragment representing a list of tips.
 */
public class TipsFragment extends BaseDataFragment<TipzApplication, TipEntity> {

    /**
     * The fragment's RecyclerView to display tips data
     */
    private RecyclerView mRecyclerView;

    /**
     * The Adapter which will be used to populate the RecyclerView
     */
    private TipsAdapter mAdapter;

    private ArrayList<TipEntity> mTips;
    private LinearLayoutManager mLayoutManager;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TipsFragment() {
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_tips;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Save reference to the recycler view
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.fragment_tips_recycler_view);

        // In contrast to other adapter-backed views such as ListView or GridView - RecyclerView
        // allows client code to provide custom layout arrangements for child views.
        // These arrangements are controlled by the RecyclerView.LayoutManager.
        // A LayoutManager must be provided for RecyclerView to function.
        // (https://developer.android.com/reference/android/support/v7/widget/RecyclerView.html)
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Create the adapter and attach to recycler view
        mTips = new ArrayList<>();
        mAdapter = new TipsAdapter(mTips);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onNewData(Cursor cursor) {

        // Remove previous content
        mTips.clear();

        // If the cursor has values
        if (cursor != null && cursor.moveToFirst()) {

            TipEntity currTip;

            // Run on the cursor and extract data for each tip
            do {
                // Create the tip and extract data
                currTip = new TipEntity();
                currTip.title = cursor.getString(
                        cursor.getColumnIndex(TipEntity.DB.TITLE));
                currTip.createdTimestamp = cursor.getLong(
                        cursor.getColumnIndex(TipEntity.DB.CREATED_TIMESTAMP));

                // Add to list
                mTips.add(currTip);
            } while (cursor.moveToNext());
        }

        // Just notify the adapter that data has changed,
        // remember the data is already referenced inside the adapter
        mAdapter.notifyDataSetChanged();

    }

    @Override
    protected Uri getFragmentDataUri() {
        return TipEntity.CONTENT_URI;
    }
}
