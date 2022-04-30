package com.ralvarez20.shopit_client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.ralvarez20.shopit_client.models.GenericResponse;
import com.ralvarez20.shopit_client.utilities.ApiAdapter;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {
    EditText txtName, txtLastName, txtEmail, txtPwd, txtPhone;
    Spinner spGender;
    Button btnRegister;
    LottieAnimationView loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ActionBar actBar = getSupportActionBar();

        if (actBar != null) {
            actBar.setDisplayHomeAsUpEnabled(true);
            actBar.setDisplayShowHomeEnabled(true);
            actBar.setTitle(R.string.abRegister);
        }

        txtName = findViewById(R.id.txtRegName);
        txtLastName = findViewById(R.id.txtRegLastName);
        txtEmail = findViewById(R.id.txtRegEmail);
        txtPwd = findViewById(R.id.txtRegPwd);
        txtPhone = findViewById(R.id.txtRegPhone);
        spGender = findViewById(R.id.spRegGender);
        spGender.setSelection(0);
        btnRegister = findViewById(R.id.btnRegister);
        loadingIndicator = findViewById(R.id.register_loading_indicator);

        btnRegister.setOnClickListener(v -> registerUser());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.onBackPressed();
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validateFields(){
        return txtName.getText().toString().trim().isEmpty() ||
                txtLastName.getText().toString().trim().isEmpty() ||
                txtEmail.getText().toString().trim().isEmpty() ||
                txtPwd.getText().toString().trim().isEmpty() ||
                txtPhone.getText().toString().trim().isEmpty();
    }

    private void registerUser(){
        if (validateFields()){
            Toasty.warning(this, getString(R.string.errEmptyFields), Toasty.LENGTH_SHORT).show();
        }else {
            btnRegister.setVisibility(View.GONE);
            loadingIndicator.setVisibility(View.VISIBLE);
            Map<String, String> registerBody = new HashMap<>();
            registerBody.put("name", txtName.getText().toString().trim());
            registerBody.put("last_name", txtLastName.getText().toString().trim());
            registerBody.put("email", txtEmail.getText().toString().trim());
            registerBody.put("password", txtPwd.getText().toString().trim());
            registerBody.put("phone", txtPhone.getText().toString().trim());
            registerBody.put("gender", spGender.getSelectedItemPosition() == 0 ? "M" : "F");

            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),(new JSONObject(registerBody)).toString());
            Call<GenericResponse> registerResp = ApiAdapter.getApiService().registerUser(body);

            registerResp.enqueue(new Callback<GenericResponse>() {
                @Override
                public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                    if(response.isSuccessful()) {
                        if (response.body() != null) {
                            Toasty.success(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            new Handler().postDelayed((Runnable) () -> onBackPressed(), 3000);
                        }else{
                            Toasty.error(getApplicationContext(), "Ha ocurrido un error al registrar el usuario", Toasty.LENGTH_SHORT).show();
                        }
                    }else {
                        if(response.errorBody() != null){
                            try {
                                JSONObject jsonErr = new JSONObject(response.errorBody().string());
                                Toasty.warning(getApplicationContext(), jsonErr.getString("error"), Toast.LENGTH_SHORT).show();
                            }catch (Exception ex){
                                Toasty.error(getApplicationContext(), "Ha ocurrido un error en la peticion", Toasty.LENGTH_SHORT).show();
                            }
                        }
                    }
                    btnRegister.setVisibility(View.VISIBLE);
                    loadingIndicator.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                    System.out.println(t.getMessage());
                    Toasty.error(getApplicationContext(), "Ha ocurrido un error en la peticion", Toast.LENGTH_SHORT).show();
                    btnRegister.setVisibility(View.VISIBLE);
                    loadingIndicator.setVisibility(View.GONE);
                }
            });
        }
    }

}