package fr.utt.if26.tasksorganizer.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import fr.utt.if26.tasksorganizer.R;
import fr.utt.if26.tasksorganizer.ViewModels.TasksViewModel;
import fr.utt.if26.tasksorganizer.ViewModels.UsersViewModel;

public class LoginPage extends AppCompatActivity {

    private EditText et_userName;
    private EditText et_password;
    private TextView tv_createAccount;
    private TextView usernameLabel;
    private  TextView loginPageInfo;
    private Button btn_go;

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
        loginPageInfo.setText("");
        usersViewModel = new ViewModelProvider(this).get(UsersViewModel.class);
        usersViewModel.getAllUsers().observe(this, users -> {
            System.out.println("users:"+users);
        });

        btn_go.setOnClickListener(view -> {
            String userName = et_userName.getText().toString();
            String password = et_password.getText().toString();
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
    }
}