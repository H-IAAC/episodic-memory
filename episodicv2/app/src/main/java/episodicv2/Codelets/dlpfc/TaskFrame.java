/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package episodicv2.Codelets.dlpfc;

import episodicv2.Codelets.storage.itc.ITCStorageHandler;
import episodicv2.core.configuration.Configuration;
import episodicv2.core.entities.Scene;
import episodicv2.core.gui.Drawing2DStringPanel;
import episodicv2.core.gui.ImageComponent;
import episodicv2.core.spike.SpikeObject;
import episodicv2.core.spike.SpikeType;
import episodicv2.core.spike.TopDownParameters;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.DefaultListModel;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author luis_
 */
public class TaskFrame extends javax.swing.JFrame {

    private ArrayList<String> landMarks;
    private String chairsPattern = "<<(57)<(1)<(61:57)<(1)<(57),<<(61:57)(57)<(57)(1)(1),(3)(4)(6)(1)(2)(5),(3)(4)(5)(5)(6)(7),(4)(4)(3)(3)(4)(3)";
    private String carPattern = "<<<(1)<<(3)<(1)(10),<<(1)(3)<(1)<(10),(1)(2)(3)(4),(4)(6)(7)(7),(3)(3)(4)(5)";
    private String umbrellaPattern = "<<<<(1)(26)<<(1),<<<(1)(1)<(26),(1)(3)(2),(5)(5)(7),(4)(5)(4)";

    private String dogPattern = "<<(73)<<<(1)<(70)<<(17),<(70)<(1)(17)<(73),(3)(2)(4)(1),(3)(6)(7)(9),(4)(3)(2)(3)";
    private String bedRoomPattern = "<<(59)<(63)<(64)<<<(60),<(60)<(59)<(63)(64),(4)(1)(2)(3),(3)(4)(5)(8),(3)(4)(4)(2)";
    private String tvRoomPattern = "<(81)<(59)<<<(63)<<(59)<(81),<(81)(81)<<(59)(63)(59),(1)(5)(2)(3)(4),(2)(3)(6)(8)(9),(2)(4)(4)(4)(2)";

    private String officePattern = "<<(64)<<(63)<<(42),<<(42)<(64)(63),(3)(1)(2),(3)(5)(7),(4)(4)(3)"; //REVISAR
    private String winesPattern = "<(41)<(57:41)<(57:41)<(61)(41)<(57:40)<(57:41)<(41),<(41)(61)(41)<(57:41)(57:41)(41)(57:40)(57:41),(1)(6)(12)(2)(3)(4)(5)(7)(8)(9)(10)(11),(2)(3)(3)(4)(4)(5)(5)(6)(6)(7)(7)(8),(2)(3)(3)(3)(3)(2)(3)(3)(3)(3)(3)(2)";
    private String colibriPattern = "<<(14)<(15)<<(15)<<(15)<(15),<<(14)<<<(15)(15)(15)(15),(5)(1)(2)(4)(3),(3)(4)(6)(8)(9),(3)(6)(6)(6)(6)";

    private DefaultListModel<Integer> objectClassModel;
    private ITCStorageHandler itcStorageHandler;
    private ImageComponent thumb;
    private Drawing2DStringPanel drawingPanel;

    private int POSIBLE_OBJECTS[] = new int[]{64, 1, 2, 3, 67, 5, 6, 70, 7, 8, 72, 9, 73, 10, 74, 11, 75, 76, 12, 14, 15, 81, 17, 18, 21, 22, 26, 27, 28, 29, 30, 33, 34, 37, 40, 41, 42, 45, 46, 48, 57, 59, 60, 61, 62, 63};

    private DLPFCBaseController controller;

    private Timer rlTimer = new Timer();
    private int MAX_CYCLES = 10;
    private int cycleCount = 0;

    private TaskSetDialog taskSetDialog;

    private Socket consolidationSocket;
    private PrintWriter consolidationPW;

