package Models;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class WType {
    private String type;
    private final List<Mean> mean;

    public WType(String type) {
        this.type = type;
        this.mean = new WType();
    }
    public WType(String type, List<Mean> mean) {
        this.type = type;
        this.mean = mean;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public List<Mean> getMean() {
        return mean;
    }
    public void addMean(Mean mean) {
        this.mean.add(mean);
    }
    public void setMean(List<Mean> mean) {
        this.mean = mean;
    }

}
