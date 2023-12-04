package fr.utt.if26.tasksorganizer.Repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import fr.utt.if26.tasksorganizer.DAOs.TasksDAO;
import fr.utt.if26.tasksorganizer.Entities.Task;
import fr.utt.if26.tasksorganizer.RoomDB;

public class TasksRepository {

    private final TasksDAO tasksDAO;
//    private final LiveData<List<Task>> liveTaskList;

    public TasksRepository(Application app) {
        RoomDB bd = RoomDB.getInstance(app);
        tasksDAO = bd.tasksDAO();
//        liveTaskList = tasksDAO.getAllTasks();
    }

//    public LiveData<List<Task>> getLiveTaskList() {
//        return liveTaskList;
//    }

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

    public LiveData<List<Task>> getSortedTasks(){
        Future<LiveData<List<Task>>> sortedTasks = RoomDB.service.submit(()->tasksDAO.getSortedTasks());
        try {
            return sortedTasks.get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }



    public LiveData<List<Task>> getFilteredTasks(int id,boolean hideCompletedTasks, List<String> sortingOrder){

        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM Tasks WHERE userId = "+id);

        if(hideCompletedTasks) queryBuilder.append(" AND STATUS != 1");
//        queryBuilder.append(" ORDER BY STATUS");
//        if(sortByDueDate) queryBuilder.append(", DueDate");
//        if(!sortByCompletion) {
//            String s = queryBuilder.toString();
//           String replacement = s.replace("ORDER BY STATUS","ORDER BY STATUS desc");
//           queryBuilder =new StringBuilder();
//           queryBuilder.append(replacement);
//        }
//        if(sortByPriority) queryBuilder.append(", priority desc");
        if(sortingOrder.size()>0){
            queryBuilder.append(" Order by "+sortingOrder.get(0));
           if(sortingOrder.size()>1) {queryBuilder.append(", ");
               queryBuilder.append(String.join(", ", sortingOrder.subList(1, sortingOrder.size())));
           }
        }
        queryBuilder.append(";");
        System.out.println("query:"+queryBuilder);
        SimpleSQLiteQuery query = new SimpleSQLiteQuery(queryBuilder.toString());
        Future<LiveData<List<Task>>> filteredTasks = RoomDB.service.submit(()->tasksDAO.getFilteredTasks(query));
        try {
            return filteredTasks.get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }


}
