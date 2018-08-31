package editor;

import javafx.geometry.VPos;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import java.util.ArrayList;

//A text buffer which holds all the data that was inputted.
public class TextBuffer {
    FastLinkedList<Text> contentsChar = new FastLinkedList<>();
    Stack<Text[]> undo = new Stack<>();
    Stack<Text[]> redo = new Stack<>();
    Stack<double[]> undoCoordinates = new Stack<>(); //contains pointers to positions
    Stack<double[]> redoCoordinates = new Stack<>();
    int charCount;
    int fontSize;
    String fontName;
    int lineCount;
    boolean redoAbility;

    public TextBuffer() {
        charCount = 0;
        fontSize = 20;
        fontName = "Verdana";
        lineCount = 0;
        redoAbility = true;
    }

    public int charCount() {
        return charCount;
    }

    //Returns the FastLinkedList data structure that holds the contents of the buffer.
    public FastLinkedList getContents() {
        return contentsChar;
    }

    //Adds a single Text object to the contents in the position that is pointed by the currentNode pointer.
    public void addText(Text c) {
        c.setTextOrigin(VPos.TOP);
        contentsChar.add(c);
        Editor.textRoot.getChildren().add(c);
        charCount = charCount + 1;
    }

    //Removes a Text object from the contents from a position that is pointed by the currentNode pointer.
    public Text removeCharacter() {
        if (contentsChar.currentNode() != null) {
            charCount = charCount - 1;
            Text removed = contentsChar.remove();
            Editor.textRoot.getChildren().remove(removed);
            return removed;
        }
        return null;
    }

    //Used for adding the last 100 actions to the stack, so that they can be undone/redone when an undo/redo command is initiated.
    public void push(Text c, String operation) {
        if (operation.equals("add")) {
            undoCoordinates.push(Editor.cursor.coordinates());
            undo.push(new Text[]{(new Text("a")), c}); //using a character "a" to represent that the character was typed.
        }
        if (operation.equals("remove")) {
            undoCoordinates.push(Editor.cursor.coordinates());
            undo.push(new Text[]{(new Text("r")), c}); //using a character "r" to represent that the character was deleted.
        }
    }

    //Undoing the latest action.
    public void undo() {
        try
        {
            if (!undo.isEmpty()) {
                redoAbility = true;
                Text[] toBeUndone = undo.pop();
                Editor.mouseClickEventHandler.setCoordinates(undoCoordinates.pop());
                Editor.mouseClickEventHandler.setPosition();
                if (toBeUndone[0].getText().equals("a")) {
                    removeCharacter();
                }
                if (toBeUndone[0].getText().equals("r")) {
                    addText(toBeUndone[1]);
                }
                redoCoordinates.push(Editor.cursor.coordinates());
                redo.push(toBeUndone);
            }
        } catch (NullPointerException e)
            {} //just prevents an exception from occurring
    }

    //Redoing the latest undone action.
    public void redo() {
        try {
            if (redoAbility && !redo.isEmpty()) {
                Text[] toBeRedone = redo.pop();
                Editor.mouseClickEventHandler.setCoordinates(redoCoordinates.pop());
                Editor.mouseClickEventHandler.setPosition();
                if (toBeRedone[0].getText().equals("a")) {
                    addText(toBeRedone[1]);
                }
                if (toBeRedone[0].getText().equals("r")) {
                    removeCharacter();
                }
            }
        } catch (NullPointerException e)
        {} //Just prevents an exception from occurring.
    }

    //Since redoing should not be always available, redoAbility is disabled whenever an action other than undo is done.
    public void disableRedo() {
        redoAbility = false;
        redo.clear();
    }

    public void enableRedo()
    {
        redoAbility = true;
    }

    //Used to change the position of the currentNode pointer by one step to the left and return the coordinates of where
    //the cursor is supposed to be.
    public double[] goLeft() {
        try {
            double[] currentInfo = currentNodeInfo();
            contentsChar.curNodeToLeft();
            return new double[]{currentInfo[0], currentInfo[1]};
        } catch (NullPointerException e) {
            return new double[]{nextNodeInfo()[0], nextNodeInfo()[1]};
        } //exception is thrown whenever a currentNode is already pointing on the first character and there is virtually no place to go left.
    }

    //Used to change the position of the currentNode pointer by one step to the right and return the coordinates of where
    //the cursor is supposed to be.
    public double[] goRight() {
        try {
            contentsChar.curNodeToRight();
            double[] coordinates = new double[]{nextNodeInfo()[0], contentsChar.getNext().getY()};
            return coordinates;
        } catch (NullPointerException e) {
            double[] currentInfo = currentNodeInfo();
            return new double[]{currentInfo[0] + currentInfo[2], currentInfo[1]};
        } //exception is thrown whenever a currentNode is pointing on the last character and there is no place to go right.
    }

