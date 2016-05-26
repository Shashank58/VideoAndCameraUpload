package shashank.treusbs.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import shashank.treusbs.R;
import shashank.treusbs.User;
import shashank.treusbs.util.AppUtils;
import shashank.treusbs.util.SharedPreferenceHandler;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Sign In Activity";
    private EditText loginEmail, loginPassword;
    private Firebase myFireBaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (new SharedPreferenceHandler().getUID(this) == null) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }
            setContentView(R.layout.activity_sign_in);

            Firebase.setAndroidContext(this);
            Button submit = (Button) findViewById(R.id.submit);
            TextView newUser = (TextView) findViewById(R.id.newUser);
            loginEmail = (EditText) findViewById(R.id.loginEmail);
            loginPassword = (EditText) findViewById(R.id.loginPassword);
            myFireBaseRef = new Firebase("https://treusbs.firebaseio.com/");

            submit.setOnClickListener(this);
            newUser.setOnClickListener(this);
        } else {
            startActivity(new Intent(this, UploadActivity.class));
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submit:
                validateInput();
                break;

            case R.id.newUser:
                Intent intent = new Intent(this, SignUpActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void validateInput(){
        if (!"".equals(loginEmail.getText().toString().trim()) &&
                !"".equals(loginPassword.getText().toString().trim())) {
            if (isValidEmail()) {
                AppUtils.getInstance().showProgressDialog(SignInActivity.this,
                        "Logging In...");
                final String email = loginEmail.getText().toString().trim();
                String password = loginPassword.getText().toString();
                myFireBaseRef.authWithPassword(email, password, new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        AppUtils.getInstance().dismissProgressDialog();
                        Log.d(TAG, "onAuthenticated: " + authData.toString());
                        Intent intent = new Intent(SignInActivity.this, UploadActivity.class);
                        if (authData.getUid().equals("5c9e58c9-f909-44de-b680-975e061661f6")){
                            intent.putExtra(UploadActivity.IS_ADMIN, true);
                        } else {
                            final String emailText = email.replace(".", "");
                            new SharedPreferenceHandler().storeUID
                                    (SignInActivity.this, authData.getUid());
                            Firebase ref = new Firebase("https://treusbs.firebaseio.com/users");
                            Query query = ref.orderByChild("name");
                            query.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    Log.d(TAG, "onChildAdded: " + dataSnapshot.toString());
                                    if (dataSnapshot.getKey().equals(emailText)) {
                                        User user = dataSnapshot.getValue(User.class);
                                        Log.d(TAG, "onChildAdded: Name: " + user.getName());
                                        Log.d(TAG, "onChildAdded: Number: " + user.getNumber());
                                        new SharedPreferenceHandler().storeName
                                                (SignInActivity.this, user.getName());
                                        new SharedPreferenceHandler().storeNumber
                                                (SignInActivity.this, user.getNumber());
                                    }
                                }

                                @Override
                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                }

                                @Override
                                public void onChildRemoved(DataSnapshot dataSnapshot) {

                                }

                                @Override
                                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {

                                }
                            });

                            intent.putExtra(UploadActivity.IS_ADMIN, false);
                        }
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        AppUtils.getInstance().dismissProgressDialog();
                        AppUtils.getInstance().showAlertDialog(SignInActivity.this,
                                firebaseError.getMessage());
                    }
                });
            } else {
                AppUtils.getInstance().showAlertDialog(this, "Invalid email");
            }
        } else {
            AppUtils.getInstance().showAlertDialog(this, "Email and password should not be empty");
        }
    }

    private boolean isValidEmail(){
        CharSequence sequence = loginEmail.getText();
        return android.util.Patterns.EMAIL_ADDRESS.matcher(sequence).matches();
    }
}
