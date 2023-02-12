package pacman;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GameManager extends JPanel {

    private static final Color SCORE_COLOR = new Color(96, 128, 255);
    private static final Font SMALL_FONT = new Font("Helvetica", Font.BOLD, 14);
    private boolean inGame = false;
    private boolean dying = false;
    static int score;
    static int level = 0;
    /**
     * Direction from the keyboard event.
     */
    static Direction dRequest;
    public final Player player;
    public final Ghost ghost;
    public final Board board;
    private final Recorder recorder = new Recorder();
    private final Timer timer = new Timer(40, recorder.getTimerRecorder(new TimerActionListener()));
    public static final Random random = new Random();

    public GameManager() {
        timer.start();
        addKeyListener(recorder.getKeyRecorder(new TAdapter()));
        setFocusable(true);
        setBackground(Color.black);
        this.player= new Player();
        this.ghost= new Ghost();
        this.board = new Board();
    }

    // ------------------------------------------

    @Override
    public void addNotify() {
        super.addNotify();
        initGame();
    }

    private void startGame() {
        inGame = true;
        recorder.start("replay.log");
        long seed = random.nextLong();
        recorder.log(-1, String.valueOf(seed));
        random.setSeed(seed);
        initGame();
    }

    private void finishGame() {
        inGame = false;
        recorder.stop();
    }

    private void initGame() {
        player.pacmansLeft = 3;
        score = 0;
        ghost.numGhosts = 0;
        ghost.ghostSpeedRank = 3;
        board.initMap();
        continueGame();
    }

    private void continueGame() {
        player.continueGame();
        dying = false;
        ghost.continueGame();
    }

    // ------------------------------------------

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGame((Graphics2D) g);
    }

    private void updateGame() {
        if (inGame) {
            if (dying) {//一回死ぬ
                death();
            } else {
                player.move();
                ghost.move();
                if (checkCollision()) {
                    dying = true;
                }
                if (checkComplete()) {
                    score += (50*level);
                    increaseLevel();
                }
            }
        }
    }

    private void drawGame(Graphics2D g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, Board.SCREEN_SIZE.width, Board.SCREEN_SIZE.height);
        board.drawMaze(g);
        drawScore(g);
        player.updateAnimationState();
        if (inGame) {
            if (!dying) {
                player.draw(g);
                ghost.draw(g);
            }
        } else {
            drawIntroScreen(g);
        }
        Toolkit.getDefaultToolkit().sync();
        g.dispose();
    }

    /**
     * Checks whether all the dots are collected.
     */
    private boolean checkComplete() {
        for (int s : Board.map) {
            if ((s & 48) != 0) {//おそらく右から5番目のビット、つまり32の値はスーパー食料な気がする
                return false;
            }
        }
        return true;
    }

    /**
     * Goes to the next level.
     */
    private void increaseLevel() {
        level +=1;
        ghost.increaseLevel();
        board.changeArrangement();
        board.initMap();
        continueGame();
    }

    private void death() {
        player.pacmansLeft--;
        if (player.pacmansLeft == 0) {
            finishGame();
        }
        continueGame();
    }


    /**
     * Judges whether Pacman touched any ghost.
     */
    private boolean checkCollision() {
        for (int i = 0; i < ghost.numGhosts; i++) {
            if (isCollision(player.pPacman, ghost.pGhost[i]) && inGame) {
                return true;
            }
        }
        return false;
    }

    // ---------------------------------------------

    /**
     * Draws the screen "Press s to start.".
     */
    void drawIntroScreen(Graphics2D g) {
        g.setColor(new Color(0, 32, 48));
        g.fillRect(50, Board.BOARD_SIZE.height / 2 - 30, Board.BOARD_SIZE.width - 100, 50);
        g.setColor(Color.white);
        g.drawRect(50, Board.BOARD_SIZE.height / 2 - 30, Board.BOARD_SIZE.width - 100, 50);

        String s = "Press s to start.";
        FontMetrics metric = this.getFontMetrics(SMALL_FONT);
        g.setColor(Color.white);
        g.setFont(SMALL_FONT);
        g.drawString(s, (Board.BOARD_SIZE.width - metric.stringWidth(s)) / 2, Board.BOARD_SIZE.height / 2);
    }
    /**
     * Draws the score and the pacmans left.
     */
    private void drawScore(Graphics2D g) {
        g.setFont(SMALL_FONT);
        g.setColor(SCORE_COLOR);
        g.drawString("Score: " + score, Board.BOARD_SIZE.width / 2 + 96, Board.BOARD_SIZE.height + 16);
        for (int i = 0; i < player.pacmansLeft; i++) {
            g.drawImage(Player.PACMAN_IMAGE_L[3], i * 28 + 8, Board.BOARD_SIZE.height + 1, this);
        }
    }

    public void replay(String replayFile) {
        /* not yet implemented ... */
    }

    class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (!inGame) {
                if (key == 's' || key == 'S') {
                    startGame();
                }
                return;
            }
            if (key == KeyEvent.VK_LEFT) {
                dRequest = Direction.L;
            } else if (key == KeyEvent.VK_RIGHT) {
                dRequest = Direction.R;
            } else if (key == KeyEvent.VK_UP) {
                dRequest = Direction.U;
            } else if (key == KeyEvent.VK_DOWN) {
                dRequest = Direction.D;
            } else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
                finishGame();
            } else if (key == KeyEvent.VK_PAUSE) {
                if (timer.isRunning()) {
                    timer.stop();
                } else {
                    timer.start();
                }
            }
        }
    }

    // --------------------------------

    private boolean isCollision(Point p1, Point p2) {
        return Math.abs(p1.x - p2.x) < 12 && Math.abs(p1.y - p2.y) < 12;
    }

    public class TimerActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            updateGame();
            repaint();
        }
    }
}
