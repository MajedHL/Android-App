package fr.utt.if26.tasksorganizer.Utils;

import android.util.Pair;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

public class CustomLiveData extends MediatorLiveData<CustomData> {
    public CustomLiveData(LiveData<Boolean> hideCompleted, LiveData<Boolean> sortDueDate,
                          LiveData<Boolean> sortCompletion, LiveData<Boolean> sortPriority, LiveData<Boolean> sortLateness)
        {

        addSource(hideCompleted, hide -> setValue(new CustomData(hide,sortDueDate.getValue(),sortCompletion.getValue(), sortPriority.getValue(),sortLateness.getValue())));
        addSource(sortDueDate, sortByDueDate -> setValue(new CustomData(hideCompleted.getValue(),sortByDueDate,sortCompletion.getValue(), sortPriority.getValue(),sortLateness.getValue())));
        addSource(sortCompletion, sortByCompletion -> setValue(new CustomData(hideCompleted.getValue(),sortDueDate.getValue(),sortByCompletion, sortPriority.getValue(),sortLateness.getValue())));
        addSource(sortPriority, sortByPriority -> setValue(new CustomData(hideCompleted.getValue(),sortDueDate.getValue(),sortCompletion.getValue(), sortByPriority,sortLateness.getValue())));
        addSource(sortLateness, sortByLateness -> setValue(new CustomData(hideCompleted.getValue(),sortDueDate.getValue(),sortCompletion.getValue(), sortPriority.getValue(),sortByLateness)));
    }

}



