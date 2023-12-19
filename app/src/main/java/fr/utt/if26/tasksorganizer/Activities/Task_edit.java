package fr.utt.if26.tasksorganizer.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.atomic.AtomicBoolean;

import fr.utt.if26.tasksorganizer.Entities.Task;
import fr.utt.if26.tasksorganizer.Entities.User;
import fr.utt.if26.tasksorganizer.R;
import fr.utt.if26.tasksorganizer.Utils.Code;
import fr.utt.if26.tasksorganizer.Utils.DateUtil;

public class Task_edit extends AppCompatActivity  {

    private Button dueDateButton, dueTimeButton;
    private Button reminderDateButton, reminderTimeButton;
    private int year, month, day, hour=-1, minute=-1;
    private int reminderYear, reminderMonth, reminderDay, reminderHour=-1, reminderMinute=-1;

    private Date duedate;
    private Date remindingDate;
    private DatePickerDialog datePickerDialog;
    private DatePickerDialog reminderdatePickerDialog;
    private EditText et_taskName;
    private EditText et_taskDescription;
    private RadioGroup priorityRadioGroup;
    private RadioButton selectedPriorityButton;
    private RadioButton taskpriority1;
    private RadioButton taskpriority2;
    private RadioButton taskpriority3;
    private RadioButton completed;
    private RadioButton uncompleted;
    private RadioGroup statusRadioGroup;
    private RadioButton selectedStatusButton;
    private Button finishEditing;
    private Button cancel;

    private String target;
    private RadioButton[] priorities;
    private Task task;
    private AtomicBoolean remindingDateModified = new AtomicBoolean(false);
    private AtomicBoolean remindingDateSet = new AtomicBoolean(false);
    private boolean statusModified = false;
    private boolean taskNameModified =false;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);
        user = (User) getIntent().getSerializableExtra("user");
        System.out.println("user creating the task:"+user);
        finishEditing = findViewById(R.id.finish);
        statusRadioGroup = findViewById(R.id.edit_taskStatusGroup);
        priorityRadioGroup = findViewById(R.id.edit_taskPriorityGroup);
        et_taskDescription = findViewById(R.id.edit_taskDescription);
        et_taskName = findViewById(R.id.edit_taskName);
        taskpriority1 = findViewById(R.id.edit_taskPriority1);
        taskpriority2 = findViewById(R.id.edit_taskPriority2);
        taskpriority3 = findViewById(R.id.edit_taskPriority3);
        priorities = new RadioButton[]{taskpriority1,taskpriority2,taskpriority3};
        completed = findViewById(R.id.edit_taskStatusTrue);
        uncompleted = findViewById(R.id.edit_taskStatusFalse);
        dueDateButton = findViewById(R.id.edit_taskDueDate);
        dueTimeButton = findViewById(R.id.edit_taskTime);
        reminderDateButton = findViewById(R.id.edit_taskReminderDate);
        reminderTimeButton = findViewById(R.id.edit_taskReminderTime);
        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(view -> {
            Intent intent = new Intent();
            setResult(Code.CANCEL.getValue(),intent);
            finish();
        });
        target= getIntent().getStringExtra("target");
        if(getIntent().getStringExtra("source")!=null){
            if(getIntent().getStringExtra("source").equals("today")) {
              int[] ymd=  DateUtil.getYearMothDayValues(new Date());
              System.out.println("month ymd:"+ymd[1]);
                dueDateButton.setText(DateUtil.getYearMonthDayFromDate(new Date()));
                duedate =  DateUtil.computeDate(ymd[0],ymd[1],ymd[2],23,55);
                year = ymd[0];
                month = ymd[1];
                day = ymd[2];
                hour = 23;
                minute = 55;
                dueTimeButton.setText(hour+":"+minute);
                System.out.println("default date:"+duedate);
            }
        }
        if(target.equals("edit")) {
           initTaskEditPage();
        } else if (target.equals("create")) {
            task = new Task(user.getId(),"");
        }

        initDatePicker((dueYear, dueMonth, dueDay) -> {

            this.year = dueYear;
            this.month = dueMonth;
            this.day = dueDay;
            Calendar calendar = new GregorianCalendar(year,month,day);
//            duedate = calendar.getTime();
//            System.out.println("Due Date picked:"+duedate);

        },(reminderYear, reminderMonth, reminderDay) -> {

            this.reminderYear = reminderYear;
            this.reminderMonth = reminderMonth;
            this.reminderDay = reminderDay;
            Calendar calendar = new GregorianCalendar(reminderYear,reminderMonth,reminderDay);
            remindingDate = calendar.getTime();
            System.out.println("Reminding Date picked:"+remindingDate);

        });


        dueTimeButton.setOnClickListener(view -> {
            DialogFragment timePicker = new TimePicker((timePicker1, hourOfDay, minute) -> {
                dueTimeButton.setText(hourOfDay+":"+minute);
                this.hour = hourOfDay;
                this.minute = minute;
            });
            timePicker.show(getSupportFragmentManager(), "time picker");
        });



        reminderTimeButton.setOnClickListener(view -> {
            DialogFragment timePicker = new TimePicker((timePicker2, hour, minute) -> {
                reminderTimeButton.setText(hour+":"+minute);
                this.reminderHour = hour;
                this.reminderMinute = minute;
                remindingDateModified.set(true);
            });
            timePicker.show(getSupportFragmentManager(), "reminder time picker");
        });

        finishEditing.setOnClickListener(finishButton_listener());

    }

    private void initDatePicker(IOnDateSet dateSet, IOnReminderDateSet reminderDateSet){

        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            dateSet.setYearMonthDay(year, month, day);
            month++;
            dueDateButton.setText(DateUtil.getAngDateFormat(year, month, day));

        };
        DatePickerDialog.OnDateSetListener reminderDateSetListener = (datePicker, year, month, day) -> {
            reminderDateSet.setReminderYearMonthDay(year, month, day);
            month++;
            reminderDateButton.setText(DateUtil.getAngDateFormat(year, month, day));
            if(task.getReminder()!=null) remindingDateModified.set(true);
            if(task.getReminder()==null) remindingDateSet.set(true);
        };
        Calendar cal = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT,dateSetListener,
                cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
        System.out.println("date picker init:"+duedate);
        if(duedate!=null) {
            System.out.println("yes in");
            int [] ymd = DateUtil.getYearMothDayValues(duedate);
            datePickerDialog = new DatePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, dateSetListener,
                    ymd[0], ymd[1], ymd[2]);
        }
        datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());

        reminderdatePickerDialog = new DatePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT,reminderDateSetListener,
                cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
        if(remindingDate!=null){
            int [] ymd = DateUtil.getYearMothDayValues(remindingDate);
            reminderdatePickerDialog = new DatePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT,reminderDateSetListener,
                    ymd[0], ymd[1], ymd[2]);
        }
        reminderdatePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
    }

    public void openDatePicker(View view){
        datePickerDialog.show();
    }
    public void openReminderDatePicker(View view){
        reminderdatePickerDialog.show();
    }

