package editor;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;

/** An EventHandler to handle keys that get pressed. */
public class KeyEventHandler implements EventHandler<KeyEvent>
{

    @Override
    public void handle(KeyEvent keyEvent) {
        if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
            // Arrow keys should be processed using the KEY_PRESSED event, because KEY_PRESSED
            // events have a code that we can check (KEY_TYPED events don't have an associated
            // KeyCode).
            KeyCode code = keyEvent.getCode();
            if (keyEvent.isShortcutDown() && code == KeyCode.P)
            {
                double[] coordinates = Editor.cursor.coordinates();
                System.out.println((int) coordinates[0] + ", " + (int) (Editor.inputted.lineCount * Editor.inputted.fontHeight()));
            }
            else if (keyEvent.isShortcutDown() && code == KeyCode.S)
            {
                Editor.save();
            }
            else if (keyEvent.isShortcutDown() && code == KeyCode.Z)
            {
                Editor.inputted.enableRedo();
                Editor.inputted.undo();
                Editor.inputted.render();
                double[] information = Editor.inputted.currentNodeInfo();
                Editor.cursor.setXY(information[0] + information[2] + 1, information[1]);
            }
            else if (keyEvent.isShortcutDown() && code == KeyCode.Y)
            {
                Editor.inputted.redo();
                Editor.inputted.render();
                double[] information = Editor.inputted.currentNodeInfo();
                Editor.cursor.setXY(information[0] + information[2] + 1, information[1]);
            }
            else if (keyEvent.isShortcutDown() && (code == KeyCode.PLUS || code == KeyCode.EQUALS))
            {
                Editor.inputted.changeFontSize(4);
                Editor.inputted.render();
                double[] information = Editor.inputted.currentNodeInfo();
                Editor.cursor.setXY(information[0] + information[2] + 1, information[1]);
                Editor.cursor.setHeight(Editor.inputted.fontHeight());
            }
            else if (keyEvent.isShortcutDown() && code == KeyCode.MINUS)
            {
                Editor.inputted.changeFontSize(-4);
                Editor.inputted.render();
                double[] information = Editor.inputted.currentNodeInfo();
                Editor.cursor.setXY(information[0] + information[2] + 1, information[1]);
                Editor.cursor.setHeight(Editor.inputted.fontHeight());
            }
            else if (code == KeyCode.BACK_SPACE && Editor.inputted.charCount() != 0)
            {
                Text removed = Editor.inputted.removeCharacter();
                Editor.inputted.disableRedo();
                Editor.inputted.render();
                Editor.cursor.setXY(Math.min(Editor.scene.getWidth() - 5, Editor.inputted.currentNodeInfo()[0] + Editor.inputted.currentNodeInfo()[2]),
                        Editor.inputted.currentNodeInfo()[1]);
                Editor.inputted.push(removed, "remove");
                if (removed != null)
                {
                    Editor.adjustLayoutForTyping(removed.getY());
                }
            }
            else if (code == KeyCode.LEFT && Editor.inputted.charCount() != 0)
            {
                double coordinates[] = Editor.inputted.goLeft();
                Editor.cursor.setXY(Math.min(Editor.scene.getWidth() - 5, coordinates[0]), coordinates[1]);
            }
            else if (code == KeyCode.RIGHT && Editor.inputted.charCount() != 0)
            {
                double coordinates[] = Editor.inputted.goRight();
                Editor.cursor.setXY(Math.min(Editor.scene.getWidth() - 5, coordinates[0]), coordinates[1]);
            }
            else if (code == KeyCode.DOWN)
            {
                double[] coordinates = Editor.inputted.goUpOrDown(1);
                Editor.cursor.setXY(coordinates[0], coordinates[1]);
                Editor.scrollBar.setValue(coordinates[1] + Editor.inputted.fontHeight());
            }
            else if (code == KeyCode.UP)
            {
                double[] coordinates = Editor.inputted.goUpOrDown(-1);
                Editor.cursor.setXY(coordinates[0], coordinates[1]);
                Editor.scrollBar.setValue(coordinates[1] + Editor.inputted.fontHeight());
            }
        }

        else if (keyEvent.getEventType() == KeyEvent.KEY_TYPED && keyEvent.getEventType() != KeyEvent.KEY_PRESSED) {
            // Use the KEY_TYPED event rather than KEY_PRESSED for letter keys, because with
            // the KEY_TYPED event, javafx handles the "Shift" key and associated
            // capitalization.
            String typed = keyEvent.getCharacter();
            Text typedText = new Text(typed);
            if (typed.equals("\r"))
            {
                Editor.inputted.addText(typedText);
                Editor.inputted.disableRedo();
                Editor.inputted.render();
                Editor.cursor.setXY(Math.min(Editor.scene.getWidth() - 5, typedText.getX() + typedText.getLayoutBounds().getWidth()),
                                    typedText.getY());
                Editor.inputted.push(typedText, "add");
                Editor.adjustLayoutForTyping(typedText.getY());
            }
            else if (!keyEvent.isShortcutDown() && typed.charAt(0) != 8) {
                // Ignore control keys, which have non-zero length, as well as the backspace
                // key, which is represented as a character of value = 8 on Windows.
                Editor.inputted.addText(typedText);
                Editor.inputted.disableRedo();
                Editor.inputted.render();
                Editor.cursor.setXY(Math.min(Editor.scene.getWidth() - 5, typedText.getX() + typedText.getLayoutBounds().getWidth()),
                        typedText.getY());
                Editor.inputted.push(typedText, "add");
                Editor.adjustLayoutForTyping(typedText.getY());
            }
            keyEvent.consume();
        }
    }
}
