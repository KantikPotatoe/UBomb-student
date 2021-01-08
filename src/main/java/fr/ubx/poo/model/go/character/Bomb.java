package fr.ubx.poo.model.go.character;

import fr.ubx.poo.game.Direction;
import fr.ubx.poo.game.Game;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.game.World;
import fr.ubx.poo.model.go.GameObject;
import fr.ubx.poo.view.image.ImageResource;
import fr.ubx.poo.model.bonus.*;
import fr.ubx.poo.model.decor.*;
import fr.ubx.poo.view.sprite.SpriteFactory;

import java.util.ArrayList;
import java.util.List;

public class Bomb extends GameObject {
    private int lifetime;
    private final int range;

    /**
     *
     * @param game Game
     * @param position Position de la bombe dans l'espace
     * @param range Sa portée : si un joueur augmente ou diminue sa portée quand une bombe est posée, alors cela ne va
     * pas influencer cette dernière.
     */
    public Bomb(Game game, Position position, int range) {
        super(game, position);
        this.range = range;
        lifetime = 4;
    }

    /**
     * En fonction de la durée de vie de la bombe actuelle, l'image renvoyée sera différente
     * @return L'imageResource correspondante
     */
    public ImageResource getImageBomb() {
        return switch (lifetime) {
            case 4 -> ImageResource.BOMB4;
            case 3 -> ImageResource.BOMB3;
            case 2 -> ImageResource.BOMB2;
            case 1 -> ImageResource.BOMB1;
            default -> ImageResource.EXPLOSION;
        };
    }

    /**
     *
     * @return La durée de vie de la bombe
     */
    public int getLifetime() {
        return this.lifetime;
    }

    /**
     * Fonction utilisée pour réduire la durée de vie de la bombe.
     */
    public void dropTime() {
        this.lifetime--;
    }

    /**
     * Va détruire les décors, monstres et joueur se trouvant sur les côtés cardinaux de la bombe, dans toute sa portée
     * (sauf si il rencontre un obstacle au passage)
     * @param w Récupère le monde numéro w, pour que les explosions puissent influencer le monde même si le joueur ne se
     * trouve pas dans le même monde que la bombe
     */
    public void destroySides(int w){

        World world = game.worldNumber(w);
        for (Direction d : Direction.values()) {
            for (int i = 1; i <= this.range; i++) {
                Position nextPos = d.nextPosition(this.getPosition(), i);
                if (world.getDecorAtPosition(nextPos) instanceof Box ||
                        (world.getDecorAtPosition(nextPos) instanceof Pickable && !(world.getDecorAtPosition(nextPos) instanceof Key))) {
                    world.clearPosition(nextPos);
                    break;
                }
                if (game.getPlayer().getPosition().equals(nextPos)) {
                    game.getPlayer().decreaseLife();
                    break;
                }
                if (game.getMonsterList().stream().anyMatch
                        (monster -> monster.getPosition().equals(nextPos))) {
                    game.getMonsterList().removeIf(monster -> monster.getPosition().equals(nextPos));
                    world.askChange();
                    break;
                }
                if (!world.isEmpty(nextPos)) {
                    break;
                }
            }
        }
    }

    /**
     *
     * @return Une liste de bombes servant juste à afficher les explosions quand une bombe explose.
     * Elles seront déclenchées automatiquement.
     */
    public List<Bomb> createExplosions(){
        List<Bomb> bombs = new ArrayList<>();
        World world = game.getWorld();
        if(this.getLifetime() == 0) {
            for (Direction d : Direction.values()) {
                for (int i = 1; i <= this.getRange(); i++) {
                    Position nextPos = d.nextPosition(this.getPosition(), i);
                    Position previousPos = d.nextPosition(this.getPosition(), i-1);

                    if ((world.getDecorAtPosition(previousPos) instanceof Box ||
                            (world.getDecorAtPosition(previousPos) instanceof Pickable &&
                                    !(world.getDecorAtPosition(previousPos) instanceof Key)))
                            || (!world.isEmpty(previousPos))){
                        break;
                    } else {
                        //Sert pour créer l'explosion (la range est à 0 donc il s'agit d'une explosion sur une seule case)
                        bombs.add( new Bomb(game, nextPos, 0 ));
                    }
                }
            }
        }
        return bombs;
    }

    /**
     *
     * @return Portée de la bombe actuelle.
     */
    public int getRange() {
        return range;
    }
}
