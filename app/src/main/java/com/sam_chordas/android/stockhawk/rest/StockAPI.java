package com.sam_chordas.android.stockhawk.rest;

import com.sam_chordas.android.stockhawk.model.HistoricalData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by dhermanu on 8/5/16.
 */
public interface StockAPI {
    @GET("public/yql?")
    Call<HistoricalData> getData
            (@Query("q") String query,
             @Query("format") String format,
             @Query("env") String env);
}
