package com.google.firebase.quickstart.auth.productDetails;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.quickstart.auth.EmailPasswordActivity;
import com.google.firebase.quickstart.auth.Home.HomeCartActivity;
import com.google.firebase.quickstart.auth.Home.HomeMainActivity;
import com.google.firebase.quickstart.auth.R;

import static android.view.View.VISIBLE;


/**
 * Created by Harsh on 10-06-2017.
 */

public class ProductActivity extends AppCompatActivity {
    ViewPager viewPager;
    SliderimageAdapter adapter;
    private int addToCartConstant;

    public int getAddToCartConstant() {
        return addToCartConstant;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailsproduct);


        viewPager =(ViewPager) findViewById(R.id.image_slider_view);


        adapter=new SliderimageAdapter(this);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(viewPager, true);

            Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
            setSupportActionBar(myToolbar);
        final Button addToCart = (Button) findViewById(R.id.cart_product_details);
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(ProductActivity.this,
                        "Added to Cart",
                        Toast.LENGTH_SHORT).show();
                        addToCartConstant=1;
            }
        });


    }
}
