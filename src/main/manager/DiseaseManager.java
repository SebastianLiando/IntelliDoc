package main.manager;

import main.model.PotentialDisease;
import org.jpl7.Query;
import org.jpl7.Term;

import java.util.ArrayList;
import java.util.Map;

/**
 * The DiseaseManager class handles the logic needed for questioning the patient's symptom.
 */
public class DiseaseManager extends AbstractManager {

    //The maximum number of potential diseases to be displayed
    private final int MAX_POTENTIAL_ENTRY = 5;

    //The private query, used to cycle between diseases
    private Query mQuery;
    //The solution of the private query, used to retrieve the symptoms of the disease
    private Map<String, Term> mCurrentSolution;
    //The object that will be notified if the diagnosis is done
    private Listener listener;

    /**
     * The constructor starts the private query <code>fetch_diseases(Disease)</code> in prolog.
     * It also starts the query <code>ask_symptom(Disease, Symptom, Gesture, Question)</code>,
     * which is accessible by the <code>Controller</code> class
     *
     * @param listener the object to that will be notified if diagnosis is finished
     */
    public DiseaseManager(Listener listener) {
        mQuery = new Query("fetch_diseases(" + DISEASE + ")");
        mCurrentSolution = mQuery.nextSolution();
        this.listener = listener;
        if (!nextDisease()) {
            diagnosePotentials();
        }
    }

    /**
     * When the user clicks the yes button, save the patient's symptom by <code>assert()</code>, notify the
     * listener that the patient has the symptom, and tries to get the next symptom. If there is no more symptoms
     * to ask for the current disease, then starts the diagnosis.
     */
    @Override
    public void onClickYes() {
        String assertedSymptom = currentSolution.get(SYMPTOM).toString();
        Query tmp = new Query("assert(patient_symptom(" + assertedSymptom + "))");
        System.out.println(tmp.hasSolution() ? "added symptom" : "not added symptom");
        tmp = new Query("english_of(" + assertedSymptom + "," + SYMPTOM + ")");
        listener.onSymptomsYes(cleanString(tmp.oneSolution().get(SYMPTOM).toString()));

        if (query.hasMoreSolutions())
            // Ask next symptom
            currentSolution = query.nextSolution();
        else {
            // All symptoms of the disease has been examined
            String disease = getPerfectMatch().trim();
            if (!disease.equals("")) {
                //There is a perfect match
                query = new Query("outro(Gesture, Question, " + CONTEXT_PERFECT_MATCH + " )");
                currentSolution = query.nextSolution();
                listener.onDiagnosePerfectMatch(getGesture(), getMessage(), disease);
            } else {
                if (!nextDisease()) {
                    //Symptoms exhausted, but no perfect match
                    diagnosePotentials();
                }
            }
        }
    }

    /**
     * When the user clicks the no button, skip the current disease and ask the symptoms of the next disease.
     * If there is none, start the diagnosis.
     */
    @Override
    public void onClickNo() {
        if (!nextDisease()) {
            // This part means all disease has been exhausted -> display bar chart of potentials
            diagnosePotentials();
        }
    }

    /**
     * Stops querying symptoms, go to the next disease and query the new symptoms. The function starts another
     * prolog query <code>valid_disease(Disease)</code> to determine if the next disease needs to be asked.
     * Valid disease means that the none of its symptoms is not the patient's symptom
     *
     * @return true if there is still another valid disease
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
            System.out.println(disease + " is not valid");
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

    /**
     * Diagnoses the patient according to the symptoms asserted. The method fetches an ArrayList of potential
     * diseases by <code>fetch_estimate(Entry)<code/> in prolog. If the result is empty, then notify that
     * the patient is healthy. Else, notify the potential diseases to the listener.
     */
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
            //Notify potential diseases
            listener.onDiagnosePartialMatch(getGesture(), getMessage(), diseases);

        } else {
            query = new Query("outro(Gesture, Question, " + CONTEXT_NO_MATCH + " )");
            currentSolution = query.nextSolution();
            //Notify patient healthy
            listener.onDiagnoseNoMatch(getGesture(), getMessage());
        }
    }

    /**
     * Internal interface to notify the listener for diagnosis related events
     */
    public interface Listener {
        void onSymptomsYes(String symptom);

        void onDiagnosePerfectMatch(String gesture, String message, String disease);

        void onDiagnosePartialMatch(String gesture, String message, ArrayList<PotentialDisease> diseases);

        void onDiagnoseNoMatch(String gesture, String message);
    }
}
