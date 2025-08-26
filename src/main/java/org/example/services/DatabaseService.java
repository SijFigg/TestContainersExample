package org.example.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;

public class DatabaseService {

    private final DataSource dataSource;
    private static final Logger log = LoggerFactory.getLogger(DatabaseService.class);

    public DatabaseService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void createTable() {
        String query = "CREATE TABLE IF NOT EXISTS data (id INT PRIMARY KEY, name VARCHAR(255))";

        try (Connection conn = dataSource.getConnection()) {
            conn.createStatement().execute(query);
            log.info("Table created or exists");
        }
        catch (SQLException e)
        {
            log.error("Table creation failed due to error: {}", e.getMessage());
            throw new RuntimeException("Table creation failed due to error: "+e.getMessage());
        }
    }

    public void insertRecord(int id, String name) {
        String query = "INSERT INTO data (id, name) VALUES (?, ?)";

        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ps.setString(2, name);
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            log.error("Insert record failed due to error: {}", e.getMessage());
            throw new RuntimeException("Insert record failed due to error: "+e.getMessage());
        }
    }

    public String findRecordById(int id)
    {
        String query = "SELECT * FROM data WHERE id = ?";
        try(Connection conn = dataSource.getConnection())
        {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            var rs = ps.executeQuery();

            if(rs.next())
            {
                return rs.getString("name");
            }
        }
        catch (SQLException e)
        {
            log.error("findRecordById failed due to error: {}", e.getMessage());
            throw new RuntimeException("findRecordById failed due to error: "+e.getMessage());
        }
        return null;
    }

    public boolean recordExists(int id, String name)
    {

        String query = "SELECT COUNT(*) FROM data WHERE id = ? AND name = ?";

        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ps.setString(2, name);
            var rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        }
        catch (SQLException e)
            {
            log.error("Record exists failed due to error: {}", e.getMessage());
            throw new RuntimeException("Record exists failed due to error: "+e.getMessage());
            }
    }
}
