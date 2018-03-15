package com.kotdroid.sociallogins;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.net.URL;
import java.security.MessageDigest;
import java.util.Arrays;

public class FaceBookActivity extends AppCompatActivity {

    private TextView tvName;

    private SimpleDraweeView sdvProfile;

    private LoginButton btnFacebookLogin;

    private CallbackManager mCallbackManager;

    private static final String EMAIL = "email";

    private static final String TAG = "Facebook";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.fragment_facebook);


        tvName = findViewById(R.id.tvName);

        sdvProfile = findViewById(R.id.sdvProfile);

        mCallbackManager = CallbackManager.Factory.create();
        btnFacebookLogin = findViewById(R.id.login_button);
        btnFacebookLogin.setReadPermissions(Arrays.asList("public_profile", "email",
                "user_birthday", "user_friends"));

        btnFacebookLogin.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override public void onSuccess(LoginResult loginResult) {
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken()
                        , new GraphRequest.GraphJSONObjectCallback() {
                            @Override public void onCompleted(JSONObject object, GraphResponse response) {
                                Bundle bundle = getFacebookData(object);
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,birthday,devices,cover,link");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override public void onCancel() {

            }

            @Override public void onError(FacebookException error) {

            }
        });
        printHashKey();

    }

    private Bundle getFacebookData(JSONObject object) {
        try {
            URL profilePicture = new URL("https://graph.facebook.com/" + object.getString("id") + "/picture?width=250&height=250");
            sdvProfile.setImageURI(Uri.parse(profilePicture.toString()));
            tvName.setText(object.getString("name"));
        } catch (Exception e) {

        }
        return null;
    }

    private void printHashKey() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo("com.kotdroid.sociallogins", PackageManager.GET_SIGNATURES);
            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
