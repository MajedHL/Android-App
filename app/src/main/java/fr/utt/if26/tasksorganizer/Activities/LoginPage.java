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
import fr.utt.if26.tasksorganizer.Utils.EmailSender;
import fr.utt.if26.tasksorganizer.ViewModels.TasksViewModel;
import fr.utt.if26.tasksorganizer.ViewModels.UsersViewModel;
import  fr.utt.if26.tasksorganizer.Entities.User;

public class LoginPage extends AppCompatActivity {

    private EditText et_userName;
    private EditText et_password;
    private TextView tv_createAccount;
    private TextView tv_forgotPassword;
    private TextView usernameLabel;
    private  TextView loginPageInfo;
    private Button btn_go;
    private Boolean modeCreate = false;
    private TextView loginPageTitle;
    private EditText confirmPassword;
    private TextView confirmPasswordLabel;
    private TextView emailLabel;
    private EditText et_email;
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
        emailLabel = findViewById(R.id.email_label);
        et_email = findViewById(R.id.email);
        tv_forgotPassword = findViewById(R.id.forgot_password);
        loginPageInfo.setText("");
        loginPageTitle.setText("LOGIN");

        modeCreate = getIntent().getBooleanExtra("modeCreate",false);
        if(modeCreate) init();
        usersViewModel = new ViewModelProvider(this).get(UsersViewModel.class);

        usersViewModel.getAllUsers().observe(this, users -> {
            System.out.println("users:"+users);
        });




        tv_forgotPassword.setOnClickListener(view -> {
            Intent intent = new Intent(this, SendTokenPage.class);
            startActivity(intent);
        });




        btn_go.setOnClickListener(view -> {
            String userName = et_userName.getText().toString().trim();
            String password = et_password.getText().toString().trim();
            String password_confirmation = confirmPassword.getText().toString().trim();
            String email = et_email.getText().toString().trim();
            if(userName.isEmpty() || password.isEmpty() ) {
                loginPageInfo.setText("required fields missing");
                loginPageInfo.setTextColor(Color.RED);

                return;
            }
          if(modeCreate) {
              whenCreating(password_confirmation, email, password, userName) ;
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
        tv_forgotPassword.setVisibility(View.INVISIBLE);
        loginPageInfo.setText("");
        loginPageTitle.setText("Create account");
        confirmPasswordLabel.setVisibility(View.VISIBLE);
        confirmPassword.setVisibility(View.VISIBLE);
        emailLabel.setVisibility(View.VISIBLE);
        et_email.setVisibility(View.VISIBLE);
    }

    private void whenCreating(String password_confirmation, String email, String password, String userName){

            if(password_confirmation.isEmpty() || email.isEmpty()) {
                loginPageInfo.setText("required fields missing");
                loginPageInfo.setTextColor(Color.RED);
                return ;
            }
            if(!password.equals(password_confirmation)){
                loginPageInfo.setText("password confirmation wrong");
                loginPageInfo.setTextColor(Color.RED);
                return ;
            }


            final AtomicBoolean enter = new AtomicBoolean(true);
            usersViewModel.getUserByUserNameOrEmail(userName, email).observe(this, users -> {
                if(users!=null && users.size()>0 && enter.get()){
                    System.out.println("users found:"+ users);
                    loginPageInfo.setText("user already exist, userName or email already in use");
                    loginPageInfo.setTextColor(Color.RED);

                }else {
                    enter.set(false);
                    loginPageInfo.setText("");
                    User newUser = new User(userName, password, email);
                    usersViewModel.insertUser(newUser);
                    usersViewModel.getUserByUserName(userName).observe(this, user -> {
                        if(user!=null){
                            Intent intent = new Intent(this, TodayActivity.class);
                            intent.putExtra("user", user);
                            startActivity(intent);
                        }
                    });


                }
            });
    }
}