package core;

import org.jpl7.Query;
import org.jpl7.Term;

import java.util.Map;

public abstract class IManager {

    public static final String GESTURE = "Gesture";
    public static final String QUESTION = "Question";

    protected Query query = null;
    protected Map<String, Term> currentSolution = null;

    public String getGesture() {
        return getTerm(GESTURE);
    }

    public String getQuestion() {
        return  getTerm(QUESTION);
    }

    private String getTerm(String key){
        StringBuilder builder = new StringBuilder(currentSolution.get(key).toString());
        builder.deleteCharAt(0);
        builder.setLength(builder.length() - 1);
        return builder.toString();
    }

    public abstract void onClickYes();

    public void onClickNo(){
        currentSolution = query.nextSolution();
    }
}
