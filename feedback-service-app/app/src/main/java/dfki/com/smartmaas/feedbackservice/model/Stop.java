package dfki.com.smartmaas.feedbackservice.model;

public class Stop implements Comparable<Stop> {
    private static final String TAG = Stop.class.getName();
    private String url;
    private String description;
    private String identifier;
    private String timeZone;
    private String name;
    private double lat, lng;
    private double distanceToUser;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getDistanceToUser() {
        return distanceToUser;
    }

    public void setDistanceToUser(double distanceToUser) {
        this.distanceToUser = distanceToUser;
    }

    public String getMessage() {
        String message = "";
        if (getName() != null) {
            message += "Name: " + getName();
        }
        if (getDescription() != null) {
            message += "\nDescription: " + getDescription();
        }
        if (getIdentifier() != null) {
            message += "\nIdentifier: " + getIdentifier();
        }
        if (getTimeZone() != null) {
            message += "\nTime Zone: " + getTimeZone();
        }
        message += "\nDistance: " + getDistanceToUser()+" km";

        return message;

    }

    @Override
    public int compareTo(Stop o) {
        return (int) (this.distanceToUser - o.getDistanceToUser());
    }

    public static String getTag() {
        return TAG;
    }
}
