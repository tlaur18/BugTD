package dk.sdu.mmmi.dummyplayer;

import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
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
            dp.add(new SpritePart("wasp.png", 32, 32));
            dp.add(new PositionPart(gameData.getDisplayWidth() / 2, gameData.getDisplayHeight() / 2, 0));
            world.addEntity(dp);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void stop(GameData gameData, World world) {
    }
    
}
