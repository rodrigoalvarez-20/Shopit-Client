package com.ralvarez20.shopit_client;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.provider.MediaStore;
import android.security.identity.ResultData;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.ralvarez20.shopit_client.models.GenericResponse;
import com.ralvarez20.shopit_client.models.Product;
import com.ralvarez20.shopit_client.utilities.ApiAdapter;
import com.ralvarez20.shopit_client.utilities.Common;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddProduct extends Fragment {

    String b64Image = "";
    Button btnFileSelector, btnAddNewProduct;
    EditText txtProdName, txtProdSku, txtProdCat, txtProdPrice, txtProdStock, txtProdDesc;
    ImageView imgPreview;
    LottieAnimationView loadingIndicator;
    int SELECT_IMAGE = 200;

    public AddProduct() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_add_product, container, false);

        btnFileSelector = v.findViewById(R.id.btnOpenFile);
        btnAddNewProduct = v.findViewById(R.id.btnCreateProduct);
        txtProdName = v.findViewById(R.id.txtAddProdName);
        txtProdDesc = v.findViewById(R.id.txtAddProdDesc);
        txtProdSku = v.findViewById(R.id.txtAddProdSku);
        txtProdCat = v.findViewById(R.id.txtAddProdCat);
        txtProdPrice = v.findViewById(R.id.txtAddProdPrice);
        txtProdStock = v.findViewById(R.id.txtAddProdStock);
        imgPreview = v.findViewById(R.id.imgPrevProd);
        loadingIndicator = v.findViewById(R.id.addProd_loading_indicator);

        // Añadir el listener para el file selector
        btnFileSelector.setOnClickListener(btnV -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            //intent.putExtra(Intent.EXTRA_MIME_TYPES, "image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(Intent.createChooser(intent, getContext().getString(R.string.lblSelImage)), SELECT_IMAGE);
        });

        // Añadir el listener para la peticion

        btnAddNewProduct.setOnClickListener(btnAdd -> {
            if(validateFields()){
                Toasty.warning(getContext(), getContext().getString(R.string.errEmptyFields), Toasty.LENGTH_SHORT).show();
            }else {
                loadingIndicator.setVisibility(View.VISIBLE);
                btnAddNewProduct.setVisibility(View.GONE);
                String prodName = txtProdName.getText().toString().trim();
                String prodDesc = txtProdDesc.getText().toString().trim();
                String prodSku = txtProdSku.getText().toString().trim();
                String prodCat = txtProdCat.getText().toString().trim();
                double prodPrice = Double.parseDouble(txtProdPrice.getText().toString().trim());
                int prodStock = Integer.parseInt(txtProdStock.getText().toString().trim());
                Product prodToAdd = new Product(0, b64Image, prodSku, prodName, prodDesc, prodCat, prodStock, 0, prodPrice);
                String tk = Common.getTokenValue(getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE));
                Call<GenericResponse> addProdReq = ApiAdapter.getApiService().addNewProduct(tk, prodToAdd);

                addProdReq.enqueue(new Callback<GenericResponse>() {
                    @Override
                    public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                        if(response.isSuccessful()){
                            if(response.body() != null){
                                Toasty.success(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
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
                        btnAddNewProduct.setVisibility(View.VISIBLE);
                        loadingIndicator.setVisibility(View.GONE);
                        restoreFields();
                    }

                    @Override
                    public void onFailure(Call<GenericResponse> call, Throwable t) {
                        System.out.println(t.getLocalizedMessage());
                        Toasty.error(AddProduct.super.getContext(), "Ha ocurrido un error en la peticion", Toast.LENGTH_SHORT).show();
                        btnAddNewProduct.setVisibility(View.VISIBLE);
                        loadingIndicator.setVisibility(View.GONE);
                    }
                });
            }
        });

        return v;
    }

    private void restoreFields(){
        txtProdCat.setText("");
        txtProdName.setText("");
        txtProdPrice.setText("");
        txtProdSku.setText("");
        txtProdStock.setText("");
        txtProdDesc.setText("");
        txtProdName.requestFocus();
        b64Image = "";
        imgPreview.setImageURI(null);
    }

    private boolean validateFields(){
        return txtProdName.getText().toString().trim().isEmpty() ||
                txtProdDesc.getText().toString().trim().isEmpty() ||
                txtProdSku.getText().toString().trim().isEmpty() ||
                txtProdCat.getText().toString().trim().isEmpty() ||
                txtProdPrice.getText().toString().trim().isEmpty() ||
                txtProdStock.getText().toString().trim().isEmpty();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_IMAGE) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    imgPreview.setImageURI(selectedImageUri);
                    ContentResolver cR = getContext().getContentResolver();
                    String mimeType = cR.getType(selectedImageUri);
                    try {
                        Bitmap bitmap= MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                        ByteArrayOutputStream stream=new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
                        byte[] bytes=stream.toByteArray();
                        b64Image += "data:"+ mimeType  +";base64,";
                        b64Image += Base64.encodeToString(bytes,Base64.DEFAULT);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toasty.error(getActivity().getApplicationContext(), "Ha ocurrido un error al abrir el archivo", Toast.LENGTH_SHORT).show();
                        b64Image = "";
                    }
                }else {
                    b64Image = "";
                }
            }
        }
    }
}