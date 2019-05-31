package com.example.firebaeimageapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class imageAdapter extends RecyclerView.Adapter<imageAdapter.MyViewHolder> {

    private Context context;
    private List<uploadImage> uploadImageList;
    private onItemClickListenerNew listenerNew;

    public imageAdapter(Context context, List<uploadImage> uploadImageList) {
        this.context = context;
        this.uploadImageList = uploadImageList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.image_layout,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        uploadImage uploadImage = uploadImageList.get(i);
        myViewHolder.textView.setText(uploadImage.getImageName());
        Picasso.with(context)
                .load(uploadImage.getImageUrl())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(myViewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return uploadImageList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener,MenuItem.OnMenuItemClickListener {

        TextView textView;
        ImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.txtCardView);
            imageView = itemView.findViewById(R.id.cardViewImage);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {

            if(listenerNew!=null){

                int position = getAdapterPosition();

                if (position!=RecyclerView.NO_POSITION){

                    listenerNew.onItemClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            menu.setHeaderTitle("Delete this image from Firebase?");
            MenuItem cancel = menu.add(Menu.NONE,1,1,"Cancel");
            MenuItem delete = menu.add(Menu.NONE,2,2,"Delete");

            cancel.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            if(listenerNew!=null){

                int position = getAdapterPosition();

                if (position!=RecyclerView.NO_POSITION){

                    switch (item.getItemId()){

                        case 1:
                            listenerNew.onCancel(position);
                            return true;
                        case 2:
                            listenerNew.onDelete(position);
                            return true;
                    }
                }
            }
            return false;
        }
    }

    public interface onItemClickListenerNew{
        void onItemClick(int position);
        void onCancel(int position);
        void onDelete(int position);
    }

    public void setOnItemClickListener(onItemClickListenerNew listenerNew){
        this.listenerNew = listenerNew;
    }
}
