package fr.utt.if26.tasksorganizer.DAOs;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SupportSQLiteQuery;

import java.util.List;

import fr.utt.if26.tasksorganizer.Entities.Task;
import fr.utt.if26.tasksorganizer.Entities.User;

@Dao
public interface TasksDAO {
    @Insert
    public void insertTask(Task task);
    @Update
    public void updateTask(Task task);
    @Delete
    public void deleteTask(Task task);
    @Query("SELECT * FROM Tasks ;")
    public LiveData<List<Task>> getAllTasks();
    @Query("Delete from Tasks;")
    void deleteAllTasks();
    @Query("SELECT * FROM Tasks WHERE STATUS = 1 ;")
    public LiveData<List<Task>> getDoneTasks();

    @Query("SELECT * FROM Tasks WHERE STATUS != 1 ;")
    public LiveData<List<Task>> getUnDoneTasks();
    @Query("SELECT * FROM Tasks order by Duedate;")
    public LiveData<List<Task>> getSortedTasks();
    @RawQuery(observedEntities = Task.class)
    public LiveData<List<Task>> getFilteredTasks(SupportSQLiteQuery query);
    @Query("SELECT COUNT(*) from Tasks where userId=:id and status = 1")
    public LiveData<Integer> countCompletedTasks(Integer id);
    @Query("SELECT COUNT(*) from Tasks where userId=:id and status != 1")
    public LiveData<Integer> countUnCompletedTasks(Integer id);
    @Query("SELECT * FROM Tasks WHERE STATUS != 1 and userId=:id and reminder is not null ;")
    public LiveData<List<Task>> getTasksWithReminders(Integer id);
    @Query("select max(id) from Tasks where userId=:id;")
    public LiveData<Integer> getMaxId(Integer id);


}
