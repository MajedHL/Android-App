package fr.utt.if26.tasksorganizer.Repositories;

import android.app.Application;
import android.database.sqlite.SQLiteConstraintException;

import androidx.lifecycle.LiveData;

import java.util.List;

import fr.utt.if26.tasksorganizer.DAOs.PendingsDAO;
import fr.utt.if26.tasksorganizer.Entities.Pending;
import fr.utt.if26.tasksorganizer.RoomDB;

public class PendingsRepository {


    private final PendingsDAO pendingsDAO;
        private final LiveData<List<Pending>> livePendingList;
    public PendingsRepository(Application app) {
        RoomDB bd = RoomDB.getInstance(app);
        pendingsDAO = bd.pendingsDAO();
        livePendingList = pendingsDAO.getAllPendings();
    }
        public LiveData<List<Pending>> getLivePendingList() {
        return livePendingList;
    }
    public void insertPending(Pending pending) throws SQLiteConstraintException {
        RoomDB.service.execute(() -> {
            try {
                pendingsDAO.insertPending(pending);
            } catch (SQLiteConstraintException e) {
                System.out.println("SQLiteConstraintException caught");
                e.printStackTrace();
            }
        });
    }

    public void deletePending(Pending pending){RoomDB.service.execute(()->pendingsDAO.deletePending(pending));}

    public void updatePending(Pending pending){RoomDB.service.execute(()->pendingsDAO.updatePending(pending));}
}