//    @Override
//    public void onTimeSet(android.widget.TimePicker timePicker, int hourOfDay, int minute) {
//        dueTimeButton.setText(hourOfDay+":"+minute);
//        this.hour = hourOfDay;
//        this.minute = minute;
//    }



    private interface IOnDateSet{
        void setYearMonthDay(int year, int month, int day);
    }
    private interface IOnReminderDateSet{
        void setReminderYearMonthDay(int year, int month, int day);
    }

    private View.OnClickListener finishButton_listener(){
        return view -> {
            String taskName = et_taskName.getText().toString().trim();
            int priority=0;
            String status_str="";
            boolean status = false;

            System.out.println("taskName:"+taskName);
            if(taskName.isEmpty()) {
               System.out.println("taskname is empty or blank:"+taskName);
               TextView taskNameLabel = findViewById(R.id.edit_taskNameLabel);

               taskNameLabel.setText(this.getResources().getString(R.string.taskName)+"(required field)");
               taskNameLabel.setTextColor(Color.RED);
               return;
           }
           String description = et_taskDescription.getText().toString().trim();
            System.out.println("description:"+description);// default: empty
            selectedPriorityButton = findViewById(priorityRadioGroup.getCheckedRadioButtonId());
            if(selectedPriorityButton!=null) {
                priority = Integer.parseInt(selectedPriorityButton.getText().toString());
            }
            System.out.println("priority:" + priority);
            System.out.println("year:"+year+"; month:"+month+"; day:"+day);// default:0
            System.out.println(hour+":"+minute);// default:0
            System.out.println("reminder year:"+reminderYear);// default:0
            System.out.println("reminder hour:"+reminderHour);// default:0


         selectedStatusButton = findViewById((statusRadioGroup.getCheckedRadioButtonId()));
         if(selectedStatusButton!=null){
             status_str = selectedStatusButton.getText().toString();
            if(status_str.equals(this.getResources().getString(R.string.completed))) status = true;
         }
            System.out.println("status_str:"+status_str +"; status:"+status);
           if((hour!=-1 && year==0) ){
                TextView dueDateLabel = findViewById(R.id.edit_taskDueDateLabel);
               dueDateLabel.setText(this.getResources().getString(R.string.due_date)+"(if u pick a time u have to pick a date)");
               dueDateLabel.setTextColor(Color.RED);
               dueDateLabel.setTextSize(17);
               return;
           }
           if((reminderHour!=-1 && reminderYear==0)){
               TextView reminderDateLabel = findViewById(R.id.edit_taskReminderDateLabel);
               reminderDateLabel.setText(this.getResources().getString(R.string.reminder_date)+"(if u pick a time u have to pick a date)");
               reminderDateLabel.setTextColor(Color.RED);
               reminderDateLabel.setTextSize(17);
               return;
           }

        boolean coherentDates = checkDatesCoherenceAndCompute();
           System.out.println("date coherence:"+coherentDates);
        if(!coherentDates){
            if(year==0 && reminderYear!=0){
                TextView reminderDateLabel = findViewById(R.id.edit_taskReminderDateLabel);
                reminderDateLabel.setText(this.getResources().getString(R.string.reminder_date)+"(u cant pick a reminder if there is no due date)");
                reminderDateLabel.setTextColor(Color.RED);
                reminderDateLabel.setTextSize(17);
                return;
            }
            TextView reminderDateLabel = findViewById(R.id.edit_taskReminderDateLabel);
            reminderDateLabel.setText(this.getResources().getString(R.string.reminder_date)+"(reminder date should be before the due date)");
            reminderDateLabel.setTextColor(Color.RED);
            reminderDateLabel.setTextSize(17);
            return;
        }

        if(!task.getName().equals(taskName)) taskNameModified = true;
        task.setName(taskName);
        task.setDescription(description);
        task.setPriority(priority);
        task.setDuedate(duedate);
        System.out.println("EDIT remindingDate:"+remindingDate);
        task.setReminder(remindingDate);

        if(task.isStatus()!=status) statusModified = true;
        task.setStatus(status);

            Intent intent = new Intent();
           if(target.equals("edit")) {
               intent.putExtra("updatedTask", task);
               intent.putExtra("remindingDateModified", remindingDateModified.get());
               intent.putExtra("remindingDateSet", remindingDateSet.get());
               intent.putExtra("statusModified",statusModified);
               intent.putExtra("taskNameModified",taskNameModified);
               System.out.println("updatedTask:" + task);
               setResult(Code.EDIT_SUCCESS.getValue(), intent);
           } else if (target.equals("create")) {
               intent.putExtra("newTask", task);
               System.out.println("newTask:" + task);
               setResult(Code.CREATE_SUCCESS.getValue(), intent);
           }
            finish();
//
        };
    }

    private void initTaskEditPage(){
        task= (Task)getIntent().getSerializableExtra("task");
        System.out.println("target=>edit; task:"+task);
        et_taskName.setText(task.getName());
        et_taskDescription.setText(task.getDescription());
        if(task.getPriority()>0 && task.getPriority()<=priorities.length) priorities[task.getPriority()-1].setChecked(true);
        if(task.getDuedate()!=null) {
            dueDateButton.setText(DateUtil.getYearMonthDayFromDate(task.getDuedate()));
            dueTimeButton.setText(DateUtil.getHourMinuteFromDate(task.getDuedate()));
            int[] ymd = DateUtil.getYearMothDayValues(task.getDuedate());
            year = ymd[0];
            month = ymd[1];
            day = ymd[2];
            int [] hm = DateUtil.getHourMinuteValues(task.getDuedate());
            hour = hm[0];
            minute = hm[1];
            duedate = task.getDuedate();
        }
        if(task.getReminder()!=null) {
            Date date = task.getReminder();
            reminderDateButton.setText(DateUtil.getYearMonthDayFromDate(date));
            reminderTimeButton.setText(DateUtil.getHourMinuteFromDate(date));
            int[] ymd = DateUtil.getYearMothDayValues(date);
            reminderYear = ymd[0];
            reminderMonth = ymd[1];
            reminderDay = ymd[2];
            int [] hm = DateUtil.getHourMinuteValues(date);
            reminderHour = hm[0];
            reminderMinute = hm[1];
            remindingDate = task.getReminder();
        }
        uncompleted.setChecked(true);
        if(task.isStatus()) completed.setChecked(true);
    }

    private boolean checkDatesCoherenceAndCompute(){
        if(year==0 && reminderYear!=0) return false;
        if(year!=0 ){
            this.duedate = DateUtil.computeDate(year,month,day,hour,minute);
        }
        if(reminderYear!=0){
            this.remindingDate = DateUtil.computeDate(reminderYear,reminderMonth,reminderDay,reminderHour,reminderMinute);
        }
        if(year!=0 && reminderYear!=0 ){
            int result = remindingDate.compareTo(duedate);
            if(result>=0) return  false;
        }
        return true;
    }

}