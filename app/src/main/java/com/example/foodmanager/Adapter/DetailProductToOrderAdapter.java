package com.example.foodmanager.Adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodmanager.databinding.LayoutItemDetailOrderProductBinding;
import com.example.foodmanager.model.ProductToOrder;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class DetailProductToOrderAdapter extends RecyclerView.Adapter<DetailProductToOrderAdapter.ViewHolderProduct> {
    private ArrayList<ProductToOrder> listProduct;

    Context context;


    public DetailProductToOrderAdapter(ArrayList<ProductToOrder> listProduct , Context context) {
     this.listProduct= listProduct;
     this.context = context;
    }



    public void setFilterList(ArrayList<ProductToOrder> filterList) {
        this.listProduct = filterList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolderProduct onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolderProduct(LayoutItemDetailOrderProductBinding.inflate(LayoutInflater.from(parent.getContext()),parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderProduct holder, int position) {
        ProductToOrder product = listProduct.get(position);
        if (product == null) {
            return;
        } else {
            holder.initData(product, context);
        }

    }

    @Override
    public int getItemCount() {
        return listProduct.size();
    }

    public class ViewHolderProduct extends RecyclerView.ViewHolder {
        ImageView imgProduct,imgAddToOder;
        TextView tvName, tvDescribe, tvPrice,tv_soluong;
        ConstraintLayout layoutItem;
        public ViewHolderProduct(LayoutItemDetailOrderProductBinding binding) {
            super(binding.getRoot());
            imgProduct = binding.imgProduct;
            tvName = binding.tvNameProduct;
            tvDescribe = binding.tvDescribeProduct;
            tvPrice = binding.tvPriceProduct;
            layoutItem = binding.layoutItem;
            tv_soluong = binding.soluongdat;
        }

        void initData(ProductToOrder product, Context context) {
            StorageReference reference = FirebaseStorage.getInstance().getReference().child("imgProducts");
            reference.listAll().addOnSuccessListener(listResult -> {
                for (StorageReference files: listResult.getItems()){
                    if(files.getName().equals(product.getId())){
                        files.getDownloadUrl().addOnSuccessListener(uri -> {
                            Log.e("Load anh", "initData: " );
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
            tvDescribe.setText(product.getNote());
            tv_soluong.setText(product.getSoLuong()+"");

        }

    }

}
