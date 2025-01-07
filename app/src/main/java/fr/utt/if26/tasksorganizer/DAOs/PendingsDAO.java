package fr.utt.if26.tasksorganizer.DAOs;

import android.database.sqlite.SQLiteConstraintException;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fr.utt.if26.tasksorganizer.Entities.Pending;



@Dao
public interface PendingsDAO {
    @Insert
    public void insertPending(Pending pending) throws SQLiteConstraintException;
    @Update
    public void updatePending(Pending pending);
    @Delete
    public void deletePending(Pending pending);
    @Query("SELECT * FROM pendings ;")
    public LiveData<List<Pending>> getAllPendings();
}