    public TaskFrame(String title) {
        try {
            initComponents();

            itcStorageHandler = ITCStorageHandler.getInstance();

            objectClassModel = new DefaultListModel();

            //UNCOMMENT FOR JDK 11 >
            //objectClassModel.addAll(itcStorageHandler.getClassFeatures().keySet());
            for (Integer key : itcStorageHandler.getClassFeatures().keySet()) {
                objectClassModel.addElement(key);
            }

            objectClasses.setModel(objectClassModel);

            thumb = new ImageComponent();
            drawingPanel = new Drawing2DStringPanel(Configuration.GRID_ROWS_Y, Configuration.GRID_COLUMNS_X);

            thumbPanel.add(thumb);
            searchPanel.add(drawingPanel);

            taskSetDialog = new TaskSetDialog(this, rootPaneCheckingEnabled);

            landMarks = new ArrayList<>();

            landMarks.add(chairsPattern);
            landMarks.add(carPattern);
            landMarks.add(umbrellaPattern);

            landMarks.add(dogPattern);
            landMarks.add(bedRoomPattern);
            landMarks.add(tvRoomPattern);

            landMarks.add(officePattern);
            landMarks.add(winesPattern);
            landMarks.add(colibriPattern);

            setTitle("Task Execution");

            jButton6.setVisible(false);
            jButton7.setVisible(false);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public DLPFCBaseController getController() {
        return controller;
    }

    public void setController(DLPFCBaseController listener) {
        this.controller = listener;
    }

    /**
     * EXPERIMENTS TIMER
     */
    public void startRelationsExperiment() {

        rlTimer = new Timer();

        rlTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                int objectIndex = new Random().nextInt(POSIBLE_OBJECTS.length);
                int objectId = POSIBLE_OBJECTS[1];

                System.out.println("SEARCHING RELATIONS OF " + objectId);

                SpikeObject<Integer> spike = new SpikeObject(SpikeType.REQUEST_OBJECT_RELATION, objectId, 0);

                controller.searchItemRelations(spike);

                cycleCount++;

                if (cycleCount >= MAX_CYCLES) {
                    rlTimer.cancel();
                }

            }
        }, 1000, 5000);
    }

    public void startObjectExperiment() {

        rlTimer = new Timer();

        rlTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                int objectIndex = new Random().nextInt(POSIBLE_OBJECTS.length);
                int objectId = POSIBLE_OBJECTS[1];

                System.out.println("SEARCHING OBJECT OF " + objectId);

                SpikeObject<Integer> spike = new SpikeObject(SpikeType.REQUEST_OBJECT, objectId, 0);

                controller.searchItem(spike);

                cycleCount++;

                if (cycleCount >= MAX_CYCLES) {
                    rlTimer.cancel();
                }

            }
        }, 1000, 5000);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        objectClasses = new javax.swing.JList<>();
        thumbPanel = new javax.swing.JPanel();
        itemClassTxt = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jButton9 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        searchPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        patternTxt = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        goalButton = new javax.swing.JButton();
        goalIDTxt = new javax.swing.JTextField();
        randomGoalButton = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Object classes"));

        objectClasses.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                objectClassesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(objectClasses);

        thumbPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        thumbPanel.setPreferredSize(new java.awt.Dimension(128, 128));
        thumbPanel.setLayout(new java.awt.BorderLayout());

        itemClassTxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemClassTxtActionPerformed(evt);
            }
        });

        jLabel1.setText("Searched ID:");

        jButton9.setText("Search scene");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton5.setText("Search relations");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton4.setText("Search features");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton6.setText("ORT");
        jButton6.setEnabled(false);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setText("OT");
        jButton7.setEnabled(false);
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton14.setText("Min positive affect");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        jButton15.setText("Max positive affect");
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        jButton16.setText("Max negative affect");
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        jButton17.setText("Min negative affect");
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(itemClassTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(thumbPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jButton9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(thumbPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton7))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(itemClassTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton4)
                    .addComponent(jButton15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton5)
                    .addComponent(jButton14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton9)
                    .addComponent(jButton16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton17))
        );

        searchPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Search scene"));
        searchPanel.setPreferredSize(new java.awt.Dimension(640, 480));
        searchPanel.setLayout(new java.awt.BorderLayout());

        patternTxt.setColumns(20);
        patternTxt.setRows(5);
        jScrollPane2.setViewportView(patternTxt);

        jButton1.setText("Show Pattern");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Clear");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Search");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton8.setText("Configure Task-Set");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton10.setText("Pattern");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton11.setText("BACKTRACKING");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton12.setText("Commands");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        goalButton.setText("Set Goal");
        goalButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goalButtonActionPerformed(evt);
            }
        });

        goalIDTxt.setToolTipText("");

        randomGoalButton.setText("Set Random Goal");
        randomGoalButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                randomGoalButtonActionPerformed(evt);
            }
        });

        jButton13.setText("Consolidation");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(goalButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(goalIDTxt))
                            .addComponent(randomGoalButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(searchPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE)
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(searchPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton8)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(goalButton)
                            .addComponent(goalIDTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(randomGoalButton)
                            .addComponent(jButton13))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton1)
                            .addComponent(jButton10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton3)
                            .addComponent(jButton11))
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton2)
                            .addComponent(jButton12))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void setImage(BufferedImage image) {
        thumb.setImage(image);
    }

    public void setPattern(String text) {
        patternTxt.setText(text);
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String pattern = drawingPanel.get2DStringPattern();
        patternTxt.setText(pattern);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        drawingPanel.clear();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed

        String pattern = drawingPanel.get2DStringPattern();//"<<(8:3),<<<<(8:3),(1)(2),(3)(3),(5)(5)"; //"<<(14)(8:3)<(1)<(1)<(1)<<<(14)<(1),<(14)<<(1)(1)(14)(1)<(8:3)(1),(1)(5)(6)(7)(8)(2)(3)(4),(3)(3)(3)(4)(5)(6)(9)(10),(2)(5)(5)(5)(4)(4)(4)(4)";//drawingPanel.get2DStringPattern();
        Scene scene = new Scene(0, pattern, 0);

        SpikeObject<Scene> spike = new SpikeObject(SpikeType.REQUEST_SIMILAR_SCENES, scene, 0);

        patternTxt.setText(pattern);

        controller.searchScenes(spike);
    }//GEN-LAST:event_jButton3ActionPerformed

    public void setTaskSet(ArrayList<Integer> allowedClasses) {
        SpikeObject<ArrayList<Integer>> spike = new SpikeObject(SpikeType.TASK_SET_ALLOWED_CLASSES, allowedClasses, 0);
        controller.setAllowedClasses(spike);
    }

    public void setTaskSet(String pattern) {
        SpikeObject<String> spike = new SpikeObject(SpikeType.TASK_SET_ALLOWED_SCENE, pattern, 0);
        controller.setAllowedScene(spike);
    }

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                taskSetDialog.setVisible(true);
            }
        });
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        String pattern = patternTxt.getText();

        Scene scene = new Scene(0, pattern, 0);

        SpikeObject<Scene> spike = new SpikeObject(SpikeType.REQUEST_SIMILAR_SCENES, scene, 0);

        controller.searchScenes(spike);

        /*
        ArrayList<Integer>[][] imageMatrix = T2DString.decodeMatrix(pattern,Configuration.GRID_COLUMNS_X,Configuration.GRID_ROWS_Y);
        
        int [][]m = new int[Configuration.GRID_ROWS_Y][Configuration.GRID_COLUMNS_X];
        
        for (int i = 0; i < Configuration.GRID_ROWS_Y; i++) {
            for (int j = 0; j < Configuration.GRID_COLUMNS_X; j++) {
                if(imageMatrix[i][j] == null)continue;
                
                for(Integer v: imageMatrix[i][j]){
                    m[i][j] = v;
                }
            }
        }
        
        
        drawingPanel.setImage(m);
         */
    }//GEN-LAST:event_jButton10ActionPerformed


    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed

        //DOG <<(73)<<<(1)<(70)<<(17),<(70)<(1)(17)<(73),(3)(2)(4)(1),(3)(6)(7)(9),(4)(3)(2)(3)
        //CHAIRS 
        String chairsPattern = "<<(57)<(1)<(61:57)<(1)<(57),<<(61:57)(57)<(57)(1)(1),(3)(4)(6)(1)(2)(5),(3)(4)(5)(5)(6)(7),(4)(4)(3)(3)(4)(3)";
        String carPattern = "<<<(1)<<(3)<(1)(10),<<(1)(3)<(1)<(10),(1)(2)(3)(4),(4)(6)(7)(7),(3)(3)(4)(5)";
        String umbrellaPattern = "<<<<(1)(26)<<(1),<<<(1)(1)<(26),(1)(3)(2),(5)(5)(7),(4)(5)(4)";

        String dogPattern = "<<(73)<<<(1)<(70)<<(17),<(70)<(1)(17)<(73),(3)(2)(4)(1),(3)(6)(7)(9),(4)(3)(2)(3)";
        String bedRoomPattern = "<<(59)<(63)<(64)<<<(60),<(60)<(59)<(63)(64),(4)(1)(2)(3),(3)(4)(5)(8),(3)(4)(4)(2)";
        String tvRoomPattern = "<(81)<(59)<<<(63)<<(59)<(81),<(81)(81)<<(59)(63)(59),(1)(5)(2)(3)(4),(2)(3)(6)(8)(9),(2)(4)(4)(4)(2)";

        String officePattern = "<<(64)<<(63)<<(42),<<(42)<(64)(63),(3)(1)(2),(3)(5)(7),(4)(4)(3)"; //REVISAR
        String winesPattern = "<(41)<(57:41)<(57:41)<(61)(41)<(57:40)<(57:41)<(41),<(41)(61)(41)<(57:41)(57:41)(41)(57:40)(57:41),(1)(6)(12)(2)(3)(4)(5)(7)(8)(9)(10)(11),(2)(3)(3)(4)(4)(5)(5)(6)(6)(7)(7)(8),(2)(3)(3)(3)(3)(2)(3)(3)(3)(3)(3)(2)";
        String colibriPattern = "<<(14)<(15)<<(15)<<(15)<(15),<<(14)<<<(15)(15)(15)(15),(5)(1)(2)(4)(3),(3)(4)(6)(8)(9),(3)(6)(6)(6)(6)";

        String landMarks[] = new String[]{chairsPattern, carPattern, umbrellaPattern, dogPattern, bedRoomPattern, tvRoomPattern, officePattern, winesPattern, colibriPattern};

        String goalPattern = umbrellaPattern;
        String startPattern = winesPattern;
        //goalPattern = startPattern;
        controller.backtracking(startPattern, goalPattern);

    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed

        try {

            ArrayList<Integer> steps = new ArrayList();

            steps.add(1);
            steps.add(2);

            Socket commandSocket = new Socket("192.168.1.73", 11000);

            PrintWriter pw = new PrintWriter(commandSocket.getOutputStream());

            JSONObject commandJSON = new JSONObject();
            JSONArray plan = new JSONArray();

            // Add elements from ArrayList to JSONArray individually
            for (Integer step : steps) {
                plan.put(step); // Using put() to add elements to JSONArray
            }

            commandJSON.put("plan", plan);

            pw.println(commandJSON.toString());

            pw.close();

            commandSocket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }//GEN-LAST:event_jButton12ActionPerformed

    private void goalButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goalButtonActionPerformed
        //String goalPattern = landMarks.get(Integer.parseInt(goalIDTxt.getText()));
        String goalPattern = drawingPanel.get2DStringPattern();
        controller.setGoalPattern(goalPattern);
    }//GEN-LAST:event_goalButtonActionPerformed

    private void randomGoalButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_randomGoalButtonActionPerformed
        String goalPattern = landMarks.get(new Random().nextInt(landMarks.size()));
        controller.setGoalPattern(goalPattern);
    }//GEN-LAST:event_randomGoalButtonActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        startObjectExperiment();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        startRelationsExperiment();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        SpikeObject<Integer> spike = new SpikeObject(SpikeType.REQUEST_OBJECT, Integer.parseInt(itemClassTxt.getText()), 0);
        controller.searchItem(spike);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:

        //SpikeObject<Integer> spike = new SpikeObject(SpikeType.SPIKE_16,Integer.parseInt(itemClassTxt.getText()),0);
        //listener.searchItemRelations(spike);
        //SEARCH OBJECTS IN LTM RELATIONS MEMORY
        SpikeObject<Integer> spike = new SpikeObject(SpikeType.REQUEST_OBJECT_RELATION, Integer.parseInt(itemClassTxt.getText()), 0);
        controller.searchItemRelations(spike);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        SpikeObject<Integer> spike = new SpikeObject(SpikeType.REQUEST_SCENE_BY_ID, Integer.parseInt(itemClassTxt.getText()), 0);
        controller.searchSceneByID(spike);
    }//GEN-LAST:event_jButton9ActionPerformed

    private void itemClassTxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemClassTxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_itemClassTxtActionPerformed

    private void objectClassesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_objectClassesMouseClicked

        int classToDraw = objectClasses.getSelectedValue();
        BufferedImage img = itcStorageHandler.getFeatures(classToDraw);

        thumb.setImage(img);
        drawingPanel.setImageToDraw(classToDraw, img);
        
        thumb.repaint();

        System.out.println(classToDraw);
    }//GEN-LAST:event_objectClassesMouseClicked

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        try {

            if (consolidationSocket == null) {
                consolidationSocket = new Socket("192.168.1.73", 11001);
                consolidationPW = new PrintWriter(consolidationSocket.getOutputStream());

                //new ConsolidationNodeHandler(consolidationSocket);
            } else {

                String pattern = drawingPanel.get2DStringPattern();
                String parts[] = pattern.split(",");

                JSONObject commandJSON = new JSONObject();

                commandJSON.put("command", "2");
                commandJSON.put("data", parts[0]);

                consolidationPW.println(commandJSON.toString());
                consolidationPW.flush();

            }

            //pw.close();
            //commandSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        SpikeObject<Integer> spike1 = new SpikeObject(SpikeType.REQUEST_OBJECT_BY_AFFECT, TopDownParameters.MAX_POSITIVE_AFFECT, 0);
        SpikeObject<Integer> spike2 = new SpikeObject(SpikeType.REQUEST_SCENE_BY_AFFECT, TopDownParameters.MAX_POSITIVE_AFFECT, 0);
        
        controller.searchItemByAffect(spike1);
        //controller.searchSceneByAffect(spike2);
    }//GEN-LAST:event_jButton15ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        SpikeObject<Integer> spike1 = new SpikeObject(SpikeType.REQUEST_OBJECT_BY_AFFECT, TopDownParameters.MIN_POSITIVE_AFFECT, 0);
        SpikeObject<Integer> spike2 = new SpikeObject(SpikeType.REQUEST_SCENE_BY_AFFECT, TopDownParameters.MIN_POSITIVE_AFFECT, 0);
        
        //listener.searchItemByAffect(spike1);
        controller.searchSceneByAffect(spike2);
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        SpikeObject<Integer> spike2 = new SpikeObject(SpikeType.REQUEST_SCENE_BY_AFFECT, TopDownParameters.MAX_NEGATIVE_AFFECT, 0);
        controller.searchSceneByAffect(spike2);
    }//GEN-LAST:event_jButton16ActionPerformed

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        SpikeObject<Integer> spike2 = new SpikeObject(SpikeType.REQUEST_SCENE_BY_AFFECT, TopDownParameters.MIN_NEGATIVE_AFFECT, 0);
        controller.searchSceneByAffect(spike2);
    }//GEN-LAST:event_jButton17ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TaskFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TaskFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TaskFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TaskFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TaskFrame("Task").setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton goalButton;
    private javax.swing.JTextField goalIDTxt;
    private javax.swing.JTextField itemClassTxt;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList<Integer> objectClasses;
    private javax.swing.JTextArea patternTxt;
    private javax.swing.JButton randomGoalButton;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JPanel thumbPanel;
    // End of variables declaration//GEN-END:variables
}
