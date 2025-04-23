package Models;

import Models.SubMean;
import Models.WType;
import Models.Word;
import java.util.List;
import java.util.ArrayList;

public class Mean {
    private String mean;

    private List<SubMean> submean;

    public Mean(String mean, List<SubMean> submean) {
        this.mean = mean;
        this.submean = submean;
    }
    public Mean(String mean) {
        this.mean = mean;
        this.submean = new ArrayList<>();
    }
    public Mean() {
        this.mean = "";
        this.submean = new ArrayList<>();
    }
    public String getMean() {
        return mean;
    }
    public void setMean(String mean) {
        this.mean = mean;
    }
    public List<SubMean> getSubmean() {
        return submean;
    }
    public void setSubmean(List<SubMean> submean) {
        this.submean = submean;
    }
    public void addSubmean(SubMean submean) {
        this.submean.add(submean);
    }
}
