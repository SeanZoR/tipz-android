package com.tipz.app.model.entities;

import java.io.Serializable;

/**
 * This entity is a "tip" entity from TipzApi
 * The naming convention is built to enable Gson instance to convert all fields from lower
 * case with underscores to camel case and vice versa.
 */
public class TipEntity implements Serializable {

    /***
     * A unix-timestamp of created time
     */
    public Long createdTimestamp;

    /**
     * The title of the tip
     */
    public String title;
}
