package pacman;

import java.awt.*;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

abstract class Character extends JPanel{
    boolean onBlock(Point p) {
        return p.x % Board.BLOCK_SIZE == 0 && p.y % Board.BLOCK_SIZE == 0;
    }
    int pointToLocation(Point p) {
        return (p.y / Board.BLOCK_SIZE) * Board.MAP_SIZE.width + (p.x / Board.BLOCK_SIZE);
    } 
    static Image load(String filename) {
        URL url = Board.class.getClassLoader().getResource("images/" + filename);
        assert url != null;
        return new ImageIcon(url).getImage();
    }

    abstract void draw(Graphics2D g);
    abstract void move();
    abstract void continueGame(); 
}
