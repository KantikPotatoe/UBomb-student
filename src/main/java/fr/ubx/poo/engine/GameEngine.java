/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.engine;

import fr.ubx.poo.game.Direction;
import fr.ubx.poo.game.Game;

import fr.ubx.poo.model.go.character.Bomb;
import fr.ubx.poo.model.go.character.Monster;
import fr.ubx.poo.model.go.character.Player;
import fr.ubx.poo.view.sprite.Sprite;
import fr.ubx.poo.view.sprite.SpriteFactory;
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


/**
 * Game Engine class, manage the engine side.
 */
public final class GameEngine {

    private static AnimationTimer gameLoop;
    private final String windowTitle;
    private final Game game;
    private final Player player;
    private final List<Sprite> sprites = new ArrayList<>();
    private final List<Sprite> monsterSprites = new ArrayList<>();
    private final List<Sprite> bombSprites = new ArrayList<>();
    private final Map<Integer, List<Bomb>> bombs = new HashMap<>();
    private StatusBar statusBar;
    private Pane layer;
    private Input input;
    private Stage stage;
    private Sprite spritePlayer;
    private Sprite spritePrincess;
    private long tick;
    private int j;

    /**
     * Instantiates a new Game engine.
     *
     * @param windowTitle the window title
     * @param game        the game
     * @param stage       the stage
     */

    public GameEngine(final String windowTitle, Game game, final Stage stage) {
        this.windowTitle = windowTitle;
        this.game = game;
        this.player = game.getPlayer();
        for (int cpt = 0; cpt < game.getLevels(); cpt++) {
            bombs.put(cpt, new ArrayList<>());
        }
        initialize(stage, game);

        tick = 0;
        j = 0;

        buildAndSetGameLoop();
    }

    /**
     * JavaFX initialize function.
     *
     * @param stage the stage
     * @param game  the game
     */
    private void initialize(Stage stage, Game game) {
        this.stage = stage;
        Group root = new Group();
        layer = new Pane();

        int height = game.getWorldHeight();
        int width = game.getWorldWidth();
        int sceneWidth = width * Sprite.SIZE;
        int sceneHeight = height * Sprite.SIZE;

        Scene scene = new Scene(root, sceneWidth, (double) sceneHeight + StatusBar.HEIGHT);
        scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());

        stage.setTitle(windowTitle);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        input = new Input(scene);

        root.getChildren().add(layer);

        statusBar = new StatusBar(root, sceneWidth, sceneHeight);

