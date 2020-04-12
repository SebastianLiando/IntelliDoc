package main.manager;

import org.jpl7.Query;

/**
 * The MoodManager class handles the logic needed for questioning the patient's mood.
 */
public class MoodManager extends AbstractManager {

    /**
     * The constructor starts the query <code>ask_mood(Mood, Gesture, Question)</code> in prolog.
     */
    public MoodManager(){
        query = new Query("ask_mood(Mood, Gesture, Question)");
        currentSolution = query.nextSolution();
    }

    /**
     * When the user clicks "yes", save the patient's mood by <code>assert()</code>.
     * A patient can have multiple mood, but will be asked at a later time if needed.
     */
    @Override
    public void onClickYes() {
        Query tmp = new Query("assert(patient_mood(" + currentSolution.get("Mood") + "))");
        System.out.println(tmp.hasSolution() ? "asserted mood" : "failed to assert mood");

        query.close();
    }
}
