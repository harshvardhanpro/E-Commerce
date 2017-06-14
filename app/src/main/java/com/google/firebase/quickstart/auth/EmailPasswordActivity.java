

package com.google.firebase.quickstart.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.quickstart.auth.Home.HomeCategoryActivity;
import com.google.firebase.quickstart.auth.Home.HomeMainActivity;
import com.google.firebase.quickstart.auth.productDetails.ProductActivity;

public class EmailPasswordActivity extends BaseActivity implements
                    View.OnClickListener {

                private static final String TAG = "EmailPassword";

                private TextView mStatusTextView;
                private TextView mDetailTextView;
                private EditText mEmailField;
                private EditText mPasswordField;

                // [START declare_auth]
                private FirebaseAuth mAuth;
                // [END declare_auth]

                @Override
                public void onCreate(Bundle savedInstanceState) {
                    super.onCreate(savedInstanceState);
                    setContentView(R.layout.activity_emailpassword);

                    // Views
                    mStatusTextView = (TextView) findViewById(R.id.status);
                    mDetailTextView = (TextView) findViewById(R.id.detail);
                    mEmailField = (EditText) findViewById(R.id.field_email);
                    mPasswordField = (EditText) findViewById(R.id.field_password);

                    // Buttons
                    findViewById(R.id.email_sign_in_button).setOnClickListener(this);
                    findViewById(R.id.email_create_account_button).setOnClickListener(this);
                    findViewById(R.id.sign_out_button).setOnClickListener(this);
                    findViewById(R.id.verify_email_button).setOnClickListener(this);
                    findViewById(R.id.button5).setOnClickListener(this);

                    // [START initialize_auth]
                    mAuth = FirebaseAuth.getInstance();
                    // [END initialize_auth]
                }

                // [START on_start_check_user]
                @Override
                public void onStart() {
                    super.onStart();
                    // Check if user is signed in (non-null) and update UI accordingly.
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    updateUI(currentUser);
                }
                // [END on_start_check_user]

                private void createAccount(String email, String password) {
                    Log.d(TAG, "createAccount:" + email);
                    if (!validateForm()) {
                        return;
                    }

                    showProgressDialog();

                    // [START create_user_with_email]
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "createUserWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        updateUI(user);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        updateUI(null);
                                    }

                                    // [START_EXCLUDE]
                                    hideProgressDialog();
                                    // [END_EXCLUDE]
                                }
                            });
                    // [END create_user_with_email]
                }

                private void signIn(String email, String password) {
                    Log.d(TAG, "signIn:" + email);
                    if (!validateForm()) {
                        return;
                    }

                    showProgressDialog();

                    // [START sign_in_with_email]
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        updateUI(user);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        updateUI(null);
                                    }

                                    // [START_EXCLUDE]
                                    if (!task.isSuccessful()) {
                                        mStatusTextView.setText(R.string.auth_failed);
                                    }
                                    hideProgressDialog();
                                    // [END_EXCLUDE]
                                }
                            });
                    // [END sign_in_with_email]
                }

                private void signOut() {
                    mAuth.signOut();
                    updateUI(null);
                }

                private void sendEmailVerification() {
                    // Disable button
                    findViewById(R.id.verify_email_button).setEnabled(false);

                    // Send verification email
                    // [START send_email_verification]
                    final FirebaseUser user = mAuth.getCurrentUser();
                    user.sendEmailVerification()
                            .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    // [START_EXCLUDE]
                                    // Re-enable button
                                    findViewById(R.id.verify_email_button).setEnabled(true);

                                    if (task.isSuccessful()) {
                                        Toast.makeText(EmailPasswordActivity.this,
                                                "Verification email sent to " + user.getEmail(),
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.e(TAG, "sendEmailVerification", task.getException());
                                        Toast.makeText(EmailPasswordActivity.this,
                                                "Failed to send verification email.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    // [END_EXCLUDE]
                                }
                            });
                    // [END send_email_verification]
                }

                private boolean validateForm() {
                    boolean valid = true;

                    String email = mEmailField.getText().toString();
                    if (TextUtils.isEmpty(email)) {
                        mEmailField.setError("Required.");
                        valid = false;
                    } else {
                        mEmailField.setError(null);
                    }

                    String password = mPasswordField.getText().toString();
                    if (TextUtils.isEmpty(password)) {
                        mPasswordField.setError("Required.");
                        valid = false;
                    } else {
                        mPasswordField.setError(null);
                    }

                    return valid;
                }

                private void updateUI(FirebaseUser user) {
                    hideProgressDialog();
                    if (user != null) {
                       /* mStatusTextView.setText(getString(R.string.emailpassword_status_fmt,
                                user.getEmail(), user.isEmailVerified()));
                        mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));

                        findViewById(R.id.email_password_buttons).setVisibility(View.GONE);
                        findViewById(R.id.email_password_fields).setVisibility(View.GONE);
                        findViewById(R.id.signed_in_buttons).setVisibility(View.VISIBLE);
                        findViewById(R.id.verify_email_button).setEnabled(!user.isEmailVerified());
                        */
                        Intent loginIntent = new Intent(EmailPasswordActivity.this, HomeMainActivity.class);
                        startActivity(loginIntent);

                    } else {
                        mStatusTextView.setText(R.string.signed_out);
                        mDetailTextView.setText(null);
                        findViewById(R.id.email_password_buttons).setVisibility(View.VISIBLE);
                       findViewById(R.id.email_password_fields).setVisibility(View.VISIBLE);
                       findViewById(R.id.signed_in_buttons).setVisibility(View.GONE);
                    }
                }

                @Override
                public void onClick(View v) {
                    int i = v.getId();
                    if (i == R.id.email_create_account_button) {
                        createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
                    } else if (i == R.id.email_sign_in_button) {
                        signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
                    } else if (i == R.id.sign_out_button) {
                        signOut();
                    } else if (i == R.id.verify_email_button) {
                        sendEmailVerification();
                    }
                    else if (i==R.id.button5){
                        Intent loginGoogleIntent = new Intent(EmailPasswordActivity.this, GoogleSignInActivity.class);
                        startActivity(loginGoogleIntent);
                    }

                }
            }