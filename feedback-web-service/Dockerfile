FROM adoptopenjdk:11-jre-hotspot
RUN mkdir -p /feedback-service/app
RUN mkdir -p /rdf4j-server
COPY /feedback_webservice/target/feedback_webservice-0.0.1-SNAPSHOT.jar feedback-service/app
COPY /rdf4j_server/target/rdf4j_server-1.0-SNAPSHOT-war-exec.jar rdf4j-server
EXPOSE 8803
EXPOSE 8090
CMD ["java", "-jar", "/feedback-service/app/feedback_webservice-0.0.1-SNAPSHOT.jar"]
CMD ["java", "-jar", "/rdf4j-server/rdf4j_server-1.0-SNAPSHOT-war-exec.jar", "--httpPort=8090"]