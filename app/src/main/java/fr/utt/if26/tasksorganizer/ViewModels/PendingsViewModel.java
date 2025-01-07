package fr.utt.if26.tasksorganizer.ViewModels;

import android.app.Application;
import android.database.sqlite.SQLiteConstraintException;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import fr.utt.if26.tasksorganizer.Entities.Pending;

import fr.utt.if26.tasksorganizer.Repositories.PendingsRepository;


public class PendingsViewModel extends AndroidViewModel {


    private PendingsRepository pendingsRepository;
    public PendingsViewModel(@NonNull Application application) {
        super(application);
        pendingsRepository = new PendingsRepository(application);
    }

    public LiveData<List<Pending>> getAllPendings() {
        return pendingsRepository.getLivePendingList();
    }

    public void insertPending(Pending pending) throws SQLiteConstraintException {
        pendingsRepository.insertPending(pending);
    }
    public void updatePending(Pending pending){
        pendingsRepository.updatePending(pending);
    }
    public void deletePending(Pending pending){pendingsRepository.deletePending(pending);}
}
