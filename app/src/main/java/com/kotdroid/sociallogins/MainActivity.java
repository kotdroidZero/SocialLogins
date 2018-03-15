package com.kotdroid.sociallogins;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnInsta, btnGoogle, btnFacebook, btnLinkedin, btnTwitter;

    TextView tvName;

    ImageView ivProfile;
    
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);

        initViews();

        registeringListeners();
    }

    private void registeringListeners() {

        btnFacebook.setOnClickListener(this);
        btnInsta.setOnClickListener(this);
        btnGoogle.setOnClickListener(this);
        btnLinkedin.setOnClickListener(this);
        btnTwitter.setOnClickListener(this);
        
        frameLayout=findViewById(R.id.flConatinerMain);
    }

    private void initViews() {
        btnFacebook = findViewById(R.id.btnFacebook);
        btnInsta = findViewById(R.id.btnInsta);
        btnGoogle = findViewById(R.id.btnGoogle);
        btnLinkedin = findViewById(R.id.btnLinkedin);
        btnTwitter = findViewById(R.id.btnTwitter);

        tvName = findViewById(R.id.tvName);
        ivProfile = findViewById(R.id.sdvProfile);
    }

    @Override public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnFacebook:
                frameLayout.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().add(R.id.flConatinerMain,new FacebookFragment()).addToBackStack(null).commit();
                break;
            case R.id.btnInsta:
                frameLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.btnGoogle:
                frameLayout.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().add(R.id.flConatinerMain,new GoogleFragment()).addToBackStack(null).commit();
                break;
            case R.id.btnLinkedin:
                frameLayout.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().add(R.id.flConatinerMain,new LinkedinFragment()).addToBackStack(null).commit();
                break;
            case R.id.btnTwitter:
                frameLayout.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().add(R.id.flConatinerMain,new TwitterFragment()).addToBackStack(null).commit();
                break;
        }
    }

}
