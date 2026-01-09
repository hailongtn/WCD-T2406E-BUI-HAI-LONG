package com.example.wcdf.model;

public class Player {
    private int playerId;
    private String name;
    private String fullName;
    private int age;
    private Integer indexId;
    private String indexerName;
    private Integer indexValue;

    public Player() {
    }

    public Player(int playerId, String name, String fullName, int age, Integer indexId) {
        this.playerId = playerId;
        this.name = name;
        this.fullName = fullName;
        this.age = age;
        this.indexId = indexId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Integer getIndexId() {
        return indexId;
    }

    public void setIndexId(Integer indexId) {
        this.indexId = indexId;
    }

    public String getIndexerName() {
        return indexerName;
    }

    public void setIndexerName(String indexerName) {
        this.indexerName = indexerName;
    }

    public Integer getIndexValue() {
        return indexValue;
    }

    public void setIndexValue(Integer indexValue) {
        this.indexValue = indexValue;
    }

    @Override
    public String toString() {
        return "Player{" +
                "playerId=" + playerId +
                ", name='" + name + '\'' +
                ", fullName='" + fullName + '\'' +
                ", age=" + age +
                ", indexId=" + indexId +
                '}';
    }
}
