package astrobee_test_ground;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.DecodeHintType;
// zxing library
import org.opencv.core.Core;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Rect;
import org.opencv.calib3d.Calib3d;
import org.opencv.objdetect.QRCodeDetector;
// opencv library
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;
// java library



public class Main {
	
	public static void main(String[] args){
		// load the native OpenCV library
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	
		
		System.out.println("[image]: "+ "loading");
		Mat image = new Mat(); 
		image = Imgcodecs.imread("D:\\GitHub\\JAXA-2nd-Kibo-RPC\\learn\\learn_opencv\\learn_opencv_d\\src\\PointAQRIlluminated0.5.PNG", Imgcodecs.IMREAD_GRAYSCALE);
		//System.out.println("[image]: "+ image.dump());
		Rect roi = new Rect(0,0,(int)image.size().width,(int)image.size().height);
		Mat undistortedPic = undistort(image,roi);//new Mat(); image.copyTo(undistortedPic);//undistort(image,roi);
		Imgcodecs.imwrite("D:\\GitHub\\JAXA-2nd-Kibo-RPC\\learn\\learn_opencv\\learn_opencv_d\\src\\undistortedImage.png", undistortedPic);
		System.out.println("[roi]: "+ roi.toString());
		
		
        cropImage(undistortedPic, undistortedPic, roi);
        Imgcodecs.imwrite("D:\\GitHub\\JAXA-2nd-Kibo-RPC\\learn\\learn_opencv\\learn_opencv_d\\src\\croppedImage.png", undistortedPic);
        
        
        cropImagePercent(undistortedPic, undistortedPic, 30, 50, 60);
        Imgcodecs.imwrite("D:\\GitHub\\JAXA-2nd-Kibo-RPC\\learn\\learn_opencv\\learn_opencv_d\\src\\croppedImage2.png", undistortedPic);
        
        
        Size sz = new Size(1920,1440);
        Imgproc.resize( undistortedPic, undistortedPic, sz );
        Imgcodecs.imwrite("D:\\GitHub\\JAXA-2nd-Kibo-RPC\\learn\\learn_opencv\\learn_opencv_d\\src\\resizedImage.png", undistortedPic);
        
        
        double contrast = 1; 
        int brightness = -30;
        undistortedPic.convertTo(undistortedPic, -1, contrast, brightness);
        Imgcodecs.imwrite("D:\\GitHub\\JAXA-2nd-Kibo-RPC\\learn\\learn_opencv\\learn_opencv_d\\src\\adjustedImage.png", undistortedPic);
        
        
        //adjustedImage.copyTo(shapenedImage);
        Mat blurredImage = new Mat();
        int kernelSize = 19;
        Imgproc.GaussianBlur(undistortedPic, blurredImage, new Size(kernelSize, kernelSize), 30);
        Core.addWeighted(undistortedPic, 1.5, blurredImage, -0.5, 0, undistortedPic);
        Core.addWeighted(undistortedPic, 1.5, blurredImage, -0.5, 0, undistortedPic);
        Core.addWeighted(undistortedPic, 1.5, blurredImage, -0.5, 0, undistortedPic);
        contrast = 1.1; 
        brightness = 0;
        //undistortedPic.convertTo(undistortedPic, -1, contrast, brightness);
        Imgcodecs.imwrite("D:\\GitHub\\JAXA-2nd-Kibo-RPC\\learn\\learn_opencv\\learn_opencv_d\\src\\blurredImage.png", blurredImage);
        Imgcodecs.imwrite("D:\\GitHub\\JAXA-2nd-Kibo-RPC\\learn\\learn_opencv\\learn_opencv_d\\src\\shapenedImage.png", undistortedPic);
        
        final Size sizeA = undistortedPic.size();
        
        Rect roi2 = new Rect((int)(sizeA.width*0.08),(int)(sizeA.height*0.1),(int)(sizeA.width*0.6),(int)(sizeA.height*0.8));
        cropImage(undistortedPic, undistortedPic, roi2);
        
        cropImagePercent(undistortedPic, undistortedPic, 50, 10, 20);
        Imgcodecs.imwrite("D:\\GitHub\\JAXA-2nd-Kibo-RPC\\learn\\learn_opencv\\learn_opencv_d\\src\\croppedImage3.png", undistortedPic);
        
        
        /*blurredImage = new Mat();
        Imgproc.GaussianBlur(undistortedPic, blurredImage, new Size(kernelSize, kernelSize), 30);
        Core.addWeighted(undistortedPic, 1.5, blurredImage, -0.5, 0, undistortedPic);
        blurredImage = new Mat();
        Imgproc.GaussianBlur(undistortedPic, blurredImage, new Size(kernelSize, kernelSize), 30);
        Core.addWeighted(undistortedPic, 1.5, blurredImage, -0.5, 0, undistortedPic);
        blurredImage = new Mat();
        Imgproc.GaussianBlur(undistortedPic, blurredImage, new Size(kernelSize, kernelSize), 30);
        Core.addWeighted(undistortedPic, 1.5, blurredImage, -0.5, 0, undistortedPic);/*
        Imgcodecs.imwrite("D:\\GitHub\\JAXA-2nd-Kibo-RPC\\learn\\learn_opencv\\learn_opencv_d\\src\\blurredImage2.png", blurredImage);
        Imgcodecs.imwrite("D:\\GitHub\\JAXA-2nd-Kibo-RPC\\learn\\learn_opencv\\learn_opencv_d\\src\\shapenedImage2.png", undistortedPic);
        /*Mat croppedPic4 = new Mat();
        cropImage(croppedPic3, croppedPic4, new Rect(0,0,));
        Imgcodecs.imwrite("D:\\GitHub\\JAXA-2nd-Kibo-RPC\\learn\\learn_opencv\\learn_opencv_d\\src\\croppedImage4.png", croppedPic4);*/
        
        
        //Mat testQR = new Mat(); 
		//testQR = Imgcodecs.imread("D:\\GitHub\\JAXA-2nd-Kibo-RPC\\learn\\learn_opencv\\learn_opencv_d\\src\\testQR.PNG", Imgcodecs.IMREAD_GRAYSCALE);
        //String qrData = readQR(croppedPic3);
        
        String qrData = null;

        
        
        final Size sizeB = undistortedPic.size();
        final int size = (int)(sizeB.width*sizeB.height);
        byte[] bite = new byte[size];
        int row = 0, col = 0;
        undistortedPic.get(row, col, bite);
        
        MatOfInt grayscale = new MatOfInt(CvType.CV_32SC1);
        undistortedPic.convertTo(grayscale,CvType.CV_32SC1);
        int[] intArray = new int[(int)(grayscale.total()*grayscale.channels())];
        grayscale.get(0,0,intArray);
        
        
        Map<DecodeHintType, String> hints = new HashMap<>();
        hints.put(DecodeHintType.TRY_HARDER, "utf-8");
        
        System.out.println(hints.toString());
        
        /*for (int i = 0; i < sizeA.height; i++) {
            for (int j = 0; j < sizeA.width; j++) {
                double[] data = undistortedPic.get(i, j);
                intArray[(int)(sizeA.width*i + j)] = (int)data[0];
                //System.out.println("QR[j][i][data[0]]:"+ "" + j  + " " + i + " " + (int)data[0]);
            }
		}*/
        

        LuminanceSource source = new RGBLuminanceSource((int)undistortedPic.size().width, (int)undistortedPic.size().height, intArray);
        source.crop(1,1,(int)sizeB.width-2,(int)sizeB.height-2);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        int kozPattern = -1;
        double result_x = -1, result_y = -1, result_z = -1;
        
        try
        {
            com.google.zxing.Result result = new QRCodeReader().decode(bitmap, hints);
            qrData = result.getText();
            System.out.println("QR[status]:" + " Detected " + qrData);
            	
            // Format : "p":<pattern>,"x":<x>,"y":<y>,"z":<z>
            qrData = qrData.replaceAll("[^0-9,.]", "");
            System.out.println(qrData);
            String[] multi_contents = qrData.split(",");
            System.out.println(multi_contents[0].replaceAll("[^0-9]", ""));
            kozPattern = Integer.parseInt(multi_contents[0]);
            result_x = Double.parseDouble(multi_contents[1]);
            result_y = Double.parseDouble(multi_contents[2]);
            result_z = Double.parseDouble(multi_contents[3]);
            
            
            
        }
        catch (Exception e)
        {
        	System.out.println("QR[status]:" + " Not detected");
        }
        
        if(qrData != null) {
        	System.out.println("QR[qrData]:" + String.valueOf(kozPattern) + " " + String.valueOf(result_x) + " " + String.valueOf(result_y) + " " + String.valueOf(result_z));
        }
		
	}
	
