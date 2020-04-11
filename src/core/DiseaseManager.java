package core;

import org.jpl7.Query;
import org.jpl7.Term;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DiseaseManager extends IManager {

    private final int MAX_POTENTIAL_ENTRY = 5;

    private Query mQuery;
    private Map<String, Term> mCurrentSolution;
    private Listener listener;

    public DiseaseManager(Listener listener) {
        mQuery = new Query("fetch_diseases(" + DISEASE + ")");
        mCurrentSolution = mQuery.nextSolution();
        this.listener = listener;
        if (!nextDisease()){
            diagnosePotentials();
        }
    }

    @Override
    public void onClickYes() {
        String assertedSymptom = currentSolution.get(SYMPTOM).toString();
        Query tmp = new Query("assert(patient_symptom(" + assertedSymptom + "))");
        System.out.println(tmp.hasSolution() ? "added symptom" : "not added symptom");
        tmp = new Query("english_of(" + assertedSymptom + "," + SYMPTOM + ")");
        listener.onSymptomsYes(cleanString(tmp.oneSolution().get(SYMPTOM).toString()));

        if (query.hasMoreSolutions())
            currentSolution = query.nextSolution();
        else {
            // This part means all symptoms of the disease has been examined
            String disease = getPerfectMatch().trim();
            if (!disease.equals("")) {
                query = new Query("outro(Gesture, Question, " + CONTEXT_PERFECT_MATCH + " )");
                currentSolution = query.nextSolution();
                listener.onDiagnosePerfectMatch(getGesture(), getQuestion(), disease);
            } else {
                if (!nextDisease()) {
                    // This part means all disease exhausted, and no perfect match
                    diagnosePotentials();
                }
            }
        }
    }

    @Override
    public void onClickNo() {
        if (!nextDisease()) {
            // This part means all disease has been exhausted -> display bar chart of potentials
            diagnosePotentials();
        }
    }

    /**
     * Stops querying symptoms, go to the next disease and query the new symptoms.
     *
     * @return true if there is still another disease not fully examined
     */
    private boolean nextDisease() {
        if (query != null) query.close();
        Query validator = null;

        if (!mQuery.hasMoreSolutions()) return false;

        do {
            mCurrentSolution = mQuery.nextSolution();
            String disease = mCurrentSolution.get(DISEASE).toString();
            System.out.println("Checking the disease " + disease);
            validator = new Query("valid_disease(" + disease + ")");
            if (validator.hasSolution()) {
                query = new Query("ask_symptom(" +
                        disease +
                        "," + SYMPTOM +
                        "," + GESTURE +
                        "," + QUESTION + ")");
                currentSolution = query.nextSolution();
                return true;
            }
        } while (mQuery.hasMoreSolutions() && !validator.hasSolution());

        return false;
    }

    /**
     * Try to get the disease which all symptoms of it matches the patient's symptoms
     *
     * @return the perfect matched disease
     */
    private String getPerfectMatch() {
        Query diagnoseQuery = new Query("has_disease(" + DISEASE + ")");
        if (diagnoseQuery.hasSolution()) {
            String rawOut = diagnoseQuery.nextSolution().get(DISEASE).toString();
            Query tmp = new Query("english_of(" + rawOut + ", X)");
            return cleanString(tmp.oneSolution().get("X").toString());
        } else {
            return "";
        }
    }

    private void diagnosePotentials() {
        Query diagnoseQuery = new Query("fetch_estimate(Entry)");
        ArrayList<PotentialDisease> diseases = new ArrayList<>();

        if (diagnoseQuery.hasSolution()) {
            while (diagnoseQuery.hasMoreSolutions() && diseases.size() < MAX_POTENTIAL_ENTRY) {
                Term[] solution = diagnoseQuery.nextSolution().get("Entry").toTermArray();
                Query tmp = new Query("english_of(" + solution[0].toString() + ", X)");
                diseases.add(
                        new PotentialDisease(cleanString(tmp.oneSolution().get("X").toString()),
                                Integer.parseInt(solution[1].toString())));
            }
            query = new Query("outro(Gesture, Question, " + CONTEXT_PARTIAL_MATCH + " )");
            currentSolution = query.nextSolution();
            listener.onDiagnosePartialMatch(getGesture(), getQuestion(), diseases);

        } else {
            query = new Query("outro(Gesture, Question, " + CONTEXT_NO_MATCH + " )");
            currentSolution = query.nextSolution();
            listener.onDiagnoseNoMatch(getGesture(), getQuestion());
        }
    }

    interface Listener {
        public void onSymptomsYes(String symptom);

        public void onDiagnosePerfectMatch(String gesture, String message, String disease);

        public void onDiagnosePartialMatch(String gesture, String message, ArrayList<PotentialDisease> diseases);

        public void onDiagnoseNoMatch(String gesture, String message);
    }
}
