package fr.utt.if26.tasksorganizer.ViewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import fr.utt.if26.tasksorganizer.Entities.User;
import fr.utt.if26.tasksorganizer.Repositories.UsersRepository;

public class UsersViewModel extends AndroidViewModel {
    private UsersRepository usersRepository;

    public UsersViewModel(Application application) {
        super(application);
        usersRepository = new UsersRepository(application);
    }
    public LiveData<List<User>> getAllUsers() {
        return usersRepository.getLiveUserList();
    }

    public void insertUser(User user){
        usersRepository.insertUser(user);
    }
    public void updateUser(User user){
        usersRepository.updateUser(user);
    }

    public LiveData<User> getUser(String userName, String password){return usersRepository.getUser(userName, password);}

    public LiveData<User> getUserByUserName(String userName){return usersRepository.getUserByUserName(userName);}
    public LiveData<List<User>> getUserByUserNameOrEmail(String userName, String email){
        return usersRepository.getUserByUserNameOrEmail(userName,email);
    }

    public void deleteAllUsers(){usersRepository.deleteAllusers();}

    public void deleteUserById(int id){usersRepository.delteUserById(id);}
}
