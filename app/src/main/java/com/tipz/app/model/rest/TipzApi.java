package com.tipz.app.model.rest;

import com.tipz.app.model.entities.TipEntity;

import java.util.List;

import retrofit.http.GET;

/***
 * An interface to describe the Tipz RESTfull API
 */
public interface TipzApi {
    @GET("/tips")
    List<TipEntity> listTips();
}
