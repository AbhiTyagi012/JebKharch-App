package com.example.expensemanagerapp.ui.expenses;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanagerapp.R;
import com.example.expensemanagerapp.SingleTask;
import com.example.expensemanagerapp.adapter.UploadAdapter;
import com.example.expensemanagerapp.model.Data;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ExpensesFragment extends Fragment {


    private ExpensesViewModel expensesViewModel;
    private  RecyclerView recyclerView;
    private ProgressBar progressBar;
    private List<Data> listupUploads;
    private UploadAdapter uploadAdapter;
    private TextView expenseSumResult;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
      return inflater.inflate(R.layout.fragment_expenses,container,false);





    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView= view.findViewById(R.id.myrecylerview);
        FloatingActionButton fab=view.findViewById(R.id.myfab);
        progressBar=view.findViewById(R.id.progress);
        listupUploads= new ArrayList<>();
       uploadAdapter= new UploadAdapter(listupUploads);
       recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
       recyclerView.setAdapter(uploadAdapter);
       expenseSumResult=view.findViewById(R.id.expense_result);



          getAllUploadedDataFromFirebase();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadAlertDialog();

            }



        });

        uploadAdapter.setDeleteListener(new UploadAdapter.DeleteListener() {
            @Override
            public void deleteItem(View view, int position) {
                Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
               Data data= listupUploads.get(position);
               deleteFromFirebase(data);
            }
        });
    }

    private void deleteFromFirebase(final Data data1) {
        progressBar.setVisibility(View.VISIBLE);
        ((SingleTask)getActivity().getApplication()).getDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("error",snapshot.getValue().toString());
                Iterator<DataSnapshot> ds = snapshot.getChildren().iterator();
                while (ds.hasNext()){
                    DataSnapshot dd = ds.next();
                    Data data2 = dd.getValue(Data.class);
                    if(data1.getItem().equals(data2.getItem())){

                        Log.e("error","delete from here" + data1.getItem() + "\t" + data1.getPostdate());
                        Log.e("error",dd.getKey());
                        dd.getRef().removeValue();
                        Log.e("error","remove");
                    }

                }
                
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            progressBar.setVisibility(View.GONE);
            }
        });


    }

    private void getAllUploadedDataFromFirebase(){
        progressBar.setVisibility(View.VISIBLE);
        ((SingleTask)getActivity().getApplication()).getDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> it=dataSnapshot.getChildren().iterator();

                while (it.hasNext()){
                    listupUploads.add(it.next().getValue(Data.class));
                   Log.e("error",listupUploads.get(0).getAmount() + "");

                   int expenseSum=0;
                   for (DataSnapshot mysnapshot:dataSnapshot.getChildren())
                   {
                       Data data=mysnapshot.getValue(Data.class);
                       expenseSum+=data.getAmount();

                       String strExpensesum= String.valueOf(expenseSum);

                       expenseSumResult.setText(strExpensesum);

                   }


                }
                uploadAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
               Log.e("error",databaseError.toString());
            }
        });







    }
 private EditText tamount,titem;

    private void uploadAlertDialog() {
        AlertDialog.Builder a=new AlertDialog.Builder(getActivity());
        final AlertDialog aa=a.create();
        View view=getActivity().getLayoutInflater().inflate(R.layout.custom_dialog,null,false);
        aa.setView(view);
        aa.show();
        tamount=view.findViewById(R.id.myamount);
        titem=view.findViewById(R.id.myitem);
           Button addbtn =view.findViewById(R.id.add);
        Button cancelbtn = view.findViewById(R.id.cancel);

        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if(valid()){
                    aa.dismiss();
                    uploadData();


                }




            }
        });
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aa.dismiss();
            }
        });
    }

    private void uploadData() {
        Data data= new Data();
        data.setAmount(Integer.parseInt(tamount.getText().toString()));
        data.setItem(titem.getText().toString());
        data.setPostdate(new Date(System.currentTimeMillis()).toString());


        ((SingleTask)getActivity().getApplication()).getDatabaseReference().push().setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getActivity(), "Successfully Added ", Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(getActivity(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private String amt,itm;
    private boolean valid() {
        amt=tamount.getText().toString();
        itm=titem.getText().toString();

       if(TextUtils.isEmpty(amt)){
           Toast.makeText(getActivity(), "Please Enter Amount", Toast.LENGTH_SHORT).show();
           tamount.requestFocus();
           return false;

       }else if (TextUtils.isEmpty(itm)){
           Toast.makeText(getActivity(), "Please Enter Product", Toast.LENGTH_SHORT).show();
           titem.requestFocus();
           return false;
       }else{
           return true;
       }

    }
}