	public static String readQR(Mat pic){
		System.out.println("readQR[status]: " + " start");
        String content = "";
        QRCodeDetector detector = new QRCodeDetector();
        int loopCounter = 0;
        final int LOOP_MAX = 5;
        while( content.isEmpty() && loopCounter < LOOP_MAX){
        	System.out.println("readQR[loopCounter]: " + loopCounter);
            content = detector.detectAndDecode(pic);
            loopCounter++;
        }
        System.out.println("readQR[content]: " + content);
        return content;
    }

	
	private static void cropImage(Mat sourceImage, Mat targetMat, Rect roi){
        System.out.println("cropImage[roi]: "+ String.valueOf(roi));
        Mat croppedImage = new Mat(sourceImage,roi);
        croppedImage.copyTo(targetMat);
        return;
    }
	
	private static void cropImagePercent(Mat sourceImage, Mat targetMat, double percent, double xPercent, double yPercent){
        System.out.println("cropImagePercent[status]: " + "start");
        
        double width = sourceImage.size().width;
        double height = sourceImage.size().height;
        double newWidth = width*percent/100;
        double newHeight = height*percent/100;
        double newX = width*xPercent/100;
        double newY = height*yPercent/100;
        
        Rect newROI = new Rect((int)newX,(int)newY,(int)newWidth,(int)newHeight);
        
        Mat croppedImage = new Mat(sourceImage,newROI);
        croppedImage.copyTo(targetMat);
        return;
    }
	
	
	public static Mat undistort(Mat sourceImage, Rect roi) {
		System.out.println("undistortImage[status]: "+ "start");
        
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		double[][] navCamIntrinsics  = {{567.229305, 0.0, 659.077221, 0.0, 574.192915, 517.007571, 0.0, 0.0, 1.0}, {-0.216247, 0.03875, -0.010157, 0.001969, 0.0}};
		
		
        System.out.println("undistortImage[status]: "+ "got navCamIntrinsics " + Arrays.deepToString(navCamIntrinsics));
        int row = 0, col = 0;
        Mat cameraMat = new Mat(3, 3, CvType.CV_32FC1);
        cameraMat.put(row, col, navCamIntrinsics[0]);
        System.out.println("undistortImage[status]: "+ "cameraMat put " + cameraMat.toString() + " " + cameraMat.dump());

        row = 0; col = 0;
        Mat distortionCoeff = new Mat(1, 5, CvType.CV_32FC1);
        distortionCoeff.put(row,col,navCamIntrinsics[1]);
        System.out.println("undistortImage[status]: "+ "distortCoeff put " + distortionCoeff.toString() + " " + distortionCoeff.dump());

        int test = 1;
        Mat undistortedPic = new Mat(1280, 960, CvType.CV_8UC1);

        System.out.println("undistortImage[sourceImage.size()]: " +  sourceImage.size().toString());
        System.out.println("undistortImage[cameraMat.size()]: " +  cameraMat.size().toString());
        System.out.println("undistortImage[distortionCoeff.size()]: " +  distortionCoeff.size().toString());
        Size sourceImageSize = sourceImage.size();
        if(test == 1){
            Mat newCameraMat = Calib3d.getOptimalNewCameraMatrix(cameraMat, distortionCoeff, sourceImageSize, 1, sourceImageSize, roi);
            System.out.println("undistortImage[status]: "+ "got optimalNewCamMat");
            System.out.println("undistortImage[status]: "+ "got optimalNewCamMat " + newCameraMat.dump());
            System.out.println("undistortImage[newCameraMat.size()]: " +  newCameraMat.size().toString());

            Calib3d.undistort(sourceImage, undistortedPic, cameraMat, distortionCoeff, newCameraMat);// (sourceImage,undistortedPic,cameraMat,distortionCoeff,newCameraMat);
            System.out.println("undistortImage[status]: "+ "finished fisheye_undistort");
        }else if(test == 2){ //This method crashes the program
            //Imgproc.undistort(sourceImage, undistortedPic, cameraMat, distortionCoeff);
            System.out.println("undistortImage[status]: "+ "finished Imgproc.undistort");
        }
        
        return undistortedPic;
	}
	
	
	
	
	
}
