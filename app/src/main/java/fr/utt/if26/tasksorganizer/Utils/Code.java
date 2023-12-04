package fr.utt.if26.tasksorganizer.Utils;

public enum Code {
     EDIT_SUCCESS(1),CANCEL(-1),CREATE_SUCCESS(2);


    private final int value;
    Code(int i) {
        value = i;
    }

    public final int getValue() {
        return value;
    }
}
