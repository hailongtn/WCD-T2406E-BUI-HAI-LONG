package com.example.wcdf.util;

import com.example.wcdf.model.Indexer;
import com.example.wcdf.model.Player;
import com.example.wcdf.model.PlayerIndex;

import java.util.ArrayList;
import java.util.List;

/**
 * Server-side Validation Utility
 * Validates data before insert and update operations
 */
public class ValidationUtil {

    /**
     * Validate Player data
     * @param player Player object to validate
     * @return List of error messages (empty if valid)
     */
    public static List<String> validatePlayer(Player player) {
        List<String> errors = new ArrayList<>();

        // Validate name - required
        if (player.getName() == null || player.getName().trim().isEmpty()) {
            errors.add("Player name is required");
        } else if (player.getName().length() > 100) {
            errors.add("Player name must not exceed 100 characters");
        }

        // Validate full name - required
        if (player.getFullName() == null || player.getFullName().trim().isEmpty()) {
            errors.add("Full name is required");
        } else if (player.getFullName().length() > 200) {
            errors.add("Full name must not exceed 200 characters");
        }

        // Validate age - must be positive number
        if (player.getAge() <= 0) {
            errors.add("Age must be a positive number");
        } else if (player.getAge() > 150) {
            errors.add("Age must be realistic (1-150)");
        }

        return errors;
    }

    /**
     * Validate PlayerIndex data
     * @param playerIndex PlayerIndex object to validate
     * @param indexer Associated Indexer for range validation
     * @return List of error messages (empty if valid)
     */
    public static List<String> validatePlayerIndex(PlayerIndex playerIndex, Indexer indexer) {
        List<String> errors = new ArrayList<>();

        // Validate player_id - required
        if (playerIndex.getPlayerId() <= 0) {
            errors.add("Player must be selected");
        }

        // Validate index_id - required
        if (playerIndex.getIndexId() <= 0) {
            errors.add("Index must be selected");
        }

        // Validate value against indexer min/max range
        if (indexer != null) {
            int value = playerIndex.getValue();
            int min = indexer.getValueMin();
            int max = indexer.getValueMax();

            if (value < min || value > max) {
                errors.add("Index value must be between " + min + " and " + max +
                          " for " + indexer.getName());
            }
        } else {
            errors.add("Invalid indexer reference");
        }

        return errors;
    }

    /**
     * Validate Indexer data
     * @param indexer Indexer object to validate
     * @return List of error messages (empty if valid)
     */
    public static List<String> validateIndexer(Indexer indexer) {
        List<String> errors = new ArrayList<>();

        // Validate name - required
        if (indexer.getName() == null || indexer.getName().trim().isEmpty()) {
            errors.add("Indexer name is required");
        } else if (indexer.getName().length() > 100) {
            errors.add("Indexer name must not exceed 100 characters");
        }

        // Validate min/max range
        if (indexer.getValueMin() < 0) {
            errors.add("Minimum value cannot be negative");
        }

        if (indexer.getValueMax() < indexer.getValueMin()) {
            errors.add("Maximum value must be greater than or equal to minimum value");
        }

        return errors;
    }

    /**
     * Parse integer safely
     * @param value String value to parse
     * @param defaultValue Default value if parsing fails
     * @return Parsed integer or default value
     */
    public static int parseIntSafe(String value, int defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Check if string is a valid integer
     * @param value String to check
     * @return true if valid integer
     */
    public static boolean isValidInteger(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(value.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Check if string is null or empty
     * @param value String to check
     * @return true if null or empty
     */
    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}

