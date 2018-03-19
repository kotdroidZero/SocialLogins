package com.kotdroid.sociallogins.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kotdroid.sociallogins.R;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.AccessToken;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by user12 on 15/3/18.
 */

public class LinkedinFragment extends Fragment implements View.OnClickListener {
    private SimpleDraweeView sdvProfile;

    private TextView tvName;

    ImageView ivLogin;

    private Button btnLogout;

    private AccessToken accessToken;

    String TAG = "LinkedinFrag";
    private static final String topCardUrl = "https://api.linkedin.com/v1/people/~:(first-name," +
            "last-name,email-address,formatted-name,phone-numbers,public-profile-url,picture-url," +
            "picture-urls::(original))";

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable
            ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_linkedin, container, false);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivLogin = view.findViewById(R.id.ivLinkedinLogin);

        tvName = view.findViewById(R.id.tvName);

        sdvProfile = view.findViewById(R.id.sdvProfile);

        btnLogout = view.findViewById(R.id.btnLogout);

        ivLogin.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        btnLogout.setVisibility(View.GONE);
    }

    @Override public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogout:
                logoutUser();
                break;
            case R.id.ivLinkedinLogin:
                handleLogin();
                break;
        }
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LISessionManager.getInstance(getActivity()).onActivityResult(getActivity(), requestCode, resultCode, data);
    }

    private void handleLogin() {
        LISessionManager.getInstance(getActivity()).init(getActivity(), buildScope(), new AuthListener() {
            @Override public void onAuthSuccess() {
                btnLogout.setVisibility(View.VISIBLE);
                ivLogin.setVisibility(View.GONE);
                getUSerProfile();
            }

            @Override public void onAuthError(LIAuthError error) {
                Log.e("error", error.toString());
            }
        }, true);
    }

    private void getUSerProfile() {
        APIHelper apiHelper = new APIHelper();
        apiHelper.getRequest(getActivity(), topCardUrl, new ApiListener() {
            @Override public void onApiSuccess(ApiResponse apiResponse) {
                JSONObject jsonObject = apiResponse.getResponseDataAsJson();
                try {
                    String fullName = jsonObject.getString("formattedName");
                    String email = jsonObject.getString("emailAddress");
                    String image = jsonObject.getString("pictureUrl");

                    StringBuffer stringBuffer = new StringBuffer(fullName);
                    stringBuffer.append("\n");
                    stringBuffer.append(email);
                    tvName.setText(stringBuffer.toString());
                    sdvProfile.setImageURI(Uri.parse(image));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override public void onApiError(LIApiError LIApiError) {

            }
        });
    }

    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.W_SHARE, Scope.R_EMAILADDRESS);
    }

    private void logoutUser() {
        tvName.setText("name");
        sdvProfile.setImageResource(R.drawable.ic_placeholder2);
        LISessionManager.getInstance(getActivity()).clearSession();
        ivLogin.setVisibility(View.VISIBLE);
        btnLogout.setVisibility(View.GONE);
    }

}
