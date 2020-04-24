/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.sdu.mmmi.ai.astar;

import dk.sdu.mmmi.commonmap.Tile;
import java.util.StringJoiner;

/**
 *
 * @author oliver
 */
public class MapTile implements GraphNode {
    private String id;
    private Tile tile;

    public MapTile(String id, Tile tile) {
        this.id = id;
        this.tile = tile;
    }
    
    @Override
    public String getId() {
        return id;
    }

    public float getX() {
        return tile.getX();
    }

    public float getY() {
        return tile.getY();
    }
    
    public String getPosition() {
        return "(" + getX() + ";" + getY() + ")";
    }

    public Tile getTile() {
        return tile;
    }
    
    @Override
    public String toString() {
        return new StringJoiner(", ", MapTile.class.getSimpleName() + "[", "]").add("id='" + id + "'")
            .add("x='" + getX() + "'").add("y=" + getY()).toString();
    }
}