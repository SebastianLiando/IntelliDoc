package main.manager;

import org.jpl7.Query;
import org.jpl7.Term;

import java.util.Map;

/**
 * This is the base class of all other managers responsible for questioning the patient.
 */
public abstract class AbstractManager {

    public static final String DISEASE = "Disease";
    public static final String SYMPTOM = "Symptom";
    public static final String GESTURE = "Gesture";
    public static final String QUESTION = "Question";

    public static final String CONTEXT_PERFECT_MATCH = "outro_perfect";
    public static final String CONTEXT_PARTIAL_MATCH = "outro_partial";
    public static final String CONTEXT_NO_MATCH = "outro_none";

    //The current query the manager do
    protected Query query = null;
    //The current solution the query has
    protected Map<String, Term> currentSolution = null;

    /**
     * Gets the gesture to be acted by the doctor
     * @return the gesture to be acted by the doctor
     */
    public String getGesture() {
        return getSolution(GESTURE);
    }

    /**
     * Gets the message to be said by the doctor
     * @return the message to be said by the doctor
     */
    public String getMessage() {
        return getSolution(QUESTION);
    }

    /**
     * Gets the solution of a particular variable in prolog.
     *
     * @param key the variable name in prolog which value is to be retrieved
     * @return the value of the variable
     */
    protected String getSolution(String key) {
        return cleanString(currentSolution.get(key).toString());
    }

    /**
     * Remove the apostrophes generated from <code>english_of()</code> in prolog
     *
     * @param toClean the string to be cleaned
     * @return the cleaned string
     */
    protected String cleanString(String toClean) {
        return toClean.replace("'", "");
    }

    public abstract void onClickYes();

    /**
     * The "no" button will get another solution from the query by default.
     */
    public void onClickNo() {
        currentSolution = query.nextSolution();
    }
}
