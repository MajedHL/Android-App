package fr.utt.if26.tasksorganizer.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.io.Serializable;
import java.util.Date;

import fr.utt.if26.tasksorganizer.Converters.DateConverter;

@Entity(tableName = "pendings",foreignKeys = { @ForeignKey( // ensures that request code references an existing Task
        entity = Task.class,
        parentColumns = "id",
        childColumns = "request_code",
        onDelete = ForeignKey.CASCADE
)})
@TypeConverters(DateConverter.class)
public class Pending implements Serializable {
    @ColumnInfo(name = "request_code")
    @PrimaryKey
    @NonNull
    private Integer request_code;
    @ColumnInfo(name = "name")
    @NonNull
    private String name;
    @ColumnInfo(name = "date")
    @NonNull
    private Date date;
    @ColumnInfo(name = "active")
    @NonNull
    private boolean active;

    public Pending(@NonNull Integer request_code, @NonNull String name, @NonNull Date date, boolean active) {
        this.request_code = request_code;
        this.name = name;
        this.date = date;
        this.active = active;
    }

    @NonNull
    public Integer getRequest_code() {
        return request_code;
    }

    public void setRequest_code(@NonNull Integer request_code) {
        this.request_code = request_code;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public Date getDate() {
        return date;
    }

    public void setDate(@NonNull Date date) {
        this.date = date;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "Pending{" +
                "request_code=" + request_code +
                ", name='" + name + '\'' +
                ", date=" + date +
                ", active=" + active +
                '}';
    }
}
