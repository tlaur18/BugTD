/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.sdu.mmmi.cbse.common.events;

/**
 *
 * @author Jakob
 */
public class ClickPosition {
    
    private int x;
    private int y;

    public ClickPosition( int screenX, int screenY) {
        x = screenX;
        y = screenY;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    
    
}
