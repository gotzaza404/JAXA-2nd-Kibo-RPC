package jp.jaxa.iss.kibo.rpc.sampleapk;

// astrobee library
import gov.nasa.arc.astrobee.Result;
import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;

// android library
import android.os.SystemClock;
import android.util.Log;
import android.graphics.Bitmap;
// zxing library
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.DecodeHintType;
import com.google.zxing.BarcodeFormat;
// opencv library
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Rect;
import org.opencv.objdetect.QRCodeDetector;
import static org.opencv.android.Utils.matToBitmap;
import org.opencv.calib3d.Calib3d;
// java library
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Arrays;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.lang.Math;
import org.opencv.aruco.Aruco;
import org.opencv.aruco.Dictionary;
import gov.nasa.arc.astrobee.Kinematics;
/**
 * Class meant to handle commands from the Ground Data System and execute them in Astrobee
 */

public class YourService extends KiboRpcService {

    final int LOOP_MAX = 3;
    static long startTime = 0;
    @Override
    protected void runPlan1(){
        final String TAG = "[main]: ";

        api.startMission();
        startTime = getTime();

        // move to point A (11.21, -9.8, 4.79, 0) quaternion A (0, 0, -0.707f, 0.707f)
        moveTo(11.21, -9.8, 4.79, 0, 0, -0.707f, 0.707f);

        // scan QR Code to get point A' (qrData[0], qrData[1], qrData[2]) quaternion A' (0, 0, -0.707, 0.707) KOZ pattern (qrData[3])
        float[] qrData = qrEvent();
        int kozPattern = (int)qrData[0];
        // move to point A' (11.21, -9.8, 4.79, 0) quaternion A (0, 0, -0.707f, 0.707f)
        Log.d(TAG, "successful");
        float x = qrData[1];
        float y = qrData[3];
        float z = 0 - qrData[2];

        GoToB_event();
        api.reportMissionCompletion();
    }


    private static long getTime(){
        return SystemClock.elapsedRealtime();
    }
    private static long getElapsedTime(){
        return getTime()-startTime;
    }
    private static String getElapsedTimeS(){
        return " _Time_: " + String.valueOf(getElapsedTime());
    }

    public void AR_event(float px, float py, float pz, float qx, float qy, float qz, float qw, int count_max, boolean sent_AR)
    {
        int contents = 0, count = 0;
        double result[] = new double[3];

        while (contents == 0 && count < count_max)
        {
            Log.d("AR[status]:", " start");
            long start_time = SystemClock.elapsedRealtime();
            //                                            //
            moveTo(px, py, pz, qx, qy, qz, qw);



            //////////////////////////////////////////////////////////////////////////////////////////////////////
            Mat source = api.getMatNavCam();
            Mat ids = new Mat();
            Dictionary dictionary = Aruco.getPredefinedDictionary(Aruco.DICT_5X5_250);
            List<Mat> corners = new ArrayList<>();

            try
            {
                Aruco.detectMarkers(source, dictionary, corners, ids);
                contents = (int) ids.get(0, 0)[0];
                Log.d("AR[status]:", " Detected");



            }
            catch (Exception e)
            {
                Log.d("AR[status]:", " Not detected");
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////
            Log.d("AR[status]:", " stop");
            long stop_time = SystemClock.elapsedRealtime();



            Log.d("AR[count]:", " " + count);
            Log.d("AR[total_time]:"," "+ (stop_time-start_time)/1000);
            count++;
        }
    }
    private void moveTo(double x, double y, double z, float qx, float qy, float qz, float qw){
        final String TAG = "[moveTo]: ";

        Point p = new Point(x,y,z);
        Quaternion q = new Quaternion(qx,qy,qz,qw);
        int loopCount = 0;
        Result result;
        Log.d(TAG, "start");

        do {
            result = api.moveTo(p,q,true);
            loopCount++;
        } while(!result.hasSucceeded() && loopCount < LOOP_MAX);

        Log.d(TAG,"finished");

    }
    private void setFlashOn(boolean status){
        final String TAG = "[setFlashOn]: ";
        Log.d(TAG, String.valueOf(status) + getElapsedTimeS());
        if(status == true){
            api.flashlightControlFront(0.5f);
            Log.d(TAG, "brightness = " + String.valueOf(0.5f) + getElapsedTimeS());
            try{
                Log.d(TAG, "sleeping");
                Thread.sleep(1000);
            }catch (InterruptedException e){
                Log.d(TAG, "interrupted" + getElapsedTimeS());
                e.printStackTrace();
            }
        }else{
            api.flashlightControlFront(0f);
            Log.d(TAG, "off" + getElapsedTimeS());
        }
        Log.d(TAG, "done" + getElapsedTimeS());
        return;
    }
    private Rect cropImage(double xPercent, double yPercent,double cropPercent){
        final String TAG = "[cropImage]: ";

        Log.d(TAG,"start");

        final int WIDTH = 1280;
        final int HEIGHT = 960;

        int x = (int)(xPercent/100*WIDTH);
        int y = (int)(yPercent/100*HEIGHT);
        int width = (int)(cropPercent/100*WIDTH);
        int height = (int)(cropPercent/100*HEIGHT);

        Log.d(TAG,"done");

        return new Rect(x,y,width,height);
    }

    private BinaryBitmap getNavCamImage(){
        final String TAG = "[getNavCamImage]: ";

        Log.d(TAG, "start");

        Mat image = new Mat(api.getMatNavCam(), cropImage(45, 50, 32));

        Bitmap bitmap = Bitmap.createBitmap(image.width(), image.height(), Bitmap.Config.ARGB_8888);
        matToBitmap(image, bitmap);

        int[] intArray = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        LuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), intArray);
        BinaryBitmap binarizedBitmap = new BinaryBitmap(new HybridBinarizer(source));

