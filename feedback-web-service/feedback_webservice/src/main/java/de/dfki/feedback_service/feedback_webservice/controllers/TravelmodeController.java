package de.dfki.feedback_service.feedback_webservice.controllers;

import de.dfki.feedback_service.feedback_webservice.swagger.SwaggerConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpStatus;

@RestController
@Api(tags = {SwaggerConfig.FEEDBACK_RECEIVER_TAG})
public class TravelmodeController {
    private static final String page_header_key = "Page";
    private static final String size_header_key = "Size";

    @ApiOperation(value = "Receiving and Storing the travel mode of a certain user")
    @PostMapping(value = "/travelmode", consumes = "text/turtle", produces = "application/ld+json")
    @ApiImplicitParam(name = "userName", value = "user name")
    public ResponseEntity<?> saveFeedback(@RequestBody String userName) {
		return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
