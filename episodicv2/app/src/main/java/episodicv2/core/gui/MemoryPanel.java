/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package episodicv2.core.gui;

import episodicv2.core.configuration.Configuration;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 *
 * @author Luis
 */
public class MemoryPanel extends JPanel {

    private String PATH = Configuration.STORAGE_BASE_PATH + "/itc/features/" + Configuration.ITC_FEATURES_BASEFILENAME;
    private final int MAX_FEATURES = 81;
    //http://phrogz.net/css/distinct-colors.html
    private Color[] classColors = new Color[]{new Color(0, 242, 40), new Color(61, 242, 236), new Color(0, 77, 153), new Color(151, 143, 191), new Color(236, 61, 242), new Color(153, 77, 105), new Color(0, 179, 30), new Color(41, 166, 162), new Color(0, 26, 51), new Color(15, 0, 51), new Color(188, 96, 191), new Color(204, 153, 172), new Color(0, 128, 21), new Color(64, 128, 125), new Color(32, 80, 128), new Color(85, 45, 179), new Color(177, 134, 179), new Color(89, 67, 75), new Color(0, 51, 9), new Color(153, 204, 202), new Color(128, 191, 255), new Color(141, 108, 217), new Color(140, 0, 126), new Color(166, 41, 70), new Color(191, 255, 202), new Color(57, 212, 230), new Color(51, 77, 102), new Color(41, 32, 64), new Color(51, 13, 47), new Color(76, 19, 33), new Color(61, 242, 115), new Color(64, 210, 255), new Color(0, 47, 128), new Color(111, 0, 255), new Color(242, 0, 186), new Color(140, 105, 113), new Color(77, 102, 84), new Color(35, 116, 140), new Color(134, 150, 179), new Color(66, 29, 115), new Color(115, 29, 95), new Color(255, 0, 26), new Color(0, 89, 39), new Color(38, 68, 77), new Color(48, 81, 191), new Color(101, 70, 140), new Color(166, 0, 105), new Color(178, 0, 18), new Color(38, 153, 88), new Color(105, 132, 140), new Color(22, 38, 89), new Color(43, 0, 77), new Color(255, 128, 208), new Color(102, 0, 10), new Color(153, 204, 175), new Color(0, 129, 204), new Color(115, 142, 230), new Color(164, 61, 242), new Color(179, 89, 146), new Color(51, 0, 5), new Color(57, 230, 155), new Color(13, 37, 51), new Color(64, 83, 255), new Color(68, 57, 77), new Color(102, 0, 51), new Color(217, 54, 70), new Color(29, 115, 89), new Color(108, 177, 217), new Color(57, 63, 115), new Color(118, 96, 128), new Color(51, 0, 26), new Color(255, 128, 140), new Color(19, 77, 59), new Color(163, 197, 217), new Color(191, 198, 255), new Color(149, 0, 179), new Color(77, 38, 57), new Color(102, 51, 56), new Color(0, 217, 181), new Color(38, 46, 51), new Color(7, 0, 217), new Color(70, 38, 77), new Color(255, 0, 94), new Color(51, 38, 40), new Color(26, 51, 47), new Color(0, 127, 255), new Color(23, 0, 140), new Color(244, 191, 255), new Color(217, 54, 114)};
    private HashMap<Integer, BufferedImage> classFeatures;

    private int columns = 5;
    private int rows = 5;
    private int dx = 0;
    private int dy = 0;
    private int classToDraw = 1;
    private boolean colorPerClass = true;

    private ArrayList<BufferedImage> images;
    private ArrayList<int[]> positions;
    private ArrayList<Integer>[][] imageMatrix;

    public MemoryPanel(ArrayList<Integer>[][] imageMatrix) {

        columns = imageMatrix[0].length;
        rows = imageMatrix.length;

        this.imageMatrix = imageMatrix;

        images = new ArrayList<>();
        positions = new ArrayList<>();

        dx = getWidth() / columns;
        dy = getHeight() / rows;

        setBackground(Color.WHITE);

    }

    public MemoryPanel(int columns, int rows) {

        this.columns = columns;
        this.rows = rows;

        images = new ArrayList<>();
        positions = new ArrayList<>();

        dx = 0;
        dy = 0;

        setBackground(Color.WHITE);

        classFeatures = new HashMap<>();
        loadImages();

    }

    private void loadImages() {
        try {

            for (int i = 1; i <= MAX_FEATURES; i++) {

                BufferedImage features = ImageIO.read(new File(PATH + "" + i + ".jpg"));

                classFeatures.put(i, features);

            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void setImage(ArrayList<Integer>[][] imgM) {
        clear();

        imageMatrix = imgM;
        columns = imageMatrix[0].length;
        rows = imageMatrix.length;

        images = new ArrayList<>();
        positions = new ArrayList<>();

        dx = getWidth() / columns;
        dy = getHeight() / rows;

        repaint();

    }

    public void clear() {
        positions.clear();
        images.clear();
        imageMatrix = new ArrayList[rows][columns];
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if(imageMatrix == null)return;

        dx = getWidth() / columns;
        dy = getHeight() / rows;

        g.setColor(Color.red);

        for (int i = 0; i < columns; i++) {
            g.drawLine(i * dx, 0, i * dx, getHeight());
        }

        for (int i = 0; i < rows; i++) {
            g.drawLine(0, i * dy, getWidth(), i * dy);
        }

        g.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
        g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);

        for (int i = 0; i < imageMatrix.length; i++) {
            for (int j = 0; j < imageMatrix[0].length; j++) {

                if (imageMatrix[i][j] == null) {
                    continue;
                }

                for (int k = 0; k < imageMatrix[i][j].size(); k++) {

                    int classId = imageMatrix[i][j].get(k);

                    if (isColorPerClass()) {
                        g.setColor(classColors[classId]);
                    } else {
                        g.setColor(classColors[0]);
                    }

                    g.fillRect(j * dx, i * dy, dx, dy);
                    g.drawImage(classFeatures.get(classId), j*dx, i*dy, dx, dy, this);//px * dx, py * dy, dx, dy, null);

                }

            }

        }

    }

    private BufferedImage loadImage(String imagePath) {
        try {
            BufferedImage img = ImageIO.read(new File(imagePath));
            return img;
        } catch (IOException ex) {
            Logger.getLogger(MemoryPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void debugMatrix() {
        System.out.println("Matrix");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                System.out.print("[" + imageMatrix[i][j] + "]");
            }
            System.out.println("");
        }
    }

    public boolean isColorPerClass() {
        return colorPerClass;
    }

    public void setColorPerClass(boolean colorPerClass) {
        this.colorPerClass = colorPerClass;
    }
}
