package com.sam_chordas.android.stockhawk.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.model.HistoricalData;
import com.sam_chordas.android.stockhawk.model.Quote;
import com.sam_chordas.android.stockhawk.rest.StockAPI;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailStockActivity extends AppCompatActivity {

    private StockAPI stockAPI;
    private int back7Days = -7;
    private int back1Months = -1;
    private int back3Months = -3;
    private int back6Months = -6;
    private int back1Year = -11;
    private int currentDate = 0;
    private String symbol;

    LineChart stockChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_stock);
        stockChart = (LineChart) findViewById(R.id.stockChart);
        stockChart.setNoDataText(getResources().getString(R.string.string_loading_chart));
        symbol = getIntent().getExtras().getString(getString(R.string.string_symbol));
        setTitle(symbol + " - 1W");
        updateGraph(getDate(back7Days), getDate(currentDate), symbol);
    }


    private void updateGraph(String startDate, String endDate, String symbol){

        final String BASE_URL = "https://query.yahooapis.com/v1/";

        String query = "select * from yahoo.finance.historicaldata where symbol = \""
                + symbol + "\" and startDate = \"" + startDate +
                "\" and endDate = \"" + endDate + "\"";

        String format = "json";
        String env = "store://datatables.org/alltableswithkeys";
        Gson gson =  new GsonBuilder().create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        stockAPI = retrofit.create(StockAPI.class);

        Call<HistoricalData> historicalDataCall = stockAPI.getData(query, format, env);
        historicalDataCall.enqueue(new Callback<HistoricalData>() {
            @Override
            public void onResponse(Call<HistoricalData> call, Response<HistoricalData> response) {
                HistoricalData dataBody = response.body();
                List<Quote> quoteList = dataBody.getQuery().getResults().getQuote();

                ArrayList<Entry> lineEntry = new ArrayList<>();
                int i = 0;
                for(Quote stock : quoteList){
                    lineEntry.add(new Entry(i , Float.parseFloat(stock.getClose())));
                    i++;
                }

                LineDataSet lineDataSet = new LineDataSet
                        (lineEntry, getString(R.string.string_graph_description));
                lineDataSet.setColor(Color.RED);
                lineDataSet.setDrawCircles(false);
                stockChart.removeAllViews();
                stockChart.setData(new LineData(lineDataSet));

            }

            @Override
            public void onFailure(Call<HistoricalData> call, Throwable t) {

            }
        });
    }
    private String getReadableDateString(long time){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat shortenedDateFormat
                = new SimpleDateFormat(getString(R.string.string_date_fromat));
        return shortenedDateFormat.format(time);
    }

    private String getDate(int dateSubs){

        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        if(dateSubs == currentDate)
            return getReadableDateString(cal.getTimeInMillis());

        else if(dateSubs == back7Days)
           cal.add(Calendar.DATE, dateSubs);

        else if(dateSubs == back1Months || dateSubs == back3Months || dateSubs == back6Months)
            cal.add(Calendar.MONTH, dateSubs);

        else if(dateSubs == back1Year)
            cal.add(Calendar.YEAR, -1);

        return getReadableDateString(cal.getTimeInMillis());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_stock,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if(id == R.id.oneWeek){
            updateGraph(getDate(back7Days), getDate(currentDate), symbol);
            setTitle(symbol + getString(R.string.string_graph_one_week));
            item.setChecked(true);
        }

        if(id == R.id.oneMonth){
            updateGraph(getDate(back1Months), getDate(currentDate), symbol);
            setTitle(symbol + getString(R.string.string_graph_one_month));
            item.setChecked(true);
        }

        if(id == R.id.threeMonths){
            updateGraph(getDate(back3Months), getDate(currentDate), symbol);
            setTitle(symbol + getString(R.string.string_graph_three_months));
            item.setChecked(true);
        }

        if(id == R.id.sixMonths){
            updateGraph(getDate(back6Months), getDate(currentDate), symbol);
            setTitle(symbol + getString(R.string.string_graph_six_months));
            item.setChecked(true);
        }

        if(id == R.id.oneYear){
            updateGraph(getDate(back1Year), getDate(currentDate), symbol);
            setTitle(symbol + getString(R.string.string_graph_one_year));
            item.setChecked(true);
        }
        return super.onOptionsItemSelected(item);
    }
}
