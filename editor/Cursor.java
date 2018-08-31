package editor;

import javafx.animation.KeyFrame;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class Cursor {

    Rectangle rectangle;

    public Cursor()
    {
        rectangle = new Rectangle(5, 5, 1, 24.306640625);
    }

    public void setHeight(double height)
    {
        rectangle.setHeight(height);
    }

    public void setXY(double X, double Y)
    {
        rectangle.setX(X);
        rectangle.setY(Y);
    }

    public double[] coordinates()
    {
        return new double[] {rectangle.getX(), rectangle.getY()};
    }

    private class RectangleBlinkEventHandler implements EventHandler<ActionEvent> {

        int currentColorIndex = 0;

        public void changeColor() {
            Color[] colors = {Color.BLACK, Color.WHITE};
            rectangle.setFill(colors[currentColorIndex]);
            currentColorIndex = 1 - currentColorIndex;
        }

        @Override
        public void handle(ActionEvent event) {
            changeColor();
        }
    }

    public void makeCursorColorChange() {
        // Create a Timeline that will call the "handle" function of RectangleBlinkEventHandler
        // every half second.
        final Timeline timeline = new Timeline();
        // The rectangle should continue blinking forever.
        timeline.setCycleCount(Timeline.INDEFINITE);
        RectangleBlinkEventHandler cursorChange = new RectangleBlinkEventHandler();
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.5), cursorChange);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }

}