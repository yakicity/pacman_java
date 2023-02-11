package pacman;

import java.awt.EventQueue;
import javax.swing.JFrame;

public class Pacman extends JFrame {

    public final Board board;

    public Pacman() {
        this.board = new Board();
        add(board);
        setTitle("Pacman");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(Board.SCREEN_SIZE.width, Board.SCREEN_SIZE.height);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        String replayFile = args.length > 0 ? args[0] : null;
        EventQueue.invokeLater(() -> {
            Pacman pacman = new Pacman();
            pacman.setVisible(true);
            if (replayFile != null) {
                pacman.board.replay(replayFile);
            }
        });
    }
}
