package ba.fet.rwa;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/")
public class App extends ResourceConfig {
    public App() {
        packages("ba.fet.rwa.endpoints");
        packages("ba.fet.rwa.filters");
        register(MultiPartFeature.class);
    }
}
