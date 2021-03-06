package dk.sdu.mmmi.queen;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.data.entityparts.LifePart;
import dk.sdu.mmmi.cbse.common.events.GameOverEvent;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;
import dk.sdu.mmmi.commonenemy.Enemy;
import dk.sdu.mmmi.commonmap.MapSPI;
import dk.sdu.mmmi.commontower.Queen;
import dk.sdu.mmmi.commontower.QueenControlSystemSPI;
import dk.sdu.mmmi.commonweapon.WeaponPart;

import java.util.List;

public class QueenControlSystem implements IEntityProcessingService, QueenControlSystemSPI {

    private Queen queen;
    private MapSPI map;

    @Override
    public void process(GameData gameData, World world) {
        if (queen == null || !world.getEntities(Queen.class).contains(queen)) {
            List<Entity> queensInGame = world.getEntities(Queen.class);
            if (queensInGame.size() < 1) {
                return;
            }
            queen = (Queen) queensInGame.get(0);
            map.fitEntityToMap(queen);
        }

        // Check if dead
        if (((LifePart) queen.getPart(LifePart.class)).isDead()) {
            gameData.setMenuFlashMessage("Game over! You lost.");
            gameData.addEvent(new GameOverEvent(queen));
        }

        attackEnemies(gameData, world);
    }

    @Override
    public void attackEnemies(GameData gameData, World world) {
        // Attack enemies
        Entity target = calculateClosestEnemy(world);      // Or something
        if (target != null) {
            WeaponPart weapon = queen.getPart(WeaponPart.class);
            weapon.setTarget(target);
        }
    }

    @Override
    public Entity calculateClosestEnemy(World world) {
        float currentMinDistance = Float.MAX_VALUE;
        Entity closestEnemy = null;

        for (Entity enemy : world.getEntities(Enemy.class)) {
            float distance = map.distance(queen, enemy);
            if (distance < currentMinDistance) {
                currentMinDistance = distance;
                closestEnemy = enemy;
            }
        }
        return closestEnemy;
    }

    public void setMapSPI(MapSPI spi) {
        this.map = spi;
    }

    public void removeMapSPI(MapSPI spi) {
        this.map = null;
    }

}
