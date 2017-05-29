import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * Created by Ivan on 5/26/2017.
 */
public class VideoPanel extends JPanel implements ActionListener, KeyListener, MouseMotionListener {
    public VideoCapture capture;
    private BufferedImage image;
    private Timer t;
    private int width, height, imageX, imageY, pixelsPerChar, fontSize, mouseX, mouseY, previousMouseX, previousMouseY;

    public VideoPanel(int width, int height) {
        super(null);
        this.width = width;
        this.height = height;
        imageX = 0;
        imageY = 0;
        pixelsPerChar = 4;
        fontSize = 6;

        setFocusable(true);
        addKeyListener(this);
        addMouseMotionListener(this);

        beginCapture(0);
        t = new Timer(33, this);
        t.start();
    }

    public void actionPerformed(ActionEvent e) {
        captureFrame();
        //repaint();
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
        repaint();
    }

    private BufferedImage matToBufferedImage(Mat m) {
        BufferedImage gray = new BufferedImage(m.width(), m.height(), BufferedImage.TYPE_3BYTE_BGR);

        byte[] data = ((DataBufferByte) gray.getRaster().getDataBuffer()).getData();
        m.get(0, 0, data);

        return gray;
    }

    public void paintComponent(Graphics g) {
        g.clearRect(0, 0, width, height);
        if (image!=null) {
            image = ASCIIConversion.writeASCIIToImage(ASCIIConversion.imageToASCII(image, pixelsPerChar, 2), fontSize);
        }
        g.drawImage(image, imageX, imageY, null);
//        Mat m = new Mat();
//        capture.read(m);
//        ASCIIConversion.drawASCIIFrameUsingBytes(m,g,4,4,3);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.isAltDown()) {
            if (e.getKeyCode() == KeyEvent.VK_UP && pixelsPerChar>1) {
                pixelsPerChar--;
                imageX=0;
                imageY=0;
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                pixelsPerChar++;
                imageX=0;
                imageY=0;
            } else if (e.getKeyCode() == KeyEvent.VK_LEFT && fontSize>1) {
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
        //if ((e.getModifiersEx() & (InputEvent.BUTTON1_DOWN_MASK)) == InputEvent.BUTTON1_DOWN_MASK) {
            float dx = e.getX() - previousMouseX;
            float dy = e.getY() - previousMouseY;
            previousMouseX = e.getX();
            previousMouseY = e.getY();
            imageX += dx;
            imageY += dy;
        //}
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        previousMouseX = mouseX;
        previousMouseY = mouseY;
    }
}
