package editor;

import javafx.geometry.VPos;
import javafx.scene.text.Text;

import java.io.*;

/**
 * Created by Nealibob on 3/6/16.
 */
public class OpenAndSave {
    private File filename;
    private LLText<Text> linkedlist;

    public OpenAndSave(String filename, LLText<Text> linkedlist) {
        this.filename = new File(filename);
        this.linkedlist = linkedlist;
    }

    public void open() {
        try {
            if (!this.filename.exists()) {
                this.filename.createNewFile();
            }

            FileReader reader = new FileReader(this.filename);
            BufferedReader bufferedReader = new BufferedReader(reader);
            int intRead = -1;
            while ((intRead = bufferedReader.read()) != -1) {
                char charRead = (char) intRead;
                String stringRead = Character.toString(charRead);
                if (stringRead.equals("\n") || stringRead.equals("\r\n")) {
                    stringRead = "\r";
                }
                Text textRead = new Text(stringRead);
                textRead.setTextOrigin(VPos.TOP);
                linkedlist.addFrontCursor(textRead);
            }
            bufferedReader.close();
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("File not found! Exception was: " + fileNotFoundException);
        } catch (IOException ioException) {
            System.out.println("Error when copying; exception was: " + ioException);
        }
    }


    public void save() {
        try {

            FileWriter writer = new FileWriter(this.filename);
            LLText.Node startnode = linkedlist.sentinel.next;
            while (startnode != linkedlist.sentinel) {
                Text textwrite = (Text) startnode.item;
                if (textwrite == null) {
                    startnode = startnode.next;
                    textwrite = (Text) startnode.item;
                }
                String textstring = textwrite.getText();
                char textchar = textstring.charAt(0);
                writer.write(textchar);
                startnode = startnode.next;
            }
            writer.close();
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("File not found! Exception was: " + fileNotFoundException);
        } catch (IOException ioException) {
            System.out.println("Error when copying; exception was: " + ioException);
        }
    }
}