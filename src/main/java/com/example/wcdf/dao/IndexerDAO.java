package com.example.wcdf.dao;

import com.example.wcdf.model.Indexer;
import com.example.wcdf.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Indexer entity
 * Handles all CRUD operations for the indexer table
 */
public class IndexerDAO {

    /**
     * Get all indexers
     * @return List of all Indexer objects
     */
    public List<Indexer> findAll() {
        List<Indexer> indexers = new ArrayList<>();
        String sql = "SELECT index_id, name, valueMin, valueMax FROM indexer ORDER BY name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Indexer indexer = new Indexer();
                indexer.setIndexId(rs.getInt("index_id"));
                indexer.setName(rs.getString("name"));
                indexer.setValueMin(rs.getInt("valueMin"));
                indexer.setValueMax(rs.getInt("valueMax"));
                indexers.add(indexer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return indexers;
    }

    /**
     * Find indexer by ID
     * @param indexId Indexer ID to find
     * @return Indexer object or null if not found
     */
    public Indexer findById(int indexId) {
        String sql = "SELECT index_id, name, valueMin, valueMax FROM indexer WHERE index_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, indexId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Indexer indexer = new Indexer();
                    indexer.setIndexId(rs.getInt("index_id"));
                    indexer.setName(rs.getString("name"));
                    indexer.setValueMin(rs.getInt("valueMin"));
                    indexer.setValueMax(rs.getInt("valueMax"));
                    return indexer;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Insert new indexer
     * @param indexer Indexer to insert
     * @return Generated ID or -1 if failed
     */
    public int insert(Indexer indexer) {
        String sql = "INSERT INTO indexer (name, valueMin, valueMax) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, indexer.getName());
            stmt.setInt(2, indexer.getValueMin());
            stmt.setInt(3, indexer.getValueMax());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Update existing indexer
     * @param indexer Indexer to update
     * @return true if successful
     */
    public boolean update(Indexer indexer) {
        String sql = "UPDATE indexer SET name = ?, valueMin = ?, valueMax = ? WHERE index_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, indexer.getName());
            stmt.setInt(2, indexer.getValueMin());
            stmt.setInt(3, indexer.getValueMax());
            stmt.setInt(4, indexer.getIndexId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Delete indexer by ID
     * @param indexId ID of indexer to delete
     * @return true if successful
     */
    public boolean delete(int indexId) {
        String sql = "DELETE FROM indexer WHERE index_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, indexId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

