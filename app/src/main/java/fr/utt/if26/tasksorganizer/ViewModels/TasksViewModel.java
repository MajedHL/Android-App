package fr.utt.if26.tasksorganizer.ViewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.List;

import fr.utt.if26.tasksorganizer.Entities.Task;
import fr.utt.if26.tasksorganizer.Entities.User;
import fr.utt.if26.tasksorganizer.Repositories.TasksRepository;
import fr.utt.if26.tasksorganizer.Repositories.UsersRepository;

public class TasksViewModel extends AndroidViewModel {

    private TasksRepository tasksRepository;

    private MutableLiveData<Boolean> hideDoneTasks = new MutableLiveData<>(true);
    private LiveData<List<Task>> allTasks;

    public TasksViewModel(Application application) {
        super(application);
        tasksRepository = new TasksRepository(application);
    }
//    public LiveData<List<Task>> getAllTasks() {
//        return tasksRepository.getLiveTaskList();
//    }

    public void insertTask(Task task) {
        System.out.println("parentID: " + task.getParentTaskID());
        System.out.println("ID: " + task.getId());
        tasksRepository.insertTask(task);
    }

    public void deleteAllTasks(){
        tasksRepository.deleteAllTasks();
    }

    public void deleteTask(Task task){tasksRepository.deleteTask(task);}

    public void updateTask(Task task){tasksRepository.updateTask(task);}

    public LiveData<List<Task>> getAllTasks() {
        allTasks = Transformations.switchMap(hideDoneTasks, hide->{
            if(hide) return tasksRepository.getUnDoneTasks();
            else  return  tasksRepository.getLiveTaskList();
        });
        System.out.println("called it ?");
//        return tasksRepository.getLiveTaskList();
        return allTasks;
    }

    public void setHideDoneTasks(Boolean newValue) {
        this.hideDoneTasks.setValue(newValue);
    }

    public void sortByDueDate(){


    }
}
