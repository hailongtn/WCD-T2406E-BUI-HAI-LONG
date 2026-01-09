package com.example.wcdf.model;

/**
 * Entity class representing an Indexer (evaluation criteria)
 * Maps to the 'indexer' table in database
 */
public class Indexer {
    private int indexId;
    private String name;
    private int valueMin;
    private int valueMax;

    // Default constructor
    public Indexer() {
    }

    // Parameterized constructor
    public Indexer(int indexId, String name, int valueMin, int valueMax) {
        this.indexId = indexId;
        this.name = name;
        this.valueMin = valueMin;
        this.valueMax = valueMax;
    }

    // Getters and Setters
    public int getIndexId() {
        return indexId;
    }

    public void setIndexId(int indexId) {
        this.indexId = indexId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        return "Indexer{" +
                "indexId=" + indexId +
                ", name='" + name + '\'' +
                ", valueMin=" + valueMin +
                ", valueMax=" + valueMax +
                '}';
    }
}

