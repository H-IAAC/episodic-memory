/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package episodicv2.core.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JComponent;

/**
 *
 * @author Luis
 */
public class ImageComponent extends JComponent {

    private int row, col, width, height;
    private Image bg;
    private float ratio = 1.0f;
    private int marginX = 10;
    private int marginY = 10;

    //Metadata
    private boolean showInfo = false;
    private int id, degrees;

    //
    private int originalWidth = 0;
    private int originalHeight = 0;

    public ImageComponent() {
    }

    public ImageComponent(String imagePath) {
        loadImage(imagePath);
    }

    public ImageComponent(BufferedImage image, int width, int height) {
        this.bg = image;
        this.width = image.getWidth();
        this.height = image.getHeight();

        originalWidth = width;
        originalHeight = height;

        width -= marginX;
        height -= marginY;

        setRatio(width, height);
    }

    public ImageComponent(String imagePath, int width, int height) {
        loadImage(imagePath);

        originalWidth = width;
        originalHeight = height;

        width -= marginX;
        height -= marginY;

        setRatio(width, height);
    }

    public void setImage(String imagePath) {

        loadImage(imagePath);

        setWidthHeight(getWidth(), getHeight());

        repaint();

    }

    public void setImage(BufferedImage image) {

        this.bg = image;
        this.width = image.getWidth();
        this.height = image.getHeight();

        setWidthHeight(getWidth(), getHeight());

        repaint();

    }

    public void setWidthHeight(int width, int height) {

        width -= marginX;
        height -= marginY;

        setRatio(width, height);
    }

    private void setRatio(int width, int height) {
        if (width > height) {
            ratio = (float) height / width;
            this.width = (int) (width * ratio);
            this.height = height;
        } else {
            ratio = (float) width / height;
            this.width = width;
            this.height = (int) (height * ratio);
        }
    }

    private void loadImage(String imagePath) {
        try {
            BufferedImage img = ImageIO.read(new File(imagePath));
            this.bg = img;
            this.width = img.getWidth();
            this.height = img.getHeight();

        } catch (IOException ex) {
            Logger.getLogger(ImageComponent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean isShowInfo() {
        return showInfo;
    }

    public void showInfo(boolean showInfo) {
        this.showInfo = showInfo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int y) {
        this.row = y;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int x) {
        this.col = x;
    }

    public int getDegrees() {
        return degrees;
    }

    public void setDegrees(int degrees) {
        this.degrees = degrees;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(originalWidth, originalHeight);
    }

    @Override
    protected void paintComponent(Graphics grphcs) {
        super.paintComponent(grphcs);

        int offsetX = (getWidth() - this.width) / 2;
        int offsetY = (getHeight() - this.height) / 2;

        grphcs.setColor(new Color(25, 25, 25));
        grphcs.fillRect(0, 0, getWidth(), getHeight());

        grphcs.drawImage(bg, offsetX, offsetY, this.width, this.height, this);

    }
}
