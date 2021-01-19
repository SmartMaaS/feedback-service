package dfki.com.smartmaas.feedbackservice.model;

public class Reason {
    private static final String TAG = Reason.class.getName();
    public static final String PERMANENT_REASON = "PermanentReason", PASSING_REASON = "PassingReason", OTHER_REASON = "OtherReason";
    private String name, type;
    private String additionalInfo;

    public Reason(String name, String type) {
        this.name = name;
        this.type = type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
