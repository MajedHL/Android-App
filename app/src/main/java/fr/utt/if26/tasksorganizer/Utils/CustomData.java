package fr.utt.if26.tasksorganizer.Utils;

public class CustomData {
    private final Boolean hide;
    private final Boolean sortByDueDate;
    private  Boolean sortByCompletion;
    private  Boolean sortByPriority;
    private Boolean sortByLateness;

    public CustomData(Boolean hide, Boolean sortByDueDate, Boolean sortByCompletion, Boolean sortByPriority, Boolean sortByLateness) {
        this.hide = hide;
        this.sortByDueDate = sortByDueDate;
        this.sortByCompletion = sortByCompletion;
        this.sortByPriority = sortByPriority;
        this.sortByLateness = sortByLateness;
    }

    public Boolean getHide() {
        return hide;
    }

    public Boolean getSortByDueDate() {
        return sortByDueDate;
    }

    public Boolean getSortByCompletion() {
        return sortByCompletion;
    }

    public Boolean getSortByPriority() {
        return sortByPriority;
    }

}
