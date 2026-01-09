package com.example.wcdf.model;

/**
 * Entity class representing a Player Index value
 * Maps to the 'player_index' table in database
 * Links players to their index evaluation values
 */
public class PlayerIndex {
    private int id;
    private int playerId;
    private int indexId;
    private int value;

    // For display purposes - joined data
    private String playerName;
    private String indexerName;
    private int valueMin;
    private int valueMax;

    // contructor
    public PlayerIndex() {
    }

    // Parameter contructor
    public PlayerIndex(int id, int playerId, int indexId, int value) {
        this.id = id;
        this.playerId = playerId;
        this.indexId = indexId;
        this.value = value;
    }

    // Getters va` Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getIndexId() {
        return indexId;
    }

    public void setIndexId(int indexId) {
        this.indexId = indexId;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getIndexerName() {
        return indexerName;
    }

    public void setIndexerName(String indexerName) {
        this.indexerName = indexerName;
    }

    public int getValueMin() {
        return valueMin;
    }

    public void setValueMin(int valueMin) {
        this.valueMin = valueMin;
    }

    public int getValueMax() {
        return valueMax;
    }

    public void setValueMax(int valueMax) {
        this.valueMax = valueMax;
    }

    @Override
    public String toString() {
        return "PlayerIndex{" +
                "id=" + id +
                ", playerId=" + playerId +
                ", indexId=" + indexId +
                ", value=" + value +
                '}';
    }
}

