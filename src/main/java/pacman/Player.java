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

public class Player extends JPanel{
    /**
     * Pacman's speed.
    */
    private static final int PACMAN_SPEED = 6;
    /**
     * Pacman's position.
     */
    Point pPacman;
    /**
     * Pacman's (logical and view's) directions.
     */
    Direction dPacman,dPacmanView;
    /**
     * Pacman's left.
     */
    int pacmansLeft;

    void continueLevelPacman(){
        pPacman = new Point(7 * Board.BLOCK_SIZE, 11 * Board.BLOCK_SIZE);
        dPacman = Board.dRequest = Direction.O;
        dPacmanView = Direction.L;
    }

    private boolean onBlock(Point p) {
        return p.x % Board.BLOCK_SIZE == 0 && p.y % Board.BLOCK_SIZE == 0;
    }
    private int pointToLocation(Point p) {
        return (p.y / Board.BLOCK_SIZE) * Board.MAP_SIZE.width + (p.x / Board.BLOCK_SIZE);
    }
    /**
     * Moves Pacman.　->packman
     */
    void movePacman() {
        // Pacman can always go in the exact opposite direction
        if (Board.dRequest.flip() == dPacman) {
            dPacmanView = dPacman = Board.dRequest;
        }
        if (onBlock(pPacman)) {
            int loc = pointToLocation(pPacman);
            int l = Board.map[loc];
            if ((l & 16) != 0) {//左から5番目のビットが少なくとも1が立ってたら、つまりまだ食料を食べていなかったら
                // eat dot
                Board.map[loc] = l & 15;//5桁目だけを0に変える
                Board.score++;
            }
            // turn
            if (Board.dRequest != Direction.O) {
                if (!((Board.dRequest == Direction.L && (l & 1) != 0)//（左に行きたい、かついけない）ではない、つまりどこかに行ける時
                        || (Board.dRequest == Direction.U && (l & 2) != 0)
                        || (Board.dRequest == Direction.R && (l & 4) != 0)
                        || (Board.dRequest == Direction.D && (l & 8) != 0))) {
                            dPacmanView = dPacman = Board.dRequest;
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
        // System.out.println(pPacman.x);
    }  
    // pacman
    private static final int[] ANIMATION_STATES = {0, 0, 1, 1, 2, 2, 3, 3, 2, 2, 1, 1};

    /**
     * Pacman's image.
    */
    private static final Image PACMAN_IMAGE_NEUTRAL = load("pacman.png");
    static final Image[] PACMAN_IMAGE_L = {PACMAN_IMAGE_NEUTRAL, load("left1.png"), load("left2.png"), load("left3.png")};
    private static final Image[] PACMAN_IMAGE_U = {PACMAN_IMAGE_NEUTRAL, load("up1.png"), load("up2.png"), load("up3.png")};
    private static final Image[] PACMAN_IMAGE_R = {PACMAN_IMAGE_NEUTRAL, load("right1.png"), load("right2.png"), load("right3.png")};
    private static final Image[] PACMAN_IMAGE_D = {PACMAN_IMAGE_NEUTRAL, load("down1.png"), load("down2.png"),  load("down3.png")};
    // pacman
    private int animationIndex = 0;

    private static Image load(String filename) {
        URL url = Board.class.getClassLoader().getResource("images/" + filename);
        assert url != null;
        return new ImageIcon(url).getImage();
    }
    /**
     * Updates Pacman's animation state.->packman
     */
    void updateAnimationState() {
        animationIndex = (animationIndex + 1) % ANIMATION_STATES.length;
    }

    /**
     * Draws Pacman.-> packman
     */
    void drawPacman(Graphics2D g) {
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

}
