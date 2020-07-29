package dfki.com.smartmaas.feedbackservice.model;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.vocabulary.RDF;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleNamespace;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.io.StringWriter;
import java.util.List;

@Root
public class Feedback {
    private static final String tag = "Feedback";
    private String feedbackURI = "http://www.example.com/feedback";
    @Element
    private Location location;
    @Element
    private int stuckTime;
    @Element
    private String measurementUnit;
    @Element
    private String vehicle;
    @Element
    private String vehicleNo;
    @ElementList
    private List<Reason> reasons;
    @Element
    private int radius;

    public Feedback(Location location, int stuckTime, String measurementUnit, String vehicle, String vehicleNo) {
        this.location = location;
        this.stuckTime = stuckTime;
        this.measurementUnit = measurementUnit;
        this.vehicle = vehicle;
        this.vehicleNo = vehicleNo;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getStuckTime() {
        return stuckTime;
    }

    public void setStuckTime(int stuckTime) {
        this.stuckTime = stuckTime;
    }

    public String getMeasurementUnit() {
        return measurementUnit;
    }

    public void setMeasurementUnit(String measurementUnit) {
        this.measurementUnit = measurementUnit;
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

    public void getRDFForm(RDFFormat rdfFormat) {
        org.eclipse.rdf4j.model.Model model = new LinkedHashModel();
        ValueFactory valueFactory = SimpleValueFactory.getInstance();

        Namespace feedbackNamespace = new SimpleNamespace("feedback", "http://example.org/feedback/");
        IRI feedback = valueFactory.createIRI(feedbackNamespace.getName());
        IRI geo = valueFactory.createIRI(feedbackNamespace.getName() + "location");
        IRI ns3 = valueFactory.createIRI(feedbackNamespace.getName() + "stuck_duration");
        IRI vhc = valueFactory.createIRI(feedbackNamespace.getName() + "travelsOn");
        IRI cse = valueFactory.createIRI(feedbackNamespace.getName() + "because_of");
        IRI rsn = valueFactory.createIRI(feedbackNamespace.getName() + "reasons");
        IRI lcnn = valueFactory.createIRI(feedbackNamespace.getName() + "name");
        IRI lcnlg = valueFactory.createIRI(feedbackNamespace.getName() + "lat");
        IRI lcnlt = valueFactory.createIRI(feedbackNamespace.getName() + "long");
        IRI emotion = valueFactory.createIRI(feedbackNamespace.getName() + "radius");

        Literal location = valueFactory.createLiteral(getLocation().getName());



        Statement statement = valueFactory.createStatement(feedback, lcnn, location);


    }

    public String getRDFOutput(String rdfOutputFormat) {

        Model model = ModelFactory.createDefaultModel();
        Property geo = model.createProperty("https://www.w3.org/2003/01/geo/wgs84_pos#", "location");
        Property ns3 = model.createProperty("http://purl.org/NET/c4dm/timeline.owl#", "stuck_duration");
        Property vhc = model.createProperty("http://purl.org/dc/elements/1.1/", "travelsOn");
        Property cse = model.createProperty("http://dublincore.org/documents/dces/", "because_of");
        Property rsn = model.createProperty("http://www.w3.org/2002/07/owl#", "reasons");
        Property lcnn = model.createProperty("https://www.w3.org/2003/01/geo/wgs84_pos#", "name");
        Property lcnlg = model.createProperty("https://www.w3.org/2003/01/geo/wgs84_pos#", "lat");
        Property lcnlt = model.createProperty("https://www.w3.org/2003/01/geo/wgs84_pos#", "long");
        Property emotion = model.createProperty("http://ns.inria.fr/emoca/#", "radius");

        Resource reasons = model.createResource(RDF.List);
        for (Reason reason : getReasons()) {
            reasons.addProperty(cse, reason.getName());
        }

        Resource location = model.createResource();
        location.addProperty(lcnn, getLocation().getName());
        location.addProperty(lcnlt, String.valueOf(getLocation().getLat()));
        location.addProperty(lcnlg, String.valueOf(getLocation().getLng()));

        model.createResource(getFeedbackURI())
                .addProperty(geo, location)
                .addProperty(ns3, getMeasurementUnit())
                .addProperty(ns3, String.valueOf(getStuckTime()))
                .addProperty(vhc, getVehicleNo())
                .addProperty(vhc, getVehicle())
                .addProperty(rsn, reasons)
                .addProperty(emotion, String.valueOf(getRadius()));

        model.setNsPrefix(RDF.List.getLocalName(), RDF.List.getNameSpace());
        model.setNsPrefix("owl", rsn.getNameSpace());
        model.setNsPrefix("cse", cse.getNameSpace());
        model.setNsPrefix("vhc", vhc.getNameSpace());
        model.setNsPrefix("geo", geo.getNameSpace());
        model.setNsPrefix("ns3", ns3.getNameSpace());
        model.setNsPrefix("emotion", emotion.getNameSpace());
        StringWriter out = new StringWriter();
        model.write(out, rdfOutputFormat);
//        System.out.println("String.valueOf(out) = " + String.valueOf(out));
        return String.valueOf(out);
    }

    public static String getTag() {
        return tag;
    }
}
