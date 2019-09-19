package com.mkretail.retail.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mkretail.retail.Description;
import com.mkretail.retail.Models.DashProductModel;
import com.mkretail.retail.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private Context context;
    private  List<DashProductModel> list;
    private OnItemClickListener itemClickListener;


    public MyAdapter(Context context, List<DashProductModel> list) {
        this.context = context;
        this.list = list;
        itemClickListener = (OnItemClickListener)context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dashboard_item, viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        yourMethodName(viewHolder,i);

        //Always leave onBind as light as possible because as you scroll, this method gets called persistently.
        viewHolder.bind(list.get(i), itemClickListener);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
            TextView title, pid, price;
            //TextView discount;
            ImageView imageView, isExpress;
            ImageView likeButton;
            TextView button, out;
            //Handle all your onClicks over here!

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.product_name);
            pid = itemView.findViewById(R.id.p_imid);
            price = itemView.findViewById(R.id.price);
            imageView = itemView.findViewById(R.id.dashimage);
            button = (TextView) itemView.findViewById(R.id.cart);
            likeButton = itemView.findViewById(R.id.wishheart);
            isExpress =  itemView.findViewById(R.id.express);
            out = itemView.findViewById(R.id.outofStock);
        }

        public void bind(final DashProductModel item, final OnItemClickListener listener) {
            title.setText(item.getTitle());
            Picasso.with(context).load(item.getImage()).fit().centerCrop().error(R.drawable.no_image).into(imageView);
            price.setText(item.getPrice());
            pid.setText(item.getImid());

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v,item.getTitle(),item.getImid());
//                    Toast.makeText(context.getApplicationContext(), "Clicked", Toast.LENGTH_SHORT).show();
                }
            });

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context.getApplicationContext(), Description.class);
                    i.putExtra("name",item.getTitle());
                    i.putExtra("desc",item.getDescription());
                    i.putExtra("image",item.getImage());
                    context.startActivity(i);
                }
            });

            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    likeButton.setBackgroundColor(Color.parseColor("#B71C1C"));
                    likeButton.setImageResource(R.drawable.ic_favorite_black_24dp);

               new CountDownTimer(2000, 20) {
                   @Override
                   public void onTick(long arg0) {
                       // TODO Auto-generated method stub
                   }

                   @Override
                   public void onFinish() {
                       likeButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
//                       likeButton.setBackgroundColor(Color.parseColor("#FAFAFA"));
                   }
               }.start();

               listener.addWish(item.getImid());
                }
            });

        }

    }
    private void yourMethodName(final ViewHolder holder,final int position)
    {
        final DashProductModel item = list.get(position);
        if(item.getInStock().equalsIgnoreCase("0")){
            holder.button.setText("OUT OF STOCK");
            holder.button.setTextColor(Color.parseColor("#FF1744"));
            holder.button.setBackgroundColor(Color.parseColor("#E8F5E9"));
        }
        else {
            holder.button.setText("ADD TO CART");
            holder.button.setTextColor(Color.parseColor("#E8F5E9"));
            holder.button.setBackgroundColor(Color.parseColor("#1b5e20"));
        }


        if(item.getIsExpress().equalsIgnoreCase("0")){
            holder.isExpress.setVisibility(View.INVISIBLE);
        }
        else {
            holder.isExpress.setVisibility(View.VISIBLE);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View v, String title, String p_imid);
        void addWish(String p_imid);
    }

}