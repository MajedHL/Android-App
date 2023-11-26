package fr.utt.if26.tasksorganizer.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import fr.utt.if26.tasksorganizer.R;
import fr.utt.if26.tasksorganizer.Utils.DateUtil;

public class Task_edit extends AppCompatActivity  {

    private Button dueDateButton, dueTimeButton;
    private Button reminderDateButton, reminderTimeButton;
    private int year, month, day, hour, minute;
    private int reminderYear, reminderMonth, reminderDay, reminderHour, reminderMinute;

    private Date duedate;
    private Date remindingDate;
    private DatePickerDialog datePickerDialog;
    private DatePickerDialog reminderdatePickerDialog;
    private EditText et_taskName;
    private EditText et_taskDescription;
    private RadioGroup priorityRadioGroup;
    private RadioButton selectedPriorityButton;
    private RadioGroup statusRadioGroup;
    private RadioButton selectedStatusButton;
    private Button finishEditing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);
        finishEditing = findViewById(R.id.finish_editing);
        statusRadioGroup = findViewById(R.id.edit_taskStatusGroup);
        priorityRadioGroup = findViewById(R.id.edit_taskPriorityGroup);
        et_taskDescription = findViewById(R.id.edit_taskDescription);
        et_taskName = findViewById(R.id.edit_taskName);

        initDatePicker((dueYear, dueMonth, dueDay) -> {

            this.year = dueYear;
            this.month = dueMonth;
            this.day = dueDay;
            Calendar calendar = new GregorianCalendar(year,month,day);
            duedate = calendar.getTime();
            System.out.println("Due Date picked:"+duedate);

        },(reminderYear, reminderMonth, reminderDay) -> {

            this.reminderYear = reminderYear;
            this.reminderMonth = reminderMonth;
            this.reminderDay = reminderDay;
            Calendar calendar = new GregorianCalendar(reminderYear,reminderMonth,reminderDay);
            remindingDate = calendar.getTime();
            System.out.println("Reminding Date picked:"+remindingDate);

        });
        dueDateButton = findViewById(R.id.edit_taskDueDate);
        dueTimeButton = findViewById(R.id.edit_taskTime);

        dueTimeButton.setOnClickListener(view -> {
            DialogFragment timePicker = new TimePicker((timePicker1, hourOfDay, minute) -> {
                dueTimeButton.setText(hourOfDay+":"+minute);
                this.hour = hourOfDay;
                this.minute = minute;
            });
            timePicker.show(getSupportFragmentManager(), "time picker");
        });

        reminderDateButton = findViewById(R.id.edit_taskReminderDate);
        reminderTimeButton = findViewById(R.id.edit_taskReminderTime);

        reminderTimeButton.setOnClickListener(view -> {
            DialogFragment timePicker = new TimePicker((timePicker2, hour, minute) -> {
                reminderTimeButton.setText(hour+":"+minute);
                this.reminderHour = hour;
                this.reminderMinute = minute;
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

        };
        Calendar cal = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT,dateSetListener,
                cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());

        reminderdatePickerDialog = new DatePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT,reminderDateSetListener,
                cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
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
            String taskName = et_taskName.getText().toString();
           if(taskName.isEmpty() || taskName.trim().isEmpty()) {
               System.out.println("taskname is empty or blank:"+taskName);
               TextView taskNameLabel = findViewById(R.id.edit_taskNameLabel);

               taskNameLabel.setText(this.getResources().getString(R.string.taskName)+"(required field)");
               taskNameLabel.setTextColor(Color.RED);
           }
//
        };
    }

}