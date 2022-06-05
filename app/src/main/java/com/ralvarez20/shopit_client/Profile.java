package com.ralvarez20.shopit_client;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.ralvarez20.shopit_client.adapters.PurchasesAdapter;
import com.ralvarez20.shopit_client.models.Purchase;
import com.ralvarez20.shopit_client.models.User;
import com.ralvarez20.shopit_client.utilities.ApiAdapter;
import com.ralvarez20.shopit_client.utilities.Common;

import org.json.JSONObject;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Profile extends Fragment {

    TextView lblName, lblEmail, lblPhone, lblGender;
    LinearLayout lyInfo, lyPurchases;
    RecyclerView rcvProfilePurchases;
    LottieAnimationView loadingIndicator;
    Button btnLogout;

    public Profile() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatActivity superAct = (AppCompatActivity) getActivity();
        if (superAct != null ){
            ActionBar actBar = superAct.getSupportActionBar();
            if (actBar != null){
                actBar.setTitle(R.string.abProfile);
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        lblName = v.findViewById(R.id.lblProfileName);
        lblEmail = v.findViewById(R.id.lblProfileEmail);
        lblPhone = v.findViewById(R.id.lblProfilePhone);
        lblGender = v.findViewById(R.id.lblProfileGender);
        rcvProfilePurchases = v.findViewById(R.id.rcvProfilePurchases);
        loadingIndicator = v.findViewById(R.id.profile_loading_indicator);
        lyInfo = v.findViewById(R.id.lyProfileInfo);
        lyPurchases = v.findViewById(R.id.lyProfilePurchases);
        btnLogout = v.findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(btn -> {
            Paper.book().delete("cart");
            Common.removeToken(getContext().getSharedPreferences("prefs", Context.MODE_PRIVATE));
            startActivity(new Intent(getContext(), Login.class));
            getActivity().finish();
        });

        getProfileInfo();

        return v;
    }

    private void getProfileInfo() {
        if (getActivity() != null) {
            Context ctx = getContext();
            String tk = Common.getTokenValue(getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE));
            Call<User> userCall = ApiAdapter.getApiService().getProfile(tk);
            if (ctx != null) {
                userCall.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                // Asignar valores
                                User usr = response.body();
                                lblName.setText(ctx.getString(R.string.phUserName, usr.getName(), usr.getLastName()));
                                lblEmail.setText(usr.getEmail());
                                lblPhone.setText(usr.getPhone());
                                lblGender.setText(usr.getGender().equals("M") ? "Masculino" : "Femenino");
                                lyInfo.setVisibility(View.VISIBLE);

                                ArrayList<Purchase> purchases = usr.getPurchases();

                                if (purchases != null){
                                    PurchasesAdapter pAdapter = new PurchasesAdapter(purchases, ctx);
                                    rcvProfilePurchases.setAdapter(pAdapter);
                                    rcvProfilePurchases.setLayoutManager(new LinearLayoutManager(ctx));
                                }

                                lyPurchases.setVisibility(View.VISIBLE);
                                loadingIndicator.setVisibility(View.GONE);

                            }
                        } else if (response.errorBody() != null) {
                            try {
                                JSONObject jsonErr = new JSONObject(response.errorBody().string());
                                Toasty.warning(ctx, jsonErr.getString("error"), Toast.LENGTH_SHORT).show();
                            } catch (Exception ex) {
                                Toasty.error(ctx, "Ha ocurrido un error en la peticion", Toasty.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                        System.out.println(t.getLocalizedMessage());
                        Toasty.error(ctx, "Ha ocurrido un error en la peticion", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

    }
}