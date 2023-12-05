package fr.utt.if26.tasksorganizer.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicBoolean;

import fr.utt.if26.tasksorganizer.R;
import fr.utt.if26.tasksorganizer.ViewModels.TasksViewModel;
import fr.utt.if26.tasksorganizer.ViewModels.UsersViewModel;
import  fr.utt.if26.tasksorganizer.Entities.User;

public class LoginPage extends AppCompatActivity {

    private EditText et_userName;
    private EditText et_password;
    private TextView tv_createAccount;
    private TextView usernameLabel;
    private  TextView loginPageInfo;
    private Button btn_go;
    private Boolean modeCreate = false;
    private TextView loginPageTitle;
    private EditText confirmPassword;
    private TextView confirmPasswordLabel;
    private UsersViewModel usersViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        et_userName = findViewById(R.id.username);
        et_password = findViewById(R.id.password);
        tv_createAccount = findViewById(R.id.create_account);
        btn_go = findViewById(R.id.go);
        usernameLabel = findViewById(R.id.username_label);
        loginPageInfo = findViewById(R.id.login_page_info);
        loginPageTitle = findViewById(R.id.login_page_title);
        confirmPasswordLabel = findViewById(R.id.confirm_password_label);
        confirmPassword = findViewById(R.id.confirm_password);
        loginPageInfo.setText("");
        loginPageTitle.setText("LOGIN");

        modeCreate = getIntent().getBooleanExtra("modeCreate",false);
        if(modeCreate) init();
        usersViewModel = new ViewModelProvider(this).get(UsersViewModel.class);
        usersViewModel.getAllUsers().observe(this, users -> {
            System.out.println("users:"+users);
        });

        btn_go.setOnClickListener(view -> {

            String userName = et_userName.getText().toString().trim();
            String password = et_password.getText().toString().trim();
            String password_confirmation = confirmPassword.getText().toString().trim();
            if(userName.isEmpty() || password.isEmpty() ) {
                loginPageInfo.setText("required fields missing");
                loginPageInfo.setTextColor(Color.RED);

                return;
            }
            if(modeCreate) {
                if(password_confirmation.isEmpty()) {
                    loginPageInfo.setText("required fields missing");
                    loginPageInfo.setTextColor(Color.RED);
                    return;
                }
                if(!password.equals(password_confirmation)){
                    loginPageInfo.setText("password confirmation wrong");
                    loginPageInfo.setTextColor(Color.RED);
                    return;
                }

            }
            if(modeCreate){
                final AtomicBoolean enter = new AtomicBoolean(true);
                usersViewModel.getUserByUserName(userName).observe(this,user -> {
                    if(user!=null && enter.get()){
                        System.out.println("user found:"+ user);
                        loginPageInfo.setText("user already exist, pick another userName");
                        loginPageInfo.setTextColor(Color.RED);

                    }else {
                        enter.set(false);
                        loginPageInfo.setText("");
                        User newUser = new User(userName,password);
                        usersViewModel.insertUser(newUser);
                        Intent intent = new Intent(this, TodayActivity.class);
                        intent.putExtra("user", newUser);
                        startActivity(intent);

                    }
                });
                return;
            }


            usersViewModel.getUser(userName,password).observe(this, user -> {
                if(user!=null){
                    Intent intent = new Intent(this, TodayActivity.class);
                    intent.putExtra("user",user);
                    startActivity(intent);
                }else {
                    loginPageInfo.setText("userName or password incorrect");
                    loginPageInfo.setTextColor(Color.RED);
                }
            });
        });

        tv_createAccount.setOnClickListener(view -> {
            Intent intent = new Intent(this, LoginPage.class);
            intent.putExtra("modeCreate",true);
            startActivity(intent);
        });
    }


    private void init(){
        modeCreate = true;
        tv_createAccount.setVisibility(View.INVISIBLE);
        loginPageInfo.setText("");
        loginPageTitle.setText("Create account");
        confirmPasswordLabel.setVisibility(View.VISIBLE);
        confirmPassword.setVisibility(View.VISIBLE);
    }
}