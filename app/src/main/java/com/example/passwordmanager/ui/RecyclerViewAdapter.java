package com.example.passwordmanager.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.example.passwordmanager.CustomFilter;
import com.example.passwordmanager.R;
import com.example.passwordmanager.data.DataBaseHandler;
import com.example.passwordmanager.model.EncDec;
import com.example.passwordmanager.model.Item;
import com.google.android.material.snackbar.Snackbar;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends Adapter<RecyclerViewAdapter.ViewHolder> implements Filterable {

    public static EditText keName;
    public static TextView pName;
    public Context context;
    public List<Item> itemList;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private LayoutInflater inflater;
    private List<Item> filterList;
    CustomFilter filter;

    public RecyclerViewAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
        this.filterList = new ArrayList<>(itemList);
    }


    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_row, viewGroup, false);


        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder viewHolder, int position) {

        Item item = itemList.get(position); // object Item

        viewHolder.itemName.setText(MessageFormat.format("Title: {0}", item.getWebName()));
        //viewHolder.itemPass.setText(MessageFormat.format("Password: {0}", item.getPassword()));
        viewHolder.dateAdded.setText(MessageFormat.format("Added on: {0}", item.getDateItemAdded()));


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter==null){
            filter = new CustomFilter(filterList,this);
        }
        return filter;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView itemName;
        //public TextView itemPass;
        public TextView dateAdded;
        public Button editButton;
        public Button deleteButton;

        public int id;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;

            itemName = itemView.findViewById(R.id.item_name);
            //    itemPass = itemView.findViewById(R.id.item_pass);
            dateAdded = itemView.findViewById(R.id.item_date);

            deleteButton = itemView.findViewById(R.id.deleteButton);
            editButton = itemView.findViewById(R.id.editButton);

            editButton.setOnClickListener(this);
            deleteButton.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {

            int position;
            position = getAdapterPosition();
            Item item = itemList.get(position);
            switch (v.getId()) {
                case R.id.deleteButton:
                    deleteItem(item.getId());
                    break;
                case R.id.editButton:
                    editItem(item);
                    break;
            }
        }


        private void deleteItem(final int id) {

            builder = new AlertDialog.Builder(context);

            inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.confirmation_pop, null);

            Button noButton = view.findViewById(R.id.no_text);
            Button yesButton = view.findViewById(R.id.yes_text);

            builder.setView(view);
            dialog = builder.create();
            dialog.show();

            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataBaseHandler db = new DataBaseHandler(context);
                    db.deleteItem(id);
                    itemList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    dialog.dismiss();
                }
            });

        }

        private void editItem(Item item) {

            builder = new AlertDialog.Builder(context);
            inflater = LayoutInflater.from(context);
            final View view = inflater.inflate(R.layout.pop, null);

            Button saveButton;
            TextView title;

            final Item i = itemList.get(getAdapterPosition());


            keName = view.findViewById(R.id.key_n);
            pName = view.findViewById(R.id.ps);

            saveButton = view.findViewById(R.id.save_butt);
            saveButton.setText(R.string.update_text);
            title = view.findViewById(R.id.text);

            title.setText(R.string.edit_item);


            builder.setView(view);
            dialog = builder.create();
            dialog.show();

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!keName.getText().toString().isEmpty()){
                        EncDec.dec(i);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                            }
                        },120000);
                        //Snackbar.make(view,"Password Saved",Snackbar.LENGTH_SHORT).show();

                    }else{
                        Snackbar.make(v,"Empty Fields not Allowed",Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}


