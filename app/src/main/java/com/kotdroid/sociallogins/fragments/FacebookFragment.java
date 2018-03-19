package com.kotdroid.sociallogins.fragments;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.kotdroid.sociallogins.R;

import org.json.JSONObject;

import java.net.URL;
import java.security.MessageDigest;
import java.util.Arrays;

/**
 * Created by user12 on 15/3/18.
 */


public class FacebookFragment extends Fragment implements View.OnClickListener {

    private TextView tvName;

    private SimpleDraweeView sdvProfile;

    private Button btnFacebookLogin;

    private CallbackManager mCallbackManager;

    private boolean userLoggedIn;

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable
            ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_facebook, container, false);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvName = view.findViewById(R.id.tvName);

        sdvProfile = view.findViewById(R.id.sdvProfile);

        btnFacebookLogin = view.findViewById(R.id.btnGoogleLogin);

        //initializing CallBackManager
        mCallbackManager = CallbackManager.Factory.create();

        btnFacebookLogin.setText("Login");

        printHashKey();
        btnFacebookLogin.setOnClickListener(this);
        facebookCallBackRegister();


    }

    private void facebookCallBackRegister() {
        FacebookSdk.sdkInitialize(getActivity());

        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override public void onSuccess(LoginResult loginResult) {
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override public void onCompleted(JSONObject object, GraphResponse response) {
                        getFacebookData(object);
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,birthday,devices,cover,link");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
                btnFacebookLogin.setText("login");
            }

            @Override public void onCancel() {
            }

            @Override public void onError(FacebookException error) {
            }
        });
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
            PackageInfo packageInfo = getActivity().getPackageManager().getPackageInfo("com.kotdroid.sociallogins", PackageManager.GET_SIGNATURES);
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
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override public void onClick(View view) {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email", "user_birthday", "user_friends"));
    }

    public boolean isUserLoggedIn() {
        userLoggedIn = AccessToken.getCurrentAccessToken() == null;
        return userLoggedIn;
    }


}

