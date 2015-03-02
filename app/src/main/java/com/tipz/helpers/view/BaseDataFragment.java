package com.tipz.helpers.view;

import android.app.Application;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.tipz.app.view.BaseFragment;
import com.tipz.helpers.control.content_provider.ProviderEntity;

public abstract class BaseDataFragment<APP extends Application, ENTITY extends ProviderEntity>
        extends BaseFragment<APP>
        implements LoaderCallbacks<Cursor> {

    public static final int LOADER_MAIN_ID = 1;

    /**
     * Data that comes from the Loader
     *
     * @param cursor The data in Cursor
     */
    @SuppressWarnings("UnusedParameters")
    protected abstract void onNewData(Cursor cursor);

    /**
     * Get the CONTENT_URI that represents the fragment
     */
    protected abstract Uri getFragmentDataUri();

    /**
     * {@inheritDoc}
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(LOADER_MAIN_ID, null, this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle arg1) {
        if (loaderId == LOADER_MAIN_ID) {
            return new CursorLoader(getActivity(), getFragmentDataUri(), getFragmentProjection(),
                    getFragmentSelection(), getFragmentSelectionArgs(), getDataOrderByQuery());
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_MAIN_ID) {
            onNewData(data);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * Get the projection part of the loader
     * By default they are null (meaning no WHERE filtering)
     */
    @SuppressWarnings("WeakerAccess")
    protected String[] getFragmentProjection() {
        return null;
    }

    /**
     * Get the selection args for the main loader. (The part that replaces question
     * marks "WHERE ?,?,?..." )
     * If this is used, you must use {@link #getFragmentSelection()}
     * By default they are null (meaning no WHERE filtering)
     */
    @SuppressWarnings("WeakerAccess")
    protected String[] getFragmentSelectionArgs() {
        return null;
    }

    /**
     * Get the selection for the main loader. (The "WHERE ?,?,?..." part).
     * If this is used, you must use {@link #getFragmentSelectionArgs()}
     * By default they are null (meaning no WHERE filtering)
     */
    @SuppressWarnings("WeakerAccess")
    protected String getFragmentSelection() {
        return null;
    }

    /**
     * Get the data order part of the loader
     * By default they are null (meaning no WHERE filtering)
     */
    @SuppressWarnings("WeakerAccess")
    protected String getDataOrderByQuery() {
        return null;
    }
}
