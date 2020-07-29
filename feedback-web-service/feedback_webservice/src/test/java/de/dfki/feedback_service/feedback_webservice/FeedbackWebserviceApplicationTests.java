package de.dfki.feedback_service.feedback_webservice;

import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.logging.Logger;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FeedbackWebserviceApplicationTests {
    private final static Logger LOGGER = Logger.getLogger(FeedbackWebserviceApplication.class.getName());
    private final static String rdf4jServer = "http://localhost:8090/rdf4j";

    @Test
    public void contextLoads() {
    }

    @Test
    public void rdf4jServerTest() {
        RepositoryManager repoManager = RepositoryProvider.getRepositoryManager(rdf4jServer);
        try {
            repoManager.getAllRepositories();
        } catch (Exception e) {
//            e.printStackTrace();
            LOGGER.severe("Please, run start_rdf4j_server.bat file!!!");
            assert false;
        }

        assert repoManager.getAllRepositories().size() >= 0;
        assert repoManager.isInitialized();
    }

}
