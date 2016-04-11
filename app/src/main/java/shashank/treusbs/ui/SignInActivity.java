package shashank.treusbs.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import shashank.treusbs.R;
import shashank.treusbs.UploadActivity;
import shashank.treusbs.util.AppUtils;
import shashank.treusbs.util.SharedPreferenceHandler;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
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
                String email = loginEmail.getText().toString().trim();
                String password = loginPassword.getText().toString();
                myFireBaseRef.authWithPassword(email, password, new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        new SharedPreferenceHandler().storeUID
                                (SignInActivity.this, authData.getUid());
                        startActivity(new Intent(SignInActivity.this, UploadActivity.class));
                        finish();
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {

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
