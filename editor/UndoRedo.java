package editor;

import java.util.Stack;

import javafx.scene.text.Text;

/**
 * Created by Nealibob on 3/7/16.
 */
public class UndoRedo {
    public static Stack<Text> nodestack1 = new Stack<>();
    public static Stack<Text> nodestack2 = new Stack<>();
    public static Stack<Integer> actionstack1 = new Stack<>();
    public static Stack<Integer> actionstack2 = new Stack<>();

    public UndoRedo() {
    }

    public void add(Text letter, Integer action) {

        nodestack1.add(letter);
        actionstack1.add(action);
    }

    public void undo() {
        if (nodestack1.isEmpty()) {

        } else {
            Text r = nodestack1.pop();
            nodestack2.add(r);
            Integer s = actionstack1.pop();
            actionstack2.add(s);
        }
    }

    public void redo() {
        if (nodestack2.isEmpty()) {

        } else {
            Text r = nodestack2.pop();
            nodestack1.add(r);
            Integer s = actionstack2.pop();
            actionstack1.add(s);
        }
    }

    public Text returntext() {
        return nodestack2.peek();
    }

    public Integer returnaction() {
        return actionstack2.peek();
    }
}
