import org.opencv.core.Core;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {

    // loads opencv c/c++ sources
    static {System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}

    public static void main(String[] args) {
        JFrame frame = new JFrame("ASCII video");
        frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());

        VideoPanel vp = new VideoPanel(frame.getWidth(), frame.getHeight());

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // The following method call allows the program to do something extra when user closes the JFrame.
        // In this case, it ends the video capture.
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                vp.endCapture();
            }
        });

        frame.add(vp);
        frame.setVisible(true);
    }
}
