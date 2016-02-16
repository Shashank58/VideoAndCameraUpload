package shashank.treusbs.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import shashank.treusbs.util.HashPassword;
import shashank.treusbs.R;
import shashank.treusbs.util.AppUtils;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText signUpName, signUpEmail, signUpPassword, signUpConfirmPassword;
    private EditText signUpNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.hide();
        }

        setContentView(R.layout.activity_sign_up);

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
                               String password = signUpPassword.getText().toString();
                               String hashedPassword = HashPassword.getInstance()
                                            .computeSHAHash(password);
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
        String name = signUpName.getText().toString();
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
