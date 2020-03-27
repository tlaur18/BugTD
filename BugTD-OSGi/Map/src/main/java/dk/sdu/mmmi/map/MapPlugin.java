/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.sdu.mmmi.map;

import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.data.entityparts.PositionPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.SpritePart;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;
import dk.sdu.mmmi.commonmap.MapSPI;
import dk.sdu.mmmi.commonmap.Tile;

/**
 *
 * @author oliver
 */
public class MapPlugin implements IGamePluginService, MapSPI {
    
    private Tile[][] tiles;
    
    @Override
    public void start(GameData gameData, World world) {
        // 52 rows length, 52 wide. Grass tiles are bigger in size and should therefore not take up as much space.
        tiles = new Tile[52][52];
        
        // The first three tiles and the last three tiles will be tiles with grass (environment)
        int rowsWithGrassInSides = 3;
        
        // TODO - Make it so environment can have different sizes than path. The problem is that when environment is bigger, there should not be places as many environment tiles as path tiles
        // which means the length becomes very long.
        final int TILE_SIZE = 16;
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                
                boolean walkable = true;
                
                // Determine if tile is environment or path tile
                if (j <= rowsWithGrassInSides - 1 || j >= tiles[0].length - rowsWithGrassInSides) {
                    walkable = false;
                }
                
                SpritePart tileSpritePart = new SpritePart(walkable ? "map/grass_16x16.png" : "map/dirt_16x16.png", TILE_SIZE, TILE_SIZE, 0);
                
                PositionPart tilePositionPart = new PositionPart(j * TILE_SIZE, i * TILE_SIZE, Math.PI / 2);
                Tile tile = new Tile(walkable, tileSpritePart, tilePositionPart);
                tiles[i][j] = tile;
                world.addEntity(tile);
            }
        }
    }

    @Override
    public void stop(GameData gameData, World world) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void loadFile(String filepath) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Tile[][] getTiles() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}