        Log.d(TAG, "done");

        return binarizedBitmap;
    }

    private float[] getQRDataContent(String qrData){
        final String TAG = "[getQRDataContent] :";
        Log.d(TAG, "start");

        String[] multi_contents = qrData.split(",");
        int kozPattern = Integer.parseInt(multi_contents[0].substring(5));
        float x = Float.parseFloat(multi_contents[1].substring(4));
        float y = Float.parseFloat(multi_contents[2].substring(4));
        float z = Float.parseFloat(multi_contents[3].substring(4, multi_contents[3].length()-1));
        Log.i(TAG, "contents: " + kozPattern + " " + x + " " + y + " " + z);
        Log.i(TAG, "finished");

        return new float[] {kozPattern, x, y, z};
    }

    private float [] qrEvent(){
        final String TAG = "[qrEvent]: ";
        Log.d(TAG,"start");

        String qrData = null;
        int loopCount = 0;
        BinaryBitmap bitmap;
        Map<DecodeHintType, Object> hints = new Hashtable<>();
        hints.put(DecodeHintType.TRY_HARDER, "");
        List<BarcodeFormat> qr = new ArrayList<>(); qr.add(BarcodeFormat.QR_CODE);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, qr);
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");

        while(qrData == null && loopCount < LOOP_MAX){
            Log.d(TAG, "loopCount " + loopCount);
            loopCount++;
            try{
                Log.d(TAG, "reading qr code");
                bitmap = getNavCamImage();
                QRCodeReader reader = new QRCodeReader();
                com.google.zxing.Result result = reader.decode(bitmap,hints);
                qrData = result.getText();
                if(qrData != null){
                    api.sendDiscoveredQR(qrData);
                    Log.i(TAG, "data : " + qrData);
                    Log.i(TAG, "finished");

                    float[] content = getQRDataContent(qrData);

                    return content;
                }
            }catch(Exception e){
                Log.d(TAG, "an error occurred while reading qr code");
                e.printStackTrace();
            }
        }

        Log.d(TAG, "failed to read qr code");
        return null;
    }

    private void GoToB_event(){
        final String TAG = "[GoB]: ";
        Log.d(TAG, "start");

        Log.d(TAG, "Move to entrance");
        moveTo(10.505,-9.2, 4.5, 0, 0, -0.707f, 0.707f);

        Log.d(TAG, "Pass through KOZ");
        moveTo(10.505, -8, 4.5, 0, 0, -0.707f, 0.707f);

        Log.d(TAG, "Go to point B");
        moveTo(10.6, -8, 4.5, 0, 0, -0.707f, 0.707f);

        Log.d(TAG, "end");

    }


}

