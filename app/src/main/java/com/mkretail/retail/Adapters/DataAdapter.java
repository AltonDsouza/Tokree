package com.mkretail.retail.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mkretail.retail.Utils.CatClick;
import com.mkretail.retail.Models.CatModel;
import com.mkretail.retail.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder>{
    private Context context;
    private List<CatModel> list;
    private CatClick customClickListener;

    public DataAdapter(Context context, List<CatModel> list) {
        this.context = context;
        this.list = list;
        this.customClickListener = (CatClick) context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_cat_list,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final CatModel catModel = list.get(i);
        viewHolder.textView.setText(catModel.getCatName());
        Picasso.with(context).load(catModel.getCatImageURLTrim()).into(viewHolder.imageView);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences pref = context.getSharedPreferences("MyPref", 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("ToList","Cat");
                editor.commit();
                customClickListener.onCatClick(v,catModel.getCatID());
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        TextView textView;
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.catName);
            imageView = itemView.findViewById(R.id.catImage);

//            imageView.setOnClickListener(this);
//            textView.setOnClickListener(this);
        }

//        @Override
//        public void onClick(View v) {
//
//        }
    }

    public interface onCatListener{
        void onCatClick(int position, String catID);
    }

}
