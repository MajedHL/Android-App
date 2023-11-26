package fr.utt.if26.tasksorganizer.Activities;

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
import fr.utt.if26.tasksorganizer.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);
        Toolbar toolbar=findViewById(R.id.toolbar_today);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Today Page");
        Todaytasks_listView = findViewById(R.id.Todaytasks);
//        this.registerForContextMenu(Todaytasks_listView);

        tasksViewModel = new ViewModelProvider(this).get(TasksViewModel.class);
        addTask_button = findViewById(R.id.addTask_button);
        addTask_button.setOnClickListener(view -> {
            System.out.println("button clicked");
            Task task = new Task(1,"NotTodayTask");
            task.setDuedate(new Date());
            tasksViewModel.insertTask(task);
//            tasksViewModel.deleteAllTasks();
        });

        Todaytasks_listView.addItemDecoration(new DividerItemDecoration(this, new LinearLayoutManager(getApplicationContext()).getOrientation()));
        tasksViewModel.getAllTasks().observe(this, tasks -> {
            Date today = new Date();

            todayTasks = (ArrayList<Task>) tasks.stream().filter(task -> DateUtil.sameDay(task.getDuedate(),today)).collect(Collectors.toList());

            TaskAdapter taskAdapter = new TaskAdapter((ArrayList<Task>) todayTasks, task -> {
                Intent intent = new Intent(this, Task_edit.class);
                startActivity(intent);
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
        if(item.getItemId()==R.id.show_done_tasks){
            tasksViewModel.setHideDoneTasks(false);
            return true;
        }
        else if (item.getItemId()==R.id.hide_completed_tasks) {
            tasksViewModel.setHideDoneTasks(true);
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