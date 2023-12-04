package fr.utt.if26.tasksorganizer.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import fr.utt.if26.tasksorganizer.Activities.Task_edit;
import fr.utt.if26.tasksorganizer.Entities.Task;
import fr.utt.if26.tasksorganizer.R;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskHolder> {

    List<Task> tasks;
    Context context;

    private IOnTaskListener taskListener;


    public  TaskAdapter(ArrayList<Task> tasks, IOnTaskListener listener){
        this.tasks = tasks;
        this.taskListener = listener;
    }
    @NonNull
    @Override
    public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.task_item, parent,false);
        return new TaskHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskHolder holder, int position) {
        holder.display(tasks.get(position));
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    class TaskHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        TextView task_name;
        TextView task_state;
        TextView task_timing;
        public TaskHolder(@NonNull View itemView) {
            super(itemView);
            task_name = itemView.findViewById(R.id.task_name);
            task_state = itemView.findViewById(R.id.task_state);
            task_timing = itemView.findViewById(R.id.task_timing);
            task_name.setOnCreateContextMenuListener(this);
            task_name.setOnClickListener(view -> {
                Task task = tasks.get(getAdapterPosition());
                if (taskListener != null) {
                    taskListener.onTaskClick(task);
                }
            });

        }

        void display(Task task){
            task_name.setText(task.getName());
           if(task.isStatus()) {
               task_state.setText("Completed");
               task_state.setTextColor(Color.GREEN);
           }
           else {
               task_state.setText("Un-Completed");
               task_state.setTextColor(Color.YELLOW);
           }
           task_timing.setText("");
           if(task.isLate()){
               task_timing.setText("Late");
               task_timing.setTextColor(Color.RED);
           }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            int position = this.getAdapterPosition();
            Task task = tasks.get(position);

            if(task.isStatus())  menu.add(position,R.id.mark_undone, 0, "Mark as not completed");
            else menu.add(position,R.id.mark_done, 0, "Mark as completed");
            menu.add(position,R.id.delete_task, 1, "delete");
            MenuItem item = menu.findItem(R.id.delete_task);
            SpannableString s = new SpannableString("delete");
            s.setSpan(new ForegroundColorSpan(Color.RED), 0, s.length(), 0);
            item.setTitle(s);
        }
    }
}
