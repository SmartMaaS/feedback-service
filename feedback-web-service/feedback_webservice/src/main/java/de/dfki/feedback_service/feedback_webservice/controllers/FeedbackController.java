package de.dfki.feedback_service.feedback_webservice.controllers;

import de.dfki.feedback_service.feedback_webservice.models.Feedback;
import de.dfki.feedback_service.feedback_webservice.services.FeedbackService;
import de.dfki.feedback_service.feedback_webservice.swagger.SwaggerConfig;
import de.dfki.feedback_service.feedback_webservice.utils.RDF4JRepositoryHandler;
import de.dfki.feedback_service.feedback_webservice.utils.Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import java.io.ByteArrayOutputStream;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.eclipse.rdf4j.query.GraphQuery;
import org.eclipse.rdf4j.query.GraphQueryResult;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.Rio;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@Api(tags = {SwaggerConfig.FEEDBACK_RECEIVER_TAG})
public class FeedbackController {
    private static final String page_header_key = "Page";
    private static final String size_header_key = "Size";

    @Autowired
    private FeedbackService feedbackService;

    @ApiOperation(value = "Receiving and Storing a single Feedback at a time")
    @PostMapping(value = "/feedback", consumes = "text/turtle", produces = "text/turtle")
    @ApiImplicitParam(name = "userFeedback", value = "Feedback Input",
            example = Feedback.example, format = "text/turtle")
    public ResponseEntity<?> saveFeedback(@RequestBody String userFeedback) {


        System.out.println("User Feedback: " + userFeedback);
        Model feedbackModel = null;
        try {
            feedbackModel = Utils.rdfToModel(feedbackService.setFeedbackID(userFeedback), RDFFormat.TURTLE);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        RDF4JRepositoryHandler.addModelToRepository(RDF4JRepositoryHandler.getFeedbackRepository(),
                feedbackModel);

        return ResponseEntity
                .ok(userFeedback);
    }

    @ApiOperation(value = "Get all Feedbacks stored in the store currently at a time in JSON LD format")
    @GetMapping(value = "/feedback", produces = "application/ld+json")
    public ResponseEntity<?> getFeedbackAsJsonLD() {
		Repository feedbackRepository = RDF4JRepositoryHandler.getRepository("example_feedback");
		String queryString = "PREFIX smf: <http://www.dfki.de/SmartMaaS/feedback#> \n"
				+ "DESCRIBE ?feedback \n"
				+ "WHERE { ?feedback a smf:Feedback }";
		try (RepositoryConnection conn = feedbackRepository.getConnection()) {
			GraphQuery query = conn.prepareGraphQuery(queryString);
			GraphQueryResult feedbacks = query.evaluate();
			Model resultModel = QueryResults.asModel(feedbacks);
			ByteArrayOutputStream modelAsJsonLd = new ByteArrayOutputStream();
			Rio.write(resultModel, modelAsJsonLd, RDFFormat.JSONLD);
			return new ResponseEntity<>(modelAsJsonLd.toString(StandardCharsets.UTF_8), HttpStatus.OK);
		} catch (Exception ex) {
			return new ResponseEntity<>("Could not get feedbacks: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }

	@ApiOperation(value = "Get all Feedbacks stored in the store currently at a time")
    @GetMapping(value = "/feedback", produces = "text/turtle")
    @ApiImplicitParam(name = "userFeedback", value = "Feedback Input",
            example = Feedback.example, format = "application/ld+json")
    public ResponseEntity<?> getFeedbackAsTurtle() {
		Repository feedbackRepository = RDF4JRepositoryHandler.getRepository("example_feedback");
		String queryString = "PREFIX smf: <http://www.dfki.de/SmartMaaS/feedback#> \n"
				+ "DESCRIBE ?feedback \n"
				+ "WHERE { ?feedback a smf:Feedback }";
		try (RepositoryConnection conn = feedbackRepository.getConnection()) {
			GraphQuery query = conn.prepareGraphQuery(queryString);
			GraphQueryResult feedbacks = query.evaluate();
			Model resultModel = QueryResults.asModel(feedbacks);
			ByteArrayOutputStream modelAsTurtle = new ByteArrayOutputStream();
			Rio.write(resultModel, modelAsTurtle, RDFFormat.TURTLE);
			return new ResponseEntity<>(modelAsTurtle.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			return new ResponseEntity<>("Could not get feedbacks: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
}
