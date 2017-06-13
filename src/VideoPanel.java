import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;

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
            mouseX, mouseY, previousMouseX, previousMouseY;

    private boolean isNormalMode;

    public VideoPanel(int width, int height) {
        super(null);

        this.width = width;
        this.height = height;

        // This combination looks decent as a starting setting on most screens
        ASCIIConversion.pixelsPerChar = 3;
        ASCIIConversion.charsetSize = 2;
        ASCIIConversion.fontSize = 6;

        setupGUI();

        // Connects to the default device, INCREMENT THE NUMBER IF YOU WISH TO USE ANOTHER CAMERA
        beginCapture(1);
        //ASCIIConversion.openInputStream("C:\\Users\\Ivan\\Desktop\\out.txt");


        Timer t = new Timer(1000 / DESIRED_FPS, this);
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
        if (!isNormalMode) {
            image = convertFrameToASCII(image);
        }
    }

    private BufferedImage convertFrameToASCII(BufferedImage img) {
        if (img != null) {
            char[][] asciiArr = ASCIIConversion.imageToASCII(img);
            image = ASCIIConversion.writeASCIIToImage(asciiArr);
//            try {
//                ASCIIConversion.writeASCIIFrameToFile(asciiArr,"C:\\Users\\Ivan\\Desktop\\out.txt");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
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
        captureFrame();
        //readImage();
        repaint();
    }

    private void readImage() {
        try {
            char[][] asciiArt = ASCIIConversion.readASCIIFrameFromFile();

            if (asciiArt != null)
                image = ASCIIConversion.writeASCIIToImage(asciiArt);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.isAltDown()) {
            if (e.getKeyCode() == KeyEvent.VK_UP && ASCIIConversion.pixelsPerChar > 1) {
                ASCIIConversion.pixelsPerChar--;
                imageX = 0;
                imageY = 0;
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                ASCIIConversion.pixelsPerChar++;
                imageX = 0;
                imageY = 0;
            } else if (e.getKeyCode() == KeyEvent.VK_LEFT && ASCIIConversion.fontSize > 1) {
                ASCIIConversion.fontSize--;
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                ASCIIConversion.fontSize++;
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

    private void setupGUI() {
        imageX = 0;
        imageY = 0;

        isNormalMode = true; // NORMAL video isNormalMode by default

        JButton modeButton = new JButton("ASCII");
        modeButton.setSize(84, 26); // the preferred size for the "NORMAL" text
        modeButton.addActionListener(e -> {
            if (isNormalMode)
                modeButton.setText("NORMAL");
            else
                modeButton.setText("ASCII");
            isNormalMode = !isNormalMode;
            imageX = 0;
            imageY = 0;
        });
        modeButton.setFocusable(false); // so that the keyboard focus doesn't switch to the button when it's pressed
        add(modeButton);


        setFocusable(true);
        addKeyListener(this);
        addMouseMotionListener(this);
    }
}
