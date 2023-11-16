package com.example.foodmanager.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.foodmanager.Adapter.DetailProductToOrderAdapter;
import com.example.foodmanager.base.BaseFragment;
import com.example.foodmanager.databinding.FragmentOrderDetailsBinding;
import com.example.foodmanager.model.Receipt;

import java.util.ArrayList;

public class DetailReceiptFragment extends BaseFragment {
    private FragmentOrderDetailsBinding binding = null;

    private Receipt receiptModel;
    private  ArrayList<String> listIdProduct;
    private DetailProductToOrderAdapter adapter;

    public DetailReceiptFragment(Receipt receipt) {
        this.receiptModel = receipt;
    }

    public DetailReceiptFragment() {
    }

    public DetailReceiptFragment newInstance() {
        return new DetailReceiptFragment(receiptModel);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOrderDetailsBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listening();
        initObSever();
    }

    @Override
    public void loadData() {
        adapter = new DetailProductToOrderAdapter(receiptModel.getListPoduct(),getContext());
        binding.listProductOder.setAdapter(adapter);
        binding.tvTotalAmount.setText(receiptModel.getMonney()+" vnđ");
        binding.tvTotalAmount2.setText(receiptModel.getMonney()+"vnđ");
        binding.tvTime.setText(receiptModel.getTime());
        binding.tvTotalAmount3.setText(receiptModel.getMonney()+" vnđ");
        binding.tvNameBill.setText("POLY000"+receiptModel.getId().substring(16,20));



    }

    @Override
    public void listening() {
        binding.icBack.setOnClickListener(v->{
            backStack();
        });


    }

    @Override
    public void initObSever() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public void initView() {

    }




}