package com.example.wcdf.dao;

import com.example.wcdf.model.Player;
import com.example.wcdf.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Player entity
 * Handles all CRUD operations for the player table
 */
public class PlayerDAO {

    /**
     * Get all players with indexer name and value (LEFT JOIN)
     * @return List of all Player objects
     */
    public List<Player> findAll() {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT p.player_id, p.name, p.full_name, p.age, p.index_id, " +
                     "i.name AS indexer_name, pi.value AS index_value " +
                     "FROM player p " +
                     "LEFT JOIN indexer i ON p.index_id = i.index_id " +
                     "LEFT JOIN player_index pi ON p.player_id = pi.player_id AND p.index_id = pi.index_id " +
                     "ORDER BY p.player_id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Player player = mapResultSetToPlayer(rs);
                players.add(player);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return players;
    }

    /**
     * Find player by ID
     * @param playerId Player ID to find
     * @return Player object or null if not found
     */
    public Player findById(int playerId) {
        String sql = "SELECT p.player_id, p.name, p.full_name, p.age, p.index_id, " +
                     "i.name AS indexer_name, pi.value AS index_value " +
                     "FROM player p " +
                     "LEFT JOIN indexer i ON p.index_id = i.index_id " +
                     "LEFT JOIN player_index pi ON p.player_id = pi.player_id AND p.index_id = pi.index_id " +
                     "WHERE p.player_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, playerId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPlayer(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Search players by name
     * @param searchName Name pattern to search
     * @return List of matching players
     */
    public List<Player> findByName(String searchName) {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT p.player_id, p.name, p.full_name, p.age, p.index_id, " +
                     "i.name AS indexer_name, pi.value AS index_value " +
                     "FROM player p " +
                     "LEFT JOIN indexer i ON p.index_id = i.index_id " +
                     "LEFT JOIN player_index pi ON p.player_id = pi.player_id AND p.index_id = pi.index_id " +
                     "WHERE p.name LIKE ? OR p.full_name LIKE ? " +
                     "ORDER BY p.name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + searchName + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    players.add(mapResultSetToPlayer(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return players;
    }

    /**
     * Insert new player
     * @param player Player to insert
     * @return Generated ID or -1 if failed
     */
    public int insert(Player player) {
        String sql = "INSERT INTO player (name, full_name, age, index_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, player.getName());
            stmt.setString(2, player.getFullName());
            stmt.setInt(3, player.getAge());

            if (player.getIndexId() != null) {
                stmt.setInt(4, player.getIndexId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }

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
     * Update existing player
     * @param player Player to update
     * @return true if successful
     */
    public boolean update(Player player) {
        String sql = "UPDATE player SET name = ?, full_name = ?, age = ?, index_id = ? " +
                     "WHERE player_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, player.getName());
            stmt.setString(2, player.getFullName());
            stmt.setInt(3, player.getAge());

            if (player.getIndexId() != null) {
                stmt.setInt(4, player.getIndexId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }

            stmt.setInt(5, player.getPlayerId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Delete player by ID
     * @param playerId ID of player to delete
     * @return true if successful
     */
    public boolean delete(int playerId) {
        String sql = "DELETE FROM player WHERE player_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, playerId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Map ResultSet row to Player object
     * @param rs ResultSet to map
     * @return Player object
     * @throws SQLException if mapping fails
     */
    private Player mapResultSetToPlayer(ResultSet rs) throws SQLException {
        Player player = new Player();
        player.setPlayerId(rs.getInt("player_id"));
        player.setName(rs.getString("name"));
        player.setFullName(rs.getString("full_name"));
        player.setAge(rs.getInt("age"));

        int indexId = rs.getInt("index_id");
        if (!rs.wasNull()) {
            player.setIndexId(indexId);
        }

        player.setIndexerName(rs.getString("indexer_name"));

        // Map index value if exists
        try {
            int indexValue = rs.getInt("index_value");
            if (!rs.wasNull()) {
                player.setIndexValue(indexValue);
            }
        } catch (SQLException e) {
            // Column might not exist in some queries
        }

        return player;
    }
}

