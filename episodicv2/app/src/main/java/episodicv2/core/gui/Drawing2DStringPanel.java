/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package episodicv2.core.gui;

import episodicv2.core.configuration.Configuration;
import episodicv2.core.entities.CObject;
import episodicv2.core.t2string.OccupancyGrid;
import episodicv2.core.t2string.T2DString;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 *
 * @author Luis
 */
public class Drawing2DStringPanel extends JPanel implements MouseListener {

    private int columns = 5;
    private int rows = 5;
    private int dx = 0;
    private int dy = 0;
    private int classToDraw = 1;
    private BufferedImage imageToDraw = null;

    private ArrayList<BufferedImage> images;
    private ArrayList<int[]> positions;

    private int imageMatrix[][];
    private ArrayList<CObject> objectList;

    public Drawing2DStringPanel(int rows, int columns) {

        this.rows = rows;
        this.columns = columns;

        images = new ArrayList<>();
        positions = new ArrayList<>();
        objectList = new ArrayList<>();

        imageMatrix = new int[rows][columns];

        dx = getWidth() / columns;
        dy = getHeight() / rows;

        setBackground(Color.WHITE);

        addMouseListener(this);
    }

    public int[][] getImage() {
        return imageMatrix;
    }

    public void setImage(int imgM[][]) {
        clear();

        imageMatrix = imgM;

        for (int i = 0; i < imageMatrix.length; i++) {
            for (int j = 0; j < imageMatrix[0].length; j++) {

                if (imageMatrix[i][j] != 0) {

                    classToDraw = imageMatrix[i][j];
                    BufferedImage img = loadImage("dataset/imgs/obj" + classToDraw + "__0.png");

                    images.add(img);
                    positions.add(new int[]{j, i});
                }

            }
        }

        repaint();

    }

    public int[][] randomImage() {

        clear();

        Stack<int[]> positionsList = new Stack();
        Stack<Integer> imageList = new Stack();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                positionsList.add(new int[]{i, j});
            }
        }

        for (int i = 1; i <= 100; i++) {
            imageList.add(i);
        }

        Collections.shuffle(positionsList);
        Collections.shuffle(imageList);

        Random rand = new Random();

        int objects = 3 + rand.nextInt((rows * columns) / 2);

        for (int i = 0; i < objects; i++) {

            classToDraw = imageList.pop();
            BufferedImage img = loadImage("dataset/imgs/obj" + classToDraw + "__0.png");
            int pos[] = positionsList.pop();

            images.add(img);
            positions.add(new int[]{pos[0], pos[1]});
            imageMatrix[pos[1]][pos[0]] = classToDraw;
        }

        //debugMatrix();
        repaint();

        return imageMatrix;

    }

    public void clear() {
        positions.clear();
        images.clear();
        imageMatrix = new int[rows][columns];
        objectList.clear();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        dx = getWidth() / columns;
        dy = getHeight() / rows;

        g.setColor(Color.red);

        for (int i = 0; i < columns; i++) {
            g.drawLine(i * dx, 0, i * dx, getHeight());
        }

        for (int i = 0; i < rows; i++) {
            g.drawLine(0, i * dy, getWidth(), i * dy);
        }

        for (int i = 0; i < images.size(); i++) {

            int px = positions.get(i)[0];
            int py = positions.get(i)[1];

            g.drawImage(images.get(i), px * dx, py * dy, dx, dy, null);
        }

    }

    private BufferedImage loadImage(String imagePath) {
        try {
            BufferedImage img = ImageIO.read(new File(imagePath));

            return img;
        } catch (IOException ex) {
            Logger.getLogger(Drawing2DStringPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void mouseClicked(java.awt.event.MouseEvent e) {

        int x = e.getX();
        int y = e.getY();

        int px = x / dx;
        int py = y / dy;

        BufferedImage img = imageToDraw;//loadImage("dataset/imgs/obj" + classToDraw + "__0.png");

        images.add(img);
        positions.add(new int[]{px, py});

        imageMatrix[py][px] = classToDraw;

        objectList.add(new CObject(classToDraw, classToDraw, x, y, "", "", 0));

        //debugMatrix();

        repaint();

    }

    public String get2DStringPattern() {

        OccupancyGrid occupancyGrid = T2DString.createOccupancyGrid(
                objectList,
                Configuration.IMAGE_WIDTH,
                Configuration.IMAGE_HEIGHT,
                Configuration.GRID_COLUMNS_X,
                Configuration.GRID_ROWS_Y);

        String pattern = T2DString.create2DStringPattern(occupancyGrid,
                Configuration.IMAGE_WIDTH,
                Configuration.IMAGE_HEIGHT,
                Configuration.GRID_COLUMNS_X,
                Configuration.GRID_ROWS_Y);
        
        return pattern;
    }

    @Override
    public void mousePressed(java.awt.event.MouseEvent e) {
    }

    @Override
    public void mouseReleased(java.awt.event.MouseEvent e) {
    }

    @Override
    public void mouseEntered(java.awt.event.MouseEvent e) {
    }

    @Override
    public void mouseExited(java.awt.event.MouseEvent e) {
    }

    public void setClassDraw(int classToDraw) {
        this.classToDraw = classToDraw;
    }

    public void setImageToDraw(int classToDraw, BufferedImage imageToDraw) {
        this.imageToDraw = imageToDraw;
        this.classToDraw = classToDraw;
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
}
