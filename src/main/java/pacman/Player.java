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

public class Player {
    /**
     * Pacman's speed.
    */
    private static final int PACMAN_SPEED = 6;
    /**
     * Pacman's position.
     */
    public static Point pPacman;
    /**
     * Pacman's (logical and view's) directions.
     */
    public static Direction dPacman;
    /**
     * Pacman's left.
     */
    public static int pacmansLeft;

    public static void continueLevelPacman(){
        pPacman = new Point(7 * Board.BLOCK_SIZE, 11 * Board.BLOCK_SIZE);
        dPacman = Board.dRequest = Direction.O;
        Board.dPacmanView = Direction.L;
    }

    public static boolean onBlock(Point p) {
        return p.x % Board.BLOCK_SIZE == 0 && p.y % Board.BLOCK_SIZE == 0;
    }
    public static int pointToLocation(Point p) {
        return (p.y / Board.BLOCK_SIZE) * Board.MAP_SIZE.width + (p.x / Board.BLOCK_SIZE);
    }
    /**
     * Moves Pacman.　->packman
     */
    public static void movePacman() {
        // Pacman can always go in the exact opposite direction
        if (Board.dRequest.flip() == dPacman) {
            Board.dPacmanView = dPacman = Board.dRequest;
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
                            Board.dPacmanView = dPacman = Board.dRequest;
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
}
