package dk.sdu.mmmi.tower;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.data.entityparts.CollisionPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.LifePart;
import dk.sdu.mmmi.cbse.common.data.entityparts.PositionPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.SpritePart;
import dk.sdu.mmmi.cbse.common.events.Event;
import dk.sdu.mmmi.cbse.common.events.PlayerArrivedEvent;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;
import dk.sdu.mmmi.commonai.events.MapChangedDuringRoundEvent;
import dk.sdu.mmmi.commonenemy.Enemy;
import dk.sdu.mmmi.commonmap.MapSPI;
import dk.sdu.mmmi.commonmap.Tile;
import dk.sdu.mmmi.commonmap.TileSizes;
import dk.sdu.mmmi.commonplayer.Player;
import dk.sdu.mmmi.commontower.Tower;
import dk.sdu.mmmi.commontower.TowerPreview;
import dk.sdu.mmmi.commonweapon.WeaponPart;
import java.util.List;
import dk.sdu.mmmi.commontower.TowerControlSystemSPI;
import dk.sdu.mmmi.commonweapon.WeaponPart.Color;

public class TowerControlSystem implements IEntityProcessingService, TowerControlSystemSPI {

    private MapSPI map;
    private Entity preview;

    @Override
    public void process(GameData gameData, World world) {
        showTowerPlacementPreview(gameData, world);
        placeNewTowers(gameData, world);
        attackEnemies(gameData, world);
    }

    @Override
    public void placeNewTowers(GameData gameData, World world) {
        for (Event event : gameData.getEvents(PlayerArrivedEvent.class)) {
            gameData.removeEvent(event);

            // Calculate placement of new Tower
            int posX = ((PlayerArrivedEvent) event).getX();
            int posY = ((PlayerArrivedEvent) event).getY();
            Tower tower = constructNewTower(posX, posY);
            map.fitEntityToMap(tower);

            if (isLegalPlacement(tower)) {
                world.addEntity(tower);
                gameData.addEvent(new MapChangedDuringRoundEvent(tower));

                // Make player stop creating more PlayerHasArrivedEvents
                if (!world.getEntities(Player.class).isEmpty()) {
                    ((Player) world.getEntities(Player.class).get(0)).setHasTarget(false);
                }
            }
        }
    }

    @Override
    public boolean isLegalPlacement(Entity e) {
        List<Tile> tiles = map.getTilesEntityIsOn(e);

        if (tiles.size() < 4) {
            return false;
        }

        for (Tile tile : tiles) {
            Class[] entitiesToIgnore = {TowerPreview.class, Player.class};
            if (map.checkIfTileIsOccupied(tile, entitiesToIgnore) || !tile.isWalkable()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Entity calculateClosestEnemy(World world, Entity tower) {
        float currentMinDistance = Float.MAX_VALUE;
        Entity closestEnemy = null;

        for (Entity enemy : world.getEntities(Enemy.class)) {
            float distance = map.distance(tower, enemy);
            if (distance < currentMinDistance) {
                currentMinDistance = distance;
                closestEnemy = enemy;
            }
        }
        return closestEnemy;
    }

    @Override
    public Entity calculateLowestHealthEnemy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Tower constructNewTower(int xpos, int ypos) {
        float x = xpos;
        float y = ypos;
        float radians = 0;
        PositionPart pos = new PositionPart(x, y, radians);

        int hp = 1;
        LifePart life = new LifePart(hp);

        CollisionPart colli = new CollisionPart(32, 32);

        float damage = 10;
        float range = 200;
        float speed = 1;
        Color c = WeaponPart.Color.YELLOW;
        WeaponPart wpn = new WeaponPart(damage, range, speed, c);

        int width = 32;
        int height = 32;
        int layer = 1;
        SpritePart sprt = new SpritePart(TowerPlugin.BASIC_TOWER_PATH, width, height, layer);

        return new Tower(pos, life, colli, wpn, sprt);
    }

    public void setMapSPI(MapSPI spi) {
        this.map = spi;
    }

    public void removeMapSPI(MapSPI spi) {
        this.map = null;
    }

    @Override
    public void showTowerPlacementPreview(GameData gameData, World world) {
        // Create the preview entity for the first time
        if (preview == null || world.getEntities(TowerPreview.class).isEmpty()) {
            TowerPreview towerPreview = new TowerPreview(
                    new PositionPart(0, 0, 0),
                    new SpritePart(TowerPlugin.BASIC_TOWER_PREVIEW_LEGAL_PATH, 2 * TileSizes.GRASS_WIDTH, 2 * TileSizes.GRASS_WIDTH, 2, 75)
            );
            world.addEntity(towerPreview);
            preview = towerPreview;
        }

        // Calculate placement of preview
        PositionPart posPart = preview.getPart(PositionPart.class);
        posPart.setX(gameData.getMouseX());
        posPart.setY(gameData.getMouseY());
        map.fitEntityToMap(preview);

        // Set preview sprite according to legalness of placement
        SpritePart sprite = preview.getPart(SpritePart.class);
        if (isLegalPlacement(preview)) {
            sprite.setSpritePath(TowerPlugin.BASIC_TOWER_PREVIEW_LEGAL_PATH);
        } else {
            sprite.setSpritePath(TowerPlugin.BASIC_TOWER_PREVIEW_ILLEGAL_PATH);
        }
    }

    @Override
    public void attackEnemies(GameData gameData, World world) {
        for (Entity tower : world.getEntities(Tower.class)) {
            // Remove dead towers
            if (((LifePart) tower.getPart(LifePart.class)).isDead()) {
                world.removeEntity(tower);
                gameData.addEvent(new MapChangedDuringRoundEvent(tower));
                continue;
            }

            Entity target = calculateClosestEnemy(world, tower);      // Or something
            if (target != null && (LifePart) target.getPart(LifePart.class) != null) {
                WeaponPart weapon = tower.getPart(WeaponPart.class);
                if (!target.equals(weapon.getTarget())) {
                    weapon.setTarget(target);
                    weapon.setIsNewTarget(true);
                }
                weapon.setColor(WeaponPart.Color.YELLOW);
            }
        }
    }
}
