/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package episodicv2.core.gui;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

/**
 *
 * @author Luis Martin
 */
public class PercentLevelComponent extends JPanel {

    private float percent = 0.0f;
    private boolean isPositive = false;

    public PercentLevelComponent() {
        setBackground(Color.WHITE);
    }

    public PercentLevelComponent(double percent, boolean isPositive) {
        this.percent = (float) percent;
        this.isPositive = isPositive;
        setBackground(Color.WHITE);
    }
    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = (float) percent;
        repaint();
        invalidate();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.WHITE);
        
        float pColor = (this.percent * 100.0f);
        float base = (155.0f + pColor)/255.0f;
        
        Color currentColor;
        
        if(isPositive){
            currentColor = new Color(80.0f/255.0f, base, 80.0f/255.0f);
        }else{
            currentColor = new Color(base, 80.0f/255.0f, 80.0f/255.0f);
        }
        
        
        g.setColor(currentColor);
        
        double pW = getWidth()*percent;
        
        g.fillRect(0, 0, (int) pW, getHeight());
        
        g.setColor(Color.BLACK);
        
        String label = String.format("%.2f", percent);
        
        int labelWidth = g.getFontMetrics().stringWidth(label);
        
        g.drawString(label, (getWidth()/2) - (labelWidth/2), 15);
                

    }

}
