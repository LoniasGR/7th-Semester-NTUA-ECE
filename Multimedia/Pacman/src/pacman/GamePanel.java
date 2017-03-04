/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JPanel;

/**
 *
 * @author Leonidas
 */
public abstract class GamePanel extends JPanel 
        implements KeyListener {

    /* Keyboard states - Here are stored 
    states for keyboard keys - is it down or not. */
    private static final  boolean[] KEYBOARD_STATE = new boolean[525];
    
        
    public GamePanel () {
        initGamePanel();
    }
    
    private void initGamePanel () {
          // We use double buffer to draw on the screen.
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        //this.setBackground(Color.black);
      
        
        /* Adds the keyboard listener to JPanel 
        to receive key events from this component. */
        this.addKeyListener(this);
        
    }
    
    
    public int findNumberOfCookies (char [][] board) {
        int cookies =0;
        for(int x = 0; x < 19; x++) {
            for (int y=0; y<22; y++) {
                if (board[y][x] == '.' || board[y][x] == 'O') {
                    cookies++;
                }
            }
        }
        return cookies;
    }    
    public Coordinates findGhostsLocation (char [][]board) {
        for(int x = 0; x < 19; x++) {
            for (int y=0; y<22; y++) {
                if (board[y][x] == 'F') {
                    return new Coordinates(24*x,24*y);
                }
            }
        }
        return null;
    }
    
    public Coordinates findEntrance (char[][] board) {
        for(int x = 0; x < 19; x++) {
            for (int y=0; y<22; y++) {
                if (board[y][x] == '-') {
                    return new Coordinates(24*x,24*y);
                }
            }
        }
        return null;
    }
    
    public Coordinates findPacmanLocation(char [][]  board) {
        for(int x = 0; x < 19; x++) {
            for (int y=0; y<22; y++) {
                if (board[y][x] == 'P') {
                    System.out.println("Pacman Starting Position: " + 
                            x + ", " + y);
                    return new Coordinates(24*x,24*y);
                }
            }
        }
        return null;
    }
    
     /* This method is overridden in GameGraphics.
    java and is used for drawing to the screen. */
    public abstract void Draw(Graphics2D g2d);
    
    @Override
    public void paintComponent(Graphics g)
    {
        Graphics2D g2d = (Graphics2D)g;        
        super.paintComponent(g2d);        
        Draw(g2d);
    }
    
    // Keyboard
    /**
     * Is keyboard key "key" down?
     * 
     * @param key Number of key for which you want to check the state.
     * @return true if the key is down, false if the key is not down.
     */
    public static boolean keyboardKeyState(int key)
    {
        return KEYBOARD_STATE[key];
    }
    
    // Methods of the keyboard listener.
    @Override
    public void keyPressed(KeyEvent e) 
    {
        KEYBOARD_STATE[e.getKeyCode()] = true;
    }
    
    @Override
    public void keyReleased(KeyEvent e)
    {
        KEYBOARD_STATE[e.getKeyCode()] = false;
        keyReleasedFramework(e);
    }
    
    @Override
    public void keyTyped(KeyEvent e) { }
    
    public abstract void keyReleasedFramework(KeyEvent e);
    
}

