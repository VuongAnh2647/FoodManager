package com.example.foodmanager.Adapter;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodmanager.databinding.DialogOrderProductBinding;
import com.example.foodmanager.databinding.LayoutItemOrderProductBinding;
import com.example.foodmanager.model.Product;
import com.example.foodmanager.model.ProductToOrder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ProductToOrderAdapter extends RecyclerView.Adapter<ProductToOrderAdapter.ViewHolderProduct> {
    private ArrayList<Product> listProduct;
    OnClickItemListener mOnClickItemListener;
    Context context;

    public interface OnClickItemListener{
        void onClickItemProduct(Product product);
    }


    public ProductToOrderAdapter(ArrayList<Product> listProduct) {
        this.listProduct = listProduct;
    }

    public ProductToOrderAdapter(ArrayList<Product> listProduct, OnClickItemListener mOnClickItemListener, Context context) {
        this.listProduct = listProduct;
        this.mOnClickItemListener = mOnClickItemListener;
        this.context = context;
    }

    public void setFilterList(ArrayList<Product> filterList) {
        this.listProduct = filterList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolderProduct onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolderProduct(LayoutItemOrderProductBinding.inflate(LayoutInflater.from(parent.getContext()),parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderProduct holder, int position) {
        Product product = listProduct.get(position);
        if (product == null) {
            return;
        } else {
            holder.initData(product, context);
        }
        holder.imgAddToOder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddToOder(product,context);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listProduct.size();
    }

    public class ViewHolderProduct extends RecyclerView.ViewHolder {
        ImageView imgProduct,imgAddToOder;
        TextView tvName, tvDescribe, tvPrice;
        ConstraintLayout layoutItem;
        public ViewHolderProduct(LayoutItemOrderProductBinding binding) {
            super(binding.getRoot());
            imgProduct = binding.imgProduct;
            tvName = binding.tvNameProduct;
            tvDescribe = binding.tvDescribeProduct;
            tvPrice = binding.tvPriceProduct;
            layoutItem = binding.layoutItem;
            imgAddToOder = binding.imgAddToOder;
        }

        void initData(Product product, Context context) {
            StorageReference reference = FirebaseStorage.getInstance().getReference().child("imgProducts");
            reference.listAll().addOnSuccessListener(listResult -> {
                for (StorageReference files: listResult.getItems()){
                    if(files.getName().equals(product.getId())){
                        files.getDownloadUrl().addOnSuccessListener(uri -> {
                            Glide.with(context).load(uri).into(imgProduct);
                        });
                    }
                }
            });
            tvName.setText(product.getNameProduct());
            Locale locale = new Locale("en","EN");
            NumberFormat numberFormat = NumberFormat.getInstance(locale);
            Double price = product.getPrice();
            String strPrice = numberFormat.format(price);
            tvPrice.setText(strPrice +"đ");
            tvDescribe.setText(product.getDescribe());
            layoutItem.setOnClickListener(v -> {
                mOnClickItemListener.onClickItemProduct(product);
            });
        }

    }
    private void AddToOder(Product product ,Context context){
            final Dialog dialog = new Dialog(context);
           DialogOrderProductBinding binding = DialogOrderProductBinding.inflate(LayoutInflater.from(context));
            dialog.setContentView(binding.getRoot());
            dialog.setCancelable(false);
            Window window = dialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            binding.tvCancel.setOnClickListener(tv ->{
                dialog.dismiss();
            });
            binding.tvAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Date date = Calendar.getInstance().getTime();
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    String strDate = dateFormat.format(date);

                    ProductToOrder product1 = new ProductToOrder(product.getId(),product.getNameProduct(),product.getPrice(),Integer.parseInt(binding.edSoluong.getText().toString()),product.getTypeProduct().getNameType(),product.getImgProduct(),product.getNote(),strDate);
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("list_oder");
                    String key = reference.push().getKey();
                    reference.child(product.getId()).setValue(product1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            dialog.dismiss();
                        }
                    });

                }

            });
            dialog.show();
        Log.e("TAG", "AddToOder: Da den day roi hehe" );
    }
}