        // Create decor sprites
        game.getWorld().forEach((pos, d) -> sprites.add(SpriteFactory.createDecor(layer, pos, d)));
        spritePlayer = SpriteFactory.createPlayer(layer, player);
        if (isPrincessInWorld(game)) {
            spritePrincess = SpriteFactory.createDecor(layer,
                    game.getWorld().findPrincessPosition().get(),
                    game.getWorld().getPrincess().get());
        }
        game.getMonsterList().stream().map(monster -> SpriteFactory.createMonster(layer, monster)).forEach(monsterSprites::add);

    }

    /**
     * Build and set game loop.
     */
    protected final synchronized void buildAndSetGameLoop() {
        gameLoop = new AnimationTimer() {
            public void handle(long now) {
                // Check keyboard actions
                processInput(now);

                // Do actions every 60 ticks
                tick++;
                if (tick % 60 == 0) {
                    j++;

                    bombActionManager();


                    game.getMonsterList().forEach(Monster::updatePosition);
                }
                update(now);

                // Graphic update
                render();
                statusBar.update(game);
            }
        };
    }

    /**
     * Fonction utilisée pour gérer les bombes à chaque seconde : la durée de vie des bombes va diminuer et
     * la destruction sera lancée. Puis, après l'explosion, les bombes seront envoyées dans l'inventaire du joueur.
     */
    private void bombActionManager() {
        bombs.forEach((w, bomb) -> bomb.forEach(Bomb::dropTime));
        bombs.forEach((w, bombList) -> bombList.stream().filter(bomb -> bomb.getLifetime() < 0).forEach(bomb -> {
            player.changeNumberOfBombs(1);
            bomb.destroySides(w);
        }));
        bombs.replaceAll((w, bombList) -> bombList = bombList.stream()
                .filter(bomb -> bomb.getLifetime() >= 0)
                .collect(Collectors.toList()));
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
        if (input.isBomb() && playerHaveBomb()) {
            setBomb();
        }
        if (input.isKey()) {
            player.openDoor();
        }
        input.clear();
    }

    /**
     * Fonction permettant de poser une bombe, déclenchée si la touche est bien tapée et que le joueur a une bombe.
     */
    private void setBomb() {
        Bomb bomb = new Bomb(game, player.getPosition(), player.getSizeBombs());
        this.bombSprites.add(SpriteFactory.createBomb(layer, bomb));
        this.bombs.get(game.getCurrentLevel()).add(bomb);
        player.changeNumberOfBombs(-1);
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
        if (!player.isAlive()) {
            gameLoop.stop();
            showMessage("GAME OVER!", Color.RED);
        }
        if (player.isWinner()) {
            gameLoop.stop();
            showMessage("CONGRATULATIONS!", Color.BLUE);
        }
    }

    private void render() {

        if (game.isNewWorld()) {
            this.player.setPosition(game.findPlayer());
            renderNewWorld();
        } else if (game.getWorld().worldHasChanged()) {
            renderWorldWhenChanged();
        }
        renderGameEntities();
        renderBombs();
    }

    /**
     * Start.
     */
    public void start() {
        gameLoop.start();
    }

    /**
     * Permet le rendu des bombes et des explosions avec les sprites
     */
    private void renderBombs() {
        clearSprites(bombSprites);
        bombs.get(game.getCurrentLevel()).forEach(bomb -> {
            bombSprites.add(SpriteFactory.createBomb(layer, bomb));
            bomb.createExplosions().forEach(b -> bombSprites.
                    add(SpriteFactory.createExplosion(layer, b)));
        });
        bombSprites.forEach(Sprite::render);
    }

    /**
     * Fonction utilisée pour mettre à jour l'aperçu global de toutes les entités en jeu.
     */
    private void renderGameEntities() {
        sprites.forEach(Sprite::render);
        spritePlayer.render(); // Rendering last to have the player on the foreground.
        if (spritePrincess != null) {
            spritePrincess.render();
        }
        monsterSprites.forEach(Sprite::render);

    }

    /**
     * Quand le monde connaît un changement dans les décors ou dans les monstres (déplacement ou destruction),
     * fonction utilisée pour mettre à jour l'aperçu global
     */
    private void renderWorldWhenChanged() {
        clearSprites(sprites);
        game.getWorld().forEach((pos, d) -> sprites.add(SpriteFactory.createDecor(layer, pos, d)));
        game.getWorld().finishChange();
        clearSprites(monsterSprites);
        game.getMonsterList().stream().map(monster -> SpriteFactory.createMonster(layer, monster)).forEach(monsterSprites::add);
    }

    /**
     * Fonction permettant de générer l'aperçu du monde quand il est changé.
     */
    private void renderNewWorld() {
        game.finishNewWorld();
        clearSprites(monsterSprites);
        clearSprites(sprites);
        spritePlayer.remove();
        initialize(stage, game);
    }

    /**
     * Fonction enlevant le rendu des sprites puis vidant le contenu de la liste passée en paramètre.
     * @param sprites Liste que l'on veut vider
     */
    private void clearSprites(List<Sprite> sprites) {
        sprites.forEach(Sprite::remove);
        sprites.clear();
    }

    /**
     *
     * @param game La partie
     * @return Si la princesse est présente dans le monde actuel
     */
    private boolean isPrincessInWorld(Game game) {
        return game.getWorld().getPrincess().isPresent() && game.getWorld().findPrincessPosition().isPresent();
    }

    /**
     *
     * @return Vrai si le joueur possède au moins une bombe.
     */
    private boolean playerHaveBomb() {
        return player.getBombsNumber() > 0;
    }

}
