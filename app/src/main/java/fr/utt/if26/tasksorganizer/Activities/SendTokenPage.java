package fr.utt.if26.tasksorganizer.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import fr.utt.if26.tasksorganizer.R;
import fr.utt.if26.tasksorganizer.Utils.EmailSender;
import fr.utt.if26.tasksorganizer.Utils.TokensUtil;
import fr.utt.if26.tasksorganizer.ViewModels.UsersViewModel;


public class SendTokenPage extends AppCompatActivity {

    private TextView tv_infos;
    private TextView emailLabel;
    private EditText et_email;
    private Button sendToken;
    private TextView tokenLabel;
    private EditText et_token;
    private Button validate;
    private String token;
    private String email;
    private UsersViewModel usersViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_token_page);

        tv_infos = findViewById(R.id.forgottonPageInfo);
        emailLabel = findViewById(R.id.forgotten_password_emailLabel);
        et_email = findViewById(R.id.forgotten_password_email);
        sendToken = findViewById(R.id.sendToken);
        tokenLabel = findViewById(R.id.forgotten_password_tokenLabel);
        et_token = findViewById(R.id.forgotten_password_token);
        validate = findViewById(R.id.validateToken);
        usersViewModel = new ViewModelProvider(this).get(UsersViewModel.class);
        tv_infos.setText("Enter your email and send token");


        sendToken.setOnClickListener(view -> {
             email = et_email.getText().toString().trim();
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                tv_infos.setText("not a valid email format");
                tv_infos.setTextColor(Color.RED);
                return;
            }
             token = TokensUtil.generateToken();
            EmailSender.sendEmail(email, "token","Enter this token in the token field in order to re-initialize your password.\ntoken:"+token);
            tv_infos.setText("we have sent you a token, please check your email box");
            tv_infos.setTextColor(Color.WHITE);
        });

        validate.setOnClickListener(view -> {
            String token_input = et_token.getText().toString().trim();
            if(email==null){
                tv_infos.setText("token was not sent, press on <<Send Token>> Button ");
                tv_infos.setTextColor(Color.RED);
                return;
            }
            if(token_input.isEmpty() || email.isEmpty()) {
                tv_infos.setText("missing required fields");
                tv_infos.setTextColor(Color.RED);
                return;
            }
            if(token_input.equals(token)){
                usersViewModel.getUserByUserNameOrEmail("", email).observe(this, users -> {
                    if(users!=null && users.size()==1) {
                        Intent intent = new Intent(this, ResetPasswordPage.class);
                        intent.putExtra("user", users.get(0));
                        startActivity(intent);
                    }else {
                        tv_infos.setText("this user email has no account");
                        tv_infos.setTextColor(Color.RED);
                    }
                });
            }else {
                tv_infos.setText("Invalid Token");
                tv_infos.setTextColor(Color.RED);
            }
        });
    }
}