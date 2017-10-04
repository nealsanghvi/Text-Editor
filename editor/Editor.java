package editor;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;

/**
 * A JavaFX application that displays the letter the user has typed most recently in the center of
 * the window. Pressing the up and down arrows causes the font size to increase and decrease,
 * respectively.
 */
public class Editor extends Application {
    private static int WINDOW_WIDTH;
    private static int WINDOW_HEIGHT;
    private static int charXPos;
    private static int charYPos;
    private static int cursory;
    private static int cursorx;
    private final Rectangle cursor;
    private static LLText<Text> wordstring = new LLText<>();
    private static ArrayList<LLText.Node> lineindex = new ArrayList<>();
    private Group root = new Group();
    private Group textRoot = new Group();
    Text cursorLocation = wordstring.getCursorItem();
    Text cursorNext = wordstring.getCursorItem();
    private int fontSize;
    private static final int STARTING_FONT_SIZE = 12;
    private String fontName = "Verdana";
    public static String firstArg;
    public static OpenAndSave opensave;
    public static double textHeight;
    public static double scrollbarlength;
    private ScrollBar scrollBar = new ScrollBar();
    private UndoRedo undoRedo = new UndoRedo();


    public Editor() {

        WINDOW_HEIGHT = 500;
        WINDOW_WIDTH = 500;
        cursor = new Rectangle(0, 0);
        charXPos = 5;
        charYPos = 15;
        cursorx = 0;
        cursory = 15;

        cursorLocation = new Text(charXPos, charYPos, "cursor");
        cursorNext = new Text(charXPos, charYPos, "nextpos");
        fontSize = STARTING_FONT_SIZE;
    }

    public void render() {
        charXPos = 5;
        charYPos = 0;
        int spaceindex = 0;
        boolean isspace = false;
        lineindex.clear();

        LLText.Node spacelocation = null;

        LLText.Node reference = wordstring.sentinel.next;
        if (reference == wordstring.cursor) {
            reference = reference.next;
        }
        lineindex.add(reference);

        for (int i = 0; i <= wordstring.size(); i++) {


            Text thingtoAdd = (Text) reference.item;
            if (thingtoAdd == null) {

            } else if (thingtoAdd.getText().equals("\r")) {

                lineindex.add(reference);
                thingtoAdd.setFont(Font.font(fontName, fontSize));
                thingtoAdd.setX(charXPos);
                thingtoAdd.setY(charYPos);
                charXPos = 5;
                charYPos = charYPos + (int) Math.round((thingtoAdd.getLayoutBounds().getHeight()) / 2);
                if (!textRoot.getChildren().contains(thingtoAdd)) {
                    textRoot.getChildren().add(thingtoAdd);
                }
            } else {

                if (thingtoAdd.getText().equals(" ")) {
                    spacelocation = reference;
                    spaceindex = i;
                    isspace = true;

                }
                thingtoAdd.setFont(Font.font(fontName, fontSize));
                thingtoAdd.setTextOrigin(VPos.TOP);

                if (!textRoot.getChildren().contains(thingtoAdd)) {
                    textRoot.getChildren().add(thingtoAdd);
                }

                int textlength = (int) Math.round(thingtoAdd.getLayoutBounds().getWidth());
                int textheight = (int) Math.round(thingtoAdd.getLayoutBounds().getHeight());

                if (charYPos + textheight > WINDOW_HEIGHT - 5) {
                    double totalheight = lineindex.size() * (fontSize * 1.5);
                    scrollBar.setMax(totalheight + textheight);

                }

                if (charXPos + textlength > WINDOW_WIDTH - 5 - scrollbarlength) {

                    if (isspace) {
                        lineindex.add(spacelocation.next);
                        charXPos = 5;
                        charYPos += textheight;

                        for (int k = spaceindex + 1; k <= i; k++) {
                            Text resettext = (Text) spacelocation.next.item;
                            if (resettext == null) {


                            } else {
                                resettext.setFont(Font.font(fontName, fontSize));
                                resettext.setTextOrigin(VPos.TOP);

                                int resetlength = (int) Math.round(resettext.getLayoutBounds().getWidth());

                                resettext.setX(charXPos);
                                resettext.setY(charYPos);
                                charXPos = charXPos + resetlength;
                            }
                            spacelocation = spacelocation.next;
                        }
                        isspace = false;

                    } else {
                        lineindex.add(reference);
                        charXPos = 5;
                        charYPos += textheight;
                        thingtoAdd.setX(charXPos);
                        thingtoAdd.setY(charYPos);
                        charXPos = charXPos + textlength;
                        isspace = false;
                    }
                } else {
                    thingtoAdd.setX(charXPos);
                    thingtoAdd.setY(charYPos);
                    charXPos = charXPos + textlength;
                }


            }
            reference = reference.next;
        }
//        System.out.println(lineindex.size());
    }

