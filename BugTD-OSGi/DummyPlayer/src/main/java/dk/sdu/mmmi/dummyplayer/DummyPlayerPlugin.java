package dk.sdu.mmmi.dummyplayer;

import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.data.entityparts.CollisionPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.LifePart;
import dk.sdu.mmmi.cbse.common.data.entityparts.PositionPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.SpritePart;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;

/**
 *
 * @author ngram
 */
public class DummyPlayerPlugin implements IGamePluginService {

    DummyPlayer dp;
       
    @Override
    public void start(GameData gameData, World world) {
        
        try {
            dp = new DummyPlayer();
            dp.add(new SpritePart("wasp.png", 16, 16, 5));
            dp.add(new PositionPart(1, 1, 0));
            dp.add(new CollisionPart(16, 16));
            dp.add(new LifePart(100));
            world.addEntity(dp);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void stop(GameData gameData, World world) {
        
    }
}
