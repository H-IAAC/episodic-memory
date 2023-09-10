/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package episodicv2.Codelets;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import java.nio.file.Path;
import java.nio.file.Paths;

// imports:
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect2d;
import org.opencv.core.Point;
import org.opencv.core.Rect2d;
import org.opencv.core.Scalar;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.utils.Converters;
import org.opencv.core.Size;



/**
 *
 * @author karenlima
 */
public class VisionCodelet extends Codelet{
    
    Idea imageReceivedPathIdea;
    MemoryObject imageReceivedMO;
    
    Idea centerPointsandClassesIdea;
    MemoryObject centerPointsandClassesMO;
    
    Integer currentFrame = 0;
        
    
    ArrayList<Idea> objectsClasses = new ArrayList<Idea>();
    ArrayList<Idea> objectsPoints = new ArrayList<Idea>();
    
    List<String> cocoLabels = new ArrayList<String>();
    
    String s;
    Net dnnNet;
    
    public VisionCodelet() {
        
        setIsMemoryObserver(true);
        try {
            loadOpenCVLibraryFromCurrentPath();
        } catch (FileNotFoundException ex){
            System.out.printf("Error reading OpenCV lib");
        }
        
    }    
    
    @Override
    public void accessMemoryObjects() {
        System.out.println("Executing accessMemoryObjects Vision Codelet");
        imageReceivedMO = (MemoryObject) getInput("imageReceivedPathMO");
        imageReceivedPathIdea = (Idea) imageReceivedMO.getI();
        
        centerPointsandClassesMO = (MemoryObject) getOutput("centerPointsandClassesMO");
        centerPointsandClassesIdea = (Idea) centerPointsandClassesMO.getI();
 
    }
    
    @Override
    public void proc() {
        System.out.println("Executing proc Vision Codelet");
        try {
            detectObjectOnImage();
        } catch (FileNotFoundException ex){
            System.out.printf("erro");
        }
        System.out.printf("objects: ");
        System.out.printf(objectsClasses.toString());
        System.out.printf("objects center: ");
        System.out.printf(objectsPoints.toString());
        System.out.println("\n");
        
        saveObjectsIdea();
        
    }
    
     private void saveObjectsIdea() {//TODO: ver se precisa criar essas novas ideas msms
        centerPointsandClassesIdea.setL(new ArrayList());
        
        Idea objectsClassesIdea = new Idea("objectsClasses", objectsClasses);
        Idea objectsPointsIdea = new Idea("objectsPoints", objectsPoints);
        centerPointsandClassesIdea.add(objectsClassesIdea);
        centerPointsandClassesIdea.add(objectsPointsIdea);
         
         
        currentFrame+=1;
        Idea currentFrameIdea = new Idea("currentFrame",currentFrame);
        
        centerPointsandClassesIdea.add(currentFrameIdea);
        
        centerPointsandClassesMO.setI(centerPointsandClassesIdea);
    }
    
    @Override
    public void calculateActivation(){
        
    }
    
        
    private void loadOpenCVLibraryFromCurrentPath() throws FileNotFoundException  {
        Path currentRelativePath = Paths.get("");
        s = currentRelativePath.toAbsolutePath().toString();
        System.load(s+"/libs/libopencv_java480.dylib");
        System.out.println("OpenCV lib loaded");
        
        //  load the COCO class labels our YOLO model was trained on
        Scanner scan = new Scanner(new FileReader(s+"/yolo/files/coco.names"));
        
        while(scan.hasNextLine()) {
            cocoLabels.add(scan.nextLine());
        }
        
        //  load our YOLO object detector trained on COCO dataset
      dnnNet = Dnn.readNetFromDarknet(s+"/yolo/files/yolov3.cfg", s+"/yolo/files/yolov3.weights");
        // YOLO on GPU:
        //      dnnNet.setPreferableBackend(Dnn.DNN_BACKEND_CUDA);
        //      dnnNet.setPreferableTarget(Dnn.DNN_TARGET_CUDA);
    }
    
    private void detectObjectOnImage() throws FileNotFoundException {

      String imageReceivedPath = (String) imageReceivedPathIdea.getValue();
      // load our input image
      Mat img = Imgcodecs.imread(s+imageReceivedPath, Imgcodecs.IMREAD_COLOR); // dining_table.jpg soccer.jpg baggage_claim.jpg
      if (img.empty() == false){
        //  -- determine  the output layer names that we need from YOLO
        // The forward() function in OpenCV’s Net class needs the ending layer till which it should run in the network.
        //  getUnconnectedOutLayers() vraca indexe za: yolo_82, yolo_94, yolo_106, (indexi su 82, 94 i 106) i to su poslednji layeri
        // u networku:
        List<String> layerNames = dnnNet.getLayerNames();
        List<String> outputLayers = new ArrayList<String>();
        for (Integer i : dnnNet.getUnconnectedOutLayers().toList()) {
            outputLayers.add(layerNames.get(i - 1));
        }
        HashMap<String, List>  result = forwardImageOverNetwork(img, outputLayers);

        ArrayList<Rect2d> boxes = (ArrayList<Rect2d>)result.get("boxes");
        ArrayList<Float> confidences = (ArrayList<Float>) result.get("confidences");
        ArrayList<Integer> class_ids = (ArrayList<Integer>)result.get("class_ids");

        // -- Now , do so-called “non-maxima suppression”
        //Non-maximum suppression is performed on the boxes whose confidence is equal to or greater than the threshold.
        // This will reduce the number of overlapping boxes:
        MatOfInt indices =  getBBoxIndicesFromNonMaximumSuppression(boxes,
                confidences);

        getObjectsCenterAndClassesID(indices, boxes, class_ids);
      }
      

    }

