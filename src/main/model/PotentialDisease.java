package main.model;

/**
 * This is an entity class to hold the information of the potential diseases.
 */
public class PotentialDisease {

    private String diseaseName;
    private int numOfMatch;

    /**
     * The constructor for PotentialDisease class
     * @param name the name of the disease
     * @param count number of symptoms that match the patient's
     */
    public PotentialDisease(String name, int count){
        diseaseName = name;
        numOfMatch = count;
    }

    /**
     * Returns the name of the potential disease.
     * @return the name of the potential disease
     */
    public String getDiseaseName() {
        return diseaseName;
    }

    /**
     * Returns the count of symptoms match.
     * @return the count of symptoms match
     */
    public int getNumOfMatch() {
        return numOfMatch;
    }
}
