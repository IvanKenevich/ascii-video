import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * This class contains all the GUI components of the program,
 * it handles the timing and display of raw and processed video capture.
 *
 * @author IvanKenevich
 */
public class VideoPanel extends JPanel implements ActionListener, KeyListener, MouseMotionListener {
    public VideoCapture capture;
    private final int DESIRED_FPS = 60;
    private BufferedImage image;

    private int width, height,
            imageX, imageY,
            pixelsPerChar, fontSize,
            mouseX, mouseY, previousMouseX, previousMouseY;

    private boolean mode;
    private JButton modeButton;

    public VideoPanel(int width, int height) {
        super(null);

        this.width = width;
        this.height = height;

        imageX = 0;
        imageY = 0;

        // This combination looks decent as a starting setting on most screens
        pixelsPerChar = 4;
        fontSize = 6;

        mode = true; // NORMAL video mode by default
        modeButton = new JButton("ASCII");
        modeButton.setSize(84,26); // the preferred size for the "NORMAL" text
        modeButton.addActionListener(this);
        modeButton.setFocusable(false); // so that the keyboard focus doesn't switch to the button when it's pressed
        add(modeButton);

        setFocusable(true);
        addKeyListener(this);
        addMouseMotionListener(this);

        // Connects to the default device, INCREMENT THE NUMBER IF YOU WISH TO USE ANOTHER CAMERA
        beginCapture(0);

        Timer t = new Timer(1000/DESIRED_FPS, this);
        t.start();
    }

    public void beginCapture(int device) {
        capture = new VideoCapture(device);
        capture.open(device);
        if (capture.isOpened()) {
            System.out.println("Capture began successfully");
        } else {
            System.out.println("Problem with starting capture");
        }
    }

    public void endCapture() {
        capture.release();
    }

    private void captureFrame() {
        Mat m = new Mat();
        capture.read(m);
        image = matToBufferedImage(m);
        if (!mode) {
            image = convertFrameToASCII(image);
        }
    }

    private BufferedImage convertFrameToASCII(BufferedImage img) {
        if (img != null) {
            image = ASCIIConversion.writeASCIIToImage(ASCIIConversion.imageToASCII(img, pixelsPerChar, 2), fontSize);
        }
        return image;
    }

    private BufferedImage matToBufferedImage(Mat m) {
        BufferedImage img = new BufferedImage(m.width(), m.height(), BufferedImage.TYPE_3BYTE_BGR);

        byte[] data = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
        m.get(0, 0, data);

        return img;
    }

    public void paintComponent(Graphics g) {
        g.clearRect(0, 0, width, height);
        g.drawImage(image, imageX, imageY, this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == modeButton) {
            if (mode)
                modeButton.setText("NORMAL");
            else
                modeButton.setText("ASCII");
            mode = !mode;
            imageX=0;
            imageY=0;
        }
        captureFrame();
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.isAltDown()) {
            if (e.getKeyCode() == KeyEvent.VK_UP && pixelsPerChar > 1) {
                pixelsPerChar--;
                imageX = 0;
                imageY = 0;
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                pixelsPerChar++;
                imageX = 0;
                imageY = 0;
            } else if (e.getKeyCode() == KeyEvent.VK_LEFT && fontSize > 1) {
                fontSize--;
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                fontSize++;
            }
        } else {
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                imageY -= 10;
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                imageY += 10;
            } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                imageX -= 10;
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                imageX += 10;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int dx = e.getX() - previousMouseX;
        int dy = e.getY() - previousMouseY;
        previousMouseX = e.getX();
        previousMouseY = e.getY();
        imageX += dx;
        imageY += dy;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        previousMouseX = mouseX;
        previousMouseY = mouseY;
    }
}
