package fr.utt.if26.tasksorganizer.Repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import fr.utt.if26.tasksorganizer.DAOs.TasksDAO;
import fr.utt.if26.tasksorganizer.Entities.Task;
import fr.utt.if26.tasksorganizer.RoomDB;

public class TasksRepository {

    private final TasksDAO tasksDAO;
    private final LiveData<List<Task>> liveTaskList;

    public TasksRepository(Application app) {
        RoomDB bd = RoomDB.getInstance(app);
        tasksDAO = bd.tasksDAO();
        liveTaskList = tasksDAO.getAllTasks();
    }

    public LiveData<List<Task>> getLiveTaskList() {
        return liveTaskList;
    }

    public void insertTask(Task task){
        RoomDB.service.execute(()->tasksDAO.insertTask(task));
    }

    public void deleteAllTasks(){
        RoomDB.service.execute(()->tasksDAO.deleteAllTasks());
    }

    public void deleteTask(Task task){RoomDB.service.execute(()->tasksDAO.deleteTask(task));}

    public void updateTask(Task task){RoomDB.service.execute(()->tasksDAO.updateTask(task));}

    public LiveData<List<Task>> getDoneTasks(){
        Future<LiveData<List<Task>>> doneTasks = RoomDB.service.submit(()->tasksDAO.getDoneTasks());
        try {
            return doneTasks.get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public LiveData<List<Task>> getUnDoneTasks(){
        Future<LiveData<List<Task>>> doneTasks = RoomDB.service.submit(()->tasksDAO.getUnDoneTasks());
        try {
            return doneTasks.get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}
