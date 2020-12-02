/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.game;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import fr.ubx.poo.model.go.character.Monster;
import fr.ubx.poo.model.go.character.Player;
import fr.ubx.poo.model.go.character.Princess;

public class Game {

    private final World[] world;
    private final Player player;
    private int initPlayerLives;
    private String prefix;
    private int levels;
    private final List<Monster> monsterList;
    private int actualLevel;
    private boolean newWorld;

    public Game(String worldPath) {
        loadConfig(worldPath);
        monsterList = new ArrayList<>();
        world = new World[this.levels];
        this.actualLevel = 0;
        for (int i = actualLevel; i < levels; i++) {
            WorldEntity[][] raw = WorldBuilder.generateWorld(worldPath + "/" + prefix + (i + 1) + ".txt");
            world[i] = new World(raw);
        }
        newWorld = false;

        Position positionPrincess = null;

        Position positionPlayer = findPlayer();
        player = new Player(this, positionPlayer);

        loadMonsters();
     }

    public int getInitPlayerLives() { return initPlayerLives; }

    private void loadConfig(String path) {

        try (InputStream input = new FileInputStream(new File(path, "config.properties"))) {
            Properties prop = new Properties();
            // load the configuration file
            prop.load(input);
            initPlayerLives = Integer.parseInt(prop.getProperty("lives", "3"));
            levels = Integer.parseInt(prop.getProperty("levels","3"));
            prefix = prop.getProperty("prefix", "level");
        } catch (IOException ex) {
            System.err.println("Error loading configuration");
        }
    }

    public World getWorld() {
        return world[actualLevel];
    }

    public Player getPlayer() {
        return player;
    }

    public int getActualLevel() {
        return actualLevel;
    }

    private void loadMonsters() {
        monsterList.clear();
        for (int i = 0; i < getWorld().findMonsters().size(); i++) {
            monsterList.add(new Monster(this, getWorld().findMonsters().get(i)));
        }
    }


    public List<Monster> getMonsterList() {
        return this.monsterList;
    }

    public void changeLevel(boolean up) {
        actualLevel += up ? 1 : -1;
        loadMonsters();
        player.changeWorld();
        this.askNewWorld();

    }

    public Position findPlayer() {
        return getWorld().findPlayer().orElseThrow();
    }

    public void askNewWorld() { this.newWorld = true;}

    public void finishNewWorld() {
        this.newWorld = false;
    }

    public boolean isNewWorld(){
        return this.newWorld;
    }

    public int getLevels() {
        return levels;
    }

    public World worldNumber (int n){
        return world[n];
    }
}
