package org.usfirst.frc.team1294.robot.subsystems;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.usfirst.frc.team1294.robot.vision.GearGripPipeline;

import java.util.Optional;
import java.util.stream.Stream;

public class GearVisionSubsystem extends Subsystem {

    private static final int IMG_WIDTH = 320;
    private static final int IMG_HEIGHT = 240;
    private final CvSink video;

    private final UsbCamera camera;
    private final CameraServer cameraServer;
    private final CvSource outputStream;
    private final GearGripPipeline gearGripPipeline;
    private final Mat frame;
    private boolean targetFound;
    private int pixelsFromCenter;

    public GearVisionSubsystem() {
        super("GearVisionSubsystem");

        cameraServer = CameraServer.getInstance();
        camera = cameraServer.startAutomaticCapture();
        camera.setFPS(5);
        camera.setResolution(IMG_WIDTH, IMG_HEIGHT);
        video = cameraServer.getVideo(camera);
        frame = new Mat();

        outputStream = cameraServer.putVideo("ProcessedVideo", IMG_WIDTH, IMG_HEIGHT);

        gearGripPipeline = new GearGripPipeline();
    }

    @Override
    protected void initDefaultCommand() {

    }

    public void process() {
        // grab a frame
        video.grabFrame(frame);

        // run the grip pipeline
        gearGripPipeline.process(frame);

        // outline all the contours
        Imgproc.drawContours(frame, gearGripPipeline.filterContoursOutput(), 0, new Scalar(160,160,160));

        // get the bounding rect for each contour
        Stream<Rect> rects = gearGripPipeline
                .filterContoursOutput()
                .stream()
                .map(contour -> Imgproc.boundingRect(contour));

        // get every combination of pairs
        Stream<PairOfRect> pairs = rects
                .flatMap(i -> rects.filter(j -> !i.equals(j)).map(j -> new PairOfRect(i, j)));

        // get the best (lowest) scoring pair
        Optional<PairOfRect> bestPair = pairs.min((a, b) -> scorePair(b).compareTo(scorePair(a)));

        this.targetFound = bestPair.isPresent();
        if (bestPair.isPresent()) {
            PairOfRect pair = bestPair.get();

            // calculate how many pixels off center
            pixelsFromCenter = (int) (pair.centerY() - IMG_WIDTH / 2);

            // draw a rectangle around the best pair
            Imgproc.rectangle(frame, pair.topLeft(), pair.bottomRight(), new Scalar(0,0,255), 2);
        } else {
            pixelsFromCenter = 0;
        }

        // output the frame
        outputStream.putFrame(frame);
    }

    public boolean isTargetFound() {
        return targetFound;
    }

    public int getPixelsFromCenter() {
        return pixelsFromCenter;
    }

    private class PairOfRect {
        public Rect a;
        public Rect b;

        public PairOfRect(Rect a, Rect b) {
            this.a = a;
            this.b = b;
        }

        public double centerY() {
            return a.y + ((double) (b.y + b.width - a.y)) / 2;
        }

        public Point topLeft() {
            return new Point(a.x, a.y);
        }

        public Point bottomRight() {
            return new Point(b.x + b.width, b.y + b.height);
        }
    }

    private Double scorePair(PairOfRect pair) {
        double score = 0;

        // difference in height
        score += Math.abs(pair.a.height - pair.b.height);

        // difference in width
        score += Math.abs(pair.a.width - pair.b.width);

        // difference in center Y
        double rect1CenterY = pair.a.y + pair.a.height / 2;
        double rect2CenterY = pair.b.y + pair.b.height / 2;
        score += Math.abs(rect1CenterY - rect2CenterY);

        // difference between combined rect width:height ratio and the ideal of 2.05
        double combinedWidth = pair.a.x + pair.b.x + pair.b.width;
        double combinedHeight = pair.a.y + pair.b.y + pair.b.height;
        double combinedRatio = combinedWidth / combinedHeight;
        score += Math.abs(combinedRatio - 2.05);

        return score;
    }
}
