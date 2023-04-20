package iti.kukumo.database.test.dialect;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.containers.GenericContainer;

import static org.junit.Assert.assertEquals;
public class PostgreSQLDatabaseTest {
    @ClassRule
    public static GenericContainer postgresContainer = new GenericContainer("postgres:13")
            .withExposedPorts(5432)
            .withEnv("POSTGRES_PASSWORD", "password")
            .withEnv("POSTGRES_DB", "mydb");

    private static JdbcTemplate jdbcTemplate;

    @BeforeClass
    public static void setUp() throws ClassNotFoundException {
        String jdbcUrl = "jdbc:postgresql://" +
                postgresContainer.getContainerIpAddress() + ":" + postgresContainer.getMappedPort(5432) + "/mydb";
        Class.forName("org.postgresql.Driver");
        jdbcTemplate = new JdbcTemplate(
                new DriverManagerDataSource(jdbcUrl, "postgres", "password"));
    }

    @Test
    public void testInsert() {
        jdbcTemplate.execute("CREATE TABLE users (id INTEGER PRIMARY KEY, name VARCHAR(255))");
        jdbcTemplate.execute("INSERT INTO users VALUES (1, 'John Doe')");
        jdbcTemplate.execute("INSERT INTO users VALUES (2, 'Jane Doe')");

        int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
        assertEquals(2, count);
    }

}
