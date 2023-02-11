package pacman;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;

public class Recorder {
    private Writer writer;

    int frame = 0;

    public ActionListener getTimerRecorder(final ActionListener delegatee) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame++;
                delegatee.actionPerformed(e);
            }
        };
    }

    public KeyAdapter getKeyRecorder(final KeyAdapter delegatee) {
        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                log(String.valueOf(key));
                delegatee.keyPressed(e);
            }
        };
    }

    public void start(String filename) {
        try {
            writer = new FileWriter(filename);
            frame = 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            log("stop");
            writer.close();
            writer = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void log(String event) {
        log(this.frame, event);
    }

    public void log(int frame, String event) {
        if (writer != null) {
            try {
                writer.write(frame + "," + event + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
