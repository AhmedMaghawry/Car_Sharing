package com.tarek.carsharing.Control;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tarek.carsharing.Model.Car;
import com.tarek.carsharing.Model.Trip;
import com.tarek.carsharing.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    ArrayList<Trip>  trips;
    ArrayList<Car>  cars;
    Context context;

    public MainAdapter(ArrayList<Trip> trips, ArrayList<Car> car, Context context) {
        this.trips = trips;
        this.cars=car;
        this.context = context;
    }


    @Override
    public MainAdapter.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder( MainAdapter.ViewHolder viewHolder, int position) {
        viewHolder.mStart.setText(trips.get(position).getStart());
        viewHolder.mEnd.setText(trips.get(position).getEnd());
        viewHolder.mTime.setText(trips.get(position).getTime());
        viewHolder.mType.setText(cars.get(position).getType());
        viewHolder.mNumber.setText(cars.get(position).getNumber());
        viewHolder.mColor.setText(cars.get(position).getColor());
        viewHolder.mfare.setText(trips.get(position).getFare()+"");
        viewHolder.mpromocode.setText(trips.get(position).getPromocode());
        Picasso.with(context).load(cars.get(position).getImage())
                .into(viewHolder.mImage);

    }

    @Override
    public int getItemCount() {
        return trips.size() ;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mStart;
        public TextView mEnd;
        public TextView mTime;
        public TextView mType;
        public TextView mColor;
        public TextView mNumber;
        public CircleImageView mImage;
        public TextView mfare;
        public TextView mpromocode;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mStart = itemView.findViewById(R.id.start);
            mEnd = itemView.findViewById(R.id.end);
            mTime = itemView.findViewById(R.id.time);
            mType = itemView.findViewById(R.id.type);
            mNumber = itemView.findViewById(R.id.number);
            mColor = itemView.findViewById(R.id.color);
            mImage = itemView.findViewById(R.id.ivProfilePic);
            mfare = itemView.findViewById(R.id.fare);
            mpromocode = itemView.findViewById(R.id.promocode);
        }
    }
}
