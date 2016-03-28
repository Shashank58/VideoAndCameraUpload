package shashank.treusbs.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import shashank.treusbs.R;
import shashank.treusbs.UploadActivity;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText loginEmail, loginPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.hide();
        }

        setContentView(R.layout.activity_sign_in);
        Button submit = (Button) findViewById(R.id.submit);
        TextView newUser = (TextView) findViewById(R.id.newUser);
        loginEmail = (EditText) findViewById(R.id.loginEmail);
        loginPassword = (EditText) findViewById(R.id.loginPassword);

        submit.setOnClickListener(this);
        newUser.setOnClickListener(this);
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
        Intent intent = new Intent(this, UploadActivity.class);
        startActivity(intent);
    }
}
