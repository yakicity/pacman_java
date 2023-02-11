package pacman;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel {
    private static final Logger log = LoggerFactory.getLogger(Board.class);

    /**
     * Pixels of one block.
     */
    private static final int BLOCK_SIZE = 24;

    /**
     * Logical map size: number of blocks.
     */
    private static final Dimension MAP_SIZE = new Dimension(15, 15);

    /**
     * Board (physical map on screen) size.
     */
    private static final Dimension BOARD_SIZE = new Dimension(MAP_SIZE.width * BLOCK_SIZE, MAP_SIZE.height * BLOCK_SIZE);

    /**
     * (Board + bottom status space) size.
     */
    public static final Dimension SCREEN_SIZE = new Dimension(BOARD_SIZE.width, BOARD_SIZE.height + 55);

    private static final int MAX_GHOSTS = 12;
    /**
     * Pacman's speed.
    */
    private static final int PACMAN_SPEED = 6;
    private static final int[] VALID_SPEEDS = {1, 2, 3, 4, 6, 8};
    // pacman
    private static final int[] ANIMATION_STATES = {0, 0, 1, 1, 2, 2, 3, 3, 2, 2, 1, 1};

    private static final Font SMALL_FONT = new Font("Helvetica", Font.BOLD, 14);
    private static final Color DOT_COLOR = new Color(192, 192, 0);
    private static final Color WALL_COLOR = new Color(5, 100, 5);
    private static final Color SCORE_COLOR = new Color(96, 128, 255);

    /**
     * ghost.
     */
    private static final Image GHOST_IMAGE = load("ghost.png");
    /**
     * Pacman's image.
    */
    private static final Image PACMAN_IMAGE_NEUTRAL = load("pacman.png");
    private static final Image[] PACMAN_IMAGE_L = {PACMAN_IMAGE_NEUTRAL, load("left1.png"), load("left2.png"), load("left3.png")};
    private static final Image[] PACMAN_IMAGE_U = {PACMAN_IMAGE_NEUTRAL, load("up1.png"), load("up2.png"), load("up3.png")};
    private static final Image[] PACMAN_IMAGE_R = {PACMAN_IMAGE_NEUTRAL, load("right1.png"), load("right2.png"), load("right3.png")};
    private static final Image[] PACMAN_IMAGE_D = {PACMAN_IMAGE_NEUTRAL, load("down1.png"), load("down2.png"),  load("down3.png")};

    private static final int[] LEVEL = {
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1,
        1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1,
        1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1,
        0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1,
        0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1,
        0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1,
        0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1,
        0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1,
        0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1,
        0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1,
    };

    private boolean inGame = false;
    private boolean dying = false;
    private final int[] map = new int[MAP_SIZE.width * MAP_SIZE.height];

    // pacman
    private int animationIndex = 0;
    /**
     * Pacman's left.
     */
    private int pacmansLeft;
    /**
     * score.
     */    
    private int score;

    /**
     * Pacman's position.
     */
    private Point pPacman;

    /**
     * Pacman's (logical and view's) directions.
     */
    private Direction dPacman, dPacmanView;

    /**
     * Direction from the keyboard event.
     */
    private Direction dRequest;

    /**
     * Number of ghosts.
     */
    private int numGhosts = 6;

    /**
     * Position of ghosts.
     */
    private final Point[] pGhost = new Point[MAX_GHOSTS];

    /**
     * Direction of ghosts.
     */
    private final Direction[] dGhost = new Direction[MAX_GHOSTS];

    /**
     * Speed of ghosts.
     */
    private final int[] ghostSpeed = new int[MAX_GHOSTS];

    /**
     * Current speed rank (indicating possible speeds of ghosts to be assigned).
     */
    private int ghostSpeedRank = 3;
    private final Recorder recorder = new Recorder();
    private final Timer timer = new Timer(40, recorder.getTimerRecorder(new TimerActionListener()));

    private final Random random = new Random();

    /**
     * character.
    */
    private static Image load(String filename) {
        URL url = Board.class.getClassLoader().getResource("images/" + filename);
        assert url != null;
        return new ImageIcon(url).getImage();
    }

    public Board() {
        timer.start();
        addKeyListener(recorder.getKeyRecorder(new TAdapter()));
        setFocusable(true);
        setBackground(Color.black);
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
        pacmansLeft = 3;
        score = 0;
        numGhosts = 6;
        ghostSpeedRank = 3;
        initLevel(LEVEL);
    }

    private void initLevel(int[] level) {
        for (int i = 0; i < level.length; i++) {
            int x = i % MAP_SIZE.width;
            int y = i / MAP_SIZE.width;
            int c = 0;
            if (x == 0 || (level[i] == 1 && level[i - 1] == 0)) {//一番左はし、もしくは今いる位置の左隣は壁なら
                c |= 1; // Lにはいけないという意味で、右から一番目のビットを1立てる
            }
            if (i < MAP_SIZE.width || (level[i] == 1 && level[i - MAP_SIZE.width] == 0)) {
                c |= 2; // U
            }
            if (x == MAP_SIZE.width - 1 || (level[i] == 1 && level[i + 1] == 0)) {
                c |= 4; // R
            }
            if (y == MAP_SIZE.height - 1 || (level[i] == 1 && level[i + MAP_SIZE.width] == 0)) {
                c |= 8; // D
            }
            if (level[i] == 1) {
                c |= 16; // dot
            }
            map[i] = c;
        }

        continueLevel();
    }

    private void continueLevel() {
        pPacman = new Point(7 * BLOCK_SIZE, 11 * BLOCK_SIZE);
        dPacman = dRequest = Direction.O;
        dPacmanView = Direction.L;
        dying = false;
        for (int i = 0; i < numGhosts; i++) {
            pGhost[i] = new Point(4 * BLOCK_SIZE, 4 * BLOCK_SIZE);
            dGhost[i] = i % 2 == 0 ? Direction.R : Direction.L;
            ghostSpeed[i] = VALID_SPEEDS[random.nextInt(ghostSpeedRank)];
            log.debug("Ghost {}: speed {}", i, ghostSpeed[i]);
        }
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
                movePacman();
                moveGhosts();
                if (checkCollision()) {
                    dying = true;
                }
                if (checkComplete()) {
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
        updateAnimationState();
        if (inGame) {
            if (!dying) {
                drawPacman(g);
                drawGhosts(g);
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
        score += 50;
        if (numGhosts < MAX_GHOSTS) {
            numGhosts++;
        }
        if (ghostSpeedRank < VALID_SPEEDS.length) {
            ghostSpeedRank++;
        }
        initLevel(LEVEL);
    }

    private void death() {
        pacmansLeft--;
        if (pacmansLeft == 0) {
            finishGame();
        }
        continueLevel();
    }

    /**
     * Moves Pacman.　->packman
     */
    private void movePacman() {
        // Pacman can always go in the exact opposite direction
        if (dRequest.flip() == dPacman) {
            dPacmanView = dPacman = dRequest;
        }
        if (onBlock(pPacman)) {
            int loc = pointToLocation(pPacman);
            int l = map[loc];
            if ((l & 16) != 0) {//左から5番目のビットが少なくとも1が立ってたら、つまりまだ食料を食べていなかったら
                // eat dot
                map[loc] = l & 15;//5桁目だけを0に変える
                score++;
            }
            // turn
            if (dRequest != Direction.O) {
                if (!((dRequest == Direction.L && (l & 1) != 0)//（左に行きたい、かついけない）ではない、つまりどこかに行ける時
                        || (dRequest == Direction.U && (l & 2) != 0)
                        || (dRequest == Direction.R && (l & 4) != 0)
                        || (dRequest == Direction.D && (l & 8) != 0))) {
                    dPacmanView = dPacman = dRequest;
                }
            }
            // Check for standstill
            if ((dPacman == Direction.L && (l & 1) != 0)
                    || (dPacman == Direction.U && (l & 2) != 0)
                    || (dPacman == Direction.R && (l & 4) != 0)
                    || (dPacman == Direction.D && (l & 8) != 0)) {
                dPacman = Direction.O;//移動できません
            }
        }
        pPacman.x += dPacman.dx * PACMAN_SPEED;
        pPacman.y += dPacman.dy * PACMAN_SPEED;
    }

    /**
     * Moves ghosts.
     */
    private void moveGhosts() {
        for (int i = 0; i < numGhosts; i++) {
            if (onBlock(pGhost[i])) {
                int loc = pointToLocation(pGhost[i]);
                List<Direction> dirs = new ArrayList<>();
                if ((map[loc] & 1) == 0 && dGhost[i] != Direction.R) {
                    dirs.add(Direction.L);
                }
                if ((map[loc] & 2) == 0 && dGhost[i] != Direction.D) {
                    dirs.add(Direction.U);
                }
                if ((map[loc] & 4) == 0 && dGhost[i] != Direction.L) {
                    dirs.add(Direction.R);
                }
                if ((map[loc] & 8) == 0 && dGhost[i] != Direction.U) {
                    dirs.add(Direction.D);
                }
                if (dirs.isEmpty()) {
                    if ((map[loc] & 15) == 15) {
                        dGhost[i] = Direction.O;
                    } else {
                        dGhost[i] = dGhost[i].flip();
                    }
                } else {
                    int n = random.nextInt(dirs.size());
                    dGhost[i] = dirs.get(n);
                }
            }
            pGhost[i].x += dGhost[i].dx * ghostSpeed[i];
            pGhost[i].y += dGhost[i].dy * ghostSpeed[i];
        }
    }

    /**
     * Judges whether Pacman touched any ghost.
     */
    private boolean checkCollision() {
        for (int i = 0; i < numGhosts; i++) {
            if (isCollision(pPacman, pGhost[i]) && inGame) {
                return true;
            }
        }
        return false;
    }

    /**
     * Updates Pacman's animation state.->packman
     */
    private void updateAnimationState() {
        animationIndex = (animationIndex + 1) % ANIMATION_STATES.length;
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
    }

    /**
     * Draws the score and the pacmans left.
     */
    private void drawScore(Graphics2D g) {
        g.setFont(SMALL_FONT);
        g.setColor(SCORE_COLOR);
        g.drawString("Score: " + score, BOARD_SIZE.width / 2 + 96, BOARD_SIZE.height + 16);
        for (int i = 0; i < pacmansLeft; i++) {
            g.drawImage(PACMAN_IMAGE_L[3], i * 28 + 8, BOARD_SIZE.height + 1, this);
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

    /**
     * Draws Pacman.-> packman
     */
    private void drawPacman(Graphics2D g) {
        int state = ANIMATION_STATES[animationIndex];
        Image img;
        switch (dPacmanView) {
        case L:
            img = PACMAN_IMAGE_L[state];
            break;
        case U:
            img = PACMAN_IMAGE_U[state];
            break;
        case R:
            img = PACMAN_IMAGE_R[state];
            break;
        default:
            img = PACMAN_IMAGE_D[state];
            break;
        }
        g.drawImage(img, pPacman.x + 1, pPacman.y + 1, this);
    }

    /**
     * Draws ghosts.
     */
    private void drawGhosts(Graphics2D g) {
        for (int i = 0; i < numGhosts; i++) {
            g.drawImage(GHOST_IMAGE, pGhost[i].x + 1, pGhost[i].y + 1, this);
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

    private boolean onBlock(Point p) {
        return p.x % BLOCK_SIZE == 0 && p.y % BLOCK_SIZE == 0;
    }

    private int pointToLocation(Point p) {
        return (p.y / BLOCK_SIZE) * MAP_SIZE.width + (p.x / BLOCK_SIZE);
    }

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
