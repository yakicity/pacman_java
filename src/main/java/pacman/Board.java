package pacman;
import javax.swing.JPanel;
import java.awt.*;

public class Board extends JPanel{

    private int numOfArr = 0;
    private final int Max_Arr;
    private static final Color DOT_COLOR = new Color(192, 192, 0);
    private static final Color SUPER_DOT_COLOR = new Color(0, 192, 192);
    private static final Color WALL_COLOR = new Color(5, 100, 5);
    static final int BLOCK_SIZE = 24;
    /**
     * Logical map size: number of blocks.
     */
    static final Dimension MAP_SIZE = new Dimension(15, 15);
    /**
     * Board (physical map on screen) size.
     */
    static final Dimension BOARD_SIZE = new Dimension(MAP_SIZE.width * BLOCK_SIZE, MAP_SIZE.height * BLOCK_SIZE);
    /**
     * (Board + bottom status space) size.
     */
    static final Dimension SCREEN_SIZE = new Dimension(BOARD_SIZE.width, BOARD_SIZE.height + 55);

    static final int[] map = new int[MAP_SIZE.width * MAP_SIZE.height];

    private final int[][] ARRANGEMENTS = {
        {
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2,
            1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2,
            1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2,
            1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2,
            1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 2,
            1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 2,
            1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 2,
            0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 2,
            0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 2,
            0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 2,
            0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 2,
            0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2,
            0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2,
            0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 2,
        },
        {
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
        },
        {
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1,
            1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1,
            1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1,
            0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1,
            0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1,
            0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1,
            0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1,
            0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1,
            0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1,
            0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1,
        }    
    };    

    Board(){
        Max_Arr = ARRANGEMENTS.length-1;
    }

    void initMap() {//レベルからMAPをつくる
        for (int i = 0; i < ARRANGEMENTS[numOfArr].length; i++) {
            int x = i % MAP_SIZE.width;
            int y = i / MAP_SIZE.width;
            int c = 0;
            if (x == 0 || (ARRANGEMENTS[numOfArr][i] == 1 && ARRANGEMENTS[numOfArr][i - 1] == 0)) {//一番左はし、もしくは今いる位置の左隣は壁なら
                c |= 1; // Lにはいけないという意味で、右から一番目のビットを1立てる
            }
            if (i < MAP_SIZE.width || (ARRANGEMENTS[numOfArr][i] == 1 && ARRANGEMENTS[numOfArr][i - MAP_SIZE.width] == 0)) {
                c |= 2; // U
            }
            if (x == MAP_SIZE.width - 1 || (ARRANGEMENTS[numOfArr][i] == 1 && ARRANGEMENTS[numOfArr][i + 1] == 0)) {
                c |= 4; // R
            }
            if (y == MAP_SIZE.height - 1 || (ARRANGEMENTS[numOfArr][i] == 1 && ARRANGEMENTS[numOfArr][i + MAP_SIZE.width] == 0)) {
                c |= 8; // D
            }
            if (ARRANGEMENTS[numOfArr][i] == 1) {
                c |= 16; // dot
            }
            if (ARRANGEMENTS[numOfArr][i] == 2) {
                c |= 32; // super dot
            }
            map[i] = c;
        }
    }


    void drawMaze(Graphics2D g) {
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
     * Goes to the next ARRANGEMENT.
     */
    void changeArrangement() {
        if(numOfArr< Max_Arr){
            numOfArr += 1;
        }
    }
}
