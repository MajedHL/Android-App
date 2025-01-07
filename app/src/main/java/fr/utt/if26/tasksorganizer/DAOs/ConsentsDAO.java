package fr.utt.if26.tasksorganizer.DAOs;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fr.utt.if26.tasksorganizer.Entities.Consent;
import fr.utt.if26.tasksorganizer.Entities.User;

@Dao
public interface ConsentsDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertConsent(Consent consent);
    @Update
    public void updateConsent(Consent consent);
    @Delete
    public void deleteConsent(Consent consent);
    @Query("SELECT * FROM consents ;")
    public LiveData<List<Consent>> getAllConsents();
    @Query("Delete from Consents;")
    void deleteAllConsents();
    @Query("Select * from consents where userId=:id")
    LiveData<Consent> getConsentByUserId(int id);
}
