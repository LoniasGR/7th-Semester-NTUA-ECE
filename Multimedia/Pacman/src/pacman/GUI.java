
package pacman;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class GUI extends JFrame {
       
    private static JPanel topPanel;
    private static JPanel scorePanel;
    private static JPanel highScorePanel;
    private static JPanel bottomPanel;
    private static JPanel livesPanel;
    private static JPanel gameControlsPanel;
    
    private static GameGraphics gamePanel;
    
    public static JLabel score;
    public static JLabel highScore;
    public static JLabel lives1;
    public static JLabel lives2;
    public static JLabel lives3;
    
    public JButton start;
    private static JButton pause;
    
    private static GridBagConstraints scoreLabelConstr;
    private static GridBagConstraints scoreConstr;
    private static GridBagConstraints highScoreLabelConstr;
    private static GridBagConstraints highScoreConstr;
    private static GridBagConstraints pauseConstr;
    private static GridBagConstraints startConstr;
            
    public JOptionPane lossmessage;
    
    private JMenuBar menubar;
    private JMenu gamemenu;
    public JMenuItem mstart;
    public JMenuItem load;
    public JMenuItem highscores;
    public JMenuItem exit;
    
    /**
     * GUI constructor.
     * Creates a new JFrame and puts all Jelements needed inside. 
     * @param s title of the JFrame
     * @throws IOException 
     */
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
        
        pauseConstr = new GridBagConstraints();
        pauseConstr.insets = new Insets (0, 0, 0, 25);
        
        startConstr = new GridBagConstraints();
        startConstr.gridy = 1;
        startConstr.insets = new Insets (0, 0, 0, 25);
        
        organiseGUI();
        createGUI();
        createMenuBar();
    }
    
    /**
     * Organizes the position of each element inside the JFrame.
     */
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
        gamePanel.setPreferredSize(new Dimension(456,528));
        gamePanel.setBackground(Color.BLACK);
        
        this.add(bottomPanel = new JPanel(), BorderLayout.PAGE_END);
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add(livesPanel = new JPanel(), BorderLayout.LINE_START);
        
        livesPanel.add(new JLabel("Lives :"));        
        livesPanel.add(lives1 = new JLabel());
        livesPanel.add(lives2 = new JLabel());
        livesPanel.add(lives3 = new JLabel());
        
        bottomPanel.add(gameControlsPanel = new JPanel(),
                BorderLayout.LINE_END);
        
        gameControlsPanel.setLayout(new GridBagLayout());
        start = new JButton("Start");
        start.setPreferredSize(new Dimension(70, 25));
        gameControlsPanel.add(start, startConstr);
        //pause = new JButton("Pause");
        //pause.setPreferredSize(new Dimension(70, 25));
        //gameControlsPanel.add(pause, pauseConstr);
        
    }
    
    /**
     * Create the JFrame.
     */
    private void createGUI() {
        setSize(600,800);
        setLocationRelativeTo(null);       
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(400,600));    
    }
    
    /**
     * Create the menu bar.
     */
    private void createMenuBar() {
        menubar = new JMenuBar();
        setJMenuBar(menubar);
        
        gamemenu = new JMenu("Game");
        menubar.add(gamemenu);
        
        mstart = new JMenuItem("Start");
        load = new JMenuItem("Load");
        highscores = new JMenuItem("Highscores");
        exit = new JMenuItem("Exit");
        
        gamemenu.add(mstart);
        gamemenu.add(load);
        gamemenu.add(highscores);
        gamemenu.add(exit);
    } 
    
    /**
     * Make the JFrame visible.
     */
    public void setVisible() {
        this.pack();
        this.setVisible(true);
    }

    /**
     * Display victory message.
     * @return option the user clicked.
     */
    public int showVictoryMessage () {
        Object[] options = {"Restart Game", "Exit Game"};
        
        int n = JOptionPane.showOptionDialog(this,
        "You Won.\n Dou you want to play again or exit?",
        "Victory",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE,
        null,     //do not use a custom Icon
        options,  //the titles of buttons
        options[0]); //default button title
        
        return n;
    }
    
    /**
     * Shows a congratulations message for making a highscore and asks 
     * for a name.
     * @param position position in the leaderboard
     * @return the name the user picked.
     */
    public String showHighscoreMessage (int position) {
        position++;
        String congrats = "Congratulations you made it to the leaderboard"
                + " \n in position " + position + "!\n Type your name for it "
                + "to be recorded!";
            String name = JOptionPane.showInputDialog(
        this, 
        congrats , 
        "HIGHSCORE ACHIEVED", 
        JOptionPane.WARNING_MESSAGE
    );
            return name;
    }
    
    /**
     * Displays the highscores popup.
     * @param highscores name and score for the best 5 highscores.
     */
    public void showHighscores (String [][] highscores) {
        
        int counter = 0;
        String message  = "HIGHSCORES\n";
        System.out.println(message);
        if (highscores[0][1] == null) {
            message += "No highscores yet! Play to make one!";
            System.out.println(message);
        }
        else {
            while (counter <= 4 && highscores[counter][1] != null){
                message += (highscores[counter][0] + "-"
                        + highscores[counter][1] + "\n");
                counter++;
            }
        }
            JOptionPane.showMessageDialog(this, 
                    message, "HIGHSCORES", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Show loss message.
     * @return the action the user clicked.
     */
    public int showLossMessage () {
        Object[] options = {"Restart Game", "Exit Game"};
        
        int n = JOptionPane.showOptionDialog(this,
        "You Lost.\n Dou you want to play again or exit?",
        "LOSS",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE,
        null,     //do not use a custom Icon
        options,  //the titles of buttons
        options[0]); //default button title
        
        return n;
    }
    
    public String chooseBoardMessage () {        
       
        String s = (String)JOptionPane.showInputDialog(
                    this,
                    "Pick board:\n",
                    "Pick a board:",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    gamePanel.boards,
                    gamePanel.boards[0]);
        return s;
    }
    
    /**
     * Used to close popup messages.
     */
    public void closeMessage() {
     JOptionPane.getRootFrame().dispose();   
    }
   

    
    
    
}
