-- Database: player_evaluation
-- Create database and tables for Player Evaluation System

CREATE DATABASE IF NOT EXISTS player_evaluation;
USE player_evaluation;

-- Drop tables if exist (for clean setup)
DROP TABLE IF EXISTS player_index;
DROP TABLE IF EXISTS player;
DROP TABLE IF EXISTS indexer;

-- Indexer table: defines evaluation criteria with min/max values
CREATE TABLE indexer (
    index_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    valueMin INT NOT NULL DEFAULT 0,
    valueMax INT NOT NULL DEFAULT 100
);

-- Player table: main player entity
CREATE TABLE player (
    player_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    full_name VARCHAR(200) NOT NULL,
    age INT NOT NULL,
    index_id INT,
    FOREIGN KEY (index_id) REFERENCES indexer(index_id) ON DELETE SET NULL
);

-- Player_index table: stores player's index values
CREATE TABLE player_index (
    id INT PRIMARY KEY AUTO_INCREMENT,
    player_id INT NOT NULL,
    index_id INT NOT NULL,
    value INT NOT NULL,
    FOREIGN KEY (player_id) REFERENCES player(player_id) ON DELETE CASCADE,
    FOREIGN KEY (index_id) REFERENCES indexer(index_id) ON DELETE CASCADE,
    UNIQUE KEY unique_player_index (player_id, index_id)
);

-- Insert sample indexer data
INSERT INTO indexer (name, valueMin, valueMax) VALUES
('Speed', 0, 100),
('Strength', 0, 100),
('Accuracy', 0, 100),
('Stamina', 0, 100),
('Skill', 1, 10);

-- Insert sample player data
INSERT INTO player (name, full_name, age, index_id) VALUES
('Ronaldo', 'Cristiano Ronaldo', 39, 1),
('Messi', 'Lionel Messi', 37, 2),
('Mbappe', 'Kylian Mbappe', 25, 1);

-- Insert sample player_index data
INSERT INTO player_index (player_id, index_id, value) VALUES
(1, 1, 95),
(1, 2, 85),
(2, 1, 88),
(2, 3, 98),
(3, 1, 99);

