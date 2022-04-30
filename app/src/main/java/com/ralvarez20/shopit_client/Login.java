package com.ralvarez20.shopit_client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.ralvarez20.shopit_client.models.GenericResponse;
import com.ralvarez20.shopit_client.utilities.ApiAdapter;
import com.ralvarez20.shopit_client.utilities.Common;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    EditText txtEmail, txtPassword;
    Button btnLogin;
    LottieAnimationView ltIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Verificar si hay token

        if(Common.verifySavedToken(this.getSharedPreferences("prefs", MODE_PRIVATE))){
            Intent homeIntent = new Intent(this, MainActivity.class);
            startActivity(homeIntent);
            finish();
        }

        ActionBar actBar = getSupportActionBar();
        if (actBar != null){
            actBar.setTitle(R.string.abLogin);
        }

        Button btnSendRegister = (Button) findViewById(R.id.btnSendRegister);

        txtEmail = findViewById(R.id.txtLogEmail);
        txtPassword = findViewById(R.id.txtLogPwd);
        ltIndicator = findViewById(R.id.login_loading_indicator);

        btnLogin = findViewById(R.id.btnLogin);

        btnSendRegister.setOnClickListener(v -> startActivity(new Intent(this,Register.class)));

        btnLogin.setOnClickListener(v -> {
            String email = txtEmail.getText().toString().trim();
            String pwd = txtPassword.getText().toString().trim();

            if(email.isEmpty() || pwd.isEmpty()){
                Toasty.warning(this, getResources().getString(R.string.errEmptyFields), Toast.LENGTH_SHORT).show();
            }else{
                btnLogin.setVisibility(View.GONE);
                ltIndicator.setVisibility(View.VISIBLE);
                Map<String, String> loginBody = new HashMap<>();
                loginBody.put("email", email);
                loginBody.put("password", pwd);

                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),(new JSONObject(loginBody)).toString());
                Call<GenericResponse> loginResp = ApiAdapter.getApiService().loginUser(body);

                loginResp.enqueue(new Callback<GenericResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                        if(response.isSuccessful()){
                            if(response.body() != null){
                                Toasty.success(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                // Guardar la token y cambiar el activity
                                SharedPreferences shPrefs = Login.super.getSharedPreferences("prefs", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = shPrefs.edit();
                                editor.putString("token", response.body().getToken());
                                editor.apply();
                                Intent homeIntent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(homeIntent);
                                finish();
                            } else
                                Toasty.warning(getApplicationContext(), "Ha ocurrido un error al obtener los datos", Toast.LENGTH_SHORT).show();
                        }else if(response.errorBody() != null){
                            try {
                                JSONObject jsonErr = new JSONObject(response.errorBody().string());
                                Toasty.warning(getApplicationContext(), jsonErr.getString("error"), Toast.LENGTH_SHORT).show();
                            }catch (Exception ex){
                                Toasty.error(getApplicationContext(), "Ha ocurrido un error en la peticion", Toasty.LENGTH_SHORT).show();
                            }
                        }
                        btnLogin.setVisibility(View.VISIBLE);
                        ltIndicator.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                        System.out.println(t.toString());
                        Toasty.error(getApplicationContext(), "Ha ocurrido un error en la peticion", Toast.LENGTH_SHORT).show();
                        btnLogin.setVisibility(View.VISIBLE);
                        ltIndicator.setVisibility(View.GONE);
                    }
                });

            }
        });

    }

}