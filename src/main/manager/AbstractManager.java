package main.manager;

import org.jpl7.Query;
import org.jpl7.Term;

import java.util.Map;

public abstract class AbstractManager {

    public static final String DISEASE = "Disease";
    public static final String SYMPTOM = "Symptom";
    public static final String GESTURE = "Gesture";
    public static final String QUESTION = "Question";

    public static final String CONTEXT_PERFECT_MATCH = "outro_perfect";
    public static final String CONTEXT_PARTIAL_MATCH = "outro_partial";
    public static final String CONTEXT_NO_MATCH = "outro_none";

    protected Query query = null;
    protected Map<String, Term> currentSolution = null;

    public String getGesture() {
        return getTerm(GESTURE);
    }

    public String getQuestion() {
        return getTerm(QUESTION);
    }

    protected String getTerm(String key){
        return cleanString(currentSolution.get(key).toString());
    }

    /**
     * Remove the apostrophes generated from english_of()
     * @param toClean the string from english_of()
     * @return the cleaned string
     */
    protected String cleanString(String toClean){
        return toClean.replace("'", "");
    }

    public abstract void onClickYes();

    public void onClickNo(){
        currentSolution = query.nextSolution();
    }
}
