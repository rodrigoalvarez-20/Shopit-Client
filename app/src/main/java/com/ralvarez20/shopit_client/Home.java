package com.ralvarez20.shopit_client;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ralvarez20.shopit_client.adapters.ProductAdapter;
import com.ralvarez20.shopit_client.models.Product;
import com.ralvarez20.shopit_client.utilities.ApiAdapter;
import com.ralvarez20.shopit_client.utilities.Common;

import org.json.JSONObject;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends Fragment {
    private RecyclerView rcvProducts;
    private LottieAnimationView loadingIndicator;
    private BadgeDrawable badgeRef;
    private EditText txtSearchQuery;
    private ImageButton btnSearch;
    private LinearLayout lySearch;

    public Home() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatActivity superAct = (AppCompatActivity) getActivity();
        if (superAct != null ){
            ActionBar actBar = superAct.getSupportActionBar();
            if (actBar != null){
                actBar.setTitle(R.string.abHome);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_home, container, false);

        rcvProducts = layout.findViewById(R.id.rvProducts);
        loadingIndicator = layout.findViewById(R.id.ltEmptyCart);
        txtSearchQuery = layout.findViewById(R.id.txtSearchQuery);
        btnSearch = layout.findViewById(R.id.btnSearch);
        lySearch = layout.findViewById(R.id.lySearchBar);

        AppCompatActivity parentAct = (AppCompatActivity) getActivity();

        if(parentAct != null){
            BottomNavigationView bNav = parentAct.findViewById(R.id.bottomNavBar);
            badgeRef = bNav.getOrCreateBadge(R.id.navCart);
        }

        btnSearch.setOnClickListener(v -> {
            System.out.println("Clicked");
            rcvProducts.setVisibility(View.GONE);
            loadingIndicator.setVisibility(View.VISIBLE);
            fetchProducts();
        });

        fetchProducts();

        return layout;
    }

    private void fetchProducts(){
        if (getContext() != null) {
            String token = Common.getTokenValue(getContext().getSharedPreferences("prefs", Context.MODE_PRIVATE));
            String queryValue = "";
            if (!txtSearchQuery.getText().toString().trim().isEmpty()){
                queryValue = txtSearchQuery.getText().toString().trim();
            }
            System.out.println(queryValue);
            Call<ArrayList<Product>> productsCall = ApiAdapter.getApiService().getProducts(token, queryValue);

            productsCall.enqueue(new Callback<ArrayList<Product>>() {
                @Override
                public void onResponse(@NonNull Call<ArrayList<Product>> call, @NonNull Response<ArrayList<Product>> response) {
                    if(response.isSuccessful()){
                        ArrayList<Product> products = response.body();
                        // Recycler View
                        setContentInAdapter(products);
                    }else if(response.errorBody() != null){
                        try {
                            JSONObject jsonErr = new JSONObject(response.errorBody().string());
                            Toasty.warning(Home.super.getContext(), jsonErr.getString("error"), Toast.LENGTH_SHORT).show();
                        }catch (Exception ex){
                            Toasty.error(Home.super.getContext(), "Ha ocurrido un error en la peticion", Toasty.LENGTH_SHORT).show();
                        }
                    }
                    rcvProducts.setVisibility(View.VISIBLE);
                    loadingIndicator.setVisibility(View.GONE);
                    lySearch.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFailure(@NonNull Call<ArrayList<Product>> call, @NonNull Throwable t) {
                    System.out.println(t.getLocalizedMessage());
                    Toasty.error(Home.super.getContext(), "Ha ocurrido un error en la peticion", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void setContentInAdapter(ArrayList<Product> products){
        ProductAdapter adapter = new ProductAdapter(products, Home.super.getContext(), badgeRef);
        rcvProducts.setAdapter(adapter);
        rcvProducts.setLayoutManager(new LinearLayoutManager(Home.super.getContext()));
    }


}