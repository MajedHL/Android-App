package fr.utt.if26.tasksorganizer.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import fr.utt.if26.tasksorganizer.Entities.User;
import fr.utt.if26.tasksorganizer.R;
import fr.utt.if26.tasksorganizer.ViewModels.TasksViewModel;

public class ProfileActivity extends AppCompatActivity {
    private BottomNavigationView nav_menu;
    private User user;
    private TextView tv_title, tv_userName, tv_email, tv_password, tv_nbCompletedTasks, tv_nbUnCompletedTasks;
    private TasksViewModel tasksViewModel;

    private String spaces ="   ";//3 spaces
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        
        user = (User) getIntent().getSerializableExtra("user");
        tv_title = findViewById(R.id.Profile_Title);
        tv_userName = findViewById(R.id.Profile_userName);
        tv_email = findViewById(R.id.Profile_email);
        tv_password = findViewById(R.id.Profile_password);
        tv_nbCompletedTasks = findViewById(R.id.Profile_NbCompletedTasks);
        tv_nbUnCompletedTasks = findViewById(R.id.Profile_NbUnCompletedTasks);
        tasksViewModel = new ViewModelProvider(this).get(TasksViewModel.class);

        tasksViewModel.countCompletedTasks(user.getId()).observe(this, nb->{
            if(nb!=null) {
                tv_nbCompletedTasks.setText(getResources().getString(R.string.Profile_completed_tasks)+spaces+nb);
            }
        });

        tasksViewModel.countUnCompletedTasks(user.getId()).observe(this, nb->{
            if(nb!=null) {
                tv_nbUnCompletedTasks.setText(getResources().getString(R.string.Profile_uncompleted_tasks)+spaces+nb);
            }
        });

        init();



        nav_menu = findViewById(R.id.bottomNavigationView);
        nav_menu.setOnItemSelectedListener(item -> {
            if(item.getItemId()==R.id.Today_Page) {
                Intent intent = new Intent(this, TodayActivity.class);
                intent.putExtra("user",user);
                startActivity(intent);
                return true;
            }
            else if(item.getItemId()==R.id.Tasks_Page){
                Intent intent = new Intent(this, TasksActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
            else if(item.getItemId()==R.id.Profile_Page){
                System.out.println("nothing just stay at Profile Page");
            }
            return true;
        });
    }





    private void init(){        
        tv_userName.setText(getResources().getString(R.string.Profile_username)+spaces+user.getUserName());
        tv_email.setText(getResources().getString(R.string.Profile_email)+spaces+user.getEmail());
        tv_password.setText(getResources().getString(R.string.Profile_password)+spaces+user.getPassword());
    }
}