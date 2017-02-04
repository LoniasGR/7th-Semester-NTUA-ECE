
package pacman;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GUI extends JFrame {
       
    private static JPanel topPanel;
    private static JPanel scorePanel;
    private static JPanel highScorePanel;
    private static JPanel bottomPanel;
    private static JPanel livesPanel;
    private static JPanel gameControlsPanel;
    
    private static GameGraphics gamePanel;
    
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
    private static GridBagConstraints pauseConstr;
    private static GridBagConstraints startConstr;
        
    
    public GUI (String s) throws IOException {
        super(s);
        
        scoreLabelConstr = new GridBagConstraints();
        scoreLabelConstr.insets = new Insets (0, 50, 0, 0);
        
        scoreConstr = new GridBagConstraints();
        scoreConstr.gridy = 1;
        scoreConstr.insets = new Insets (0, 50, 0, 0);
        
        highScoreLabelConstr = new GridBagConstraints();
        highScoreLabelConstr.insets = new Insets(0, 0, 0, 50);
        
        highScoreConstr = new GridBagConstraints();
        highScoreConstr.gridy = 1;
        highScoreConstr.insets = new Insets(0, 0, 0, 50);
        
        livesLabelConstr = new GridBagConstraints();
        livesLabelConstr.insets = new Insets(0, 10, 0, 0);
        
        livesConstr = new GridBagConstraints();
        livesConstr.gridx = 1;
        livesConstr.insets = new Insets (0, 10, 0, 0);
        
        pauseConstr = new GridBagConstraints();
        pauseConstr.insets = new Insets (0, 0, 0, 25);
        
        startConstr = new GridBagConstraints();
        startConstr.gridy = 1;
        startConstr.insets = new Insets (0, 0, 0, 25);
        
        organiseGUI();
        createGUI();
        
        
    }
    private void organiseGUI () {
        this.setLayout(new BorderLayout());

        
        this.add(topPanel = new JPanel(), BorderLayout.PAGE_START);
        topPanel.setLayout(new BorderLayout());
        topPanel.add(scorePanel = new JPanel(), BorderLayout.LINE_START);
        scorePanel.setLayout(new GridBagLayout());
        scorePanel.add(new JLabel("Score"), scoreLabelConstr);
        scorePanel.add(score = new JLabel("0"), scoreConstr);
        topPanel.add(highScorePanel = new JPanel(), BorderLayout.LINE_END);
        highScorePanel.setLayout(new GridBagLayout());
        highScorePanel.add(new JLabel("High Score"), highScoreLabelConstr);
        highScorePanel.add(highScore = new JLabel("0"), highScoreConstr);
        
        this.add(gamePanel = new GameGraphics(this), BorderLayout.CENTER);
        gamePanel.setLayout(new GridLayout(22, 19));
        
        this.add(bottomPanel = new JPanel(), BorderLayout.PAGE_END);
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add(livesPanel = new JPanel(), BorderLayout.LINE_START);
        
        livesPanel.setLayout(new GridBagLayout());
        livesPanel.add(new JLabel("Lives"), livesLabelConstr);        
        livesPanel.add(lives = new JLabel(), livesConstr);
       
        bottomPanel.add(gameControlsPanel = new JPanel(),
                BorderLayout.LINE_END);
        
        gameControlsPanel.setLayout(new GridBagLayout());
        start = new JButton("Start");
        start.setPreferredSize(new Dimension(70, 25));
        gameControlsPanel.add(start, startConstr);
        pause = new JButton("Pause");
        pause.setPreferredSize(new Dimension(70, 25));
        gameControlsPanel.add(pause, pauseConstr);
        
    }
    
    private void createGUI() {
        setSize(800,600);
        setLocationRelativeTo(null);       
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(400,400));

        
    }
    
    public void setVisible() {
        this.pack();
        this.setVisible(true);
    }
    
    private void initializeButtons () {
        //TODO
    }
    
}
