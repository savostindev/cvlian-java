package com.cvlian;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;

public class CVlian {
    private CascadeClassifier clf;
    private double angle;

    public CVlian(String faceCascadePath) {
        clf = new CascadeClassifier(faceCascadePath);
    }

    public void run(String directory) {
        long checkpoint = System.currentTimeMillis();
        File[] files = new File(directory).listFiles();
        processImage(files);
        System.out.println("Изображения обработаны за "+
                (System.currentTimeMillis() - checkpoint)+" мс.");
    }

    private void processImage(File[] files) {
        Mat image;
        for (File file : files) {
            if (file.isDirectory()) {
                processImage(file.listFiles());
            } else {
                image = Imgcodecs.imread(file.getPath());
                detectFace(image);
            }

        }
    }

    private void detectFace(Mat image) {
        MatOfRect faceROI = new MatOfRect();

        clf.detectMultiScale(image, faceROI, 1.15, 5, 0, new Size(image.width() / 8, image.width() / 8), new Size());
        adjustImage(image, faceROI, 32);
        if (faceROI.empty()) {
            if (angle < 360.0) {
                setRotationAngle(90);
                Mat rotatedImage = getRotatedImage(image, angle);
                detectFace(rotatedImage);
            } else {
                System.out.println("Face was not found :(");
                angle = 0.0;
            }
        } else {
            //TODO make gender classification
        }
    }

    private void adjustImage(Mat image, MatOfRect faceROI, int frameSize) {
        int x, y, w, h;
        for (Rect rect : faceROI.toArray()) {
            x = ((rect.x - frameSize) < 0) ? 0 : (rect.x - frameSize);
            y = ((rect.y - frameSize) < 0) ? 0 : (rect.y - frameSize);
            w = ((image.width()-frameSize*2-rect.width)<rect.x) ? (image.width() - x) : (rect.width + frameSize*2);
            h = ((image.height()-frameSize*2-rect.height)<rect.y) ? (image.height() - y) : (rect.height + frameSize*2);
            Rect adjustedFrame = new Rect(x, y, w, h);
            Mat adjustedImage = image.submat(adjustedFrame);
            Imgcodecs.imwrite("/home/alexey/sandbox/dst/" + Math.random() + ".jpg", adjustedImage);
            System.out.println("Лицо обнаружено на " + "file.getName()");
        }
    }

    private void setRotationAngle(double rotationAngle) {
        System.out.println("Rotate image");
        angle += rotationAngle;
    }

    private Mat getRotatedImage(Mat image, double angle) {
        Mat result = new Mat(image.rows(), image.cols(), image.type());
        Point center = new Point(result.cols() / 2, result.rows() / 2);
        Mat rotationMatrix = Imgproc.getRotationMatrix2D(center, angle, 1);
        Imgproc.warpAffine(image, result, rotationMatrix, result.size());
        return result;
    }

    private Mat bytesToImage(byte[] imageBytes){
        Mat image = Imgcodecs.imdecode(new MatOfByte(imageBytes), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
        return image;
    }

    private byte[] imageToBytes(Mat image) {
        MatOfByte imageBytes = new MatOfByte();
        Imgcodecs.imencode("jpg", image, imageBytes);
        return imageBytes.toArray();
    }

}
