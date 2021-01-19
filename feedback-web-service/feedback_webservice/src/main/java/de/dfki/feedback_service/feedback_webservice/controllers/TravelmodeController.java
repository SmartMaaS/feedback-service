package de.dfki.feedback_service.feedback_webservice.controllers;

import de.dfki.feedback_service.feedback_webservice.swagger.SwaggerConfig;
import de.dfki.feedback_service.feedback_webservice.utils.RDF4JRepositoryHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.query.GraphQuery;
import org.eclipse.rdf4j.query.GraphQueryResult;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.Rio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@Api(tags = {SwaggerConfig.FEEDBACK_RECEIVER_TAG})
public class TravelmodeController {
    private static final String page_header_key = "Page";
    private static final String size_header_key = "Size";

    @ApiOperation(value = "Receiving the most recent travel mode of a certain user")
    @GetMapping(value = "/travelmode", produces = "application/ld+json")
    @ApiImplicitParam(name = "userName", value = "user name")
    public ResponseEntity<?> getTravelModeOfSpecificUser(@RequestParam final String userName) {
		Repository feedbackRepository = RDF4JRepositoryHandler.getRepository("travel_mode");
		String queryString = "@PREFIX foaf: <http://xmlns.com/foaf/spec/> \n"
				+ "@PREFIX  smf: <http://www.dfki.de/SmartMaaS/feedback#> \n"
				+ "@PREFIX  time: <http://www.w3.org/2006/time#> \n"
				+ "CONSTRUCT ?t a smf:Travelmode . \n"
				+ "WHERE { \n"
				+ userName + " smf:travelsBy ?t. \n"
				+ "?t time:xsdDateTime ?timestamp \n"
				+ "} \n"
				+ "ORDER BY ?timestamp \n"
				+ "LIMIT 1";
		try (RepositoryConnection conn = feedbackRepository.getConnection()) {
			ByteArrayOutputStream modelAsJsonLd = getQueryResultAsJsonLD(conn, queryString);
			return new ResponseEntity<>(modelAsJsonLd.toString(StandardCharsets.UTF_8), HttpStatus.OK);
		} catch (Exception ex) {
			return new ResponseEntity<>("Could not get travelmode: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }

	@ApiOperation(value = "Get all users who are currently travelling by a certain means of transport")
    @GetMapping(value = "/travelmode/byBus", produces = "application/ld+json")
    public ResponseEntity<?> getUserOnBus(@RequestParam final String vehicleType) {
		Repository feedbackRepository = RDF4JRepositoryHandler.getRepository("travel_mode");
		String queryString = "@PREFIX foaf: <http://xmlns.com/foaf/spec/> \n"
				+ "@PREFIX  smf: <http://www.dfki.de/SmartMaaS/feedback#> \n"
				+ "@PREFIX  time: <http://www.w3.org/2006/time#> \n"
				+ "CONSTRUCT ?u smf:travelsBy " + vehicleType + " . \n"
				+ "WHERE { \n"
				+ " SELECT DISTINCT ?user smf:travelsBy " + vehicleType + " AS ?u WHERE { \n"
				+ " ?travelmode foaf:accountName ?user \n"
				+ "	?travelmode time:xsdDateTime ?t \n"
				+ "	 FILTER NOT EXISTS { \n"
				+ "	?travelmode foaf:accountName ?user \n"
				+ "	?travelmode time:xsdDateTime ?t2"
				+ "	filter (?t2 > ?t) \n"
				+ "		}"
				+ "	}"
				+ "}";
		try (RepositoryConnection conn = feedbackRepository.getConnection()) {
			ByteArrayOutputStream modelAsJsonLd = getQueryResultAsJsonLD(conn, queryString);
			return new ResponseEntity<>(modelAsJsonLd.toString(StandardCharsets.UTF_8), HttpStatus.OK);
		} catch (Exception ex) {
			return new ResponseEntity<>("Could not get travelmode: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }

	private ByteArrayOutputStream getQueryResultAsJsonLD(final RepositoryConnection conn, String queryString) throws RepositoryException, QueryEvaluationException, MalformedQueryException, RDFHandlerException {
		GraphQuery query = conn.prepareGraphQuery(queryString);
		GraphQueryResult travelMode = query.evaluate();
		Model resultModel = QueryResults.asModel(travelMode);
		ByteArrayOutputStream modelAsJsonLd = new ByteArrayOutputStream();
		Rio.write(resultModel, modelAsJsonLd, RDFFormat.JSONLD);
		return modelAsJsonLd;
	}
    }
}
