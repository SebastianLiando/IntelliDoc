package core;

import org.jpl7.Query;

public class MoodManager extends IManager {

    public MoodManager(){
        query = new Query("ask_mood(Mood, Gesture, Question)");
        currentSolution = query.nextSolution();
    }

    @Override
    public void onClickYes() {
        Query tmp = new Query("assert(patient_mood(" + currentSolution.get("Mood") + "))");
        System.out.println(tmp.hasSolution() ? "asserted mood" : "failed to assert mood");
        query.close();
    }
}
