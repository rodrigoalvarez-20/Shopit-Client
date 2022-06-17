package com.ralvarez20.shopit_client;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ralvarez20.shopit_client.adapters.CartProductAdapter;
import com.ralvarez20.shopit_client.models.GenericResponse;
import com.ralvarez20.shopit_client.models.Product;
import com.ralvarez20.shopit_client.utilities.ApiAdapter;
import com.ralvarez20.shopit_client.utilities.Common;

import org.json.JSONObject;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Cart extends Fragment {

    private ArrayList<Product> productsInCart;
    private BadgeDrawable badgeRef;
    private TextView lblTitle, lblEmpty;
    private Button btnCheckout;
    private RecyclerView rcvProducts;
    private LottieAnimationView emptyAnim, loadingIndicator;
    private Button btnDeleteCart;

    public Cart() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatActivity superAct = (AppCompatActivity) getActivity();
        if (superAct != null ){
            ActionBar actBar = superAct.getSupportActionBar();
            if (actBar != null){
                actBar.setTitle(R.string.abCart);
            }
            Paper.init(superAct.getApplicationContext());
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_cart, container, false);

        lblTitle = v.findViewById(R.id.lblCartTitle);
        lblEmpty = v.findViewById(R.id.lblEmptyCar);
        btnCheckout = v.findViewById(R.id.btnCheckout);
        rcvProducts = v.findViewById(R.id.rcvCartProds);
        emptyAnim = v.findViewById(R.id.ltEmptyCart);
        loadingIndicator = v.findViewById(R.id.ltLoadingIndicator);
        btnDeleteCart = v.findViewById(R.id.btnDeleteCart);

        AppCompatActivity parentAct = (AppCompatActivity) getActivity();

        if(parentAct != null){
            BottomNavigationView bNav = parentAct.findViewById(R.id.bottomNavBar);
            badgeRef = bNav.getOrCreateBadge(R.id.navCart);
        }

        productsInCart = Paper.book().read("cart", new ArrayList<>());

        if ( productsInCart != null && productsInCart.size() > 0){
            badgeRef.setVisible(true);
            badgeRef.setNumber(productsInCart.size());
            double totalToPay = 0;

            for(Product p : productsInCart)
                totalToPay += (p.getQuantity() * p.getPrice());

            btnCheckout.setText(getContext().getString(R.string.btnCheckout, totalToPay));

            CartProductAdapter productAdapter = new CartProductAdapter(productsInCart, getContext(), this.badgeRef, btnCheckout);
            rcvProducts.setAdapter(productAdapter);
            rcvProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        }else {
            badgeRef.setVisible(false);
            lblTitle.setVisibility(View.GONE);
            btnCheckout.setVisibility(View.GONE);
            rcvProducts.setVisibility(View.GONE);
            lblEmpty.setVisibility(View.VISIBLE);
            emptyAnim.setVisibility(View.VISIBLE);
            btnDeleteCart.setVisibility(View.GONE);
        }

        btnCheckout.setOnClickListener(ck -> {
            btnCheckout.setEnabled(false);
            if (getContext() != null){
                productsInCart = Paper.book().read("cart", new ArrayList<>());
                String tk = Common.getTokenValue(getContext().getSharedPreferences("prefs", Context.MODE_PRIVATE));
                Call<GenericResponse> checkoutCall = ApiAdapter.getApiService().makePurchase(tk, productsInCart);

                checkoutCall.enqueue(new Callback<GenericResponse>() {
                    @Override
                    public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                        if (getContext() != null){
                            if(response.isSuccessful()){
                                if(response.body() != null){
                                    Toasty.success(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    // Limpiar los productos en el carrito y refrescar el fragmento
                                    Paper.book().delete("cart");
                                    Cart.super.getActivity().recreate();

                                } else
                                    Toasty.warning(getContext(), "Ha ocurrido un error al obtener los datos", Toast.LENGTH_SHORT).show();
                            }else if(response.errorBody() != null){
                                try {
                                    JSONObject jsonErr = new JSONObject(response.errorBody().string());
                                    Toasty.warning(getContext(), jsonErr.getString("error"), Toast.LENGTH_SHORT).show();
                                }catch (Exception ex){
                                    Toasty.error(getContext(), "Ha ocurrido un error en la peticion", Toasty.LENGTH_SHORT).show();
                                }
                            }
                        }
                        btnCheckout.setEnabled(true);
                    }

                    @Override
                    public void onFailure(Call<GenericResponse> call, Throwable t) {
                        System.out.println(t.toString());
                        Toasty.error(getContext(), "Ha ocurrido un error en la peticion", Toast.LENGTH_SHORT).show();
                        btnCheckout.setVisibility(View.VISIBLE);
                        loadingIndicator.setVisibility(View.GONE);
                        btnCheckout.setEnabled(true);
                    }
                });

            }

        });

        btnDeleteCart.setOnClickListener(ck -> {
            AlertDialog.Builder dgConfirm = new AlertDialog.Builder(getContext());

            dgConfirm.setPositiveButton(getContext().getString(R.string.btnDeleteProdInCart), (dialog, id) -> {
                Paper.book().delete("cart");
                Cart.super.getActivity().recreate();

            });
            dgConfirm.setNegativeButton(getContext().getString(R.string.btnDissmisDialog), (dialog, id) -> { });
            dgConfirm.setMessage(getContext().getString(R.string.lblDialogRemoveCart));
            dgConfirm.create().show();
        });

        return v;
    }
}