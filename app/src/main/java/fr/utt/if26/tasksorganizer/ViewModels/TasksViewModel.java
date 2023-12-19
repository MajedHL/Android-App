package fr.utt.if26.tasksorganizer.ViewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.ArrayList;
import java.util.List;

import fr.utt.if26.tasksorganizer.Entities.Task;
import fr.utt.if26.tasksorganizer.Entities.User;
import fr.utt.if26.tasksorganizer.Repositories.TasksRepository;
import fr.utt.if26.tasksorganizer.Repositories.UsersRepository;
import fr.utt.if26.tasksorganizer.Utils.CustomLiveData;

public class TasksViewModel extends AndroidViewModel {

    private TasksRepository tasksRepository;

    private MutableLiveData<Boolean> hideDoneTasks = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> sortByDueDate = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> sortByCompletion = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> sortByPriority = new MutableLiveData<>(false);

    private MutableLiveData<Boolean> sortByLateness = new MutableLiveData<>(false);

    private List<String> sortingOrder = new ArrayList<>();

    private LiveData<List<Task>> allTasks;
    private MutableLiveData<LiveData<List<Task>>> allMutableTasks;

    private CustomLiveData trigger;

    public TasksViewModel(Application application) {
        super(application);
        tasksRepository = new TasksRepository(application);
        trigger = new CustomLiveData(hideDoneTasks, sortByDueDate, sortByCompletion,sortByPriority, sortByLateness);
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

    public LiveData<Integer> countCompletedTasks(Integer id){return tasksRepository.countCompletedTasks(id);}

    public LiveData<Integer> countUnCompletedTasks(Integer id){return tasksRepository.countUnCompletedTasks(id);}

    public LiveData<List<Task>> getTasksWithReminders(Integer id){return  tasksRepository.getTasksWithReminders(id);}

    public LiveData<List<Task>> getAllTasks(int id) {
        allTasks = Transformations.switchMap(trigger, value->{
                return tasksRepository.getFilteredTasks(id,value.getHide(), sortingOrder);//
        });
        return allTasks;
    }

    public LiveData<Integer> getMaxId(int id){
        return tasksRepository.getMaxId(id);
    }

    public void setHideDoneTasks() {
        this.hideDoneTasks.setValue(!this.hideDoneTasks.getValue());
    }

    public void setSortByDueDate(){
        if(!this.sortByDueDate.getValue()) sortingOrder.add("DueDate");
        else sortingOrder.remove("DueDate");
        this.sortByDueDate.setValue(!this.sortByDueDate.getValue());
    }

    public void setSortByCompletion() {
        if(!this.sortByCompletion.getValue()) sortingOrder.add("STATUS");
        else sortingOrder.remove("STATUS");
        this.sortByCompletion.setValue(!this.sortByCompletion.getValue());
    }

    public void setSortByPriority() {
        if(!this.sortByPriority.getValue()) sortingOrder.add("priority desc");
        else sortingOrder.remove("priority desc");
        this.sortByPriority.setValue(!this.sortByPriority.getValue());
    }

    public void setSortByLateness(){
       String sortParam = "CASE WHEN duedate < strftime('%s', 'now') * 1000 and status = 0 THEN 0 ELSE 1 END";
        if(!this.sortByLateness.getValue()) sortingOrder.add(sortParam);
        else sortingOrder.remove(sortParam);
        this.sortByLateness.setValue(!this.sortByLateness.getValue());
    }
}
