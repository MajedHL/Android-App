package fr.utt.if26.tasksorganizer.Activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import fr.utt.if26.tasksorganizer.Adapters.TaskAdapter;
import fr.utt.if26.tasksorganizer.Entities.Pending;
import fr.utt.if26.tasksorganizer.Entities.Task;
import fr.utt.if26.tasksorganizer.Entities.User;
import fr.utt.if26.tasksorganizer.R;
import fr.utt.if26.tasksorganizer.Utils.Code;
import fr.utt.if26.tasksorganizer.Utils.NotificationSystem;
import fr.utt.if26.tasksorganizer.ViewModels.PendingsViewModel;
import fr.utt.if26.tasksorganizer.ViewModels.TasksViewModel;


public class TasksActivity extends AppCompatActivity {
    private TasksViewModel tasksViewModel;

    private ImageButton addTask_button;
    private BottomNavigationView nav_menu;

    private RecyclerView Alltasks_listView;
    private ArrayList<Task> tasks;
    private User user;
    private PendingsViewModel pendingsViewModel;
    private  NotificationSystem notificationSystem;


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
                        boolean remindingDateModified = result.getData().getBooleanExtra("remindingDateModified",false);
                        boolean statusModified = result.getData().getBooleanExtra("statusModified",false);
                        boolean taskNameModified = result.getData().getBooleanExtra("taskNameModified",false);
                        if(result.getData().getBooleanExtra("remindingDateSet",false)){
                            if(task.getReminder()!=null) {
                                System.out.println("remindingDateSet");
                                pendingsViewModel.insertPending(new Pending(task.getId(), task.getName(),task.getReminder(), !task.isStatus()));
                            }
                        }
                        else if(remindingDateModified || statusModified || taskNameModified){
                            if(task.getReminder()!=null) {
                                pendingsViewModel.updatePending(new Pending(task.getId(),task.getName(), task.getReminder(), !task.isStatus()));
                                System.out.println("updating pending");
                            }
                        }
                    }
                    else if(code == Code.CREATE_SUCCESS.getValue()){
                        Task newTask = (Task) result.getData().getSerializableExtra("newTask");
                        Toast.makeText(getApplicationContext()," task created successfully",Toast.LENGTH_LONG).show();
                        tasksViewModel.insertTask(newTask);
                        if(newTask.getReminder()!=null) {
                            AtomicBoolean insert =new AtomicBoolean(true);
                            tasksViewModel.getMaxId(user.getId()).observe(TasksActivity.this, max -> {
                                if (max != null && insert.get()) {
                                    Pending pending = new Pending(max, newTask.getName(), newTask.getReminder(), !newTask.isStatus());
                                    System.out.println("inserting pending:" + pending);
                                    pendingsViewModel.insertPending(pending);
                                    insert.set(false);
                                }
                            });

                        }
                    }
                }
            }
    );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);

        getSupportActionBar().setTitle("All Tasks Page");
        user = (User) getIntent().getSerializableExtra("user");
        Alltasks_listView = findViewById(R.id.Todaytasks);

        tasksViewModel = new ViewModelProvider(this).get(TasksViewModel.class);
        pendingsViewModel = new ViewModelProvider(this).get(PendingsViewModel.class);
        addTask_button = findViewById(R.id.addTask_button);
        notificationSystem = NotificationSystem.getInstance(this);
        addTask_button.setOnClickListener(view -> {
            System.out.println("button clicked");
            Intent intent = new Intent(this, Task_edit.class);
            intent.putExtra("user",user);
            intent.putExtra("target","create");
            activityResultLauncher.launch(intent);
        });

        Alltasks_listView.addItemDecoration(new DividerItemDecoration(this, new LinearLayoutManager(getApplicationContext()).getOrientation()));

        tasksViewModel.getAllTasks(user.getId()).observe(this, tasks -> {
            this.tasks = (ArrayList<Task>) tasks;
            TaskAdapter taskAdapter = new TaskAdapter((ArrayList<Task>) tasks, task -> {
                Intent intent = new Intent(this, Task_edit.class);
                intent.putExtra("target","edit");
                intent.putExtra("task",task);
                intent.putExtra("user",user);
                activityResultLauncher.launch(intent);
            });
            Alltasks_listView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            Alltasks_listView.setAdapter(taskAdapter);

        });

        pendingsViewModel.getAllPendings().observe(this, pendings -> {
            if(pendings!=null){
                System.out.println("pendings:"+pendings);
                notificationSystem.setPendingReflection(pendings);
            }
        });

        nav_menu = findViewById(R.id.bottomNavigationView);
        nav_menu.setOnItemSelectedListener(item -> {
            if(item.getItemId()==R.id.Today_Page) {
                Intent intent = new Intent(this, TodayActivity.class);
                intent.putExtra("user",user);
                startActivity(intent);
                return true;
            }
            else if(item.getItemId()==R.id.Tasks_Page){
                System.out.println("nothing just stay at TASKS Page");
            }
            else if(item.getItemId()==R.id.Profile_Page){
                Intent intent = new Intent(this, ProfileActivity.class);
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
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        // Get the position from the tag
        if(item.getItemId()==R.id.mark_done){
            Task task = tasks.get(item.getGroupId());
            task.setStatus(true);
            tasksViewModel.updateTask(task);
            if(task.getReminder()!=null) {
                pendingsViewModel.updatePending(new Pending(task.getId(), task.getName(),task.getReminder(), !task.isStatus()));
            }
            return true;
        }
        else if(item.getItemId()==R.id.delete_task){
            Task task = tasks.get(item.getGroupId());
            tasksViewModel.deleteTask(task);
            if(task.getReminder()!=null) {
                pendingsViewModel.deletePending(new Pending(task.getId(), task.getName(),task.getReminder(), !task.isStatus()));
            }
            return true;
        }
        else if(item.getItemId()==R.id.mark_undone){
            Task task = tasks.get(item.getGroupId());
            task.setStatus(false);
            tasksViewModel.updateTask(task);
            if(task.getReminder()!=null) {
                pendingsViewModel.updatePending(new Pending(task.getId(), task.getName(),task.getReminder(), !task.isStatus()));
            }
            return true;
        }
        else return super.onContextItemSelected(item);
    }
}