    //Implements the cursor movement by up and down arrows.
    public double[] goUpOrDown(int by) {
        double posX = nextNodeInfo()[0];
        int nextLine = getCurrLine() + by;
        try {
            contentsChar.setCurrentNode(nextLine);
        } catch (NullPointerException e) {
            return nextNodeInfo();
        }
        if (posX == 5.0)
        {
            return currentNodeInfo();
        }
        double prevX = 5;
        double currX = Editor.inputted.nextNodeInfo()[0];
        try {
            while (posX > currX) {
                Editor.inputted.getContents().curNodeToRight();
                prevX = currX;
                currX = Editor.inputted.nextNodeInfo()[0];
                if (currX < prevX) {
                    return new double[]{Editor.inputted.currentNodeInfo()[0] + Editor.inputted.currentNodeInfo()[2], Editor.inputted.nextNodeInfo()[1]};
                }
            }
            if (posX - prevX < currX - posX) {
                Editor.inputted.goLeft();
            }
        } catch (NullPointerException e) {
            return new double[]{Editor.inputted.nextNodeInfo()[0], Editor.inputted.nextNodeInfo()[1]};
        }
        return new double[]{Editor.inputted.currentNodeInfo()[0] + Editor.inputted.currentNodeInfo()[2], Editor.inputted.nextNodeInfo()[1]};
    }

    //Rearranges the whole thing, word wraps the text and is used to display the text.
    public void render() {
        double cumWidth = 5;
        double height = 5;
        lineCount = 0;
        int index = 0;
        contentsChar.clearLinks();
        contentsChar.clearPointers();
        contentsChar.copyPointersTo();
        if (charCount != 0) {
            putPointer(0, index);
        }
        ArrayList<Text> contentsArray = contentsChar.copyTo(); //copying the elements of linked list into an array so that get takes const time.
        while (index < charCount) {
            contentsArray.get(index).setFont(Font.font(fontName, fontSize));
            if (cumWidth + contentsArray.get(index).getLayoutBounds().getWidth() >= Editor.scene.getWidth() - 5 - Editor.scrollBar.getWidth()
                    && !contentsArray.get(index).getText().equals(" ")) {
                cumWidth = 5;
                lineCount = lineCount + 1;
                height = lineCount * fontHeight() + 5;
                index = index - wordWrapLength(index, contentsArray);
                putPointer(lineCount, index);
            }
            if (contentsArray.get(index).getText().equals("\r")) {
                cumWidth = 5;
                lineCount = lineCount + 1;
                height = lineCount * fontHeight() + 5;
                //putPointer(lineCount, index - 1);
            }
            contentsArray.get(index).setX(cumWidth);
            contentsArray.get(index).setY(height);
            cumWidth = cumWidth + contentsArray.get(index).getLayoutBounds().getWidth();
            index = index + 1;
        }
            Editor.scrollBar.setMax(Math.max(Editor.scene.getHeight(), totalHeight()));
            if (totalHeight() + fontHeight() <= Editor.scene.getHeight())
            {
                Editor.changeTextLayout(0);
            }
    }

    public int wordWrapLength(int index, ArrayList<Text> contentsArray) {
        int length = 0;
        double cumWidth = 5;
        try {
            while (!contentsArray.get(index).getText().equals(" ")) {
                cumWidth = cumWidth + contentsArray.get(index).getLayoutBounds().getWidth();
                if (cumWidth >= Editor.scene.getWidth() - Editor.scrollBar.getWidth() - 5) {
                    return 0;
                }
                length = length + 1;
                index = index - 1;
            }
        } catch (RuntimeException e) //done to avoid the error which happens when word wrap is attempted on a single word on the first line
        {
            return 0;
        }
        return length - 1;
    }

    public void changeFontSize(int changeBy) {
        fontSize = Math.max(0, fontSize + changeBy);
    }

    public double[] currentNodeInfo() {
        if (contentsChar.currentNode() == null) {
            return new double[]{5, 5, 0};
        }
        return new double[]{contentsChar.currentNode().getX(),
                contentsChar.currentNode().getY(),
                contentsChar.currentNode().getLayoutBounds().getWidth()};
    }

    public double[] nextNodeInfo() {
        if (contentsChar.getNext() == null) {
            return new double[]{currentNodeInfo()[0] + currentNodeInfo()[2], currentNodeInfo()[1]};
        }
        return new double[]{contentsChar.getNext().getX(), contentsChar.getNext().getY(),
                contentsChar.getNext().getLayoutBounds().getWidth()};
    }

    public double[] previousNodeInfo() {
        if (contentsChar.getPrevious() == null)
        {
            return new double[]{5, 5, 0};
        }
        return new double[]{contentsChar.getPrevious().getX(), contentsChar.getPrevious().getY(),
                            contentsChar.getPrevious().getLayoutBounds().getWidth()};
    }

    public double fontHeight() {
        Text irrelevant = new Text("l");
        irrelevant.setFont(Font.font(fontName, fontSize));
        return irrelevant.getLayoutBounds().getHeight();
    }

    public int getCurrLine() {
        return (int) Math.round((nextNodeInfo()[1] - 5) / fontHeight());
    }

    public double totalHeight() {
        return 5 + (lineCount + 1) * fontHeight();
    }

    public double lineCount()
    {
        return lineCount;
    }


    public void putPointer(int line, int index) {
        contentsChar.pointers.put(line, contentsChar.links.get(index));
    }

}