    private void setCursor() {

        textHeight = fontSize * 1.25;
        cursor.setHeight(textHeight);
        cursor.setWidth(1);
        try {
            cursorLocation = wordstring.getPrevious();
            cursorNext = wordstring.getNext();
            if (cursorLocation == null) {
                cursorx = 5;
                cursory = 0;
            } else if (cursorLocation.getText().equals("\r") || cursorLocation.getText().equals("\n") || cursorLocation.getText().equals("\r\n")) {
                cursorx = 5;
                cursory = (int) Math.round(cursorLocation.getY()) + (int) Math.round(cursorLocation.getLayoutBounds().getHeight() / 2);
            } else if (cursorNext != null && cursorNext.getX() == 5 && cursorLocation.getY() < cursorNext.getY()) {
                cursorx = 5;
                cursory = (int) cursorNext.getY();
            } else {
                cursorx = (int) Math.round(cursorLocation.getX() + cursorLocation.getLayoutBounds().getWidth());
                cursory = (int) Math.round(cursorLocation.getY());
            }
        } catch (NullPointerException nullpoint) {
        }
        cursor.setX(cursorx);
        cursor.setY(cursory);

    }

    private class MouseClickEventHandler implements EventHandler<MouseEvent> {

        Text positionText;

        MouseClickEventHandler(Group root) {
            positionText = new Text("");
            positionText.setTextOrigin(VPos.BOTTOM);
            textRoot.getChildren().add(positionText);
        }


        @Override
        public void handle(MouseEvent mouseEvent) {

            double mousePressedX = mouseEvent.getX();
            double mousePressedY = mouseEvent.getY();
            int prevX = 0;
            int nextX = 0;

            int clickheight = (int) cursor.getHeight();

            try {
                int index = (int) (mousePressedY / clickheight);
                LLText.Node indextoiterate = lineindex.get(index);
                int currX = (int) (mousePressedX);
                int currIndex = index;
                while (indextoiterate != lineindex.get(currIndex + 1)) {
                    Text iterateprev = (Text) indextoiterate.item;
                    Text iteratenext = (Text) indextoiterate.next.item;
                    if (iterateprev == null) {
                        prevX = (int) cursor.getX();
                    } else if (iteratenext == null) {
                        nextX = (int) cursor.getX();
                    } else if (iteratenext == null && iterateprev == null) {
                        prevX = (int) cursor.getX();
                        nextX = (int) cursor.getX();
                    } else {
                        prevX = (int) iterateprev.getX();
                        nextX = (int) iteratenext.getX();
                    }

                    if (currX <= nextX && currX >= prevX) {
                        LLText.Node prevnode = indextoiterate;
                        wordstring.moveCursorLocation(prevnode);
                        render();
                        setCursor();
                        break;
                    }
                    indextoiterate = indextoiterate.next;
                }

                if (indextoiterate == lineindex.get(currIndex + 1)) {
                    wordstring.moveCursorLocation(lineindex.get(currIndex + 1).previous);
                    render();
                    setCursor();
                }
            } catch (IndexOutOfBoundsException e) {
            }
            render();
            setCursor();

        }
    }

    private class KeyEventHandler implements EventHandler<KeyEvent> {

        private static final int STARTING_TEXT_POSITION_X = 0;
        private static final int STARTING_TEXT_POSITION_Y = 0;

        private Text displayText = new Text(STARTING_TEXT_POSITION_X, STARTING_TEXT_POSITION_Y, "");

        KeyEventHandler(final Group root, int windowWidth, int windowHeight) {
            displayText = new Text(charXPos, charYPos, "");
            displayText.setTextOrigin(VPos.TOP);
            displayText.setFont(Font.font(fontName, fontSize));
            textRoot.getChildren().add(displayText);
            WINDOW_WIDTH = windowWidth;
            WINDOW_HEIGHT = windowHeight;
            render();
            setCursor();

        }


