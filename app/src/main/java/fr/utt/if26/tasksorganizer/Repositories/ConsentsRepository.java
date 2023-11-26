package fr.utt.if26.tasksorganizer.Repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import fr.utt.if26.tasksorganizer.DAOs.ConsentsDAO;
import fr.utt.if26.tasksorganizer.Entities.Consent;
import fr.utt.if26.tasksorganizer.RoomDB;

public class ConsentsRepository {

    private final ConsentsDAO consentsDAO;
    private final LiveData<List<Consent>> liveConsentList;

    public ConsentsRepository(Application app) {
        RoomDB bd = RoomDB.getInstance(app);
        consentsDAO = bd.consentsDAO();
        liveConsentList = consentsDAO.getAllConsents();
    }

    public LiveData<List<Consent>> getLiveConsentsList() {
        return liveConsentList;
    }

    public void insertConsent(Consent consent){
        RoomDB.service.execute(()->consentsDAO.insertConsent(consent));
    }

    public void deleteAllConsents(){
        RoomDB.service.execute(()->consentsDAO.deleteAllConsents());
    }
}
