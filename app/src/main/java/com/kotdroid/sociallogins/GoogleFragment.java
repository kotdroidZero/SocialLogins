package com.kotdroid.sociallogins;

import android.content.Intent;
import android.net.Uri;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

/**
 * Created by user12 on 15/3/18.
 */

public class GoogleFragment extends Fragment implements View.OnClickListener {


    private TextView tvName;

    private SimpleDraweeView sdvProfile;

    private Button btnGoogleLogin;

    private SignInButton signInButton;

    private GoogleSignInClient mGoogleSignInClient;

    private GoogleSignInOptions mGso;

    private GoogleSignInAccount mGoogleSignInAccount;

    private static final int RC_SIGN_IN = 100;

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_facebook, container, false);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvName = view.findViewById(R.id.tvName);

        sdvProfile = view.findViewById(R.id.sdvProfile);

        view.findViewById(R.id.login_button).setVisibility(View.GONE);


        //using google's sign in button
        signInButton = view.findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setColorScheme(SignInButton.COLOR_LIGHT);
        signInButton.setOnClickListener(this);


        /**
         * here we need only public profile of user so requesting for DefaultSignIn
         * else we can specify the addition request in requestScope
         * at there "https://developers.google.com/identity/sign-in/android/additional-scopes"
         */
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        mGso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        //now create a GoogleSignIn client with the option or requirement we specified above
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), mGso);


        /** Check for existing Google Sign In account, if the user is already signed in
         the GoogleSignInAccount will be non-null.
         if(null!=mGoogleSignInAccount)
         update UI with this account
         */
        mGoogleSignInAccount = GoogleSignIn.getLastSignedInAccount(getContext());
        updateUI(mGoogleSignInAccount);


    }


    @Override public void onClick(View view) {
        if (null != mGoogleSignInAccount) {
            mGoogleSignInClient.signOut();
            updateUI(null);
            mGoogleSignInAccount = null;
        }
        Intent intentSignIn = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intentSignIn, RC_SIGN_IN);


    }

    //after signin you will get the result in onActivityResult

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount mAccount = completedTask.getResult(ApiException.class);
            updateUI(mAccount);
        } catch (ApiException e) {
            e.printStackTrace();
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount mGoogleSignInAccount) {
        if (null != mGoogleSignInAccount) {
            //btnGoogleLogin.setText(R.string.sign_out);
            tvName.setText(String.format("%s %s", mGoogleSignInAccount.getDisplayName(), mGoogleSignInAccount.getEmail()));
            sdvProfile.setImageURI(Uri.parse(mGoogleSignInAccount.getPhotoUrl().toString()));
        } else {
            //btnGoogleLogin.setText(R.string.sign_in);
            sdvProfile.setImageResource(R.drawable.ic_placeholder2);
            tvName.setText(R.string.name);
        }

    }
}