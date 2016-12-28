
package pacman;


import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GUI {
   
    private static JFrame window;
    
    private static JPanel topPanel;
    private static JPanel scorePanel;
    private static JPanel highScorePanel;
    private static JPanel gamePanel;
    private static JPanel bottomPanel;
    private static JPanel livesPanel;
    private static JPanel gameControlsPanel;
    
    private static JLabel score;
    private static JLabel highScore;
    private static JLabel lives;
    
    private static JButton start;
    private static JButton pause;
    
    private static GridBagConstraints scoreLabelConstr;
    private static GridBagConstraints scoreConstr;
    private static GridBagConstraints highScoreLabelConstr;
    private static GridBagConstraints highScoreConstr;
    private static GridBagConstraints livesLabelConstr;
    private static GridBagConstraints livesConstr;
    
    private static BufferedImage livesImage;
    
    
    public GUI () throws IOException {
        scoreLabelConstr = new GridBagConstraints();
        scoreLabelConstr.insets = new Insets (0, 100, 0, 0);
        
        scoreConstr = new GridBagConstraints();
        scoreConstr.gridy = 1;
        scoreConstr.insets = new Insets (0, 100, 0, 0);
        
        highScoreLabelConstr = new GridBagConstraints();
        highScoreLabelConstr.insets = new Insets(0, 0, 0, 150);
        
        highScoreConstr = new GridBagConstraints();
        highScoreConstr.gridy = 1;
        highScoreConstr.insets = new Insets(0, 0, 0, 150);
        
        livesLabelConstr = new GridBagConstraints();
        livesLabelConstr.insets = new Insets(0, 50, 0, 0);
        
        livesConstr = new GridBagConstraints();
        livesConstr.gridx = 1;
        livesConstr.insets = new Insets (0, 10, 0, 0);
       
        try {
            livesImage = ImageIO.read(new File("PMright3.gif"));
        }
        catch (IOException e) {
            System.err.println("Lives Image: " + e.getMessage());
            System.exit(-1);
        }
        
        organiseGUI();
        createGUI();
        
        
    }
    private void organiseGUI () {
        window = new JFrame("MediaLab Pac-Man");
        window.setLayout(new BorderLayout());
        
        window.add(topPanel = new JPanel(), BorderLayout.PAGE_START);
        topPanel.setLayout(new BorderLayout());
        topPanel.add(scorePanel = new JPanel(), BorderLayout.LINE_START);
        scorePanel.setLayout(new GridBagLayout());
        scorePanel.add(new JLabel("Score"), scoreLabelConstr);
        scorePanel.add(score = new JLabel("0"), scoreConstr);
        topPanel.add(highScorePanel = new JPanel(), BorderLayout.LINE_END);
        highScorePanel.setLayout(new GridBagLayout());
        highScorePanel.add(new JLabel("High Score"), highScoreLabelConstr);
        highScorePanel.add(highScore = new JLabel("0"), highScoreConstr);
        
        window.add(gamePanel = new JPanel(), BorderLayout.CENTER);
        gamePanel.setLayout(new GridLayout(22, 19));
        
        window.add(bottomPanel = new JPanel(), BorderLayout.PAGE_END);
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add(livesPanel = new JPanel(), BorderLayout.LINE_START);
        livesPanel.setLayout(new GridBagLayout());
        livesPanel.add(new JLabel("Lives"), livesLabelConstr);
        
        //TODO add support for more than 1 life
        livesPanel.add(score = 
                new JLabel(new ImageIcon(livesImage)), livesConstr);
        bottomPanel.add(gameControlsPanel = new JPanel(),
                BorderLayout.LINE_END);
        
        //TODO Fix Layout
        gameControlsPanel.setLayout(new GridLayout(2, 1));
        gameControlsPanel.add(start = new JButton("Start"));
        gameControlsPanel.add(pause = new JButton("Pause"));
        
    }
    
    private void createGUI() {
        window.setSize(600,800);
        window.setLocationRelativeTo(null);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
    }
    
    private void initializeButtons () {
        //TODO
    }
    
}
