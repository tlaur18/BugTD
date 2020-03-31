/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.sdu.mmmi.commonmap;

import dk.sdu.mmmi.cbse.common.data.Entity;

/**
 *
 * @author oliver
 */
public interface MapSPI {
    
    Tile[][] getTiles();
    Tile getClosestTile(float x, float y);
    Tile getTileXAndY(Tile t);
    Tile getTileEntityIsOn(Entity e);
    boolean CheckIfTileIsOccupied(Tile t);

    void loadFile(String filepath);
}
