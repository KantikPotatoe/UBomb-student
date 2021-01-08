/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.game;


import fr.ubx.poo.model.go.character.Monster;
import fr.ubx.poo.model.go.character.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.IntStream;

public class Game {

    private final World[] world;
    private final Player player;
    private int initPlayerLives;
    private String prefix;
    private int levels;
    private final Map<Integer, List<Monster>> monsterList;
    private int currentLevel;
    private boolean newWorld;

    public Game(String worldPath) {
        loadConfig(worldPath);
        monsterList = new HashMap<>();
        world = new World[this.levels];
        this.currentLevel = 0;
        //Génère le monde pour chaque niveau, les niveaux sont récupérés dans le fichier de configuration.
        IntStream.range(currentLevel, levels).forEach(i -> {
            WorldEntity[][] raw = WorldBuilder.generateWorld(worldPath + "/" + prefix + (i + 1) + ".txt");
            world[i] = new World(raw);
            monsterList.put(i, new ArrayList<>());
        });
        newWorld = false;

        Position positionPlayer = findPlayer();
        player = new Player(this, positionPlayer);
        loadMonsters();

    }

    /**
     * Récupère les données du fichier de configuration passé en paramètre et met à jour toutes les informations importantes :
     * Le nombre de vie du joueur au départ (par défaut 3)
     * Le nombre de niveau disponible (par défaut 3)
     * Les préfix pour les fichiers de niveau.
     * @param path Chemin vers le fichier de configuration
     */
    private void loadConfig(String path) {

        try (InputStream input = new FileInputStream(new File(path, "config.properties"))) {
            Properties prop = new Properties();
            // load the configuration file
            prop.load(input);
            initPlayerLives = Integer.parseInt(prop.getProperty("lives", "3"));
            levels = Integer.parseInt(prop.getProperty("levels", "3"));
            prefix = prop.getProperty("prefix", "level");
        } catch (IOException ex) {
            System.err.println("Error loading configuration");
        }
    }

    /**
     *
     * @return Le monde du niveau actuel, affiché à l'écran
     */
    public World getWorld() {
        return world[currentLevel];
    }

    /**
     *
     * @return Le joueur et ses données
     */
    public Player getPlayer() {
        return player;
    }

    /**
     *
     * @return Récupère le niveau actuel ou se trouve le joueur (et qui est donc affiché à l'écran)
     */
    public int getCurrentLevel() {
        return currentLevel;
    }

    /**
     * Génère les monstres dans le monde actuel.
     */
    private void loadMonsters() {
        monsterList.get(getCurrentLevel()).clear();
        getWorld().initMonstersPositions().stream().map(position -> new Monster(this, position))
                .forEach(monsterList.get(getCurrentLevel())::add);
    }


    /**
     * Récupère la liste des monstres dans le monde actuel
     * @return Liste de monstres
     */
    public List<Monster> getMonsterList() {
        return this.monsterList.get(this.getCurrentLevel());
    }

    /**
     * Change le niveau actuel du monde en fonction de la porte qui a été traversée.
     * @param up On augmente de niveau ou non.
     */
    public void changeLevel(boolean up) {
        currentLevel += up ? 1 : -1;
        if (getMonsterList().isEmpty()){
            loadMonsters();
        }
        player.changeWorld();
        this.newWorld = true;


    }

    /**
     *
     * @return La position du joueur dans le monde.
     */
    public Position findPlayer() {
        return getWorld().findPlayerPosition().orElseThrow();
    }

    /**
     * Le nouveau monde a fini de chargé
     */
    public void finishNewWorld() {
        this.newWorld = false;
    }

    /**
     *
     * @return Si le monde est un nouveau monde ou non
     */
    public boolean isNewWorld() {
        return this.newWorld;
    }

    /**
     *
     * @return Le nombre de niveau de la partie
     */
    public int getLevels() {
        return levels;
    }

    /**
     *
     * @param n numéro du monde souhaité
     * @return Renvoie le monde numéro n du tableau
     */
    public World worldNumber(int n) {
        return world[n];
    }

    /**
     *
     * @return Hauteur de la dimension du monde actuel
     */
    public int getWorldHeight() {
        return getWorld().getDimension().height;
    }

    /**
     *
      * @return Largeur de la dimension du monde actuel
     */
    public int getWorldWidth() {
        return getWorld().getDimension().width;
    }


    public int getInitPlayerLives() {
        return initPlayerLives;
    }

    /**
     *
     * @param position Position dans l'espace
     * @return Vrai si un monstre est présent dans cette position
     */
    public boolean containsMonster(Position position){
        return getMonsterList().stream().anyMatch(monster -> monster.getPosition().equals(position));
    }

    /**
     *
     * @param position Position de l'entité dont on veut connaître la distance par rapport au joueur
     * @return Distance par rapport au joueur
     */
    public double distancePlayer(Position position){
        return player.getPosition().distance(position);
    }

}
