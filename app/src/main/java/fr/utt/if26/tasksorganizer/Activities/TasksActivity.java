package fr.utt.if26.tasksorganizer.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

import fr.utt.if26.tasksorganizer.Adapters.TaskAdapter;
import fr.utt.if26.tasksorganizer.Entities.Task;
import fr.utt.if26.tasksorganizer.R;
import fr.utt.if26.tasksorganizer.Utils.DateUtil;
import fr.utt.if26.tasksorganizer.ViewModels.TasksViewModel;


public class TasksActivity extends AppCompatActivity {
    private TasksViewModel tasksViewModel;

    private ImageButton addTask_button;
    private BottomNavigationView nav_menu;

    private RecyclerView Alltasks_listView;
    private ArrayList<Task> tasks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);
        Toolbar toolbar=findViewById(R.id.toolbar_today);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Tasks Page");
        Alltasks_listView = findViewById(R.id.Todaytasks);
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

        Alltasks_listView.addItemDecoration(new DividerItemDecoration(this, new LinearLayoutManager(getApplicationContext()).getOrientation()));
        tasksViewModel.getAllTasks().observe(this, tasks -> {
            this.tasks = (ArrayList<Task>) tasks;
            TaskAdapter taskAdapter = new TaskAdapter((ArrayList<Task>) tasks, task -> {
                Intent intent = new Intent(this, Task_edit.class);
                startActivity(intent);
            });
            Alltasks_listView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            Alltasks_listView.setAdapter(taskAdapter);

            System.out.println("tasks size:"+tasks.size());
            System.out.println("Alltasks_listView:"+tasks);
        });

        nav_menu = findViewById(R.id.bottomNavigationView);
        nav_menu.setOnItemSelectedListener(item -> {

            if(item.getItemId()==R.id.Today_Page) {

                Intent intent = new Intent(this, TodayActivity.class);
                startActivity(intent);
                return true;
            }
            else if(item.getItemId()==R.id.Tasks_Page){
                System.out.println("nothing just stay at TASKS Page");
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
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        // Get the position from the tag
        if(item.getItemId()==R.id.mark_done){
            Task task = tasks.get(item.getGroupId());
            task.setStatus(true);
            tasksViewModel.updateTask(task);
            return true;
        }
        else if(item.getItemId()==R.id.delete_task){
            tasksViewModel.deleteTask(tasks.get(item.getGroupId()));
            return true;
        }
        else if(item.getItemId()==R.id.mark_undone){
            Task task = tasks.get(item.getGroupId());
            task.setStatus(false);
            tasksViewModel.updateTask(task);
            return true;
        }
        else return super.onContextItemSelected(item);
    }
}