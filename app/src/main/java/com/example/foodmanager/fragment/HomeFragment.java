package com.example.foodmanager.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import com.example.foodmanager.Adapter.TableAdapter;
import com.example.foodmanager.R;

import com.example.foodmanager.base.BaseFragment;
import com.example.foodmanager.base.OnclickOptionMenu;
import com.example.foodmanager.databinding.FragmentHomeBinding;

import com.example.foodmanager.model.Receipt;
import com.example.foodmanager.model.Table;
import com.example.foodmanager.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class HomeFragment extends BaseFragment implements OnclickOptionMenu {
   private FragmentHomeBinding binding;
    private User user;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private Table table;
    private List<Table> listTable ;
    private FirebaseDatabase database;
    private TableAdapter adapter ;
    private Receipt receipt;
    private Double doanhthu = Double.valueOf(0);
    private  ArrayList<Receipt> listReceipt;
    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater,container,false);

        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(ListOrderFragment.newInstance());
            }
        }

        );
        binding.btnProduct.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e("replace", "onClick: " );
                        replaceFragment(ProductFragment.newInstance());
                    }
                }
        );
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        StorageReference reference = FirebaseStorage.getInstance().getReference().child("avatars");
        reference.listAll().addOnSuccessListener(listResult -> {
            for (StorageReference files: listResult.getItems()
            ) {
                if (files.getName().equals(firebaseUser.getUid())){
                    files.getDownloadUrl().addOnSuccessListener(uri -> {
                        if(getActivity() != null){
                            Glide.with(getActivity()).load(uri).into(binding.icUserSetting);
                        }

                    });
                }
            }
        }).addOnFailureListener(e -> {

        });
        listening();
        initObSever();
    }

    @Override
    public void loadData() {
        user = new User();
        firebaseUser = firebaseAuth.getCurrentUser();
        String userID = firebaseUser.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                if (firebaseUser != null) {
                    binding.tvName.setText(user.getName_user());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        Date toDay = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String strToday = dateFormat.format(toDay);
        getAllTable();
        getReceipt(strToday);

    }

    @Override
    public void listening() {
        binding.icCloseSlide.setOnClickListener(ic ->{
            binding.layoutSlide.setVisibility(View.GONE);

        });
        binding.tvShowDetailsTurnover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(DailySalesReportFragment.newInstance());
            }
        });
        selectTabFragment();
        binding.layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment( ListOrderFragment.newInstance());
            }
        });
        binding.layoutFunction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment( ProductFragment.newInstance());
            }
        });
        binding.doanhThu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment( StatisticalFragment.newInstance());
            }
        });
    }

    @Override
    public void initObSever() {

    }

    @Override
    public void initView() {

        binding.tvTitleAll.setBackgroundColor(getContext().getColor(R.color.red_100));
        getAllTable();
    }



    private void selectTabFragment(){

        binding.btnAllTable.setOnClickListener(btn ->{
            changeBgColorTextView(binding.tvTitleAll,getContext().getColor(R.color.red_100));
            changeBgColorTextView(binding.tvTitleEmpty,getContext().getColor(R.color.grey_55));
            changeBgColorTextView(binding.tvTitleOpen,getContext().getColor(R.color.grey_55));
            getAllTable();
        });
        binding.btnTableEmpty.setOnClickListener(btn ->{
            changeBgColorTextView(binding.tvTitleAll,getContext().getColor(R.color.grey_55));
            changeBgColorTextView(binding.tvTitleEmpty,getContext().getColor(R.color.red_100));
            changeBgColorTextView(binding.tvTitleOpen,getContext().getColor(R.color.grey_55));

        });

        binding.btnTableOpen.setOnClickListener(btn ->{
            changeBgColorTextView(binding.tvTitleAll,getContext().getColor(R.color.grey_55));
            changeBgColorTextView(binding.tvTitleEmpty,getContext().getColor(R.color.grey_55));
            changeBgColorTextView(binding.tvTitleOpen,getContext().getColor(R.color.red_100));


        });
    }


    private void changeBgColorTextView( TextView tv ,int idColor){
        tv.setBackgroundColor(idColor);
    }

    private void getTable(String statusTable){
        database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("tables");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                listTable.clear();
                for (DataSnapshot snapshot1: snapshot.getChildren()) {
                    Table table = snapshot1.getValue(Table.class);
                    if(table.isHidden() && table.getStatus().equals(statusTable)){
                        listTable.add(table);
                    }

                }
                Log.e("get1", " DAy la cai minh can"+listTable.get(0).getNameTable() );
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        adapter = new TableAdapter(listTable, HomeFragment.this,getContext());
//        binding.revListTable.setAdapter(adapter);

    }
    private void getAllTable(){

        listTable = new ArrayList<>();
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("tables");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(listTable!=null){
                    listTable.clear();
                }

                for (DataSnapshot snapshot1: snapshot.getChildren()) {
                    Table table = snapshot1.getValue(Table.class);
                    Log.e("ABC", "onDataChange: "+table.getNameTable() );
                        listTable.add(table);
                }
                Log.e("ABCD", "onDataChange: "+listTable.size());
                adapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapter = new TableAdapter(listTable,HomeFragment.this,getContext());
        binding.revListTable.setAdapter(adapter);

    }

    private void getReceipt(String date){
        listReceipt = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("list_bill");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(listReceipt!=null){
                    listReceipt.clear();
                }
                doanhthu = 0.0;
                for(DataSnapshot dataSnapshot :snapshot.getChildren()){
                    Receipt receipt = dataSnapshot.getValue(Receipt.class);
                    String strDay = receipt.getTime().substring(0, receipt.getTime().lastIndexOf(" "));
                    Log.e("Dayoer", "onDataChange: "+strDay );
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Date dayOder = dateFormat.parse(strDay);
                        Date toDay = dateFormat.parse(date);
                        if (toDay.getTime() == dayOder.getTime()) {
                           doanhthu= doanhthu+receipt.getMonney();
                            listReceipt.add(receipt);
                        }
                    }catch (ParseException e){
                        e.printStackTrace();
                    }



                }
                binding.tvTotalMoneyToDay.setText(""+doanhthu);
                binding.tvOderNew.setText(""+listReceipt.size());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




    @Override
    public void onClick(Table table) {
        replaceFragment(AddProductToOrderFragment.newInstance());
    }



}