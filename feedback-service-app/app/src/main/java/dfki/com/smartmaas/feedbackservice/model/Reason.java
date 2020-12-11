package dfki.com.smartmaas.feedbackservice.model;

public class Reason {
    private static final String TAG = Reason.class.getName();
    private String name;
    private String additionalInfo;

    public Reason(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public static String getTag() {
        return TAG;
    }
}
