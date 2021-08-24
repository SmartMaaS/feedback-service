package dfki.com.smartmaas.feedbackservice.model;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.util.RDFCollections;
import org.eclipse.rdf4j.model.vocabulary.RDF;
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


    // TODO the username should be received via logging in.
    private String username = "username";

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getFeedbackMessage() {
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
        String feedbackID = "Feedback_xxx";
        /* !!!!!!
         Feedback app is unaware of the amount of feedback messages in feedback repository.
         Therefore, instead of a correct Feedback ID, we write "Feedback_xxx" which is replaced with the correct
         Feedback ID in feedback service.
        */
        String base = "http://www.dfki.de/SmartMaaS/feedback#";
        String exf = "http://www.example.fe/edback#";
        String owl = "http://www.w3.org/2002/07/owl#";
        String xsd = "http://www.w3.org/2001/XMLSchema#";
        String rdfs = "http://www.w3.org/2000/01/rdf-schema#";
        String xml = "http://www.w3.org/XML/1998/namespace#";
        String time = "http://www.w3.org/2006/time#";
        String foaf = "http://xmlns.com/foaf/spec/";
        String geo = "http://www.w3.org/2003/01/geo/wgs84_pos#";
        String rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
        modelBuilder.setNamespace("base", base)
                .setNamespace("owl", owl)
                .setNamespace("rdf", rdf)
                .setNamespace("xml", xml)
                .setNamespace("xsd", xsd)
                .setNamespace("rdfs", rdfs)
                .setNamespace("time", time)
                .setNamespace("geo", geo)
                .setNamespace("exf", exf)
                .setNamespace("foaf", foaf);


        IRI feedbackIRI = vf.createIRI(exf, feedbackID);

        modelBuilder.subject(feedbackIRI)
                .add(RDF.TYPE, "base:Feedback");

        Model feedbackModel = modelBuilder.build();

        Resource userBNode = vf.createBNode();
        Resource permanentReasonBNode = vf.createBNode();
        Resource passingReasonBNode = vf.createBNode();
        Resource otherReasonBNode = vf.createBNode();
        Resource timestampBNode = vf.createBNode();
        Resource delayBNode = vf.createBNode();
        Resource vehicleBNode = vf.createBNode();
        Resource locationBNode = vf.createBNode();

        // username statements
        Statement userStatement = vf.createStatement(feedbackIRI, vf.createIRI(base, "submittedBy"), userBNode);
        Statement userBNodeStatement = vf.createStatement(userBNode, vf.createIRI(foaf, "accountName"), vf.createLiteral(username));
        feedbackModel.add(userStatement);
        feedbackModel.add(userBNodeStatement);

        // reason statements
        IRI reasonForIRI = vf.createIRI(base, "reasonFor");
        for (Reason r : getReasons()) {
            switch (r.getType()) {
                case Reason.PERMANENT_REASON: {
                    Statement permanentReasonStatement = vf.createStatement(feedbackIRI, vf.createIRI(base, "becauseOf"), permanentReasonBNode);
                    Statement pRS1 = vf.createStatement(permanentReasonBNode, RDF.TYPE, vf.createLiteral(Reason.PERMANENT_REASON));
                    Statement pRS2 = vf.createStatement(permanentReasonBNode, RDF.TYPE, vf.createLiteral(r.getName()));
                    Statement pRS3 = vf.createStatement(permanentReasonBNode, reasonForIRI, feedbackIRI);
                    feedbackModel.add(permanentReasonStatement);
                    feedbackModel.add(pRS1);
                    feedbackModel.add(pRS2);
                    feedbackModel.add(pRS3);
                    break;
                }
                case Reason.PASSING_REASON: {
                    Statement passingReasonStatement = vf.createStatement(feedbackIRI, vf.createIRI(base, "becauseOf"), passingReasonBNode);
                    Statement pRS1 = vf.createStatement(passingReasonBNode, RDF.TYPE, vf.createLiteral(Reason.PASSING_REASON));
                    Statement pRS2 = vf.createStatement(passingReasonBNode, RDF.TYPE, vf.createLiteral(r.getName()));
                    Statement pRS3 = vf.createStatement(passingReasonBNode, reasonForIRI, feedbackIRI);
                    feedbackModel.add(passingReasonStatement);
                    feedbackModel.add(pRS1);
                    feedbackModel.add(pRS2);
                    feedbackModel.add(pRS3);
                    break;
                }
                case Reason.OTHER_REASON:
                    Statement otherReasonStatement = vf.createStatement(feedbackIRI, vf.createIRI(base, "becauseOf"), otherReasonBNode);
                    Statement oRS1 = vf.createStatement(otherReasonBNode, RDF.TYPE, vf.createLiteral(Reason.OTHER_REASON));
                    Statement oRS2 = vf.createStatement(otherReasonBNode, vf.createIRI(base, "givenBy"), vf.createLiteral(r.getName()));
                    Statement oRS3 = vf.createStatement(otherReasonBNode, reasonForIRI, feedbackIRI);
                    feedbackModel.add(otherReasonStatement);
                    feedbackModel.add(oRS1);
                    feedbackModel.add(oRS2);
                    feedbackModel.add(oRS3);
                    break;
            }
        }
        // timestamp statements
        Statement timestampStatement = vf.createStatement(feedbackIRI, vf.createIRI(base, "hasTimestamp"), timestampBNode);
        Statement tsBS1 = vf.createStatement(timestampBNode, vf.createIRI(time, "xsdDateTime"),
                vf.createLiteral(getDateTime(), vf.createIRI(xsd, "dateTime")));
        Statement tsBS2 = vf.createStatement(timestampBNode, vf.createIRI(base, "timeStampOf"), feedbackIRI);
        feedbackModel.add(timestampStatement);
        feedbackModel.add(tsBS1);
        feedbackModel.add(tsBS2);

        // delay statements
        Statement delayStatement = vf.createStatement(feedbackIRI, vf.createIRI(base, "causedDelay"), delayBNode);
        Statement dBS1 = vf.createStatement(delayBNode, vf.createIRI(time, "hasXSDDuration"),
                vf.createLiteral(duration, vf.createIRI(xsd, "Duration")));
        Statement dBS2 = vf.createStatement(delayBNode, vf.createIRI(base, "delayOf"), feedbackIRI);
        feedbackModel.add(delayStatement);
        feedbackModel.add(dBS1);
        feedbackModel.add(dBS2);

        // vehicle statements
        Statement vehicleStatement = vf.createStatement(feedbackIRI, vf.createIRI(base, "affects"), vehicleBNode);
        Statement vBS1 = vf.createStatement(vehicleBNode, RDF.TYPE, vf.createIRI(base, getVehicle()));
        Statement vBS2 = vf.createStatement(vehicleBNode, vf.createIRI(base, "Line"),
                vf.createLiteral(getVehicleNo(), vf.createIRI(xsd, "String")));
        Statement vBS3 = vf.createStatement(vehicleBNode, vf.createIRI(base, "affectedBy"), feedbackIRI);
        feedbackModel.add(vehicleStatement);
        feedbackModel.add(vBS1);
        feedbackModel.add(vBS2);
        feedbackModel.add(vBS3);

        // location statements
        Statement locationStatement = vf.createStatement(feedbackIRI, vf.createIRI(base, "atLocation"), locationBNode);
        Statement lBS1 = vf.createStatement(locationBNode, vf.createIRI(geo, "long"), vf.createLiteral(getLocation().getLng() + ""));
        Statement lBS2 = vf.createStatement(locationBNode, vf.createIRI(geo, "lat"), vf.createLiteral(getLocation().getLat() + ""));
        Statement lBS3 = vf.createStatement(locationBNode, vf.createIRI(base, "Address"),
                vf.createLiteral(getLocation().getName(), vf.createIRI(xsd, "String")));
        Statement lBS4 = vf.createStatement(locationBNode, vf.createIRI(base, "locationOf"), feedbackIRI);
        feedbackModel.add(locationStatement);
        feedbackModel.add(lBS1);
        feedbackModel.add(lBS2);
        feedbackModel.add(lBS3);
        feedbackModel.add(lBS4);

        // convert model to string
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

    public String getRDFModel() {

        /*
         * 1. blank nodlari yarat
         * 2. her blank nod ucun bir statement seti yarat.
         * 3. blank nodun subject oldugu statementleri bu sete add
         * 4. blank nodun object oldugu statementler yarat
         * 5. foreach blank node
         * { blankin object oldugu statementi modele add
         *   blankin subject oldugu statement seti modele add}
         *
         * which blank nodes exist?
         *  - user_blank_node,
         *  - permanent_reason_blank_node,
         *  - other_reason_blank_node,
         *  - passing_reason_blank_node,
         *  - timestamp_blank_node
         *  - delay_blank_node
         *  - location_blank_node
         *  - vehicle_blank_node
         *
         * */
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
