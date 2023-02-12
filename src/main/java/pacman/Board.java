package pacman;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel {
    /**
     * Pixels of one block.
     */
    static final int BLOCK_SIZE = 24;

    /**
     * Logical map size: number of blocks.
     */
    static final Dimension MAP_SIZE = new Dimension(15, 15);

    /**
     * Board (physical map on screen) size.
     */
    private static final Dimension BOARD_SIZE = new Dimension(MAP_SIZE.width * BLOCK_SIZE, MAP_SIZE.height * BLOCK_SIZE);

    /**
     * (Board + bottom status space) size.
     */
    static final Dimension SCREEN_SIZE = new Dimension(BOARD_SIZE.width, BOARD_SIZE.height + 55);

    private static final Font SMALL_FONT = new Font("Helvetica", Font.BOLD, 14);
    private static final Color DOT_COLOR = new Color(192, 192, 0);
    private static final Color SUPER_DOT_COLOR = new Color(0, 192, 192);
    private static final Color WALL_COLOR = new Color(5, 100, 5);
    private static final Color SCORE_COLOR = new Color(96, 128, 255);

    private boolean inGame = false;
    private boolean dying = false;
    static final int[] map = new int[MAP_SIZE.width * MAP_SIZE.height];
    static int score;
    static int level = 0;

    /**
     * Direction from the keyboard event.
     */
    static Direction dRequest;

    
    public final Player player;
    public final Ghost ghost;
    public final Arrangement arrangement;
    private final Recorder recorder = new Recorder();
    private final Timer timer = new Timer(40, recorder.getTimerRecorder(new TimerActionListener()));

    public static final Random random = new Random();

    public Board() {
        System.out.println("Board");
        timer.start();
        addKeyListener(recorder.getKeyRecorder(new TAdapter()));
        setFocusable(true);
        setBackground(Color.black);
        this.player= new Player();
        this.ghost= new Ghost();
        this.arrangement = new Arrangement();
    }

    // ------------------------------------------

    @Override
    public void addNotify() {
        super.addNotify();
        initGame();
    }

    private void startGame() {
        System.out.println("startGame");
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
        initMap(arrangement.arrangement);
        continueGame();
    }
    private void initMap(int[] arrangement) {//レベルからMAPをつくる
        for (int i = 0; i < arrangement.length; i++) {
            int x = i % MAP_SIZE.width;
            int y = i / MAP_SIZE.width;
            int c = 0;
            if (x == 0 || (arrangement[i] == 1 && arrangement[i - 1] == 0)) {//一番左はし、もしくは今いる位置の左隣は壁なら
                c |= 1; // Lにはいけないという意味で、右から一番目のビットを1立てる
            }
            if (i < MAP_SIZE.width || (arrangement[i] == 1 && arrangement[i - MAP_SIZE.width] == 0)) {
                c |= 2; // U
            }
            if (x == MAP_SIZE.width - 1 || (arrangement[i] == 1 && arrangement[i + 1] == 0)) {
                c |= 4; // R
            }
            if (y == MAP_SIZE.height - 1 || (arrangement[i] == 1 && arrangement[i + MAP_SIZE.width] == 0)) {
                c |= 8; // D
            }
            if (arrangement[i] == 1) {
                c |= 16; // dot
            }
            if (arrangement[i] == 2) {
                c |= 32; // super dot
            }
            map[i] = c;
        }
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
        System.out.println("UpdateGame");
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
        g.fillRect(0, 0, SCREEN_SIZE.width, SCREEN_SIZE.height);
        drawMaze(g);
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
        for (int s : map) {
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
        arrangement.changeArrangement();
        initMap(arrangement.arrangement);
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
     * Draws the maze.
     */
    private void drawMaze(Graphics2D g) {
        // draw walls
        g.setColor(WALL_COLOR);
        g.setStroke(new BasicStroke(2));
        for (int i = 0; i < map.length; i++) {
            int x = (i % MAP_SIZE.width) * BLOCK_SIZE;
            int y = (i / MAP_SIZE.width) * BLOCK_SIZE;
            if ((map[i] & 1) != 0) { // WEST
                g.drawLine(x, y, x, y + BLOCK_SIZE - 1);
            }
            if ((map[i] & 2) != 0) { // NORTH
                g.drawLine(x, y, x + BLOCK_SIZE - 1, y);
            }
            if ((map[i] & 4) != 0) { // EAST
                g.drawLine(x + BLOCK_SIZE - 1, y, x + BLOCK_SIZE - 1, y + BLOCK_SIZE - 1);
            }
            if ((map[i] & 8) != 0) { // SOUTH
                g.drawLine(x, y + BLOCK_SIZE - 1, x + BLOCK_SIZE - 1, y + BLOCK_SIZE - 1);
            }
        }

        // draw dots
        g.setColor(DOT_COLOR);
        for (int i = 0; i < map.length; i++) {
            if ((map[i] & 16) != 0) {
                int x = (i % MAP_SIZE.width) * BLOCK_SIZE;
                int y = (i / MAP_SIZE.width) * BLOCK_SIZE;
                g.fillRect(x + BLOCK_SIZE/2 - 1, y + BLOCK_SIZE/2 - 1, 2, 2);
            }
        }
        // draw super dots
        g.setColor(SUPER_DOT_COLOR);
        for (int i = 0; i < map.length; i++) {
            if ((map[i] & 32) != 0) {
                int x = (i % MAP_SIZE.width) * BLOCK_SIZE;
                int y = (i / MAP_SIZE.width) * BLOCK_SIZE;
                g.fillRect(x + BLOCK_SIZE/2 - 1, y + BLOCK_SIZE/2 - 1, 4, 4);
            }
        }
    }

    /**
     * Draws the score and the pacmans left.
     */
    private void drawScore(Graphics2D g) {
        g.setFont(SMALL_FONT);
        g.setColor(SCORE_COLOR);
        g.drawString("Score: " + score, BOARD_SIZE.width / 2 + 96, BOARD_SIZE.height + 16);
        for (int i = 0; i < player.pacmansLeft; i++) {
            g.drawImage(Player.PACMAN_IMAGE_L[3], i * 28 + 8, BOARD_SIZE.height + 1, this);
        }
    }

    /**
     * Draws the screen "Press s to start.".
     */
    private void drawIntroScreen(Graphics2D g) {
        g.setColor(new Color(0, 32, 48));
        g.fillRect(50, BOARD_SIZE.height / 2 - 30, BOARD_SIZE.width - 100, 50);
        g.setColor(Color.white);
        g.drawRect(50, BOARD_SIZE.height / 2 - 30, BOARD_SIZE.width - 100, 50);

        String s = "Press s to start.";
        FontMetrics metric = this.getFontMetrics(SMALL_FONT);
        g.setColor(Color.white);
        g.setFont(SMALL_FONT);
        g.drawString(s, (BOARD_SIZE.width - metric.stringWidth(s)) / 2, BOARD_SIZE.height / 2);
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
