package org.example.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.example.services.DatabaseService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.postgresql.ds.PGSimpleDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public abstract class TestBase
{
    protected static WireMockServer wireMockServer;
    protected static PostgreSQLContainer<?> postgreSQLContainer;
    protected static DatabaseService dataBaseService;

    @BeforeAll
    static void oneTimeSetUp()
    {
        postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15-alpine"))
                .withDatabaseName("testdb")
                .withUsername("testuser")
                .withPassword("testpass");
        postgreSQLContainer.start();

        dataBaseService = new DatabaseService(getDataSource());
        dataBaseService.createTable();

        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();
    }

    @AfterAll
    static void oneTimeTearDown()
    {
        if(postgreSQLContainer != null)
            postgreSQLContainer.stop();

        if (wireMockServer != null)
            wireMockServer.stop();
    }

    private static DataSource getDataSource()
    {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setURL(postgreSQLContainer.getJdbcUrl());
        dataSource.setUser(postgreSQLContainer.getUsername());
        dataSource.setPassword(postgreSQLContainer.getPassword());
        return dataSource;
    }

    protected String getApiBaseUrl()
    {
        return "http://localhost:"+wireMockServer.port();
    }
}
