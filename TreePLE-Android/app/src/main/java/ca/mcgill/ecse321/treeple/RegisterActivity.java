package ca.mcgill.ecse321.treeple;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private UserLoginTask mAuthTask = null;
    private static final String TAG = MapsActivity.class.getSimpleName();

    // UI references.
    private EditText mUserView;
    private EditText mPasswordView;
    private Spinner mRoleView;
    private View mProgressView;
    private View mLoginFormView;
    private EditText mReenterView;
    private EditText mPostalView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);
        setupActionBar();

        // Set up the login form.
        mUserView = findViewById(R.id.user);
        mRoleView = findViewById(R.id.role_spinner);

        //Populate spinner with enum
        ArrayAdapter<CharSequence> roleAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.role_enum, R.layout.support_simple_spinner_dropdown_item);
        mRoleView.setAdapter(roleAdapter);

        mPostalView = findViewById(R.id.postal_code);

        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                return id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL;
            }
        });

        mReenterView = findViewById(R.id.password_reentry);
        mReenterView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

        Button mSignInButton = findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        Button backToSignIn = findViewById(R.id.backto_signin_button);
        backToSignIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(signInIntent);
                finish();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void switchToMap() {
        Intent mapsIntent = new Intent(this, MapsActivity.class);
        startActivity(mapsIntent);
        finish();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Show the Up button in the action bar.
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptRegister() {

        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUserView.setError(null);
        mPasswordView.setError(null);
        mReenterView.setError(null);
        mPostalView.setError(null);

        // Store values at the time of the login attempt.
        String user = mUserView.getText().toString();
        String password = mPasswordView.getText().toString();
        String reenter = mReenterView.getText().toString();
        String role = mRoleView.getSelectedItem().toString();
        String postalCode = mPostalView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        //Check if reentered password matches
        if (TextUtils.isEmpty(reenter)) {
            mReenterView.setError(getString(R.string.error_field_required));
            focusView = mReenterView;
            cancel = true;
        } else if (!isPasswordSame(password, reenter)) {
            mReenterView.setError(getString(R.string.error_not_matching));
            focusView = mReenterView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(user)) {
            mUserView.setError(getString(R.string.error_field_required));
            focusView = mUserView;
            cancel = true;
        } else if (!isUsernameValid(user)) {
            mUserView.setError(getString(R.string.error_invalid_user));
        }

        if (TextUtils.isEmpty(postalCode)) {
            mPostalView.setError(getString(R.string.error_no_postal));
            focusView = mPostalView;
            cancel = true;
        } else if (!isPostalCodeValid(postalCode)) {
            mPostalView.setError(getString(R.string.error_wrong_postal));
            focusView = mPostalView;
            cancel = true;
        }

        if (TextUtils.isEmpty(role)) {
            focusView = mRoleView;
            Toast.makeText(getApplicationContext(), "This field is required", Toast.LENGTH_SHORT).show();
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(user, password, role, postalCode);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isPostalCodeValid(String postalCode) {
        String regex = "^(?!.*[DFIOQU])[A-VXY][0-9][A-Z] ?[0-9][A-Z][0-9]$";
        Matcher matcher = Pattern.compile(regex).matcher(postalCode);

        return matcher.matches();
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    private boolean isPasswordSame(String password, String reenter) {
        return password.equals(reenter);
    }

    private boolean isUsernameValid(String username) {
        return username.length() > 1;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    @SuppressLint("StaticFieldLeak")
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        private final String mPassword;
        private final String mRole;
        private final String mPostalCode;

        private JSONObject user;
        private boolean accountExists = false;

        UserLoginTask(String username, String password, String role, String postalCode) {
            mUsername = username;
            mPassword = password;
            mRole = role;
            mPostalCode = postalCode;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            RequestFuture<JSONObject> registerReq = RequestFuture.newFuture();
            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET, VolleyController.DEFAULT_BASE_URL + "users/" + mUsername + "/", new JSONObject(), registerReq, registerReq);
            VolleyController.getInstance(getApplicationContext()).addToRequestQueue(jsonReq);

            try {
                user = registerReq.get(10, TimeUnit.SECONDS);
                if (user != null) {
                    accountExists = true;
                    return false;
                }
            } catch (InterruptedException | ExecutionException e) {
                if (e.getCause() instanceof VolleyError) {
                    VolleyError volleyError = (VolleyError) e.getCause();
                    NetworkResponse networkResponse = volleyError.networkResponse;
                    Log.e(TAG, "Backend error: " + networkResponse.toString());
                }
            } catch (TimeoutException e) {
                Log.e(TAG,"Timeout occurred when waiting for response");
            }

            user = new JSONObject();

            try {
                user.put("username", mUsername);
                user.put("password", mPassword);
                user.put("role", mRole);
                user.put("myAddresses", mPostalCode);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestFuture<JSONObject> newAccountReq = RequestFuture.newFuture();
            JsonObjectRequest accountReq = new JsonObjectRequest(Request.Method.POST, VolleyController.DEFAULT_BASE_URL + "newuser/", user, newAccountReq, newAccountReq);
            VolleyController.getInstance(getApplicationContext()).addToRequestQueue(accountReq);

            try {
                newAccountReq.get(10, TimeUnit.SECONDS);
                return true;
            } catch (InterruptedException | ExecutionException e) {
                if (e.getCause() instanceof VolleyError) {
                    VolleyError volleyError = (VolleyError) e.getCause();
                    NetworkResponse networkResponse = volleyError.networkResponse;
                    Log.e(TAG, "Backend error: " + networkResponse.toString());
                }
            } catch (TimeoutException e) {
                Log.e(TAG,"Timeout occurred when waiting for response");
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            mAuthTask = null;
            showProgress(false);

            if (success) {
                if (user != null) {
                    LoginActivity.loggedInUser = user;
                    switchToMap();
                }
            } else if (accountExists) {
                mUserView.setError(getString(R.string.error_account_exists));
                mUserView.requestFocus();
            } else {
                mUserView.setError(getString(R.string.error_registration));
                mUserView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

