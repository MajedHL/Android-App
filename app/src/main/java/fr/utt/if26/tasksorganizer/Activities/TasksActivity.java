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
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

import fr.utt.if26.tasksorganizer.Adapters.TaskAdapter;
import fr.utt.if26.tasksorganizer.Entities.Task;
import fr.utt.if26.tasksorganizer.R;
import fr.utt.if26.tasksorganizer.Utils.Code;
import fr.utt.if26.tasksorganizer.Utils.DateUtil;
import fr.utt.if26.tasksorganizer.ViewModels.TasksViewModel;


public class TasksActivity extends AppCompatActivity {
    private TasksViewModel tasksViewModel;

    private ImageButton addTask_button;
    private BottomNavigationView nav_menu;

    private RecyclerView Alltasks_listView;
    private ArrayList<Task> tasks;


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
//        Toolbar toolbar=findViewById(R.id.toolbar_today);
//        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Tasks Page");
        Alltasks_listView = findViewById(R.id.Todaytasks);
//        this.registerForContextMenu(Todaytasks_listView);

        tasksViewModel = new ViewModelProvider(this).get(TasksViewModel.class);
        addTask_button = findViewById(R.id.addTask_button);

        addTask_button.setOnClickListener(view -> {
            System.out.println("button clicked");
            Intent intent = new Intent(this, Task_edit.class);
            intent.putExtra("target","create");
            activityResultLauncher.launch(intent);
        });

        Alltasks_listView.addItemDecoration(new DividerItemDecoration(this, new LinearLayoutManager(getApplicationContext()).getOrientation()));

        tasksViewModel.getAllTasks().observe(this, tasks -> {
            this.tasks = (ArrayList<Task>) tasks;
            TaskAdapter taskAdapter = new TaskAdapter((ArrayList<Task>) tasks, task -> {
                Intent intent = new Intent(this, Task_edit.class);
                intent.putExtra("target","edit");
                intent.putExtra("task",task);
                activityResultLauncher.launch(intent);
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