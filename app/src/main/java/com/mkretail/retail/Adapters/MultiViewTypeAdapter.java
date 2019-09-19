package com.mkretail.retail.Adapters;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.mkretail.retail.Models.Model;
import com.mkretail.retail.R;
import com.mkretail.retail.SearchResults;
import com.mkretail.retail.SubCat;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MultiViewTypeAdapter extends RecyclerView.Adapter {
    @NonNull
    private static ArrayList<Model> dataSet;
    public static Context mContext;
    static int total_types;

    public MultiViewTypeAdapter(ArrayList<Model> data, Context mContext) {
        this.dataSet = data;
        this.mContext = mContext;
        total_types = dataSet.size();
       // multiItemClickListener = (MultiTypeViewHolder.OnMultiItemClickListener)mContext;



    }

    public static class TextTypeViewHolder extends RecyclerView.ViewHolder {

        TextView txtType;

        public TextTypeViewHolder(View itemView) {
            super(itemView);

            this.txtType = (TextView) itemView.findViewById(R.id.text);
        }

        public void bind(final Model item) {
            txtType.setText(item.getText());
            txtType.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SharedPreferences pref = mContext.getSharedPreferences("MyPref", 0);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("ToList","Dashboard");
                    editor.commit();

                    if(item.getLinktype().equals("cat")){
                        Intent i = new Intent(mContext, SubCat.class);
                        i.putExtra("cid",item.getMyid());
                        mContext.startActivity(i);
                    }
                    else if(item.getLinktype().equals("list")){

                        Intent i = new Intent(mContext, SubCat.class);
                        i.putExtra("cid",item.getCid());
                        //  Log.e("cid",object.getCid());
                        i.putExtra("sid",item.getMyid());

                        mContext.startActivity(i);
                        //Toast.makeText(mContext.getApplicationContext(), "list", Toast.LENGTH_SHORT).show();
                    }
                    else if(item.getLinktype().equals("prd")){
                        //Toast.makeText(mContext.getApplicationContext(), "Work in progress", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(mContext, SubCat.class);
                        i.putExtra("cid", item.getCid());
                        i.putExtra("p_imid", item.getMyid());
                        mContext.startActivity(i);
                    }
                }
            });
        }
    }

    public static class ImageTypeViewHolder extends RecyclerView.ViewHolder {

        ImageView image;

        public ImageTypeViewHolder(View itemView) {
            super(itemView);

            this.image = (ImageView) itemView.findViewById(R.id.dashimage);
        }

        public void bind(final Model item) {
            Picasso.with(mContext).load(item.getImage()).into(image);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SharedPreferences pref = mContext.getSharedPreferences("MyPref", 0);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("ToList","Dashboard");
                    editor.commit();

                    if(item.getLinktype().equals("cat")){
                        Intent i = new Intent(mContext, SubCat.class);
                        i.putExtra("cid",item.getMyid());
                        mContext.startActivity(i);
                    }
                    else if(item.getLinktype().equals("list")){

                        Intent i = new Intent(mContext, SubCat.class);
                        i.putExtra("cid",item.getCid());
                        //  Log.e("cid",object.getCid());
                        i.putExtra("sid",item.getMyid());
                        mContext.startActivity(i);
                    }
                    else if(item.getLinktype().equals("prd")){
                        Intent i = new Intent(mContext, SubCat.class);
//                        i.putExtra("cid", item.getCid());
                        i.putExtra("p_imid", item.getMyid());
                        mContext.startActivity(i);
                    }
                }
            });
        }
    }

    public static class MultiTypeViewHolder extends RecyclerView.ViewHolder {

        TextView txtType;
        RecyclerView recyclerView;
        TextView viewMore;
         LinearLayoutManager linearLayoutManager;


        ImageView left, right;

        int currentVisibleItem;

        public MultiTypeViewHolder(View itemView) {
            super(itemView);

           this.txtType = (TextView) itemView.findViewById(R.id.multi_text);
//            this.txttoshowwhenviewmore = (TextView) itemView.findViewById(R.id.multi_text_viewmore);
           this.recyclerView = (RecyclerView)itemView.findViewById(R.id.prdsview);
           this.viewMore = (TextView) itemView.findViewById(R.id.viewmore);
           this.left = (ImageView)itemView.findViewById(R.id.left);
            this.right = (ImageView)itemView.findViewById(R.id.right);
            //gridLayoutManager = new GridLayoutManager(mContext, 1);//row count

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    switch (newState){
                        case RecyclerView.SCROLL_STATE_DRAGGING:
                            right.setVisibility(View.GONE);
                            left.setVisibility(View.GONE);
                            break;

                        case RecyclerView.SCROLL_STATE_IDLE:
                            currentVisibleItem = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                            if (currentVisibleItem == (recyclerView.getAdapter().getItemCount() - 1)) {
                                right.setVisibility(View.GONE);
                                left.setVisibility(View.VISIBLE);
                            } else if (currentVisibleItem != 0) {
                                right.setVisibility(View.VISIBLE);
                                left.setVisibility(View.VISIBLE);
                            } else if (currentVisibleItem == 0) {
                                left.setVisibility(View.GONE);
                                right.setVisibility(View.VISIBLE);
                            }
                            break;
                    }
                }
            });

            right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerView.smoothScrollToPosition(linearLayoutManager.getItemCount()-1);
