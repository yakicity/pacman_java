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
public class Ghost extends JPanel{
    static final int MAX_GHOSTS = 12;
    static final int[] VALID_SPEEDS = {1, 2, 3, 4, 6, 8};
    final Point[] pGhost = new Point[MAX_GHOSTS];
    final Direction[] dGhost = new Direction[MAX_GHOSTS];
    final int[] ghostSpeed = new int[MAX_GHOSTS];
    private static final Logger log = LoggerFactory.getLogger(Board.class);
    int numGhosts = 6; 
    
    int ghostSpeedRank = 3;

    void continueGameGhost(){
        for (int i = 0; i < numGhosts; i++) {
            pGhost[i] = new Point(4 * Board.BLOCK_SIZE, 4 * Board.BLOCK_SIZE);
            dGhost[i] = i % 2 == 0 ? Direction.R : Direction.L;
            ghostSpeed[i] = VALID_SPEEDS[Board.random.nextInt(ghostSpeedRank)];
            log.debug("Ghost {}: speed {}", i, ghostSpeed[i]);
        }
    }
    void moveGhosts() {
        for (int i = 0; i < numGhosts; i++) {
            if (onBlock(pGhost[i])) {
                int loc = pointToLocation(pGhost[i]);
                List<Direction> dirs = new ArrayList<>();
                if ((Board.map[loc] & 1) == 0 && dGhost[i] != Direction.R) {
                    dirs.add(Direction.L);
                }
                if ((Board.map[loc] & 2) == 0 && dGhost[i] != Direction.D) {
                    dirs.add(Direction.U);
                }
                if ((Board.map[loc] & 4) == 0 && dGhost[i] != Direction.L) {
                    dirs.add(Direction.R);
                }
                if ((Board.map[loc] & 8) == 0 && dGhost[i] != Direction.U) {
                    dirs.add(Direction.D);
                }
                if (dirs.isEmpty()) {
                    if ((Board.map[loc] & 15) == 15) {
                        dGhost[i] = Direction.O;
                    } else {
                        dGhost[i] = dGhost[i].flip();
                    }
                } else {
                    int n = Board.random.nextInt(dirs.size());
                    dGhost[i] = dirs.get(n);
                }
            }
            pGhost[i].x += dGhost[i].dx * ghostSpeed[i];
            pGhost[i].y += dGhost[i].dy * ghostSpeed[i];
        }
    }
    private boolean onBlock(Point p) {
        return p.x % Board.BLOCK_SIZE == 0 && p.y % Board.BLOCK_SIZE == 0;
    }

    private int pointToLocation(Point p) {
        return (p.y / Board.BLOCK_SIZE) * Board.MAP_SIZE.width + (p.x / Board.BLOCK_SIZE);
    }
    private static Image load(String filename) {
        URL url = Board.class.getClassLoader().getResource("images/" + filename);
        assert url != null;
        return new ImageIcon(url).getImage();
    }
    private static final Image GHOST_IMAGE = load("ghost.png");
    /**
     * Draws ghosts.
     */
    void drawGhosts(Graphics2D g) {
        for (int i = 0; i < numGhosts; i++) {
            g.drawImage(GHOST_IMAGE, pGhost[i].x + 1, this.pGhost[i].y + 1, this);
        }
    }
}
    