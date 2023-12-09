package fr.utt.if26.tasksorganizer.DAOs;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fr.utt.if26.tasksorganizer.Entities.User;

@Dao
public interface UsersDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void insertUser(User user);
    @Update
    public void updateUser(User user);
    @Delete
    public void deleteUser(User user);

    @Query("SELECT * FROM users ;")
    public LiveData<List<User>> getAllUsers();

    @Query("Delete from Users where id<>1 and id<>2;")
    void deleteAllUsers();
    @Query("SELECT * from users where userName=:userName and password=:password")
    public LiveData<User> getUser(String userName, String password);

    @Query("SELECT * from users where userName=:userName;")
    public LiveData<User> getUserByUserName(String userName);

    @Query("SELECT * from users where userName=:userName OR email=:email;")
    public LiveData<List<User>> getUserByUserNameOrEmail(String userName, String email);
}
