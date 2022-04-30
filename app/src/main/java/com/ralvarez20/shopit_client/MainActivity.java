package com.ralvarez20.shopit_client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.ralvarez20.shopit_client.models.Product;

import java.util.ArrayList;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    BottomNavigationView bottomNavBar;
    private final Home homeFrag = new Home();
    private final Profile profileFrag = new Profile();
    private final AddProduct addProdFrag = new AddProduct();
    private final Cart cartFrag = new Cart();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavBar = findViewById(R.id.bottomNavBar);

        bottomNavBar.setOnItemSelectedListener(this);
        bottomNavBar.setSelectedItemId(R.id.navHome);

        Paper.init(getApplicationContext());

        //sPaper.book().delete("cart");

        ArrayList<Product> savedProducts;

        savedProducts = Paper.book().read("cart", new ArrayList<>());

        BadgeDrawable cartBadge = bottomNavBar.getOrCreateBadge(R.id.navCart);
        if(savedProducts != null && savedProducts.size() > 0){
            cartBadge.setVisible(true);
            cartBadge.setNumber(savedProducts.size());
        }else{
            cartBadge.setVisible(false);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.navHome:
                getSupportFragmentManager().beginTransaction().replace(R.id.lyContent, homeFrag).commit();
                return true;
            case R.id.navProfile:
                getSupportFragmentManager().beginTransaction().replace(R.id.lyContent, profileFrag).commit();
                return true;
            case R.id.navCart:
                getSupportFragmentManager().beginTransaction().replace(R.id.lyContent, cartFrag).commit();
                return true;
            case R.id.navAddProd:
                getSupportFragmentManager().beginTransaction().replace(R.id.lyContent, addProdFrag).commit();
                return true;
        }

        return false;
    }
}