        @Override
        public void handle(KeyEvent keyEvent) {

            if (keyEvent.getEventType() == KeyEvent.KEY_TYPED) {

                String charTyped = keyEvent.getCharacter();

                if (charTyped.length() > 0 && charTyped.charAt(0) != 8 && !keyEvent.isShortcutDown()) {

                    Text textTyped = new Text(charXPos, charYPos, charTyped);

                    wordstring.addFrontCursor(textTyped);
                    undoRedo.add(textTyped, 1);
                    render();
                    setCursor();
                    keyEvent.consume();
                } else if (charTyped.equals("\r")) {

                    Text enterTyped = new Text(charXPos, charYPos, charTyped);

                    wordstring.addFrontCursor(enterTyped);
                    undoRedo.add(enterTyped, 1);
                    render();
                    setCursor();
                }
            } else if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {

                KeyCode code = keyEvent.getCode();

                if (code == KeyCode.UP) {
                    int afterheight = (int) (fontSize * 1.25);
                    int prevX = 0;
                    int nextX = 0;

                    try {
                        int index = (int) Math.round(((cursor.getY() - afterheight) / afterheight));
                        LLText.Node indextoiterate = lineindex.get(index);
                        int currX = (int) (cursor.getX());
                        int currIndex = index;
                        while (indextoiterate != lineindex.get(currIndex + 1)) {
                            Text iterateprev = (Text) indextoiterate.item;
                            Text iteratenext = (Text) indextoiterate.next.item;

                            if (iterateprev == null) {
                                prevX = 5;
                                nextX = (int) Math.round(iteratenext.getX());
                            } else if (iterateprev.getY() < iteratenext.getY()) {
                                prevX = 5;
                                nextX = (int) Math.round(iteratenext.getX());
                            } else {
                                prevX = (int) Math.round(iterateprev.getX());
                                nextX = (int) Math.round(iteratenext.getX());
                            }

                            if (currX <= nextX && currX >= prevX) {
                                LLText.Node prevnode = indextoiterate;
                                wordstring.moveCursorLocation(prevnode);
                                render();
                                setCursor();
                                break;
                            }
                            indextoiterate = indextoiterate.next;
                        }

                        if (indextoiterate == lineindex.get(currIndex + 1)) {
                            wordstring.moveCursorLocation(lineindex.get(currIndex + 1).previous);
                            render();
                            setCursor();
                        }
                    } catch (IndexOutOfBoundsException e) {
                    }
                    render();
                    setCursor();
                }

                if (code == KeyCode.DOWN) {
                    int prevX = 0;
                    int nextX = 0;

                    int beforeheight = (int) (fontSize * 1.25);

                    try {

                        int index = (int) Math.round((cursor.getY() + beforeheight) / beforeheight);
//                        System.out.print(index);
                        //Tried to debug this, but it says that size of the array list is currently 8 for example, but
                        //every time I try to index element 7 of my array list, it says it's out of bounds. I left the print
                        //statements so you can see for yourself.
                        LLText.Node indextoiterate = lineindex.get(index);
                        int currX = (int) (cursor.getX());
                        int currIndex = index;
                        while (indextoiterate != lineindex.get(currIndex + 1)) {
                            Text iterateprev = (Text) indextoiterate.item;
                            Text iteratenext = (Text) indextoiterate.next.item;

                            if (iterateprev.getY() < iteratenext.getY()) {
                                prevX = 5;
                                nextX = (int) Math.round(iteratenext.getX());
                            } else {
                                prevX = (int) Math.round(iterateprev.getX());
                                nextX = (int) Math.round(iteratenext.getX());
                            }

                            if (currX <= nextX && currX >= prevX) {
                                LLText.Node prevnode = indextoiterate;
                                wordstring.moveCursorLocation(prevnode);
                                render();
                                setCursor();
                                break;
                            }
                            indextoiterate = indextoiterate.next;
                        }
                        if (indextoiterate == lineindex.get(currIndex + 1)) {
                            wordstring.moveCursorLocation(lineindex.get(currIndex + 1).previous);
                            render();
                            setCursor();
                        }

                    } catch (IndexOutOfBoundsException e) {
//                        System.out.println("OUT OF BOUNDS");
                    }
                    render();
                    setCursor();
                }

                if (code == KeyCode.LEFT) {
                    if (wordstring.getPrevious() == null) {
                        return;
                    }

                    wordstring.moveLeftCursor();
                    render();
                    setCursor();

                } else if (code == KeyCode.RIGHT) {
                    if (wordstring.getNext() == null) {
                        return;
                    }


                    wordstring.moveRightCursor();
                    render();
                    setCursor();

                } else if (keyEvent.getCode() == KeyCode.BACK_SPACE) {

                    if (wordstring.getPrevious() == null) {
                        return;
                    }

                    Text removedletter = wordstring.deleteBackCursor();

                    textRoot.getChildren().remove(removedletter);
                    undoRedo.add(removedletter, 0);
                    render();
                    setCursor();
                    keyEvent.consume();

                } else if (keyEvent.isShortcutDown()) {
                    if (code == KeyCode.MINUS) {
                        if (fontSize - 4 <= 0) {
                            return;
                        } else {
                            fontSize -= 4;
                            render();
                            setCursor();
                        }

                    } else if (code == KeyCode.PLUS || code == KeyCode.EQUALS) {
                        fontSize += 4;
                        render();
                        setCursor();
                    } else if (code == KeyCode.P) {
                        setCursor();
                        System.out.println(cursorx + "," + cursory);

                    } else if (code == KeyCode.S) {
                        opensave.save();
                        render();
                        setCursor();
                    } else if (code == KeyCode.Z) {
                        undoRedo.undo();
                        Text undotext = undoRedo.returntext();
                        int action = undoRedo.returnaction();
                        if (action == 0) {

                            wordstring.addFrontCursor(undotext);
                            if (!textRoot.getChildren().contains(undotext)) {
                                textRoot.getChildren().add(undotext);
                            }
                            render();
                            setCursor();
                        } else {
                            wordstring.deleteBackCursor();
                            textRoot.getChildren().remove(undotext);
                            render();
                            setCursor();
                        }

                    } else if (code == KeyCode.Y) {
                        if (!undoRedo.nodestack2.isEmpty()) {
                            Text redotext = undoRedo.returntext();
                            int action = undoRedo.returnaction();
                            undoRedo.redo();
                            if (action == 1) {
                                wordstring.addFrontCursor(redotext);
                                if (!textRoot.getChildren().contains(redotext)) {
                                    textRoot.getChildren().add(redotext);
                                }
                                render();
                                setCursor();
                            } else {
                                wordstring.deleteBackCursor();
                                render();
                                setCursor();
                            }
                        }
                    }
                }

            }
        }

    }


