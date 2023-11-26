package fr.utt.if26.tasksorganizer.DAOs;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fr.utt.if26.tasksorganizer.Entities.User;

@Dao
public interface UsersDAO {
    @Insert
    public void insertUser(User user);
    @Update
    public void updateUser(User user);
    @Delete
    public void deleteUser(User user);

    @Query("SELECT * FROM users ;")
    public LiveData<List<User>> getAllUsers();

    @Query("Delete from Users;")
    void deleteAllUsers();
}
