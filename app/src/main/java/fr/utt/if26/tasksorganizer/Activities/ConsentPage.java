package fr.utt.if26.tasksorganizer.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;

import fr.utt.if26.tasksorganizer.Entities.Consent;
import fr.utt.if26.tasksorganizer.Entities.User;
import fr.utt.if26.tasksorganizer.R;
import fr.utt.if26.tasksorganizer.ViewModels.ConsentsViewModel;

public class ConsentPage extends AppCompatActivity {

    private User user;
    private CheckBox agree;
    private Button Continue;
    private ConsentsViewModel consentsViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consent_page);
        user = (User) getIntent().getSerializableExtra("user");
        consentsViewModel = new ViewModelProvider(this).get(ConsentsViewModel.class);
        agree = findViewById(R.id.consent_agree);
        Continue = findViewById(R.id.consent_continue);

        Continue.setOnClickListener(view -> {
            if(!agree.isChecked()){
                agree.setTextColor(Color.RED);
                return;
            }
            Consent consent = new Consent(user.getId(),getResources().getString(R.string.agreement));
            consentsViewModel.insertConsent(consent);

            Intent intent = new Intent(this, TodayActivity.class);
            intent.putExtra("user",user);
            startActivity(intent);
        });


    }
}