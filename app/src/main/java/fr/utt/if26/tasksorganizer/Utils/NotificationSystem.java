package fr.utt.if26.tasksorganizer.Utils;


import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Pair;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;

import fr.utt.if26.tasksorganizer.Activities.LoginPage;
import fr.utt.if26.tasksorganizer.Entities.Pending;
import fr.utt.if26.tasksorganizer.Entities.Task;
import fr.utt.if26.tasksorganizer.Entities.User;
import fr.utt.if26.tasksorganizer.R;
import fr.utt.if26.tasksorganizer.ViewModels.TasksViewModel;

public class NotificationSystem   {

    private Context context;
    private static String CHANNEL_ID = "my channel ID 54321";
    private AlarmManager alarmManager;
    private Boolean activated=false;
    private boolean listInitiliazed = false;
//    private Set<Task> reminderTasks = new HashSet<>();

    private static NotificationSystem instance = null;

//    private HashMap<Task, PendingIntent> map =new HashMap<>();
    private List<Pending> pendingList = new ArrayList<>();
    private List<Pending> pendingReflection = new ArrayList<>();
    private NotificationSystem(Context context) {
        this.context = context;
    }

    public static NotificationSystem getInstance(Context context){
        if(instance==null) {
           instance = new NotificationSystem(context);
            return instance;
        }
        return instance;
    }

    public void activate(){
        if(activated) return;
        activated = true;
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        createNotificationChannel();

    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getResources().getString(R.string.reminders_channel);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system. You can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }


    public void addNotification(Pending pending){
       if(!pending.isActive())return;
       if(!pending.getDate().after(new Date())) return;
        String taskName = pending.getName();
        Integer taskId = pending.getRequest_code();
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("taskName",taskName);
        intent.putExtra("taskId",taskId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, taskId, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, pending.getDate().getTime(), pendingIntent);
    }


    public void deleteNotification(Pending pending){
        String taskName = pending.getName();
        Integer taskId = pending.getRequest_code();
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntentToDelete = PendingIntent.getBroadcast(context, taskId, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(pendingIntentToDelete);
        pendingIntentToDelete.cancel();

    }

    public void deleteAllNotifs(){
        if (Build.VERSION.SDK_INT >= 34) {
            alarmManager.cancelAll();
        }else {
//            reminderTasks.forEach(task -> alarmManager.cancel(map.get(task)));
        }
//        reminderTasks.clear();
//        map.clear();
    }

    public void setPendingSet(List<Pending> pendingList) {
        this.pendingList = pendingList;
        listInitiliazed = true;
    }

    public void setPendingReflection(List<Pending> pendingReflection) {
        this.pendingReflection = pendingReflection;
        if(!listInitiliazed) setPendingSet(pendingReflection);
        refresh();
    }

    public static String getChannelId() {
        return CHANNEL_ID;
    }

    public Boolean isActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

//    public void setReminderTasks(List<Task> reminderTasks) {
//        this.reminderTasks = new HashSet<>(reminderTasks);
//        initNotifs();
//    }


//    private void initNotifs(){
////        for(Task task:reminderTasks){
////            addNotif(task);
////        }
//    }

    public boolean isListInitiliazed() {
        return listInitiliazed;
    }



    private void refresh(){
        //added a task
        if(pendingReflection.size()>pendingList.size()){
         OptionalInt max = pendingReflection.stream().mapToInt(Pending::getRequest_code).max();
            if(max.isPresent()){
               List<Pending> getPendingOfMaxId = pendingReflection.stream().filter(pending -> pending.getRequest_code()== max.getAsInt()).collect(Collectors.toList());
                if(getPendingOfMaxId!=null && getPendingOfMaxId.size()==1){
                    Pending p =getPendingOfMaxId.get(0);
                    pendingList.add(p);
                    //TODO add the notif
                    if (p.isActive()) addNotification(p);
                }
            }
        } //removed a task
        else if (pendingReflection.size()<pendingList.size()) {
            Pending toDelete=null;
            for(Pending p:pendingList){
                if(!listContainsPending(pendingReflection,p)){
                    toDelete = p;
                }
            }
           if(toDelete!=null) {
               //TODO delete the notif and the pending
               pendingList.remove(toDelete);
               deleteNotification(toDelete);
           }
        }//name or date or status changed
        else {
//            List<Integer> ids = pendingReflection.stream().mapToInt(pending -> pending.getRequest_code()).boxed().collect(Collectors.toList());
            Collections.sort(pendingReflection, Comparator.comparing(Pending::getRequest_code));
            Collections.sort(pendingList, Comparator.comparing(Pending::getRequest_code));
//            Collections.sort(ids);
            for(int i =0;i<pendingReflection.size();i++){
                if(!equals(pendingList.get(i),pendingReflection.get(i))){
                    // status changed
                    if(pendingReflection.get(i).isActive()!=pendingList.get(i).isActive()) {
                       //TODO delete the notif or add it
                       if(!pendingReflection.get(i).isActive())     deleteNotification(pendingList.get(i));
                       else if (pendingReflection.get(i).isActive()){
                           addNotification(pendingReflection.get(i));
                       }
                   }// name or reminding date changed
                    else {
                        //TODO add the notif (same request code so the old will be overriden)
                        if (pendingReflection.get(i).isActive()) {
                            deleteNotification(pendingReflection.get(i));
                            addNotification(pendingReflection.get(i));
                        }
                    }
                    pendingList.get(i).setName(pendingReflection.get(i).getName());
                    pendingList.get(i).setDate(pendingReflection.get(i).getDate());
                    pendingList.get(i).setActive(pendingReflection.get(i).isActive());
                }
            }
        }
    }


    private boolean listContainsPending(List<Pending> list, Pending p){
       List<Pending> filtered = list.stream().filter(pending -> pending.getRequest_code()==p.getRequest_code()).collect(Collectors.toList());
        return filtered.size()>0;
    }
    private boolean equals(Pending pone, Pending ptwo){
        return pone.getDate().equals(ptwo.getDate()) && pone.getName().equals(ptwo.getName()) && pone.isActive()==ptwo.isActive();
    }

}
