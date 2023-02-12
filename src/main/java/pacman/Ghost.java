package pacman;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Ghost extends Character{
    static final int MAX_GHOSTS = 12;
    static final int[] VALID_SPEEDS = {1, 2, 3, 4, 6, 8};
    final Point[] pGhost = new Point[MAX_GHOSTS];
    final Direction[] dGhost = new Direction[MAX_GHOSTS];
    final int[] ghostSpeed = new int[MAX_GHOSTS];
    private static final Logger log = LoggerFactory.getLogger(GameManager.class);
    int numGhosts = 6; 
    int ghostSpeedRank = 3;

    @Override
    void continueGame(){
        for (int i = 0; i < numGhosts; i++) {
            pGhost[i] = new Point(4 * Board.BLOCK_SIZE, 4 * Board.BLOCK_SIZE);
            dGhost[i] = i % 2 == 0 ? Direction.R : Direction.L;
            ghostSpeed[i] = VALID_SPEEDS[GameManager.random.nextInt(ghostSpeedRank)];
            log.debug("Ghost {}: speed {}", i, ghostSpeed[i]);
        }
    }
    @Override
    void move() {
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
                    int n = GameManager.random.nextInt(dirs.size());
                    dGhost[i] = dirs.get(n);
                }
            }
            pGhost[i].x += dGhost[i].dx * ghostSpeed[i];
            pGhost[i].y += dGhost[i].dy * ghostSpeed[i];
        }
    }

    private static final Image GHOST_IMAGE = load("ghost.png");
    /**
     * Draws ghosts.
     */
    @Override
    void draw(Graphics2D g) {
        for (int i = 0; i < numGhosts; i++) {
            g.drawImage(GHOST_IMAGE, pGhost[i].x + 1, this.pGhost[i].y + 1, this);
        }
    }
    void increaseLevel(){
        if (numGhosts < MAX_GHOSTS) {
            numGhosts++;
        }
        if (ghostSpeedRank < VALID_SPEEDS.length) {
            ghostSpeedRank++;
        }    
    }
}
    