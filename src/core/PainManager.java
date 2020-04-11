package core;

import org.jpl7.Query;

public class PainManager extends IManager {

    public PainManager(){
        query = new Query("ask_pain(Pain, Gesture, Question)");
        currentSolution = query.nextSolution();
    }

    @Override
    public void onClickYes() {
        Query tmp = new Query("assert(patient_pain(" + currentSolution.get("Pain") + "))");
        System.out.println(tmp.hasSolution() ? "asserted pain" : "failed to assert pain");

        tmp = new Query("assert_all_pain(_)");
        while(tmp.hasMoreSolutions()) tmp.nextSolution();

        query.close();
    }
}
