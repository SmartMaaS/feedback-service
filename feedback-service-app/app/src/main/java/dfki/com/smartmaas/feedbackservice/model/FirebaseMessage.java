package dfki.com.smartmaas.feedbackservice.model;

import com.google.firebase.messaging.RemoteMessage;

import java.util.List;

public class FirebaseMessage {
    private static final String tag = "FirebaseMessage";
    private List<Stop> stops;
    private RemoteMessage.Notification notification;
    private int totalItems;


    public List<Stop> getStops() {
        return stops;
    }

    public void setStops(List<Stop> stops) {
        this.stops = stops;
    }

    public RemoteMessage.Notification getNotification() {
        return notification;
    }

    public void setNotification(RemoteMessage.Notification notification) {
        this.notification = notification;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public static String getTag() {
        return tag;
    }

    public void addStops(List<Stop> stops) {
        if (this.stops != null) {
            this.stops.addAll(stops);
        } else {
            this.stops = stops;
        }
    }
}