    private HashMap<String, List> forwardImageOverNetwork(Mat img, List<String> outputLayers) {
        // --We need to prepare some data structure  in order to store the data returned by the network  (ie, after Net.forward() call))
        // So, Initialize our lists of detected bounding boxes, confidences, and  class IDs, respectively
        // This is what this method will return:
        HashMap<String, List> result = new HashMap<String, List>();
        result.put("boxes", new ArrayList<Rect2d>());
        result.put("confidences", new ArrayList<Float>());
        result.put("class_ids", new ArrayList<Integer>());

        // -- The input image to a neural network needs to be in a certain format called a blob.
        //  In this process, it scales the image pixel values to a target range of 0 to 1 using a scale factor of 1/255.
        // It also resizes the image to the given size of (416, 416) without cropping
        // Construct a blob from the input image and then perform a forward  pass of the YOLO object detector,
        // giving us our bounding boxes and  associated probabilities:

        Mat blob_from_image = Dnn.blobFromImage(img, 1 / 255.0, new Size(416, 416), // Here we supply the spatial size that the Convolutional Neural Network expects.
                new Scalar(new double[]{0.0, 0.0, 0.0}), true, false);
        dnnNet.setInput(blob_from_image);

        // -- the output from network's forward() method will contain a List of OpenCV Mat object, so lets prepare one
        List<Mat> outputs = new ArrayList<Mat>();

        // -- Finally, let pass forward throught network. The main work is done here:
        dnnNet.forward(outputs, outputLayers);

        // --Each output of the network outs (ie, each row of the Mat from 'outputs') is represented by a vector of the number
        // of classes + 5 elements.  The first 4 elements represent center_x, center_y, width and height.
        // The fifth element represents the confidence that the bounding box encloses the object.
        // The remaining elements are the confidence levels (ie object types) associated with each class.
        // The box is assigned to the category corresponding to the highest score of the box:

        for(Mat output : outputs) {
            //  loop over each of the detections. Each row is a candidate detection,
            for (int i = 0; i < output.rows(); i++) {
                Mat row = output.row(i);
                List<Float> detect = new MatOfFloat(row).toList();
                List<Float> score = detect.subList(5, output.cols());
                int class_id = argmax(score); // index maximalnog elementa liste
                float conf = score.get(class_id);
                if (conf >= 0.5) {
                    int center_x = (int) (detect.get(0) * img.cols());
                    int center_y = (int) (detect.get(1) * img.rows());
                    int width = (int) (detect.get(2) * img.cols());
                    int height = (int) (detect.get(3) * img.rows());
                    int x = (center_x - width / 2);
                    int y = (center_y - height / 2);
                    Rect2d box = new Rect2d(x, y, width, height);
                    result.get("boxes").add(box);
                    result.get("confidences").add(conf);
                    result.get("class_ids").add(class_id);
                    
                }
            }
        }
        return result;
    }
    
    /**
     Returns index of maximum element in the list
     */
    private int argmax(List<Float> array){
        float max = array.get(0);
        int re = 0;
        for (int i = 1; i < array.size(); i++) {
            if (array.get(i) > max) {
                max = array.get(i);
                re = i;
            }
        }
        return re;
    }

    private MatOfInt getBBoxIndicesFromNonMaximumSuppression(ArrayList<Rect2d> boxes, ArrayList<Float> confidences ) {
        MatOfRect2d mOfRect = new MatOfRect2d();
        mOfRect.fromList(boxes);
        MatOfFloat mfConfs = new MatOfFloat(Converters.vector_float_to_Mat(confidences));
        MatOfInt result = new MatOfInt();
        Dnn.NMSBoxes(mOfRect, mfConfs, (float)(0.6), (float)(0.5), result);
        return result;
    }
    
    private void getObjectsCenterAndClassesID(MatOfInt indices,
                                    ArrayList<Rect2d> boxes,
                                    ArrayList<Integer> class_ids) {
        objectsClasses.clear();
        objectsPoints.clear();
        List indices_list = indices.toList();
        for (int i = 0; i < boxes.size(); i++) {
            if (indices_list.contains(i)) {
                Rect2d box = boxes.get(i);
                String label = cocoLabels.get(class_ids.get(i));
                
                Double xpoint =  box.x + (box.width/2);
                Double ypoint = box.y + (box.height/2);
                
                Idea xpointIdea = new Idea("x", xpoint.intValue());
                Idea ypointIdea = new Idea("y", ypoint.intValue());
                
                Idea point = new Idea("point");
                point.add(xpointIdea);
                point.add(ypointIdea);
                
                Idea classIdea = new Idea("classIdea");
                Idea labelIdea = new Idea("label", label);
                Idea classIdIdea = new Idea("classId", i);
                classIdea.add(labelIdea);
                classIdea.add(classIdIdea);
                
                objectsClasses.add(classIdea);
                objectsPoints.add(point);
            }
        }
    
    }
    
}
