package com.mkretail.retail.Adapters;


import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mkretail.retail.Models.DetailModel;
import com.mkretail.retail.R;
import com.mkretail.retail.Utils.Operations;

import java.util.List;

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.ViewHolder>
{
    private Context context;
    private List<DetailModel> detailModels;
    private Operations listener;

    public DetailsAdapter(Context context, List<DetailModel> detailModels) {
        this.context = context;
        this.detailModels = detailModels;
        this.listener = (Operations)context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.detail_item,viewGroup,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final DetailModel detailModel = detailModels.get(i);
        viewHolder.title.setText(detailModel.getTitle());
        viewHolder.price.setText(detailModel.getPrice());
        viewHolder.imid.setText(detailModel.getImid());

        if(!detailModel.getOriginalAmount().equals("")){
            //Show original amount striked
            viewHolder.disc.setText(detailModel.getOriginalAmount());
            viewHolder.disc.setPaintFlags( viewHolder.disc.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        viewHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = Integer.parseInt(String.valueOf(viewHolder.count.getText()));
                count+=1;

                listener.addToCart(detailModel.getImid(), String.valueOf(count));
                viewHolder.count.setText(String.valueOf(count));

                viewHolder.button.setVisibility(View.GONE);
                viewHolder.minus.setVisibility(View.VISIBLE);
                viewHolder.count.setVisibility(View.VISIBLE);
                viewHolder.plus.setVisibility(View.VISIBLE);

            }
        });
        viewHolder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = Integer.parseInt(String.valueOf(viewHolder.count.getText()));
                count+=1;

                if(count>1)
                {
                    viewHolder.count.setText(String.valueOf(count));

                    listener.updateCart(detailModel.getImid(), String.valueOf(count));

                }
            }
        });

        viewHolder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = Integer.parseInt(String.valueOf(viewHolder.count.getText()));
                if(count>0)
                {
                    count-=1;
                    if(count==0)
                    {
                        listener.deleteCart(detailModel.getImid(), String.valueOf(count));
                        viewHolder.count.setText(String.valueOf(count));
                        viewHolder.button.setVisibility(View.VISIBLE);
                        viewHolder.minus.setVisibility(View.GONE);
                        viewHolder.count.setVisibility(View.GONE);
                        viewHolder.plus.setVisibility(View.GONE);
                    }
                    else
                    {
                        listener.updateCart(detailModel.getImid(),String.valueOf(count));
                        viewHolder.count.setText(String.valueOf(count));
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return detailModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView title, imid, disc , price, count;
        ImageView minus, plus;
        Button button;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.smtitle);
            imid = itemView.findViewById(R.id.imid);
            disc = itemView.findViewById(R.id.disc);
            price = itemView.findViewById(R.id.price);
            button = itemView.findViewById(R.id.cartadd);
            minus = (ImageView) itemView.findViewById(R.id.minus);
            plus = (ImageView) itemView.findViewById(R.id.plus);
            count = itemView.findViewById(R.id.count);
        }
    }
}
