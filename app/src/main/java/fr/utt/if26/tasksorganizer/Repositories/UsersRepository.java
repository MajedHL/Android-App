package fr.utt.if26.tasksorganizer.Repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import fr.utt.if26.tasksorganizer.DAOs.UsersDAO;
import fr.utt.if26.tasksorganizer.Entities.User;
import fr.utt.if26.tasksorganizer.RoomDB;

public class UsersRepository {
    private final UsersDAO usersDAO;
    private final LiveData<List<User>> liveUserList;

    public UsersRepository(Application app) {
        RoomDB bd = RoomDB.getInstance(app);
        usersDAO = bd.usersDAO();
        liveUserList = usersDAO.getAllUsers();
    }

    public LiveData<List<User>> getLiveUserList() {
        return liveUserList;
    }

    public void insertUser(User user){
       RoomDB.service.execute(()->usersDAO.insertUser(user));
    }

    public LiveData<User> getUser(String userName, String password){
       Future<LiveData<User>> user = RoomDB.service.submit(()->usersDAO.getUser(userName, password));
        try {
            return user.get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
