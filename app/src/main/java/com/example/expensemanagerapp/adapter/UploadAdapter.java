package com.example.expensemanagerapp.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanagerapp.R;
import com.example.expensemanagerapp.model.Data;
import com.google.firebase.database.core.Context;

import java.util.List;

  public class UploadAdapter extends RecyclerView.Adapter<UploadAdapter.MyView> {
    private List<Data> list;
    private DeleteListener deleteListener;


    public UploadAdapter(List<Data> list) {
        this.list= list;
        Log.e("error", "adapter" );
    }

    @NonNull
    @Override
    public MyView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_upload,parent,false);
        return new MyView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyView holder, int position) {

        Log.e("error1","execute");
        Data data =list.get(position);
        holder.tproductname.setText(data.getItem());
        holder.tproductamt.setText(String.valueOf(data.getAmount()));
        holder.tdate.setText(data.getPostdate());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

     class MyView extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView tproductname,tproductamt,tdate;
        ImageView deleteimage;


         public MyView(@NonNull View itemView) {
             super(itemView);
             tproductname=itemView.findViewById(R.id.showitem);
             tproductamt=itemView.findViewById(R.id.showamount);
             tdate=itemView.findViewById(R.id.showdate);
             deleteimage=itemView.findViewById(R.id.delete_item);
             deleteimage.setOnClickListener(this);





         }


         @Override
         public void onClick(View view) {


           final Dialog dialog= new  Dialog(itemView.getContext());
           dialog.create();
           dialog.setContentView(R.layout.delete_dialog);
           dialog.show();

             Button yesbtn = (Button)dialog.findViewById(R.id.yes);
             Button nobtn = (Button)dialog.findViewById(R.id.no);

             yesbtn.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     dialog.dismiss();
                     deleteListener.deleteItem(view,getAdapterPosition());
                 }
             });
             nobtn.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     dialog.dismiss();
                 }
             });



         }

     }



      public void setDeleteListener(DeleteListener deleteListener){
        this.deleteListener=deleteListener;

      }
     public interface DeleteListener{
        void deleteItem(View view, int position);
     }
}
