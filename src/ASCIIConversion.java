import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgproc.Imgproc;

import javax.imageio.*;
import java.awt.image.*;
import java.io.*;
import java.awt.*;
import java.nio.CharBuffer;


/**
 * A class with static methods used in the process of converting an image of a format such as .jpg, .png, and .bmp to art made with ASCII characters.
 * <p>
 * Made to be utilized by the ASCIIConverterFrame.java class. imageToASCII generates a two-dimensional character array, and that character array is
 * written to a file using writeASCIIToFile.
 *
 * @author Christopher Medlin
 * @author Ivan Kenevich
 * @version 1.0
 * @since 0.2
 */

public class ASCIIConversion {
    //Declaration of large, medium, and small character sets.
    private static final char[] LARGE_CHAR_SET = "@B%8&WM#*oahkbdpqwmZO0QLCJUYXzcvunxrjft/|1?-_+~i!lI;:,^`'. ".toCharArray(); //61 chars
    private static final char[] MEDIUM_CHAR_SET = "@B8&M#oakbpqmZ0QCJYXcvnxjf/|?-+~!l;:^`. ".toCharArray(); //40 chars
    private static final char[] SMALL_CHAR_SET = "@B&#abqZQJXvxf|-~l:` ".toCharArray(); //21 chars

    public static int pixelsPerChar, charsetSize, fontSize;

    private static int asciiArrayWidth, asciiArrayHeight;

    private static FileReader inputStream;

    /**
     * Converts an image into an array of ASCII symbols.
     * <p>
     * Using bitwise operations, converts the RGB decimal given by BufferedImage.getRGB into
     * 4 integers. Takes the average of the 4 integers, and from that gets the greyscale. Then,
     * the greyscale is converted to a character of varying brightness, and then is put into the
     * character array.
     *
     * @param img The image that is to be converted into ASCII art.
     * @return A character array containing the ASCII art.
     */
    public static char[][] imageToASCII(BufferedImage img) {
        int argb, red, green, blue, imgHeight, imgWidth, blockAverage, stepSize;

        BufferedImage tempImage = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        tempImage.createGraphics().drawImage(img, 0, 0, Color.WHITE, null);
        img = tempImage;
        imgHeight = img.getHeight();
        imgWidth = img.getWidth();
        tempImage = null;

        //Takes into account whether pixel density is odd or even, and essentially crops the ASCII art accordingly.
        int[][] greyscale = new int[imgHeight - (imgHeight % pixelsPerChar)][imgWidth - (imgWidth % pixelsPerChar)];
        char[][] asciiArt = new char[greyscale.length / pixelsPerChar][greyscale[1].length / pixelsPerChar];

        //Step size is a variable used in generateChar()
        //Each stepsize forms a range in which a certain character will be generated if a greyscale value falls within that range.
        //stepsize is calculated outside of generateChar() so that it only has to be calculated once instead of for each character.
        stepSize = 0;
        if (charsetSize == 0) {
            stepSize = 12;
        } else if (charsetSize == 1) {
            stepSize = 6;
        } else if (charsetSize == 2) {
            stepSize = 4;
        }

        //generates greyscale array
        for (int row = 0; row < greyscale.length; row++) {
            for (int col = 0; col < greyscale[1].length; col++) {
                //get rgb of pixel
                argb = img.getRGB(col, row);

                //retrieve red, green, and blue integers from encoded argb decimal
                //using bitwise operation
                red = (argb >> 16) & 0xff;
                green = (argb >> 8) & 0xff;
                blue = argb & 0xff;

                //calculate greyscale by taking average of r, g, and b values`
                greyscale[row][col] = (red + green + blue) / 3;
            }
        }

        //generates ASCII array
        for (int row = 0; row < asciiArt.length; row++) {
            for (int col = 0; col < asciiArt[1].length; col++) {
                //nested for loop for finding the average of a block of greyscale values.
                int total = 0;
                int numberOfPixels = 0;
                for (int blockRow = row * pixelsPerChar; blockRow < (row * pixelsPerChar) + pixelsPerChar; blockRow++) {
                    for (int blockCol = col * pixelsPerChar; blockCol < ((col * pixelsPerChar) + pixelsPerChar); blockCol++) {
                        //adds greyscale value to total variable
                        total += greyscale[blockRow][blockCol];
                    }
                }

                //generate character for average of block
                blockAverage = total / (pixelsPerChar * pixelsPerChar);
                asciiArt[row][col] = generateChar(blockAverage, stepSize);
            }
        }

        return asciiArt;
    }


