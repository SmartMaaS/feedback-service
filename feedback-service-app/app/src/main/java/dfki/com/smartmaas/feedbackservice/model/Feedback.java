package dfki.com.smartmaas.feedbackservice.model;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.util.RDFCollections;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.turtle.TurtleWriter;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


@Root
public class Feedback {
    private static final String TAG = Feedback.class.getName();
    private String feedbackURI = "http://www.example.com/feedback";
    @Element
    private Location location;
    @Element
    private int stuckTime;
    @Element
    private String timeMeasurementUnit;
    @Element
    private String vehicle;
    @Element
    private String vehicleNo;
    @Element
    private String dateTime;
    @ElementList
    private List<Reason> reasons;
    @Element
    private final String distanceMeasurementUnit = "km";

    public Feedback(Location location, int stuckTime, String timeMeasurementUnit, String vehicle, String vehicleNo, String date) {
        this.location = location;
        this.stuckTime = stuckTime;
        this.timeMeasurementUnit = timeMeasurementUnit;
        this.vehicle = vehicle;
        this.vehicleNo = vehicleNo;
        this.dateTime = date;
    }

    public String getTimeMeasurementUnit() {
        return timeMeasurementUnit;
    }

    public void setTimeMeasurementUnit(String timeMeasurementUnit) {
        this.timeMeasurementUnit = timeMeasurementUnit;
    }

    public String getDistanceMeasurementUnit() {
        return distanceMeasurementUnit;
    }

    public int getStuckTime() {
        return stuckTime;
    }

    public void setStuckTime(int stuckTime) {
        this.stuckTime = stuckTime;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<Reason> getReasons() {
        return reasons;
    }

    public void setReasons(List<Reason> reasons) {
        this.reasons = reasons;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public String getFeedbackURI() {
        return feedbackURI;
    }

    public void setFeedbackURI(String feedbackURI) {
        this.feedbackURI = feedbackURI;
    }

    public static String getTag() {
        return TAG;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getRDFModel() {

        String duration = "P";
        if (getTimeMeasurementUnit().equalsIgnoreCase("Day")
                || getTimeMeasurementUnit().equalsIgnoreCase("Month")
                || getTimeMeasurementUnit().equalsIgnoreCase("Year")) {
            duration += getStuckTime() + getTimeMeasurementUnit().charAt(0);
        } else {
            duration += "T" + getStuckTime() + getTimeMeasurementUnit().charAt(0);
        }
        duration = duration.toUpperCase();

        ValueFactory vf = SimpleValueFactory.getInstance();
        ModelBuilder modelBuilder = new ModelBuilder();

        String base = "http://www.dfki.de/SmartMaaS/feedback/";
        modelBuilder.setNamespace("base", "http://www.dfki.de/SmartMaaS/feedback#")
                .setNamespace("owl", "http://www.w3.org/2002/07/owl#")
                .setNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
                .setNamespace("xml", "http://www.w3.org/XML/1998/namespace#")
                .setNamespace("xsd", "http://www.w3.org/2001/XMLSchema#")
                .setNamespace("rdfs", "http://www.w3.org/2000/01/rdf-schema#")
                .setNamespace("time", "http://www.w3.org/2006/time#")
                .setNamespace("geo", "http://www.w3.org/2003/01/geo/wgs84_pos#");


        org.eclipse.rdf4j.model.Resource reasonsHead = vf.createBNode();
        org.eclipse.rdf4j.model.Resource timeStampHead = vf.createBNode();
        org.eclipse.rdf4j.model.Resource delayHead = vf.createBNode();
        org.eclipse.rdf4j.model.Resource locationHead = vf.createBNode();
        org.eclipse.rdf4j.model.Resource vehicleHead = vf.createBNode();

        String feedbackID = "Feedback_xxx";
        /* !!!!!!
         Feedback app is unaware of the amount of feedback messages in feedback repository.
         Therefore, instead of a correct Feedback ID, we write "Feedback_xxx" which is replaced with the correct
         Feedback ID in feedback service.
        */
        modelBuilder.subject("base:" + feedbackID)
                .add(org.eclipse.rdf4j.model.vocabulary.RDF.TYPE, "base:Feedback")
                .add("base:hasTimestamp", timeStampHead)
                .subject(timeStampHead)
                .add("time:xsdDateTime", getDateTime())
                .add("base:timeStampOf", "base:" + feedbackID)
                .subject("base:" + feedbackID)
                .add("base:causedDelay", delayHead)
                .subject(delayHead)
                .add("time:hasCSDDuration", duration)
                .subject("base:" + feedbackID)
                .add("base:atLocation", locationHead)
                .subject(locationHead)
                .add("base:locationOf", "base:" + feedbackID)
                .add("geo:long", getLocation().getLng())
                .add("geo:lat", getLocation().getLat())
                .add("base:Address", getLocation().getName())
                .subject("base:" + feedbackID)
                .add("base:affects", vehicleHead)
                .subject(vehicleHead)
                .add(org.eclipse.rdf4j.model.vocabulary.RDF.TYPE, "base:" + getVehicle())
                .add("base:affectedBy", "base:" + feedbackID)
                .add("base:Line", getVehicleNo())
                .subject("base:" + feedbackID)
                .add("base:becauseOf", reasonsHead);

        List<IRI> reasonIRIs = new ArrayList<>();
        for (Reason r : getReasons()) {
            if (r.getName().equals("Other")) {
                reasonIRIs.add(vf.createIRI(base, r.getAdditionalInfo()));
            } else
                reasonIRIs.add(vf.createIRI(base, r.getName()));
        }
        List<org.eclipse.rdf4j.model.Statement> statements =
                RDFCollections.asRDF(reasonIRIs, reasonsHead, new ArrayList<>());

        org.eclipse.rdf4j.model.Model feedbackModel = modelBuilder.build();

        feedbackModel.addAll(statements);

        String modelAsString = null;
        try {
            OutputStream outputStream = new ByteArrayOutputStream();
            TurtleWriter turtleWriter = new TurtleWriter(outputStream);
            Rio.write(feedbackModel, turtleWriter);
            modelAsString = outputStream.toString();
            outputStream.close();

            /*
            Since rdf4j-rio-turtle library produces duplicate class error when added in gradle, it is added as a
            jar library.
            */
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        return modelAsString;
    }
}
