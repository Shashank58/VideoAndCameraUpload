package shashank.treusbs.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

import shashank.treusbs.R;
import shashank.treusbs.User;
import shashank.treusbs.util.AppUtils;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText signUpName, signUpEmail, signUpPassword, signUpConfirmPassword;
    private EditText signUpNumber;
    private Firebase myFirebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.hide();
        }
        setContentView(R.layout.activity_sign_up);
        Firebase.setAndroidContext(this);

        myFirebaseRef = new Firebase("https://treusbs.firebaseio.com/");
        signUpName = (EditText) findViewById(R.id.signUpName);
        signUpEmail = (EditText) findViewById(R.id.signUpEmail);
        signUpNumber = (EditText) findViewById(R.id.signUpNumber);
        signUpPassword = (EditText) findViewById(R.id.signUpPassword);
        signUpConfirmPassword = (EditText) findViewById(R.id.signUpConfirmPassword);
        Button submit = (Button) findViewById(R.id.signUpSubmit);

        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signUpSubmit:
                if (checkIfNotEmpty()){
                   if (nameAndNumberValidation()){
                       if (isValidEmail()){
                           if (checkPassword()){
                               AppUtils.getInstance().showProgressDialog(SignUpActivity.this,
                                       "Please wait...");
                               final String email = signUpEmail.getText().toString();
                               String password = signUpPassword.getText().toString();

                               myFirebaseRef.createUser(email, password,
                                       new Firebase.ValueResultHandler<Map<String, Object>>() {
                                   @Override
                                   public void onSuccess(Map<String, Object> result) {
                                       AppUtils.getInstance().dismissProgressDialog();
                                       String emailKey = email.replace(".", "");
                                       Firebase user = myFirebaseRef.child("users").child(emailKey);
                                       User newUser = new User(signUpName.getText()
                                               .toString(), signUpNumber.getText().toString());
                                       user.setValue(newUser);
                                       new AlertDialog.Builder(SignUpActivity.this)
                                                .setTitle("TREUSBS")
                                                .setMessage("Sign Up successful")
                                                .setPositiveButton(android.R.string.ok,
                                                        new OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                finish();
                                                            }
                                                        }).create().show();
                                   }
                                   @Override
                                   public void onError(FirebaseError firebaseError) {
                                       // there was an error
                                       AppUtils.getInstance().showAlertDialog
                                               (SignUpActivity.this, firebaseError.getMessage());
                                   }
                               });
                           } else {
                               AppUtils.getInstance().showAlertDialog(this, "Passwords not matching");
                           }
                       } else {
                           AppUtils.getInstance().showAlertDialog(this, "Please enter a valid" +
                                   "email address");
                       }
                   } else {
                       AppUtils.getInstance().showAlertDialog(this, "Please enter proper name " +
                               "and mobile number");
                   }
                } else {
                    AppUtils.getInstance().showAlertDialog(this, "Please enter all fields");
                }
                break;
        }
    }

    private boolean checkIfNotEmpty(){
        return  (signUpName.getText() != null || signUpEmail.getText() != null ||
                signUpNumber.getText() != null || signUpPassword.getText() != null);
    }

    private boolean nameAndNumberValidation(){
        String name = signUpName.getText().toString().trim();
        if (name.matches("[a-zA-Z]+") && name.length() > 2){
            String number = signUpNumber.getText().toString();
            return  (number.matches("[0-9]+") && number.length() == 10);

        } else {
            return false;
        }
    }

    private boolean isValidEmail(){
        CharSequence sequence = signUpEmail.getText();
        return android.util.Patterns.EMAIL_ADDRESS.matcher(sequence).matches();
    }

    private boolean checkPassword(){
        String password = signUpPassword.getText().toString();
        String confirm = signUpConfirmPassword.getText().toString();
        if (!password.equals("")){
            return password.equals(confirm);
        } else {
            return false;
        }
    }
}
