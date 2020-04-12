package main.manager;

import org.jpl7.Query;

/**
 * The PainManager class handles the logic needed for questioning the patient's pain level.
 */
public class PainManager extends AbstractManager {

    /**
     * The constructor starts the query <code>ask_pain(Pain, Gesture, Question)</code> in prolog.
     */
    public PainManager(){
        query = new Query("ask_pain(Pain, Gesture, Question)");
        currentSolution = query.nextSolution();
    }

    /**
     * When the user clicks "yes", save the patient's pain level by <code>assert()</code> and add all pain levels
     * to the history. A patient can only have one pain level.
     */
    @Override
    public void onClickYes() {
        Query tmp = new Query("assert(patient_pain(" + currentSolution.get("Pain") + "))");
        System.out.println(tmp.hasSolution() ? "asserted pain" : "failed to assert pain");

        tmp = new Query("assert_all_pain(_)");
        while(tmp.hasMoreSolutions()) tmp.nextSolution();

        query.close();
    }
}
