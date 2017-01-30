/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 *
 * @author Leonidas
 */
public class GamePanel extends JPanel 
        implements KeyListener, MouseListener {

    /* Keyboard states - Here are stored 
    states for keyboard keys - is it down or not. */
    private static final  boolean[] KEYBOARD_STATE = new boolean[525];
    
    /* Mouse states - Here are stored states 
    for mouse keys - is it down or not. */
    private static final  boolean[] MOUSE_STATE = new boolean[3];
        
    public GamePanel () {
        initGamePanel();
    }
    
    private void initGamePanel () {
          // We use double buffer to draw on the screen.
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        this.setBackground(Color.black);
        
        BufferedImage blankCursorImg =
                new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = 
                Toolkit.getDefaultToolkit().createCustomCursor
                   (blankCursorImg, new Point(0, 0), null);
        this.setCursor(blankCursor);
        
        
        /* Adds the keyboard listener to JPanel 
        to receive key events from this component. */
        this.addKeyListener(this);
        /* Adds the mouse listener to JPanel 
        to receive mouse events from this component. */
        this.addMouseListener(this);
    }
    
     /* This method is overridden in Framework.
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
    
    
    // Mouse
    /**
     * Is mouse button "button" down?
     * Parameter "button" can be "MouseEvent.BUTTON1" - Indicates mouse button #1
     * or "MouseEvent.BUTTON2" - Indicates mouse button #2 ...
     * 
     * @param button Number of mouse button for which you want to check the state.
     * @return true if the button is down, false if the button is not down.
     */
    public static boolean mouseButtonState(int button)
    {
        return MOUSE_STATE[button - 1];
    }
    
    // Sets mouse key status.
    private void mouseKeyStatus(MouseEvent e, boolean status)
    {
        switch (e.getButton()) {
            case MouseEvent.BUTTON1:
                MOUSE_STATE[0] = status;
                break;
            case MouseEvent.BUTTON2:
                MOUSE_STATE[1] = status;
                break;
            case MouseEvent.BUTTON3:
                MOUSE_STATE[2] = status;
                break;
            default:
                break;
        }
    }
    
    // Methods of the mouse listener.
    @Override
    public void mousePressed(MouseEvent e)
    {
        mouseKeyStatus(e, true);
    }
    
    @Override
    public void mouseReleased(MouseEvent e)
    {
        mouseKeyStatus(e, false);
    }
    
    @Override
    public void mouseClicked(MouseEvent e) { }
    
    @Override
    public void mouseEntered(MouseEvent e) { }
    
    @Override
    public void mouseExited(MouseEvent e) { }
    
}