    /**
     * Generates a character of varying brigtness based on a greyscale value.
     * <p>
     * Does so by checking which range the greyscale value falls in according to stepsize.
     *
     * @param greyscale Greyscale value to generate character based on.
     * @param stepSize  Size of each iteration by how many greyscale values are covered by each character.
     * @return A character of varying darkness which is based on greyscale and stepSize.
     */
    private static char generateChar(int greyscale, int stepSize) {
        char[] charSet;
        //if a character falls into none of the below boolean statements, that means it is a very bright
        //greyscale value and the character remains a space.
        char character = ' ';

        if (stepSize == 4) {
            charSet = LARGE_CHAR_SET;
        } else if (stepSize == 6) {
            charSet = MEDIUM_CHAR_SET;
        } else if (stepSize == 12) {
            charSet = SMALL_CHAR_SET;
        } else {
            charSet = MEDIUM_CHAR_SET;
        }

        int charIndex = 0;
        for (int i = 0; i < charSet.length * stepSize; i += stepSize) {
            if (greyscale <= i) {
                character = charSet[charIndex];
                break;
            }

            charIndex++;
        }

        return character;
    }

    /**
     * Writes ASCII art represented in a character array to a file.
     * <p>
     * Uses java.io.PrintWriter. Prints a row of ascii symbols, and then once it has reached the end of that row, PrintWriter.println().
     *
     * @param asciiArt The ASCII art to be written to a file.
     * @param path     The path to the file in which the ASCII art will be written.
     * @throws IOException if the file path specified cannot be found.
     */
    @Deprecated //for now
    public static void writeASCIIToFile(char[][] asciiArt, String path) throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(path, true));

        for (int row = 0; row < asciiArt.length; row++) {
            for (int col = 0; col < asciiArt[1].length; col++) {
                try {
                    writer.print(asciiArt[row][col] + " ");
                    if (col == asciiArt[row].length - 1) {
                        writer.println();
                    }
                } finally {
                }

            }
        }
        writer.close();
    }

    public static void writeASCIIFrameToFile(char[][] asciiArt, String path) throws IOException {
        FileWriter outputStream = null;

        //System.out.println(".length"+asciiArt.length);
        //System.out.println("[0].length"+asciiArt[0].length);

        try {
            outputStream = new FileWriter(path, true); // true is for appending
            for (char[] row : asciiArt) {
                for (char ch : row) {
                    outputStream.write(ch);
                }
            }
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    public static void openInputStream(String path) {
        inputStream = null;

        try {
            inputStream = new FileReader(new File(path));

        } catch (IOException e) {

        }
    }

    public static char[][] readASCIIFrameFromFile() throws IOException {

        char[][] result = new char[160][213];

        for (int row = 0; row < result.length; row++) {
            for (int col = 0; col < result[0].length; col++) {
                int c;
                if ((c = inputStream.read()) != -1) {
                    result[row][col] = (char) c;
                }
                else {
                    inputStream.close();
                }
            }
        }

        return result;
    }

    /**
     * Writes ASCII art represented in a character array to a BigBufferedImage object.
     * <p>
     * Using a Graphics2D object, writeASCIIToImage() first converts the character array to a string array to account for lack of a Graphics2D.drawChar()
     * method, and then proceeds to individually draw each character on a buffered image using a fixed font size, fixed spacing, and a varying image
     * size.
     *
     * @param asciiArt the ASCII art to be written to a file.
     */
    public static BufferedImage writeASCIIToImage(char[][] asciiArt) {
        int spacing = fontSize + 2;
        BufferedImage img = new BufferedImage(asciiArt[0].length * spacing, (asciiArt.length * spacing) + spacing, BufferedImage.TYPE_INT_RGB);

        //    img = BigBufferedImage.create(new File(tempPath), asciiArt[0].length * spacing, (asciiArt.length * spacing) + spacing, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2 = img.createGraphics();
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Meme font", Font.PLAIN, fontSize));
        g2.fillRect(0, 0, asciiArt[0].length * spacing, (asciiArt.length * spacing) + spacing);
        g2.setColor(Color.BLACK);
        for (int i = 0; i < asciiArt.length; i++) {
            for (int j = 0; j < asciiArt[0].length; j++) {
                g2.drawString(String.valueOf(asciiArt[i][j]), j * spacing, (i * spacing) + spacing);
            }
        }
        return img;
    }


    @Deprecated
    public static void drawASCIIFrameUsingBytes(Mat m, Graphics g) {
        Imgproc.cvtColor(m, m, Imgproc.COLOR_RGB2GRAY); // converting the frame to greyscale

        byte[] data = new byte[m.width() * m.height()];
        m.get(0, 0, data);

        int spacing = fontSize + 2;

        g.clearRect(0, 0, 1920, 1080);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Meme font", Font.PLAIN, fontSize));

        for (int row = 0; row < m.height(); row += pixelsPerChar) {
            for (int col = 0; col < m.width(); col += pixelsPerChar) {
                int total = 0;
                for (int n = 0; n < pixelsPerChar; n++) {
                    total += data[row * m.width() + col + n];
                }
                int average = total / pixelsPerChar;
                g.drawString(String.valueOf(generateChar(average, 6)), (col / pixelsPerChar) * spacing, ((row / pixelsPerChar) * spacing) + spacing);
            }
        }
    }
}
