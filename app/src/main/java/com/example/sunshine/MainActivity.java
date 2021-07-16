package com.example.sunshine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunshine.data.SunshinePreferences;
import com.example.sunshine.utilities.NetworkUtils;
import com.example.sunshine.utilities.OpenWeatherJsonUtils;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements ForecastAdapter.ForecastAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView errorMessageText;

    private ProgressBar progressBar;

    private RecyclerView mRecyclerView;

    private ForecastAdapter mForecastAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        errorMessageText = (TextView) findViewById(R.id.error_message);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_forecast);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mForecastAdapter = new ForecastAdapter(this);
        mRecyclerView.setAdapter(mForecastAdapter);




        loadWeatherData();


    }

    private void showWeatherDataView(){
        errorMessageText.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage(){
        errorMessageText.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void loadWeatherData(){
        showWeatherDataView();
        String location = SunshinePreferences.getPreferredWeatherLocation(this);
        new FetchWeatherTask().execute(location);
    }

    @Override
    public void onClick(String item) {

        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, item);
        startActivity(intentToStartDetailActivity);
    }


    public class FetchWeatherTask extends AsyncTask<String, Void, String[]>{

        @Override
        protected String[] doInBackground(String... strings) {
           if(strings.length==0){
               return null;
           }

           String location = strings[0];
           URL weatherRequestUrl = NetworkUtils.buildUrl(location);

           try{
               String jsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl);
               String[] simpleJsonWeatherData = OpenWeatherJsonUtils.getSimpleWeatherStringsFromJson(MainActivity.this, jsonWeatherResponse);
               return simpleJsonWeatherData;
           } catch(Exception ex){
               ex.printStackTrace();
               return null;
           }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String[] weatherData) {
            progressBar.setVisibility(View.INVISIBLE);

           if(weatherData != null){
               showWeatherDataView();
                mForecastAdapter.setWeatherData(weatherData);
           }else{
               showErrorMessage();
           }
        }
    }

    private void openLocationMap(){
        String addressString = "1600 Ampitheatre Parkway, CA";
        Uri geoLocation = Uri.parse("geo:0,0?q="+ addressString);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if(intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        } else{
            Log.d(TAG, "Couldn't call "+ geoLocation.toString()
                        + ", no receiving apps installed!");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.forcast, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_refresh){
            mForecastAdapter.setWeatherData(null);
            loadWeatherData();
            return true;
        }

        if(id == R.id.action_map){
            openLocationMap();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}