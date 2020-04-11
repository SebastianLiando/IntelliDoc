package core;

public class PotentialDisease {

    private String diseaseName;
    private int numOfMatch;

    public PotentialDisease(String name, int count){
        diseaseName = name;
        numOfMatch = count;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public int getNumOfMatch() {
        return numOfMatch;
    }
}
