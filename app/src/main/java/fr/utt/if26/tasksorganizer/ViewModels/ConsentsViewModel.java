package fr.utt.if26.tasksorganizer.ViewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import fr.utt.if26.tasksorganizer.Entities.Consent;
import fr.utt.if26.tasksorganizer.Entities.User;
import fr.utt.if26.tasksorganizer.Repositories.ConsentsRepository;
import fr.utt.if26.tasksorganizer.Repositories.UsersRepository;

public class ConsentsViewModel extends AndroidViewModel {

    private ConsentsRepository consentsRepository;

    public ConsentsViewModel(Application application) {
        super(application);
        consentsRepository = new ConsentsRepository(application);
    }
    public LiveData<List<Consent>> getAllConsents() {
        return consentsRepository.getLiveConsentsList();
    }

    public void insertConsent(Consent consent){
        consentsRepository.insertConsent(consent);
    }

    public void deleteAllConsents(){
        consentsRepository.deleteAllConsents();
    }

}
