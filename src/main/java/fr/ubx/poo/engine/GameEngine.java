/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.engine;

import fr.ubx.poo.game.Direction;
import fr.ubx.poo.model.go.character.Bomb;
import fr.ubx.poo.model.go.character.Monster;
import fr.ubx.poo.model.go.character.Princess;
import fr.ubx.poo.view.sprite.Sprite;
import fr.ubx.poo.view.sprite.SpriteFactory;
import fr.ubx.poo.game.Game;
import fr.ubx.poo.model.go.character.Player;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public final class GameEngine {

    private static AnimationTimer gameLoop;
    private final String windowTitle;
    private final Game game;
    private final Player player;
    //private final Princess princess;
    private final List<Sprite> sprites = new ArrayList<>();
    private final List<Sprite> monsterSprites = new ArrayList<>();
    private StatusBar statusBar;
    private Pane layer;
    private Input input;
    private Stage stage;
    private Sprite spritePlayer;
    private Sprite spritePrincess;
    private final List<Sprite> bombSprites = new ArrayList<>();
    private final Map<Integer,List<Bomb>> bombs = new HashMap<>();
    private long i;
    private int j;
    public GameEngine(final String windowTitle, Game game, final Stage stage) {
        this.windowTitle = windowTitle;
        this.game = game;
        this.player = game.getPlayer();
        for(int i = 0; i < game.getLevels(); i++){
            bombs.put(i, new ArrayList<>() );
        }
        initialize(stage, game);
        i= 0;
        j = 0;
        buildAndSetGameLoop();
    }

    private void initialize(Stage stage, Game game) {
        this.stage = stage;
        Group root = new Group();
        layer = new Pane();

        int height = game.getWorld().dimension.height;
        int width = game.getWorld().dimension.width;
        int sceneWidth = width * Sprite.size;
        int sceneHeight = height * Sprite.size;
        Scene scene = new Scene(root, sceneWidth, sceneHeight + StatusBar.height);
        scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());

        stage.setTitle(windowTitle);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        input = new Input(scene);
        root.getChildren().add(layer);
        statusBar = new StatusBar(root, sceneWidth, sceneHeight, game);
        // Create decor sprites
        game.getWorld().forEach((pos, d) -> sprites.add(SpriteFactory.createDecor(layer, pos, d)));
        spritePlayer = SpriteFactory.createPlayer(layer, player);
        if(game.getWorld().getPrincess().isPresent() && game.getWorld().findPrincess().isPresent()){
            spritePrincess = SpriteFactory.createDecor(layer, game.getWorld().findPrincess().get(),
                    game.getWorld().getPrincess().get());
        }
        game.getMonsterList().stream().map(monster -> SpriteFactory.createMonster(layer, monster)).forEach(monsterSprites::add);

    }

    protected final void buildAndSetGameLoop() {
        gameLoop = new AnimationTimer() {
            public void handle(long now) {
                // Check keyboard actions
                processInput(now);

                // Do actions
                i++;
                if(i%60==0){
                    j++;
                    bombs.forEach((w, bomb) -> bomb.forEach(Bomb::dropTime));
                    bombs.forEach((w,bombs) -> bombs.stream().filter(bomb -> bomb.getLifetime() < 0).forEach(bomb -> {
                        player.incDecBomb(1);
                        bomb.destroySides(w);
                    }));
                    bombs.replaceAll((w,bombs) -> bombs = bombs.stream()
                            .filter(bomb -> bomb.getLifetime() >= 0)
                            .collect(Collectors.toList()));
                    game.getMonsterList().forEach(monster -> monster.doMove(Direction.random()));
                }
                update(now);

                // Graphic update
                render();
                statusBar.update(game);
            }
        };
    }

    private void processInput(long now) {
        if (input.isExit()) {
            gameLoop.stop();
            Platform.exit();
            System.exit(0);
        }
        if (input.isMoveDown()) {
            player.requestMove(Direction.S);
        }
        if (input.isMoveLeft()) {
            player.requestMove(Direction.W);
        }
        if (input.isMoveRight()) {
            player.requestMove(Direction.E);
        }
        if (input.isMoveUp()) {
            player.requestMove(Direction.N);
        }
        if (input.isBomb() && player.getBombsNumber() > 0) {
            Bomb bomb = new Bomb(game, player.getPosition(), player.getSizeBombs());
            this.bombSprites.add(SpriteFactory.createBomb(layer,bomb));
            this.bombs.get(game.getActualLevel()).add(bomb);
            player.incDecBomb(-1);
        }
        if (input.isKey()) {
            player.openDoor();
        }
        input.clear();
    }

    private void showMessage(String msg, Color color) {
        Text waitingForKey = new Text(msg);
        waitingForKey.setTextAlignment(TextAlignment.CENTER);
        waitingForKey.setFont(new Font(60));
        waitingForKey.setFill(color);
        StackPane root = new StackPane();
        root.getChildren().add(waitingForKey);
        Scene scene = new Scene(root, 400, 200, Color.WHITE);
        stage.setTitle(windowTitle);
        stage.setScene(scene);
        input = new Input(scene);
        stage.show();
        new AnimationTimer() {
            public void handle(long now) {
                processInput(now);
            }
        }.start();
    }


    private void update(long now) {
        player.update(now);
<<<<<<< Updated upstream
=======

>>>>>>> Stashed changes
        if (!player.isAlive()) {
            gameLoop.stop();
            showMessage("Perdu!", Color.RED);
        }
        if (player.isWinner()) {
            gameLoop.stop();
            showMessage("Gagné", Color.BLUE);
        }


    }

    private void render() {
        if(game.isNewWorld()){
            this.player.setPosition(game.findPlayer());

            game.finishNewWorld();

            monsterSprites.forEach(Sprite::remove);
            monsterSprites.clear();
            sprites.forEach(Sprite::remove);
            sprites.clear();
            spritePlayer.remove();
            initialize(stage, game);

        } else
        if(game.getWorld().worldHasChanged()) {
            sprites.forEach(Sprite::remove);
            sprites.clear();
            game.getWorld().forEach((pos, d) -> sprites.add(SpriteFactory.createDecor(layer, pos, d)));
            game.getWorld().finishChange();
            monsterSprites.forEach(Sprite::remove);
            monsterSprites.clear();
            game.getMonsterList().stream().map(monster -> SpriteFactory.createMonster(layer, monster)).forEach(monsterSprites::add);
        }


        sprites.forEach(Sprite::render);
        // last rendering to have player in the foreground
        spritePlayer.render();
        if (spritePrincess != null) {
            spritePrincess.render();
        }
        monsterSprites.forEach(Sprite::render);
        bombSprites.forEach(Sprite::remove);
        bombSprites.clear();
        bombs.get(game.getActualLevel()).forEach(bomb -> bombSprites.add(SpriteFactory.createBomb(layer,bomb)));
        bombSprites.forEach(Sprite::render);
    }

    public void start() {
        gameLoop.start();
    }
}
