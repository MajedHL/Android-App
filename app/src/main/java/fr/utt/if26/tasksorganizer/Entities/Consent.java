package fr.utt.if26.tasksorganizer.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.text.SimpleDateFormat;
import java.util.Date;

import fr.utt.if26.tasksorganizer.Converters.DateConverter;

@Entity(tableName = "Consents", foreignKeys = @ForeignKey(
        entity = User.class,
        parentColumns = "id",
        childColumns = "userId",
        onDelete = ForeignKey.CASCADE
    )
)
@TypeConverters(DateConverter.class)
public class Consent {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "userId")
    private int userId;
    @ColumnInfo(name = "terms")
    @NonNull
    private String terms;
    @ColumnInfo(name = "timeStamp")
    @NonNull
//    @TypeConverters(DateConverter.class)
    private Date timeStamp;

    public Consent(int userId, @NonNull String terms) {
        this.userId = userId;
        this.terms = terms;
        this.timeStamp = new Date();
    }

    public void setTimeStamp(@NonNull Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getUserId() {
        return userId;
    }

    @NonNull
    public String getTerms() {
        return terms;
    }

    @NonNull
    public Date getTimeStamp() {
        return timeStamp;
    }

    public String getFormattedDate(Date date){
        if(date==null) return"" ;
        return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(date);
    }

    @Override
    public String toString() {
        return "Consent{" +
                " userId=" + userId +
                ", terms='" + terms + '\'' +
                ", timeStamp=" + getFormattedDate(timeStamp) +
                '}';
    }
}
