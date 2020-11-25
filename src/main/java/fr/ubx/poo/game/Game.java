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

    private final World world;
    private final Player player;
    private final String worldPath;
    private int initPlayerLives;
    private String prefix;
    private int levels;
    private final List<Monster> monsterList;

    public Game(String worldPath) {
        loadConfig(worldPath);
        //WorldEntity[][] thisworld = WorldBuilder.generateWorld(worldPath+"/"+prefix+"1.txt");
        monsterList = new ArrayList<>();
        world = new World(worldPath, prefix, levels);
        /*world = new World(WorldBuilder.generateWorld(worldPath+"/"+prefix+"2.txt"));
        world = new World(WorldBuilder.generateWorld(worldPath+"/"+prefix+"3.txt"));*/
        this.worldPath = worldPath;

        Position positionPlayer = null;
        Position positionPrincess = null;
        try {
            positionPlayer = world.findPlayer();
            player = new Player(this, positionPlayer);

        } catch (PositionNotFoundException e) {
            System.err.println("Position not found : " + e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
        loadMonsters();
     }

    public int getInitPlayerLives() {
        return initPlayerLives;
    }

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
        return world;
    }

    public Player getPlayer() {
        return this.player;
    }

    private void loadMonsters() {
        for (int i = 0; i < world.findMonsters().size(); i++) {
            monsterList.add(new Monster(this, world.findMonsters().get(i)));
        }
    }

    public List<Monster> getMonsterList() {
        return this.monsterList;
    }


}
