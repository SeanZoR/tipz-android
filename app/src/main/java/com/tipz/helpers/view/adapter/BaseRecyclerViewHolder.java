package com.tipz.helpers.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * Represents an abstract view holder with auto-binding functions
 * NOTE: If subclassed from an Internal class, it must be a static class
 */
public abstract class BaseRecyclerViewHolder extends RecyclerView.ViewHolder {

    public BaseRecyclerViewHolder(View itemView) {
        super(itemView);
    }

    public static <T> T create(Class<T> cls, View view) {
        T entity = null;
        try {
            final Constructor<T> constructor = cls.getConstructor(View.class);
            entity = constructor.newInstance(view);

            // Run on all the fields in the API object
            for (Field currField : entity.getClass().getFields()) {

                // If this field has annotation
                if (currField.getAnnotation(ViewHolderBinder.class) != null) {
                    // Check to see the db name of the fields
                    ViewHolderBinder annotation = currField.getAnnotation(ViewHolderBinder.class);
                    int resId = annotation.resId();

                    currField.set(entity, view.findViewById(resId));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return entity;
    }

}
