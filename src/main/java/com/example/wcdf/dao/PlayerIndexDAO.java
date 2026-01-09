package com.example.wcdf.dao;

import com.example.wcdf.model.PlayerIndex;
import com.example.wcdf.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for PlayerIndex entity
 * Handles all CRUD operations for the player_index table
 */
public class PlayerIndexDAO {

    /**
     * Get all player indexes with joined data
     * @return List of all PlayerIndex objects
     */
    public List<PlayerIndex> findAll() {
        List<PlayerIndex> playerIndexes = new ArrayList<>();
        String sql = "SELECT pi.id, pi.player_id, pi.index_id, pi.value, " +
                     "p.name AS player_name, i.name AS indexer_name, " +
                     "i.valueMin, i.valueMax " +
                     "FROM player_index pi " +
                     "INNER JOIN player p ON pi.player_id = p.player_id " +
                     "INNER JOIN indexer i ON pi.index_id = i.index_id " +
                     "ORDER BY p.name, i.name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                playerIndexes.add(mapResultSetToPlayerIndex(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playerIndexes;
    }

    /**
     * Find player index by ID
     * @param id PlayerIndex ID to find
     * @return PlayerIndex object or null if not found
     */
    public PlayerIndex findById(int id) {
        String sql = "SELECT pi.id, pi.player_id, pi.index_id, pi.value, " +
                     "p.name AS player_name, i.name AS indexer_name, " +
                     "i.valueMin, i.valueMax " +
                     "FROM player_index pi " +
                     "INNER JOIN player p ON pi.player_id = p.player_id " +
                     "INNER JOIN indexer i ON pi.index_id = i.index_id " +
                     "WHERE pi.id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPlayerIndex(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Find all indexes for a specific player
     * @param playerId Player ID
     * @return List of PlayerIndex for the player
     */
    public List<PlayerIndex> findByPlayerId(int playerId) {
        List<PlayerIndex> playerIndexes = new ArrayList<>();
        String sql = "SELECT pi.id, pi.player_id, pi.index_id, pi.value, " +
                     "p.name AS player_name, i.name AS indexer_name, " +
                     "i.valueMin, i.valueMax " +
                     "FROM player_index pi " +
                     "INNER JOIN player p ON pi.player_id = p.player_id " +
                     "INNER JOIN indexer i ON pi.index_id = i.index_id " +
                     "WHERE pi.player_id = ? " +
                     "ORDER BY i.name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, playerId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    playerIndexes.add(mapResultSetToPlayerIndex(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playerIndexes;
    }

    /**
     * Check if player-index combination already exists
     * @param playerId Player ID
     * @param indexId Index ID
     * @param excludeId ID to exclude (for updates)
     * @return true if combination exists
     */
    public boolean existsPlayerIndexCombination(int playerId, int indexId, int excludeId) {
        String sql = "SELECT COUNT(*) FROM player_index WHERE player_id = ? AND index_id = ? AND id != ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, playerId);
            stmt.setInt(2, indexId);
            stmt.setInt(3, excludeId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Insert new player index
     * @param playerIndex PlayerIndex to insert
     * @return Generated ID or -1 if failed
     */
    public int insert(PlayerIndex playerIndex) {
        String sql = "INSERT INTO player_index (player_id, index_id, value) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, playerIndex.getPlayerId());
            stmt.setInt(2, playerIndex.getIndexId());
            stmt.setInt(3, playerIndex.getValue());

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
     * Update existing player index
     * @param playerIndex PlayerIndex to update
     * @return true if successful
     */
    public boolean update(PlayerIndex playerIndex) {
        String sql = "UPDATE player_index SET player_id = ?, index_id = ?, value = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, playerIndex.getPlayerId());
            stmt.setInt(2, playerIndex.getIndexId());
            stmt.setInt(3, playerIndex.getValue());
            stmt.setInt(4, playerIndex.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Delete player index by ID
     * @param id ID of player index to delete
     * @return true if successful
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM player_index WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Delete all indexes for a player
     * @param playerId Player ID
     * @return Number of deleted records
     */
    public int deleteByPlayerId(int playerId) {
        String sql = "DELETE FROM player_index WHERE player_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, playerId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Map ResultSet row to PlayerIndex object
     * @param rs ResultSet to map
     * @return PlayerIndex object
     * @throws SQLException if mapping fails
     */
    private PlayerIndex mapResultSetToPlayerIndex(ResultSet rs) throws SQLException {
        PlayerIndex playerIndex = new PlayerIndex();
        playerIndex.setId(rs.getInt("id"));
        playerIndex.setPlayerId(rs.getInt("player_id"));
        playerIndex.setIndexId(rs.getInt("index_id"));
        playerIndex.setValue(rs.getInt("value"));
        playerIndex.setPlayerName(rs.getString("player_name"));
        playerIndex.setIndexerName(rs.getString("indexer_name"));
        playerIndex.setValueMin(rs.getInt("valueMin"));
        playerIndex.setValueMax(rs.getInt("valueMax"));
        return playerIndex;
    }
}

