package learn_opencv_d;

import java.io.ByteArrayInputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import learn_opencv_d.utils.Utils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class FXController {
	@FXML // stating that this variable connects to .fxml file
	private Button button;
	@FXML
	private ImageView currentFrame;
	@FXML
	private CheckBox grayscale;
	@FXML
	private CheckBox logoCheckBox;
	
	Mat logo;
	
	
	// a timer for acquiring the video stream
	private ScheduledExecutorService timer;
	
	// the OpenCV object that realizes the video capture
	private VideoCapture capture = new VideoCapture();
	
	// a flag to change the button behavior
	private boolean cameraActive = false;
	// the id of the camera to be used
	private static int cameraId = 0;
	
	
	
	
	/**
	 * The action triggered by pushing the button on the GUI
	 *
	 * @param event
	 *            the push button event
	 */
	@FXML
	protected void startCamera(ActionEvent event){
		if (!this.cameraActive){

			this.capture.open(cameraId);
			
			// is the video stream available?
			if (this.capture.isOpened()){
				this.cameraActive = true;
				
				// grab a frame every 33 ms (30 frames/sec)
				Runnable frameGrabber = new Runnable() {
					@Override
					public void run(){
						// effectively grab and process a single frame
						Mat frame = grabFrame();
						
						
						
						// convert and show the frame
						MatOfByte buffer = new MatOfByte();
						Imgcodecs.imencode(".png", frame, buffer);
						Image imageToShow = new Image(new ByteArrayInputStream(buffer.toArray()));
						
						
						
						
						Platform.runLater(new Runnable() {
					        @Override public void run() { currentFrame.setImage(imageToShow); }
						});
						//Image imageToShow = Utils.mat2Image(frame);
						//updateImageView(currentFrame, imageToShow);
						
						System.out.println("running ");
					}
				};
				
				this.timer = Executors.newSingleThreadScheduledExecutor();
				this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);
				//frameGrabber.run();
				
				// update the button content
				this.button.setText("Stop Camera");
			}
			else{
				// log the error
				System.err.println("Impossible to open the camera connection...");
			}
		}
		else{
			// the camera is not active at this point
			this.cameraActive = false;
			// update again the button content
			this.button.setText("Start Camera");
			
			// stop the timer
			this.stopAcquisition();
		}
	}
	
	@FXML
	protected void loadLogo()
	{
	 if (logoCheckBox.isSelected())
	    this.logo = Imgcodecs.imread("resources/Poli.png");
	}
	
	/**
	 * Get a frame from the opened video stream (if any)
	 *
	 * @return the {@link Mat} to show
	 */
	private Mat grabFrame(){
		// init everything
		Mat frame = new Mat();
		
		// check if the capture is open
		if (this.capture.isOpened()){
			try{
				// read the current frame
				System.out.println("reading footage");
				this.capture.read(frame);
				
				// if the frame is not empty, process it
				if (!frame.empty()){
					System.out.println("converting->grayscale");
					processFrame(frame);
					
				}
				
			}
			catch (Exception e)
			{
				// log the error
				System.err.println("Exception during the image elaboration: " + e);
			}
		}
		
		return frame;
	}
	
	/**
	 * Process the frame and returns it back
	 * @param frame
	 * 				the {@link Mat} to process
	 * @return the {@link Mat} processed and to be returned
	 */
	private Mat processFrame(Mat frame) {
		if(grayscale.isSelected()) {
			Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);	
		}
		
		
		return frame;
	}
		
	/**
	 * Stop the acquisition from the camera and release all the resources
	 */
	private void stopAcquisition()
	{
		System.out.println("stopped aqquiring");
		if (this.timer!=null && !this.timer.isShutdown())
		{
			try
			{
				// stop the timer
				this.timer.shutdown();
				this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
				
			}
			catch (InterruptedException e)
			{
				// log any exception
				System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
			}
		}
		
		if (this.capture.isOpened())
		{
			// release the camera
			this.capture.release();
		}
	}
	
	/**
	 * Update the {@link ImageView} in the JavaFX main thread
	 * 
	 * @param view
	 *            the {@link ImageView} to update
	 * @param image
	 *            the {@link Image} to show
	 */
	@SuppressWarnings("unused")
	private void updateImageView(ImageView view, Image image)
	{
		Utils.onFXThread(view.imageProperty(), image);
	}
	
	/**
	 *	Flip the matrix horizontally - vertically
	 
	private void flipMatrix(Mat mat, boolean horizontally, boolean vertically) {
		
		
		for(int i = 0 ; i < mat.rows(); ++i) {
			for(int j = 0 ; j < mat.cols()/2; ++j) {
				mat.get(i, j);
			}
		}
	}*/
	
	/**
	 * On application close, stop the acquisition from the camera
	 */
	protected void setClosed(){
		this.stopAcquisition();
	}
	

}
