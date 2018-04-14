package ca.mcgill.ecse321.treeple;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;

import ca.mcgill.ecse321.treeple.utils.PasswordHash;
import ca.mcgill.ecse321.treeple.utils.VolleyController;

public class ResetPasswordActivity extends AppCompatActivity {

    private static final String TAG = ResetPasswordActivity.class.getSimpleName();

    private EditText mUserView;
    private EditText mOldPassView;
    private EditText mNewPassView;
    private EditText mNewPassReenterView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mUserView = findViewById(R.id.user);
        mOldPassView = findViewById(R.id.old_password);
        mNewPassView = findViewById(R.id.new_password);
        mNewPassReenterView = findViewById(R.id.new_pass_reentry);

        Button resetPassButton = findViewById(R.id.reset_password_button);
        resetPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptReset();
            }
        });

        Button backToSignInButton = findViewById(R.id.backto_signin_button);
        backToSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToSignIn();
            }
        });
    }

    public void switchToSignIn() {
        Intent signInIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(signInIntent);
        finish();
    }

    private void attemptReset() {

        mUserView.setError(null);
        mOldPassView.setError(null);
        mNewPassView.setError(null);
        mNewPassReenterView.setError(null);

        String user = mUserView.getText().toString();
        String oldPass = mOldPassView.getText().toString();
        String newPass = mNewPassView.getText().toString();
        String newPassReenter = mNewPassReenterView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(user)) {
            mUserView.setError(getString(R.string.error_field_required));
            focusView = mUserView;
            cancel = true;
        } else if (!isUsernameValid(user)) {
            mUserView.setError(getString(R.string.error_invalid_user));
            focusView = mUserView;
            cancel = true;
        }

        if (TextUtils.isEmpty(oldPass)) {
            mOldPassView.setError(getString(R.string.error_field_required));
            focusView = mOldPassView;
            cancel = true;
        }

        if (TextUtils.isEmpty(newPass)) {
            mNewPassView.setError(getString(R.string.error_field_required));
            focusView = mNewPassView;
            cancel = true;
        } else if (!isPasswordValid(newPass)) {
            mNewPassView.setError(getString(R.string.error_invalid_password));
            focusView = mNewPassView;
            cancel = true;
        }

        if (TextUtils.isEmpty(newPassReenter)) {
            mNewPassReenterView.setError(getString(R.string.error_field_required));
            focusView = mNewPassReenterView;
            cancel = true;
        } else if (!newPass.equals(newPassReenter)) {
            mNewPassReenterView.setError(getString(R.string.error_not_matching));
            focusView = mNewPassReenterView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            resetPassword(user, oldPass, newPass);
        }
    }

    private boolean isUsernameValid(String user) {
        return user.length() > 1;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    private void resetPassword(String user, String oldPass, String newPass) {

        JSONObject updatedUser = new JSONObject();
        try {
            updatedUser.put("username", user);
            updatedUser.put("oldPass", PasswordHash.generatePasswordHash(oldPass));
            updatedUser.put("newPass", PasswordHash.generatePasswordHash(newPass));
        } catch (JSONException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        JsonObjectRequest updateReq = new JsonObjectRequest(Request.Method.PATCH, VolleyController.DEFAULT_BASE_URL + "user/update/password/", updatedUser, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e(TAG, "ResetResponse: " + response.toString());
                Toast.makeText(getApplicationContext(), "Password reset", Toast.LENGTH_SHORT).show();
                switchToSignIn();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "ResetError: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Error resetting password", Toast.LENGTH_LONG).show();
            }
        });

        VolleyController.getInstance(getApplicationContext()).addToRequestQueue(updateReq);
    }
}
