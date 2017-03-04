/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 *
 * @author Leonidas Avdelas
 */
public class LivesPanel extends JPanel{
    
    private Graphics2D g2d;
    private int lives;
    private Image pacman;
    
    public LivesPanel () {
        super();
        
    }
    
    public void Dopaint () {
            repaint();
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        g2d = (Graphics2D)g;        
        super.paintComponent(g2d);        
        Draw(g2d);
    }
    
    public void getLives(int lives) {
        this.lives = lives;
    }
    
    public void getIcon (Image pacman) {
        this.pacman = pacman;
    }
    
    public void Draw(Graphics2D g2d) {
        for (int i=0; i < lives; i ++) {
            g2d.drawImage(pacman, i, 0, this);
            
        }
    }
    
}