    private class RectangleBlinkEventHandler implements EventHandler<ActionEvent> {
        private int currentColorIndex = 0;
        private Color[] boxColors =
                {Color.BLACK, Color.WHITE};

        RectangleBlinkEventHandler() {
            changeColor();
        }

        private void changeColor() {
            cursor.setFill(boxColors[currentColorIndex]);
            currentColorIndex = (currentColorIndex + 1) % boxColors.length;
        }

        @Override
        public void handle(ActionEvent event) {
            changeColor();
        }
    }

    public void makeRectangleColorChange() {

        final Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        RectangleBlinkEventHandler cursorChange = new RectangleBlinkEventHandler();
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(.5), cursorChange);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }


    @Override
    public void start(Stage primaryStage) {

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT, Color.WHITE);

        root.getChildren().add(textRoot);

        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldScreenWidth,
                    Number newScreenWidth) {
                WINDOW_WIDTH = newScreenWidth.intValue();
                scrollBar.setLayoutX(WINDOW_WIDTH - scrollBar.getLayoutBounds().getWidth());
                render();
                setCursor();
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldScreenHeight,
                    Number newScreenHeight) {
                WINDOW_HEIGHT = newScreenHeight.intValue();
                scrollBar.setPrefHeight(WINDOW_HEIGHT);
                render();
                setCursor();
            }
        });


        EventHandler<KeyEvent> keyEventHandler =
                new KeyEventHandler(textRoot, WINDOW_WIDTH, WINDOW_HEIGHT);

        scene.setOnKeyTyped(keyEventHandler);
        scene.setOnKeyPressed(keyEventHandler);
        scene.setOnMouseClicked(new MouseClickEventHandler(textRoot));

        opensave = new OpenAndSave(firstArg, wordstring);
        opensave.open();
        wordstring.moveToSentinel();
        render();
        setCursor();


        textRoot.getChildren().add(cursor);


        scrollBar.setOrientation(Orientation.VERTICAL);

        scrollBar.setMin(0);
        scrollBar.setMax(0);
        scrollBar.setPrefHeight(WINDOW_HEIGHT);

        root.getChildren().add(scrollBar);

        double usableScreenWidth = WINDOW_WIDTH - scrollBar.getLayoutBounds().getWidth();
        scrollbarlength = scrollBar.getLayoutBounds().getWidth();
        scrollBar.setLayoutX(usableScreenWidth);

        scrollBar.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldValue,
                    Number newValue) {
                textRoot.setLayoutY(-newValue.doubleValue());
            }
        });

        makeRectangleColorChange();

        primaryStage.setTitle("Editor");

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("No File Name Given");
            System.exit(1);
        } else if (args.length >= 1) {
            firstArg = args[0];
            if (args.length > 1) {
                String secondArg = args[1];
                if (secondArg == "debug") {
                    System.out.println("DEBUG");
                }
            }

        }
        launch(args);
    }
}

