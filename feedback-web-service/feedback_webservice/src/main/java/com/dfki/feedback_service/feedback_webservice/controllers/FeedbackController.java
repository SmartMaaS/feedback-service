package com.dfki.feedback_service.feedback_webservice.controllers;

import com.dfki.feedback_service.feedback_webservice.models.Feedback;
import com.dfki.feedback_service.feedback_webservice.services.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

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
        System.out.println("FeedbackController.saveFeedback");
        Feedback feedback = feedbackService.rdfToFeedback(userFeedback, "TURTLE");
        feedbackService.setToken(token);
        feedbackService.setPage(page);
        feedbackService.setSize(size);
        feedbackService.findNearbyStops(feedback, page, size);
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
