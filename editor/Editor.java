package editor;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;

import javafx.scene.text.Text;

public class Editor extends Application
{
        static Scene scene;
        static Cursor cursor;
        static Group root;
        static Group textRoot;
        static TextBuffer inputted;
        static String filename;
        static ScrollBar scrollBar;
        static double layoutDifference;
        static MouseClickEventHandler mouseClickEventHandler;
        static EventHandler<KeyEvent> keyEventHandler;

        @Override
        public void start(Stage primaryStage)
        {
            root = new Group();
            textRoot = new Group();
            scene = new Scene(root, 500, 500, Color.WHITE);
            cursor = new Cursor();
            inputted = new TextBuffer();
            scrollBar = new ScrollBar();
            keyEventHandler = new KeyEventHandler();
            mouseClickEventHandler = new MouseClickEventHandler();
            layoutDifference = 0;

            scene.setOnKeyTyped(keyEventHandler);
            scene.setOnKeyPressed(keyEventHandler);
            scene.setOnMouseClicked(mouseClickEventHandler);

            scrollBar.setOrientation(Orientation.VERTICAL);
            scrollBar.setLayoutX(Editor.scene.getWidth() - scrollBar.getWidth());
            scrollBar.setMin(5);
            scrollBar.setMax(Editor.scene.getHeight());
            scrollBar.setVisibleAmount(scrollBar.getMax());
            scrollBar.setPrefHeight(Editor.scene.getHeight());

            root.getChildren().add(textRoot);
            root.getChildren().add(scrollBar);
            textRoot.getChildren().add(cursor.rectangle);

            cursor.makeCursorColorChange();

            File workFile = new File(filename);
            if (workFile.exists())
            {
                try{
                    FileReader reader = new FileReader(workFile);
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    int intRead;
                    while ((intRead = bufferedReader.read()) != -1) {
                        inputted.addText(new Text(Character.toString((char) intRead)));
                    }
                } catch (IOException e)
                {
                    System.out.println("Error when creating a file; exception was: " + e);
                }
            }
            else
            {
                save(); //this is equivalent to creating a new file.
            }

            inputted.render();

            cursor.setXY(inputted.currentNodeInfo()[0] + inputted.currentNodeInfo()[2] + 1, inputted.currentNodeInfo()[1]);

            changeTextLayout(Math.max(0, inputted.totalHeight() - Editor.scene.getHeight()));
            scrollBar.setValue(inputted.totalHeight());

            scene.widthProperty().addListener(new ChangeListener<Number>() {
                @Override public void changed(
                        ObservableValue<? extends Number> observableValue,
                        Number oldScreenWidth,
                        Number newScreenWidth) {
                        inputted.render();
                        scrollBar.setLayoutX(Editor.scene.getWidth() - scrollBar.getWidth());
                        cursor.setXY(inputted.currentNodeInfo()[0] + inputted.currentNodeInfo()[2], inputted.currentNodeInfo()[1]);
                }
            });
            scene.heightProperty().addListener(new ChangeListener<Number>() {
                @Override public void changed(
                        ObservableValue<? extends Number> observableValue,
                        Number oldScreenHeight,
                        Number newScreenHeight) {
                        scrollBar.setPrefHeight(Editor.scene.getHeight());
                }
            });

           scrollBar.valueProperty().addListener(new ChangeListener<Number>() {
                public void changed(
                        ObservableValue<? extends Number> observableValue,
                        Number oldValue,
                        Number newValue) {
                        scrollBar.setValue(newValue.doubleValue());
                        double by = newValue.doubleValue() / scrollBar.getMax();
                        double irrelevant = inputted.totalHeight() - scene.getHeight();
                        if (irrelevant > 0)
                        {
                            changeTextLayout(by * irrelevant);
                        }
                }
            });

            primaryStage.setTitle("Text Editor");
            primaryStage.setScene(scene);
            primaryStage.show();
        }

        public static void changeTextLayout(double by)
        {
                layoutDifference = by;
                textRoot.setLayoutY(-layoutDifference);
        }

        public static void adjustLayoutForTyping(double Y)
        {
            if (Y > Editor.layoutDifference + Editor.scene.getHeight() + inputted.fontHeight())
            {
                Editor.changeTextLayout(Y - Editor.scene.getHeight() + inputted.fontHeight());
                return;
            }
            else if (Y < Editor.layoutDifference)
            {
                Editor.changeTextLayout(Y);
                return;
            }
            Editor.scrollBar.setValue(Y + inputted.fontHeight());

        }

        public static void save()
        {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
                ArrayList<Text> write = inputted.contentsChar.copyTo();
                String text = "";
                for (int i = 0; i < inputted.charCount(); i++)
                {
                    text = text + write.get(i).getText();
                }
                writer.write(text);
                writer.close(); } catch (IOException e) {
                System.out.println("Error when saving: exception was: " + e);
            }
        }


    public static void main(String[] args)
        {
            if (args.length != 1) {
                System.out.println("No filename to edit.");
                System.exit(1);
            }
            filename = args[0];
            launch(args);
        }
}