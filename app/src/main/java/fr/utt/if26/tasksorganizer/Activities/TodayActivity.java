package fr.utt.if26.tasksorganizer.Activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

import fr.utt.if26.tasksorganizer.Adapters.TaskAdapter;
import fr.utt.if26.tasksorganizer.Entities.Task;
import fr.utt.if26.tasksorganizer.Entities.User;
import fr.utt.if26.tasksorganizer.R;
import fr.utt.if26.tasksorganizer.Utils.Code;
import fr.utt.if26.tasksorganizer.Utils.DateUtil;
import fr.utt.if26.tasksorganizer.ViewModels.ConsentsViewModel;
import fr.utt.if26.tasksorganizer.ViewModels.TasksViewModel;
import fr.utt.if26.tasksorganizer.ViewModels.UsersViewModel;

public class TodayActivity extends AppCompatActivity {


    private TasksViewModel tasksViewModel;

    private ImageButton addTask_button;
    private BottomNavigationView nav_menu;

    private RecyclerView Todaytasks_listView;
    private ArrayList<Task> todayTasks;
    private User user;

    private ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    System.out.println("BACK");
                    if(result.getData()==null) {
                        System.out.println("NULL intent");
                        return;
                    }
                    int code  = result.getResultCode();
                    if(code == Code.EDIT_SUCCESS.getValue()){
                        Task task = (Task) result.getData().getSerializableExtra("updatedTask");
                        Toast.makeText(getApplicationContext()," task updated successfully",Toast.LENGTH_LONG).show();
                        tasksViewModel.updateTask(task);
                    }
                    else if(code == Code.CREATE_SUCCESS.getValue()){
                        Task task = (Task) result.getData().getSerializableExtra("newTask");
                        Toast.makeText(getApplicationContext()," task created successfully",Toast.LENGTH_LONG).show();
                        tasksViewModel.insertTask(task);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);
        getSupportActionBar().setTitle("Today Page");
        user = (User) getIntent().getSerializableExtra("user");
        Todaytasks_listView = findViewById(R.id.Todaytasks);


        tasksViewModel = new ViewModelProvider(this).get(TasksViewModel.class);
        addTask_button = findViewById(R.id.addTask_button);
        addTask_button.setOnClickListener(view -> {
            System.out.println("button clicked");
            Intent intent = new Intent(this, Task_edit.class);
            intent.putExtra("target","create");
            intent.putExtra("source","today");
            intent.putExtra("user",user);
            activityResultLauncher.launch(intent);
        });

        Todaytasks_listView.addItemDecoration(new DividerItemDecoration(this, new LinearLayoutManager(getApplicationContext()).getOrientation()));
        tasksViewModel.getAllTasks(user.getId()).observe(this, tasks -> {
            Date today = new Date();

            todayTasks = (ArrayList<Task>) tasks.stream().filter(task -> DateUtil.sameDay(task.getDuedate(),today)).collect(Collectors.toList());

            TaskAdapter taskAdapter = new TaskAdapter((ArrayList<Task>) todayTasks, task -> {
                Intent intent = new Intent(this, Task_edit.class);
                intent.putExtra("target","edit");
                intent.putExtra("task",task);
                intent.putExtra("user",user);
                activityResultLauncher.launch(intent);
            });
            Todaytasks_listView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            Todaytasks_listView.setAdapter(taskAdapter);

            System.out.println("tasks:"+tasks);
            System.out.println("todayTasks:"+todayTasks);
        });

    nav_menu = findViewById(R.id.bottomNavigationView);
    nav_menu.setOnItemSelectedListener(item -> {

        if(item.getItemId()==R.id.Today_Page) {
            System.out.println("nothing just stay at TODAY PAGE");
            return true;
        }
        else if(item.getItemId()==R.id.Tasks_Page){
            Intent intent = new Intent(this, TasksActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        }

        return true;
    });

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_manip_menu, menu);
        return  true;
    }


    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId()==R.id.hide_completed_tasks) {
            tasksViewModel.setHideDoneTasks();
            item.setChecked(!item.isChecked());
            return true;
        }
        else if(item.getItemId()==R.id.sort_duedate){
            tasksViewModel.setSortByDueDate();
            item.setChecked(!item.isChecked());
            return true;
        }
        else if(item.getItemId()==R.id.sort_completion){
            tasksViewModel.setSortByCompletion();
            item.setChecked(!item.isChecked());
            return true;
        }
        else if(item.getItemId()==R.id.sort_priority){
            tasksViewModel.setSortByPriority();
            item.setChecked(!item.isChecked());
            return true;
        }
        else if(item.getItemId()==R.id.sort_late){
            tasksViewModel.setSortByLateness();
            item.setChecked(!item.isChecked());
            return true;
        }
        else return super.onOptionsItemSelected(item);
    }


//
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        // Get the position from the tag
        if(item.getItemId()==R.id.mark_done){
            Task task = todayTasks.get(item.getGroupId());
            task.setStatus(true);
            tasksViewModel.updateTask(task);
            return true;
        }
        else if(item.getItemId()==R.id.delete_task){
            tasksViewModel.deleteTask(todayTasks.get(item.getGroupId()));
            return true;
        }
        else if(item.getItemId()==R.id.mark_undone){
            Task task = todayTasks.get(item.getGroupId());
            task.setStatus(false);
            tasksViewModel.updateTask(task);
            return true;
        }
        else return super.onContextItemSelected(item);
    }



}