package fr.utt.if26.tasksorganizer.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import fr.utt.if26.tasksorganizer.Entities.User;
import fr.utt.if26.tasksorganizer.R;
import fr.utt.if26.tasksorganizer.ViewModels.UsersViewModel;

public class ResetPasswordPage extends AppCompatActivity {

    private User user;
    private EditText et_newPassword;
    private EditText et_confirmNewPassword;
    private TextView infos;
    private Button submit;
    private UsersViewModel usersViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_page);
        et_newPassword = findViewById(R.id.Reset_newPassword);
        et_confirmNewPassword = findViewById(R.id.Reset_confirmNewPassword);
        submit = findViewById(R.id.Reset_submit_newPassword);
        infos = findViewById(R.id.Reset_Info);
        usersViewModel = new ViewModelProvider(this).get(UsersViewModel.class);
        user = (User) getIntent().getSerializableExtra("user");

        submit.setOnClickListener(view -> {
            String newPassword = et_newPassword.getText().toString().trim();
            String confirmNewPassword = et_confirmNewPassword.getText().toString().trim();
            if(newPassword.isEmpty() || confirmNewPassword.isEmpty()){
                infos.setText("missing required fields");
                infos.setTextColor(Color.RED);
                return;
            }
            if(!newPassword.equals(confirmNewPassword)){
                infos.setText("wrong password confirmation");
                infos.setTextColor(Color.RED);
                return;
            }
            user.setPassword(newPassword);
            usersViewModel.updateUser(user);
            Intent intent = new Intent(this, TodayActivity.class);
            intent.putExtra("user",user);
            startActivity(intent);

        });
    }
}