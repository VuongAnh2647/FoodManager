package com.example.foodmanager.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodmanager.databinding.LayoutItemOrderProductBinding;
import com.example.foodmanager.model.ProductToOrder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class ConfirmItemBillApdater extends RecyclerView.Adapter<ConfirmItemBillApdater.ViewHolder> {
    private ArrayList<ProductToOrder> list;
    Context context;
    private int soluong= 0 ;

    public ConfirmItemBillApdater(ArrayList<ProductToOrder> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConfirmItemBillApdater.ViewHolder(LayoutItemOrderProductBinding.inflate(LayoutInflater.from(parent.getContext()),parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductToOrder product = list.get(position);
        if(product!=null){
            soluong = product.getSoLuong();
            holder.initData(product,context);
            holder.btnadd.setOnClickListener(v->{
                soluong++;
                Locale locale = new Locale("en","EN");
                NumberFormat numberFormat = NumberFormat.getInstance(locale);
                Double price = product.getPrice()*soluong;
                String strPrice = numberFormat.format(price);
                holder.tvPrice.setText(strPrice +"đ");
                holder.tvSoLuong.setText(soluong+"");
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("list_oder");
                ProductToOrder product1 = new ProductToOrder(product.getId(),product.getNameProduct(),product.getPrice(),soluong,product.getTypeProduct(),product.getImgProduct(),product.getNote(),product.getTime());
                reference.child(product.getId()).setValue(product1).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });

            });
            holder.btnremove.setOnClickListener(v->{
                soluong--;

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("list_oder");
                if(soluong>0){
                    Locale locale = new Locale("en","EN");
                    NumberFormat numberFormat = NumberFormat.getInstance(locale);
                    Double price = product.getPrice()*soluong;
                    String strPrice = numberFormat.format(price);
                    holder.tvPrice.setText(strPrice +"đ");
                    holder.tvSoLuong.setText(soluong+"");
                    ProductToOrder product1 = new ProductToOrder(product.getId(),product.getNameProduct(),product.getPrice(),soluong,product.getTypeProduct(),product.getImgProduct(),product.getNote(),product.getTime());
                    reference.child(product.getId()).setValue(product1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
                }


            });
            Locale locale = new Locale("en","EN");
            NumberFormat numberFormat = NumberFormat.getInstance(locale);
            Double price = product.getPrice()*soluong;
            String strPrice = numberFormat.format(price);
            holder.tvPrice.setText(strPrice +"đ");
            holder.tvSoLuong.setText(soluong+"");
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct,imgAddToOder,btnadd,btnremove;
        TextView tvName, tvDescribe, tvPrice,tvSoLuong;
        CardView cardView ;
        ConstraintLayout layoutItem;
        public ViewHolder(LayoutItemOrderProductBinding binding) {
            super(binding.getRoot());
            imgProduct = binding.imgProduct;
            tvName = binding.tvNameProduct;
            tvDescribe = binding.tvDescribeProduct;
            tvPrice = binding.tvPriceProduct;
            layoutItem = binding.layoutItem;
            imgAddToOder = binding.imgAddToOder;
            tvSoLuong = binding.editQuantity;
            btnadd = binding.btnAugment;
            btnremove = binding.btnReduce;
            cardView = binding.cavEditQuantity;
        }
        void initData(ProductToOrder product, Context context) {

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
            imgAddToOder.setVisibility(View.GONE);
            cardView.setVisibility(View.VISIBLE);

        }

    }

}
