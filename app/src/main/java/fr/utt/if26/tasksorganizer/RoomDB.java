package fr.utt.if26.tasksorganizer;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import fr.utt.if26.tasksorganizer.Converters.DateConverter;
import fr.utt.if26.tasksorganizer.Cred.Credentials;
import fr.utt.if26.tasksorganizer.DAOs.ConsentsDAO;
import fr.utt.if26.tasksorganizer.DAOs.TasksDAO;
import fr.utt.if26.tasksorganizer.DAOs.UsersDAO;
import fr.utt.if26.tasksorganizer.Entities.Consent;
import fr.utt.if26.tasksorganizer.Entities.Task;
import fr.utt.if26.tasksorganizer.Entities.User;

@Database(entities = {User.class, Task.class, Consent.class}, version = 1)
@TypeConverters({DateConverter.class})
public abstract class RoomDB extends RoomDatabase {

    private static RoomDB Instance = null;
    public abstract UsersDAO usersDAO();
    public abstract TasksDAO tasksDAO();

    public abstract ConsentsDAO consentsDAO();
    public static ExecutorService service = Executors.newSingleThreadExecutor();
    public static RoomDB getInstance(final Context context){
        if(Instance == null){
            synchronized (RoomDB.class){
                if(Instance==null){
                    Instance = Room.databaseBuilder(context.getApplicationContext(),
                                    RoomDB.class, "tasksorganizer.db")
                            .addCallback(roomDatabasePopulateJava)
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return  Instance;
    }

    private static  RoomDatabase.Callback roomDatabasePopulateJava = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db){
            super.onCreate(db);

            Runnable[] tasks = {
                    ()->{
                        UsersDAO usersDAO = Instance.usersDAO();
                        usersDAO.deleteAllUsers();
                        User testUser = new User("alpha","password", Credentials.Email);
                        usersDAO.insertUser(testUser);

                        },
                    ()->{
                        TasksDAO tasksDAO = Instance.tasksDAO();
                        tasksDAO.deleteAllTasks();
                        Task testTask = new Task(1,"Task1");
                        tasksDAO.insertTask(testTask);

                    },
                    ()->{
                        ConsentsDAO consentsDAO = Instance.consentsDAO();
                        consentsDAO.deleteAllConsents();
                        Consent consent = new Consent(1,"no terms");
                        consentsDAO.insertConsent(consent);

                    }
            };

            service.execute(tasks[0]);
            service.execute(tasks[1]);
            service.execute(tasks[2]);
            service.shutdown();

        }

    };

}
