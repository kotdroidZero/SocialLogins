package com.kotdroid.sociallogins.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.TwitterAuthProvider;
import com.kotdroid.sociallogins.R;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

/**
 * Created by user12 on 15/3/18.
 */

public class TwitterFragment extends Fragment implements View.OnClickListener {

    TwitterLoginButton btnLogin;

    Button btnLogout;

    TextView tvName;

    SimpleDraweeView sdvProfile;

    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_twitter, container, false);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        btnLogout = view.findViewById(R.id.btnLogout);
        btnLogin = view.findViewById(R.id.login_button);
        tvName = view.findViewById(R.id.tvName);
        sdvProfile = view.findViewById(R.id.sdvProfile);


        btnLogout.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();

        btnLogin.setCallback(new Callback<TwitterSession>() {
            @Override public void success(Result<TwitterSession> result) {
                tvName.setText(String.format("%s : %d", result.data.getUserName(), result.data.getUserId()));


            }

            @Override public void failure(TwitterException exception) {
                updateUI(null);
            }
        });

    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        btnLogin.onActivityResult(requestCode, resultCode, data);
    }

    private void handleTwitterSession(TwitterSession data) {
        AuthCredential authCredential = TwitterAuthProvider.getCredential(data.getAuthToken().token, data.getAuthToken().secret);

        mAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            updateUI(null);
                        }
                    }
                });

    }

    @Override public void onStart() {
        super.onStart();

        //check is user logged in and non null
        mFirebaseUser = mAuth.getCurrentUser();
        if (null != mFirebaseUser)
            updateUI(mFirebaseUser);
    }

    private void updateUI(FirebaseUser mFirebaseUser) {
        if (null != mFirebaseUser) {
            tvName.setText(mFirebaseUser.getDisplayName());
            //sdvProfile.setImageURI(Uri.parse(mFirebaseUser.));
        } else {
            tvName.setText("name");
        }
    }

    @Override public void onClick(View view) {
        FirebaseAuth.getInstance().signOut();
    }
}
