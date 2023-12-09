package fr.utt.if26.tasksorganizer.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import fr.utt.if26.tasksorganizer.Converters.DateConverter;

@Entity(tableName = "tasks", foreignKeys = {
        @ForeignKey( // ensures that userId references an existing User
            entity = User.class,
            parentColumns = "id",
            childColumns = "userId",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey( // ensures that parentTaskID references an existing Task
            entity = Task.class,
            parentColumns = "id",
            childColumns = "parentTaskID",
            onDelete = ForeignKey.CASCADE
        )
    }
)
@TypeConverters(DateConverter.class)
public class Task implements Serializable {
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private Integer id;
    @ColumnInfo(name = "userId")
    @NonNull
    private int userId;
    @ColumnInfo(name = "parentTaskID", defaultValue = "0")
    private Integer parentTaskID;
    @ColumnInfo(name = "name")
    @NonNull
    private String name;
    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "priority")
    private int priority;
    @ColumnInfo(name = "labelId")
    private int labelId;
    @ColumnInfo(name = "Duedate")
    private Date Duedate;
    @ColumnInfo(name = "reminder")
    private Date reminder;
    @ColumnInfo(name = "status",defaultValue = "false")
    private boolean status;

    public Task(int userId, @NonNull String name) {
        this.userId = userId;
        this.name = name;
    }

    @NonNull
    public Integer getId() {
        return id;
    }

    public void setId(@NonNull Integer id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Integer getParentTaskID() {
        return parentTaskID;
    }

    public void setParentTaskID(Integer parentTaskID) {
        this.parentTaskID = parentTaskID;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getLabelId() {
        return labelId;
    }

    public void setLabelId(int labelId) {
        this.labelId = labelId;
    }

    public Date getDuedate() {
        return Duedate;
    }
    public String getFormattedDate(Date date){
        if(date==null) return"" ;
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }
    public String getYear(String formattedDate){
        String [] ymdhms = formattedDate.split(" ");
        String [] ymd = ymdhms[0].split("-");
        String year = ymd[0];
        System.out.println("year:"+year);
        return year;
    }


    public void setDuedate(Date duedate) {
        Duedate = duedate;
    }

    public Date getReminder() {
        return reminder;
    }

    public void setReminder(Date reminder) {
        this.reminder = reminder;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isLate(){
       if(this.getDuedate()==null) return false;
        Date today = new Date();
        if(today.after(this.Duedate) && !this.isStatus()) return true;
        return false;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", userId=" + userId +
                ", parentTaskID=" + parentTaskID +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", priority=" + priority +
                ", labelId=" + labelId +
                ", Duedate=" + getFormattedDate(Duedate) +
                ", reminder=" + getFormattedDate(reminder) +
                ", status=" + status +
                '}';
    }
}
