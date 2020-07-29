package de.dfki.feedback_service.feedback_webservice;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.RDFCollections;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.config.RepositoryConfig;
import org.eclipse.rdf4j.repository.config.RepositoryConfigException;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.repository.manager.RepositoryInfo;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryProvider;
import org.eclipse.rdf4j.repository.sail.config.SailRepositoryConfig;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.memory.config.MemoryStoreConfig;

import java.util.Collection;

public class RDF4JRepositoryHandler {
    private final static String rdf4jServer = "http://localhost:8090/rdf4j";

    public static Repository getRepository(String repoID) {
        Repository repository = RepositoryProvider.getRepositoryManager(rdf4jServer).getRepository(repoID);
        if (repository == null) {
            repository = createRemoteRepository(repoID);
        }

        return initialize(repository);
    }

    public static Repository createRemoteRepository(final String repoID) throws RepositoryConfigException, RepositoryException {
        RepositoryManager repositoryManager = RepositoryProvider.getRepositoryManager(rdf4jServer);
        MemoryStoreConfig backendConfig = new MemoryStoreConfig();
        SailRepositoryConfig repositoryTypeSpec = new SailRepositoryConfig(backendConfig);
        RepositoryConfig config = new RepositoryConfig(repoID, repositoryTypeSpec);
        config.setID(repoID);
        repositoryManager.addRepositoryConfig(config);
        RepositoryInfo info = repositoryManager.getRepositoryInfo(repoID);
        return new HTTPRepository(info.getLocation().toString());
    }

    public static Collection<Repository> getAllRepositories() {
        return RepositoryProvider.getRepositoryManager(rdf4jServer).getAllRepositories();
    }

    public static Repository getFeedbackRepository() {
        String feedbackRepoID = "feedback_repository";
        return getRepository(feedbackRepoID);
    }

    public static void addModelToRepository(Repository repository, Model model) {
        try (RepositoryConnection conn = initialize(repository).getConnection()) {
            conn.add(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Repository initialize(Repository repository) {
        if (!repository.isInitialized()) {
            repository.init();
        }
        return repository;
    }

}
