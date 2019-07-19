package depends.persistent.neo4j.executor;

import org.neo4j.ogm.config.ClasspathConfigurationSource;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

class Neo4jSessionFactory {

    private static ClasspathConfigurationSource configurationSource =
            new ClasspathConfigurationSource("ogm.properties");
    private static Configuration configuration = new Configuration.Builder(configurationSource).build();
    
    private static SessionFactory sessionFactory = new SessionFactory(configuration, "depends.persistent.neo4j.executor");
    private static Neo4jSessionFactory factory = new Neo4jSessionFactory();

    static Neo4jSessionFactory getInstance() {
        return factory;
    }

    private Neo4jSessionFactory() {
    }

    Session getNeo4jSession() {
    	configurationSource.properties().setProperty(key, value)
        return sessionFactory.openSession();
    }
}