//                        right.setVisibility(View.GONE);
//                        left.setVisibility(View.VISIBLE);
                }
            });

            left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerView.smoothScrollToPosition(0);
//                            right.setVisibility(View.VISIBLE);
//                            left.setVisibility(View.GONE);
                }
            });

        }


        public void bind(final Model item) {

            txtType.setText(item.getHeaderTitle());
            txtType.setPaintFlags(txtType.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
             ArrayList prodList = item.getAllItemsInSection();
             final MyAdapter myAdapter = new MyAdapter(mContext, prodList);
         //   recyclerView.setHasFixedSize(true);
            linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false){
                @Override
                public boolean canScrollVertically() {
                    return false;
                }

            };

//            gridLayoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
            recyclerView.setHasFixedSize(true);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(myAdapter);


            if(recyclerView.getAdapter().getItemCount()>2){
                viewMore.setVisibility(View.VISIBLE);
                left.setVisibility(View.VISIBLE);
                right.setVisibility(View.VISIBLE);

               viewMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences pref = mContext.getSharedPreferences("MyPref", 0);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("ToList","Dashboard");
                        editor.commit();


                        Intent i = new Intent(mContext, SearchResults.class);
                        i.putExtra("ncid", item.getNcid());
                        mContext.startActivity(i);
                    }
                });
            }

            else {
                viewMore.setVisibility(View.GONE);
            }
        }


    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;
        switch (viewType) {
            case Model.TEXT_TYPE:
                view = LayoutInflater.from(mContext).inflate(R.layout.text_item, viewGroup, false);
                return new TextTypeViewHolder(view);
            case Model.IMAGE_TYPE:
                view = LayoutInflater.from(mContext).inflate(R.layout.image_item, viewGroup, false);
                return new ImageTypeViewHolder(view);
            case Model.MULTI_TYPE:
                view = LayoutInflater.from(mContext).inflate(R.layout.multi, viewGroup, false);
                return new MultiTypeViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int listPosition) {
//     final  Model object = dataSet.get(listPosition);
//        if (object != null) {
            switch (dataSet.get(listPosition).getType()) {
                case Model.TEXT_TYPE:
                    ((TextTypeViewHolder) holder).bind(dataSet.get(listPosition));
                    break;

                case Model.IMAGE_TYPE:
                    ((ImageTypeViewHolder)holder).bind(dataSet.get(listPosition));
                    break;

                case Model.MULTI_TYPE:
                    ((MultiTypeViewHolder)holder).bind(dataSet.get(listPosition));
                    break;
            }
    }

    @Override
    public int getItemViewType(int position) {

        switch (dataSet.get(position).getType()) {
            case 0:
                return Model.TEXT_TYPE;
            case 1:
                return Model.IMAGE_TYPE;
            case 2:
                return Model.MULTI_TYPE;
            default:
                return -1;
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}