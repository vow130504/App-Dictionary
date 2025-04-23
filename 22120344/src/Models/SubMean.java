package Models;

public class SubMean {
    private String submean;
    private String define;

    public SubMean(String submean, String define) {
        this.submean = submean;
        this.define = define;
    }
    public SubMean() {
        this.submean = "";
        this.define = "";
    }
    public String getSubmean() {
        return submean;
    }
    public void setSubmean(String submean) {
        this.submean = submean;
    }
    public String getDefine() {
        return define;
    }
    public void setDefine(String define) {
        this.define = define;
    }



}
