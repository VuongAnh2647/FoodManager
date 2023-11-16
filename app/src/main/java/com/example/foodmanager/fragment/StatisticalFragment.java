package com.example.foodmanager.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;



import com.example.foodmanager.Adapter.ListOderAdapter;
import com.example.foodmanager.R;
import com.example.foodmanager.base.BaseFragment;
import com.example.foodmanager.databinding.DialogChooseFunctionFilterStatisticBinding;
import com.example.foodmanager.databinding.DialogChooseTimeStatisticBinding;
import com.example.foodmanager.databinding.FragmentOrderStatisticsBinding;
import com.example.foodmanager.model.Receipt;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StatisticalFragment extends BaseFragment implements ListOderAdapter.OnClickListener {
    private FragmentOrderStatisticsBinding binding = null;

    private ListOderAdapter adapter;
    private int lastSelectedYear;
    private int lastSelectedMonth;
    private int lastSelectedDayOfMonth;
    private Receipt receipt;
    private Double doanhthu = Double.valueOf(0);
    private  ArrayList<Receipt> listReceipt;
    public StatisticalFragment() {
        // Required empty public constructor
    }

    public static StatisticalFragment newInstance() {
        StatisticalFragment fragment = new StatisticalFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Calendar c = Calendar.getInstance();
        this.lastSelectedYear = c.get(Calendar.YEAR);
        this.lastSelectedMonth = c.get(Calendar.MONTH);
        this.lastSelectedDayOfMonth = c.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOrderStatisticsBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Window window = getActivity().getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(getActivity().getColor(R.color.white));
        listening();
        initObSever();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void loadData() {
       getReceipt();
        adapter= new ListOderAdapter(listReceipt,StatisticalFragment.this);
        binding.recVListOder.setAdapter(adapter);
    }

    @Override
    public void listening() {
        binding.icBack.setOnClickListener(v -> backStack());
        binding.tvFilterTime.setOnClickListener(v -> dialogFunctionPickDate(requireContext()));




    }


    @Override
    public void initObSever() {


    }

    @Override
    public void initView() {
    }






    private void dialogFunctionPickDate(Context context) {
        final Dialog dialog = new Dialog(context);
        DialogChooseTimeStatisticBinding bindingDialog = DialogChooseTimeStatisticBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(bindingDialog.getRoot());
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
        window.setAttributes(layoutParams);
        bindingDialog.dialogChooserFunction.setTranslationY(150);
        bindingDialog.dialogChooserFunction.animate().translationYBy(-150).setDuration(400);

        bindingDialog.layoutChooserTimeStart.setOnClickListener(v -> {
            DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                    bindingDialog.tvTimeStart.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                    bindingDialog.tvTimeStart.setVisibility(View.VISIBLE);
                    lastSelectedYear = year;
                    lastSelectedMonth = monthOfYear;
                    lastSelectedDayOfMonth = dayOfMonth;
                }
            };
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), dateSetListener, lastSelectedYear, lastSelectedMonth,
                    lastSelectedDayOfMonth);
            datePickerDialog.show();
        });
        bindingDialog.layoutChooserTimeEnd.setOnClickListener(tv -> {
            DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    bindingDialog.tvTimeEnd.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                    bindingDialog.tvTimeEnd.setVisibility(View.VISIBLE);
                    lastSelectedYear = year;
                    lastSelectedMonth = monthOfYear;
                    lastSelectedDayOfMonth = dayOfMonth;
                }
            };
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), dateSetListener, lastSelectedYear, lastSelectedMonth,
                    lastSelectedDayOfMonth);
            datePickerDialog.show();
        });

        bindingDialog.layoutChooseAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("TAG", "onClick: all" );
                getReceipt();
                adapter= new ListOderAdapter(listReceipt,StatisticalFragment.this);
                binding.recVListOder.setAdapter(adapter);
                binding.tvFilterTime.setText("Tất cả");
                dialog.cancel();
            }
        });

        bindingDialog.btnFilter.setOnClickListener(v -> {

            if (bindingDialog.tvTimeStart.getVisibility() == View.GONE){
                Toast.makeText(context, "Hẫy chọn ngày bắt đầu", Toast.LENGTH_SHORT).show();
            }else if(bindingDialog.tvTimeEnd.getVisibility() == View.GONE){
                Toast.makeText(context, "Hẫy chọn ngày kết thúc", Toast.LENGTH_SHORT).show();
            }else {

                   getPaidOder(bindingDialog.tvTimeStart.getText().toString(), bindingDialog.tvTimeEnd.getText().toString());
                    adapter= new ListOderAdapter(listReceipt,StatisticalFragment.this);
                    binding.recVListOder.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                binding.tvFilterTime.setText(bindingDialog.tvTimeStart.getText().toString()+ " đến " +bindingDialog.tvTimeEnd.getText().toString());
                dialog.cancel();
            }

        });

        dialog.show();
    }

    public void getPaidOder(String startDate , String endDate){
        listReceipt = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("list_bill");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(listReceipt!=null){
                    listReceipt.clear();
                }
                doanhthu=0.0;
                for(DataSnapshot dataSnapshot :snapshot.getChildren()){
                    Receipt receipt = dataSnapshot.getValue(Receipt.class);
                    String strDay = receipt.getTime().substring(0, receipt.getTime().lastIndexOf(" "));

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Date dayOder = dateFormat.parse(strDay);
                        Date start = dateFormat.parse(startDate);
                        Date end = dateFormat.parse(endDate);

                        if (start.getTime() <= dayOder.getTime() && end.getTime() >= dayOder.getTime()) {
                            doanhthu = doanhthu+receipt.getMonney();
                            listReceipt.add(receipt);
                        }
                    }catch (ParseException e){
                        e.printStackTrace();
                    }

                }
                binding.tvTotalOderValue.setText(doanhthu+"");
                binding.tvOrderNumber.setText(listReceipt.size()+"");
                adapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void getCancelOder(){

    }
    private void getReceipt(){
        listReceipt = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("list_bill");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(listReceipt!=null){
                    listReceipt.clear();
                }
                doanhthu=0.0;
                for(DataSnapshot dataSnapshot :snapshot.getChildren()){
                    Receipt receipt = dataSnapshot.getValue(Receipt.class);
                     listReceipt.add(receipt);
                    doanhthu = doanhthu+receipt.getMonney();
                }
                binding.tvTotalOderValue.setText(""+doanhthu);
                binding.tvOrderNumber.setText(""+listReceipt.size());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }





    @Override
    public void onClickListener(Receipt receipt) {
        replaceFragment(new DetailReceiptFragment(receipt));
    }
}