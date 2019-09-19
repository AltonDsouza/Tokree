package com.mkretail.retail.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mkretail.retail.Models.OfferModel;
import com.mkretail.retail.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.MyViewHolder> {

    private Context context;
    private List<OfferModel> list;

    public OfferAdapter(Context context, List<OfferModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.offer_item,viewGroup,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        OfferModel offerModel = list.get(i);
        Picasso.with(context).load(offerModel.getImage()).into(myViewHolder.imageView);
        myViewHolder.title.setText(offerModel.getOfferTitle());
        myViewHolder.text.setText(offerModel.getOffertext());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
{
        ImageView imageView;
        TextView text, title;
    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.offerimage);
//        imageText = itemView.findViewById(R.id.offerimagetext);
        text = itemView.findViewById(R.id.offertext);
        title = itemView.findViewById(R.id.title);
    }
}
}
