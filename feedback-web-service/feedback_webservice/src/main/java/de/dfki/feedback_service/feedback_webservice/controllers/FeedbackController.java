package de.dfki.feedback_service.feedback_webservice.controllers;

import de.dfki.feedback_service.feedback_webservice.models.Feedback;
import de.dfki.feedback_service.feedback_webservice.services.FeedbackService;
import de.dfki.feedback_service.feedback_webservice.swagger.SwaggerConfig;
import de.dfki.feedback_service.feedback_webservice.utils.RDF4JRepositoryHandler;
import de.dfki.feedback_service.feedback_webservice.utils.Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

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
}
