package com.example.wcdf.util;

import com.example.wcdf.model.Indexer;
import com.example.wcdf.model.Player;
import com.example.wcdf.model.PlayerIndex;

import java.util.ArrayList;
import java.util.List;

public class ValidationUtil {

    public static List<String> validatePlayer(Player player) {
        List<String> errors = new ArrayList<>();

        if (player.getName() == null || player.getName().trim().isEmpty()) {
            errors.add("Player name is required");
        } else if (player.getName().length() > 100) {
            errors.add("Player name must not exceed 100 characters");
        }

        if (player.getFullName() == null || player.getFullName().trim().isEmpty()) {
            errors.add("Full name is required");
        } else if (player.getFullName().length() > 200) {
            errors.add("Full name must not exceed 200 characters");
        }

        if (player.getAge() <= 0) {
            errors.add("Age must be a positive number");
        } else if (player.getAge() > 150) {
            errors.add("Age must be realistic (1-150)");
        }

        return errors;
    }

    public static List<String> validatePlayerIndex(PlayerIndex playerIndex, Indexer indexer) {
        List<String> errors = new ArrayList<>();

        if (playerIndex.getPlayerId() <= 0) {
            errors.add("Player must be selected");
        }

        if (playerIndex.getIndexId() <= 0) {
            errors.add("Index must be selected");
        }

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

    public static List<String> validateIndexer(Indexer indexer) {
        List<String> errors = new ArrayList<>();

        if (indexer.getName() == null || indexer.getName().trim().isEmpty()) {
            errors.add("Indexer name is required");
        } else if (indexer.getName().length() > 100) {
            errors.add("Indexer name must not exceed 100 characters");
        }

        if (indexer.getValueMin() < 0) {
            errors.add("Minimum value cannot be negative");
        }

        if (indexer.getValueMax() < indexer.getValueMin()) {
            errors.add("Maximum value must be greater than or equal to minimum value");
        }

        return errors;
    }

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

    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
