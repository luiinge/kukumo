/**
 * @author Luis Iñesta Gelabert - linesta@iti.es | luiinge@gmail.com
 */
package iti.kukumo.rest.test;


import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import iti.commons.configurer.Configurator;
import iti.commons.configurer.Property;
import iti.kukumo.api.KukumoConfiguration;
import iti.kukumo.gherkin.GherkinResourceType;
import iti.kukumo.junit.KukumoJUnitRunner;
import iti.kukumo.rest.test.mockserver.HttpServerAdapter;
import iti.kukumo.rest.test.mockserver.InMemoryServer;



@Configurator(properties = {
                @Property(key = KukumoConfiguration.RESOURCE_TYPES, value = GherkinResourceType.NAME),
                @Property(key = KukumoConfiguration.RESOURCE_PATH, value = "src/test/resources/test-rest-json.feature"),
                @Property(key = KukumoConfiguration.OUTPUT_FILE_PATH, value = "target/kukumo.json")
})
@RunWith(KukumoJUnitRunner.class)
public class TestRestStepsJson {

    private static InMemoryServer server;


    @BeforeClass
    public static void setupServer() throws IOException {
        server = new InMemoryServer(
            HttpServerAdapter.serverFactory(),
            InMemoryServer.Format.JSON, StandardCharsets.UTF_8, 8888, "src/test/resources/data.json",
            InMemoryServer.Format.JSON
        );
    }


    @AfterClass
    public static void teardownServer() throws IOException {
        server.stop();
    }
}
