package fr.utt.if26.tasksorganizer.Activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import fr.utt.if26.tasksorganizer.Adapters.TaskAdapter;
import fr.utt.if26.tasksorganizer.Entities.Consent;
import fr.utt.if26.tasksorganizer.Entities.Pending;
import fr.utt.if26.tasksorganizer.Entities.Task;
import fr.utt.if26.tasksorganizer.Entities.User;
import fr.utt.if26.tasksorganizer.R;
import fr.utt.if26.tasksorganizer.Utils.Code;
import fr.utt.if26.tasksorganizer.Utils.DateUtil;
import fr.utt.if26.tasksorganizer.Utils.NotificationSystem;
import fr.utt.if26.tasksorganizer.ViewModels.ConsentsViewModel;
import fr.utt.if26.tasksorganizer.ViewModels.PendingsViewModel;
import fr.utt.if26.tasksorganizer.ViewModels.TasksViewModel;
import fr.utt.if26.tasksorganizer.ViewModels.UsersViewModel;
import android.Manifest;

public class TodayActivity extends AppCompatActivity {


    private TasksViewModel tasksViewModel;

    private ImageButton addTask_button;
    private BottomNavigationView nav_menu;

    private RecyclerView Todaytasks_listView;
    private ArrayList<Task> todayTasks;
    private User user;

    private NotificationSystem notificationSystem;

    private PendingsViewModel pendingsViewModel;
    private CheckBox agree;
    private Button Continue;
    private ConsentsViewModel consentsViewModel;


    private ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
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
                                     pendingsViewModel.insertPending(new Pending(task.getId(), task.getName(),task.getReminder(), !task.isStatus()));
                            }
                        }
                        else if(remindingDateModified || statusModified || taskNameModified){
                            if(task.getReminder()!=null) {
                                pendingsViewModel.updatePending(new Pending(task.getId(),task.getName(), task.getReminder(), !task.isStatus()));
                            }
                        }
                    }
                    else if(code == Code.CREATE_SUCCESS.getValue()){
                        Task newTask = (Task) result.getData().getSerializableExtra("newTask");
                        tasksViewModel.insertTask(newTask);
                        Toast.makeText(getApplicationContext()," task created successfully",Toast.LENGTH_LONG).show();
                        if(newTask.getReminder()!=null) {
                            AtomicBoolean insert =new AtomicBoolean(true);
                            tasksViewModel.getMaxId(user.getId()).observe(TodayActivity.this, max -> {
                                if (max != null && insert.get()) {
                                    Pending pending = new Pending(max, newTask.getName(), newTask.getReminder(), !newTask.isStatus());
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
        getSupportActionBar().setTitle("Today Page");
        user = (User) getIntent().getSerializableExtra("user");
        Todaytasks_listView = findViewById(R.id.Todaytasks);
        tasksViewModel = new ViewModelProvider(this).get(TasksViewModel.class);
        pendingsViewModel = new ViewModelProvider(this).get(PendingsViewModel.class);
        notificationSystem = NotificationSystem.getInstance(this);

        checkConsentement(() -> {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(TodayActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);

                }else {
                    notificationSystem.activate();
                }
            }
        });







        addTask_button = findViewById(R.id.addTask_button);
        addTask_button.setOnClickListener(view -> {
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
        });

        pendingsViewModel.getAllPendings().observe(this, pendings -> {
            if(pendings!=null){
                notificationSystem.setPendingReflection(pendings);
            }
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


//
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        // Get the position from the tag
        if(item.getItemId()==R.id.mark_done){
            Task task = todayTasks.get(item.getGroupId());
            task.setStatus(true);
            tasksViewModel.updateTask(task);
            if(task.getReminder()!=null) {
                pendingsViewModel.updatePending(new Pending(task.getId(), task.getName(),task.getReminder(), !task.isStatus()));
            }
            return true;
        }
        else if(item.getItemId()==R.id.delete_task){
            Task task = todayTasks.get(item.getGroupId());
            tasksViewModel.deleteTask(task);
            if(task.getReminder()!=null) {
                pendingsViewModel.deletePending(new Pending(task.getId(),task.getName(), task.getReminder(), !task.isStatus()));
            }
            return true;
        }
        else if(item.getItemId()==R.id.mark_undone){
            Task task = todayTasks.get(item.getGroupId());
            task.setStatus(false);
            tasksViewModel.updateTask(task);
            if(task.getReminder()!=null) {
                pendingsViewModel.updatePending(new Pending(task.getId(),task.getName(), task.getReminder(), !task.isStatus()));
            }
            return true;
        }
        else return super.onContextItemSelected(item);
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 101:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Instantiate NotificationSystem
                    notificationSystem.activate();
                } else {
                    // Permission is denied. Explain to the user that the feature is unavailable because the feature requires a permission that the user has denied.
                    System.out.println("Permission denied");
                }
        }
    }



    private void checkConsentement(Runnable initNotificationSystem){
        // Inflate the custom layout
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_consent_page, null);
        consentsViewModel = new ViewModelProvider(this).get(ConsentsViewModel.class);
        agree = view.findViewById(R.id.consent_agree);
        Continue = view.findViewById(R.id.consent_continue);
        AtomicBoolean consentGiven = new AtomicBoolean(false);
// Create and show the AlertDialog
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(false)
                .create();

// Set the checkbox click listener
        agree.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Update the consentGiven variable
            consentGiven.set(isChecked);
        });

// Set the button click listener
        Continue.setOnClickListener((View.OnClickListener) v -> {
            if (consentGiven.get()) {
                Consent consent = new Consent(user.getId(),getResources().getString(R.string.agreement));
                consentsViewModel.insertConsent(consent);
                // If the user has given consent, dismiss the dialog

            } else {
                // If the user has not given consent, show a message
                agree.setTextColor(Color.RED);
                Toast.makeText(this, "You must agree to the terms to use this app", Toast.LENGTH_SHORT).show();
            }
        });

        consentsViewModel.getConsentByUserId(user.getId()).observe(this, consent->{
            if(consent!=null ){
                if(!consent.getTerms().equals(getResources().getString(R.string.agreement))){
                    TextView title = view.findViewById(R.id.consent_chart_title);
                    title.setText(getResources().getString(R.string.consent_title)+" updated");
                    alertDialog.show();
                }
                else{
                    alertDialog.dismiss();
                    initNotificationSystem.run();
                }
            }else {
                alertDialog.show();
            }
        });
    }


}