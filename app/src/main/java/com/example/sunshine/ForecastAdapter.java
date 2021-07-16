package com.example.sunshine;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    private static final String TAG = ForecastAdapter.class.getSimpleName();

    private String [] mWeatherData;

    final private ForecastAdapterOnClickHandler mClickHandler;

    public interface ForecastAdapterOnClickHandler{
        void onClick(String item);
    }


    public ForecastAdapter(ForecastAdapterOnClickHandler listener){

        mClickHandler = listener;

    }


    @Override
    public ForecastAdapterViewHolder onCreateViewHolder( ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdFromListItem = R.layout.forecast_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdFromListItem, viewGroup, shouldAttachToParentImmediately);
        ForecastAdapterViewHolder viewHolder = new ForecastAdapterViewHolder(view);

        Log.d(TAG, "onCreatedViewHolder: number if ViewHolder created");
        return viewHolder;
    }

    @Override
    public void onBindViewHolder( ForecastAdapterViewHolder holder, int position) {
        Log.d(TAG, "#"+ position);
        String weatherForThisDay = mWeatherData[position];
        holder.mWeatherTextView.setText(weatherForThisDay);
    }

    @Override
    public int getItemCount() {
        if(null == mWeatherData) return 0;
        return  mWeatherData.length;
    }

    public void setWeatherData(String[] weatherData){
        mWeatherData = weatherData;
        notifyDataSetChanged();
    }

    public class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public final TextView mWeatherTextView;

        public ForecastAdapterViewHolder( View itemView) {
            super(itemView);
            mWeatherTextView = itemView.findViewById(R.id.tv_weather_data);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String weatherForDay = mWeatherData[adapterPosition];
            mClickHandler.onClick(weatherForDay);
        }
    }
}
