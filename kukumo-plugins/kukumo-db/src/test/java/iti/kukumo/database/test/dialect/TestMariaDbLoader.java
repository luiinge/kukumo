/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

/**
 * author Luis Iñesta Gelabert - linesta@iti.es | luiinge@gmail.com
 */
package iti.kukumo.database.test.dialect;

import imconfig.AnnotatedConfiguration;
import imconfig.Configuration;
import imconfig.Property;
import iti.kukumo.api.KukumoConfiguration;
import iti.kukumo.core.junit.KukumoJUnitRunner;
import iti.kukumo.database.DatabaseConfigContributor;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


@RunWith(KukumoJUnitRunner.class)
@AnnotatedConfiguration({
        @Property(key = KukumoConfiguration.RESOURCE_TYPES, value = "gherkin"),
        @Property(key = KukumoConfiguration.RESOURCE_PATH, value = "src/test/resources/features"),
        @Property(key = DatabaseConfigContributor.DATABASE_CONNECTION_USERNAME, value = "root"),
        @Property(key = DatabaseConfigContributor.DATABASE_CONNECTION_PASSWORD, value = "password"),
        @Property(key = DatabaseConfigContributor.DATABASE_METADATA_CASE_SENSITIVITY, value = "lower_cased"),
        @Property(key = DatabaseConfigContributor.DATABASE_ENABLE_CLEANUP_UPON_COMPLETION, value = "false")
})

@Category({LinuxTests.class, WindowsTests.class})
public class TestMariaDbLoader {

    @BeforeClass
    public static Configuration setUp(Configuration config) throws IOException {

        GenericContainer mysqlContainer = new GenericContainer("mariadb:10.6.4")
                .withExposedPorts(3306)
                .withEnv("MARIADB_ROOT_PASSWORD", "password")
                .withCommand("--character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci");
        mysqlContainer.start();

        String jdbcUrl = "jdbc:mysql://" + mysqlContainer.getContainerIpAddress() + ":" + mysqlContainer.getMappedPort(3306);
        new JdbcTemplate(new DriverManagerDataSource(jdbcUrl, "root", "password")).execute("CREATE DATABASE mydb");

        JdbcTemplate jdbcTemplate = new JdbcTemplate(new DriverManagerDataSource(jdbcUrl + "/mydb", "root", "password"));


        File schemaFile = new File(TestMariaDbLoader.class.getResource("/db/create-schema.sql").getFile());
        String createSchema = FileUtils.readFileToString(schemaFile, StandardCharsets.UTF_8);
        for (String sentence : createSchema.split(";")) {
            jdbcTemplate.execute(sentence);
        }


        return config.appendProperty(DatabaseConfigContributor.DATABASE_CONNECTION_URL,jdbcUrl + "/mydb");
    }

}