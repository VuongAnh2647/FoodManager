package com.example.foodmanager.fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;

import com.example.foodmanager.Adapter.ProductToOrderAdapter;
import com.example.foodmanager.base.BaseFragment;
import com.example.foodmanager.databinding.FragmentAddProductToOrderBinding;
import com.example.foodmanager.model.Product;
import com.example.foodmanager.model.ProductToOrder;
import com.example.foodmanager.model.Receipt;
import com.example.foodmanager.model.TypeProduct;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class AddProductToOrderFragment extends BaseFragment implements ProductToOrderAdapter.OnClickItemListener {
    private FragmentAddProductToOrderBinding binding = null;
    private ArrayList<Product> listProduct;
    public ProductToOrderAdapter productAdapter = null;
    private TypeProduct typeProduct;
    private ProductToOrder productToOder;
    private ArrayList<ProductToOrder> toOderArrayList;
    private Receipt receipt;

    public AddProductToOrderFragment() {
        // Required empty public constructor
    }

    public static AddProductToOrderFragment newInstance() {
        AddProductToOrderFragment fragment = new AddProductToOrderFragment();
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
        // Inflate the layout for this fragment
        binding = FragmentAddProductToOrderBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listProduct = new ArrayList<>();
        getProduct();
//        SaveOder();
        binding.icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backStack();
            }
        });
        binding.btnAddOder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Abc", "onClick: DA den day" );
                SaveBill();
            }
        });
        binding.btnReselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClearOder();
            }
        });
        binding.searchViewProduct.clearFocus();
        binding.searchViewProduct.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText, listProduct);
                return true;
            }
        });
    }

    @Override
    public void loadData() {
    }
    @Override
    public void listening() {


    }
    @Override
    public void initObSever() {
    }
    @Override
    public void initView() {
    }
    private void getProduct() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("list_product");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listProduct.clear();
                for (DataSnapshot datasnapshot : snapshot.getChildren()) {
                    Product product = datasnapshot.getValue(Product.class);
                    Log.e("Product", "onDataChange: "+product.getNameProduct() );
                       if(product.isHidden()) listProduct.add(product);
//                    Log.e("TAG", "onDataChange: "+listProduct.get(0).getNameProduct() );
                }
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });
        productAdapter = new ProductToOrderAdapter(listProduct, AddProductToOrderFragment.this,getActivity());
        binding.listProductToOder.setAdapter(productAdapter);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Product product = snapshot.getValue(Product.class);
                if (product == null || listProduct == null || listProduct.isEmpty()) {
                    return;
                }
                for (int i = 0; i < listProduct.size(); i++) {
                    if (product.getId() == listProduct.get(i).getId()) {
                        listProduct.remove(listProduct.get(i));
                        break;
                    }
                }
                productAdapter.notifyDataSetChanged();
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    public void backStack() {
        getParentFragmentManager().popBackStack();
    }


    @Override
    public void onClickItemProduct(Product product ) {

    }
    private void SaveBill(){


        toOderArrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("list_oder");
        reference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(toOderArrayList!=null){
                    toOderArrayList.clear();
                }
                for(DataSnapshot dataSnapshot : task.getResult().getChildren()){

                    ProductToOrder productToOder = dataSnapshot.getValue(ProductToOrder.class);
                    toOderArrayList.add(productToOder);


                }
                if(toOderArrayList.size()!=0){
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("list_bill");
                    String key = reference1.push().getKey();
                    Date date = Calendar.getInstance().getTime();
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    String strDate = dateFormat.format(date);
                    Double monney  = Double.valueOf(0);
                    for (int i = 0;i<toOderArrayList.size();i++){
                       monney = monney+(toOderArrayList.get(i).getPrice()*toOderArrayList.get(i).getSoLuong());
                        Log.e("TAG", "onComplete:  MONNEY "+monney );
                    }
                    receipt = new Receipt(key,toOderArrayList,monney,strDate);
                    reference1.child(key).setValue(receipt, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            Log.e("TAG", "onComplete: them vao bill " );
                            reference.removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                    backStack();

                                }
                            });
                        }
                    });
                }
            }
        });
        //replaceFragment(new DetailReceiptFragment(receipt));
        //replaceFragment( StatisticalFragment.newInstance());

    }
    private void ClearOder() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("list_oder");
        ref.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

            }
        });

    }
    private void filterList(String text, ArrayList<Product> listProduct) {
        ArrayList<Product> filterLists = new ArrayList<>();
        for (Product product : listProduct) {
            if (product.getNameProduct().toLowerCase().contains(text.toLowerCase())) {
                filterLists.add(product);
            }
        }
        if (!filterLists.isEmpty()) {
            productAdapter.setFilterList(filterLists);

        }
    }

}