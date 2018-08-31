package editor;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class MouseClickEventHandler implements EventHandler<MouseEvent> {

        double mouseX;
        double mouseY;

        @Override
        public void handle(MouseEvent mouseEvent) {
            mouseX = mouseEvent.getX();
            mouseY = mouseEvent.getY() + Editor.layoutDifference;
            setPosition();
        }

        public void setPosition()
        {
            int lineNeeded = (int) ((mouseY - 5) / Editor.inputted.fontHeight());
            if (Editor.inputted.charCount() != 0)
            {
                try
                {
                    Editor.inputted.getContents().setCurrentNode(lineNeeded);
                } catch (NullPointerException e)
                {
                    Editor.inputted.getContents().setCurrentNodetoLast();
                }
                double prevX = 5;
                double currX = Editor.inputted.currentNodeInfo()[0] + Editor.inputted.currentNodeInfo()[2];
                try
                {
                    while (mouseX > currX)
                    {
                        Editor.inputted.getContents().curNodeToRight();
                        prevX = currX;
                        currX = Editor.inputted.nextNodeInfo()[0];
                        if (currX < prevX)
                        {
                            //Editor.inputted.goLeft();
                            Editor.cursor.setXY(Editor.inputted.currentNodeInfo()[0] + Editor.inputted.currentNodeInfo()[2],
                                    Editor.inputted.currentNodeInfo()[1]);
                            return;
                        }
                    }
                    if (mouseX - prevX < currX - mouseX)
                    {
                        Editor.inputted.goLeft();
                    }
                } catch (NullPointerException e)
                {
                    Editor.cursor.setXY(Editor.inputted.nextNodeInfo()[0], Editor.inputted.nextNodeInfo()[1]);
                }
                Editor.cursor.setXY(Editor.inputted.nextNodeInfo()[0], Editor.inputted.nextNodeInfo()[1]);
            }
        }

        public void setCoordinates(double[] coordinates)
        {
            mouseX = coordinates[0];
            mouseY = coordinates[1];
        }
}
