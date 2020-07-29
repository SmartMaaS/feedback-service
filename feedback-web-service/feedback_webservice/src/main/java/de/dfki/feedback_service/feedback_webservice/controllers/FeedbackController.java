package de.dfki.feedback_service.feedback_webservice.controllers;

import de.dfki.feedback_service.feedback_webservice.RDF4JRepositoryHandler;
import de.dfki.feedback_service.feedback_webservice.models.Feedback;
import de.dfki.feedback_service.feedback_webservice.services.FeedbackService;
import de.dfki.feedback_service.feedback_webservice.utils.Utils;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class FeedbackController {
    private static final String token_header_key = "Current-Token_FBase";
    private static final String page_header_key = "Page";
    private static final String size_header_key = "Size";

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping(value = "/feedback", consumes = "text/turtle")
    public ResponseEntity<?> saveFeedback(@RequestHeader(page_header_key) int page,
                                          @RequestHeader(size_header_key) int size,
                                          @RequestHeader(token_header_key) String token,
                                          @RequestBody final String userFeedback) {
        System.out.println("User Feedback: " + userFeedback);
        Model feedbackModel = null;
        try {
            feedbackModel = Utils.rdfToModel(userFeedback, RDFFormat.TURTLE);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        RDF4JRepositoryHandler.addModelToRepository(RDF4JRepositoryHandler.getFeedbackRepository(),
                feedbackModel);

         /*Following lines:
         1. parses input into Feedback object
         2. finds nearby stops by calling gtfsld service
         3. sends nearby stops to the feedback app
         Since Alternative Routes functionality is out of the scope of Feedback Service,
         functionality is useless.

        Feedback feedback = null;
        try {
            feedback = feedbackService.rdfToFeedback(userFeedback, RDFFormat.TURTLE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        feedbackService.setToken(token);
        feedbackService.setPage(page);
        feedbackService.setSize(size);
        feedbackService.findNearbyStops(feedback, page, size);*/
        return ResponseEntity.accepted().body(userFeedback);
    }

    @PostMapping(value = "/feedback", consumes = {"application/xml", "application/json"})
    public ResponseEntity<?> saveFeedbackNonRdf(@RequestHeader(page_header_key) String page,
                                                @RequestHeader(size_header_key) String size,
                                                @RequestHeader(token_header_key) String token,
                                                @RequestBody final String userFeedback) {
        System.out.println("User has send the following feedback:");
        System.out.println(userFeedback);

        Feedback feedback = null;
        try {
            feedback = feedbackService.convert_JSON_or_XML_to_FEEDBACK(userFeedback);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.accepted().body(feedback);
    }
}
