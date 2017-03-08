/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.JPanel;

/**
 * Lives panel is used to easily display the lives pacman has.
 * @author Leonidas Avdelas
 */
public class LivesPanel extends JPanel{
    
    private Graphics2D g2d;
    private int lives;
    private Image pacman;
    
    /**
     * Lives Panel constructor.
     */
    public LivesPanel () {
        super();
        
    }
    
    /**
     * Paint lives.
     */
    public void Dopaint () {
            repaint();
    }
    
    /**
     * Overriden paint Component to draw lives.
     * @param g Graphics object.
     */
    @Override
    public void paintComponent(Graphics g)
    {
        g2d = (Graphics2D)g;        
        super.paintComponent(g2d);        
        Draw(g2d);
    }
    
    /**
     * Get number of lives.
     * @param lives number of lives pacman has.
     */
    public void getLives(int lives) {
        this.lives = lives;
    }
    
    /**
     * Get icon to display as lives.
     * @param pacman Icon
     */
    public void getIcon (Image pacman) {
        this.pacman = pacman;
    }
    
    /**
     * Draw lives. Used by paintComponent.
     * @param g2d Graphics2D object.
     */
    public void Draw(Graphics2D g2d) {
        for (int i=0; i < lives; i ++) {
            g2d.drawImage(pacman, i, 0, this);
            
        }
    }
    
}
