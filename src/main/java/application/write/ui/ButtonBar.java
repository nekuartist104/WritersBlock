package application.write.ui;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ButtonBar extends Button {

    private List<Button> listOfButtons = new ArrayList<>();
    private List<Integer> listOfIDs = new ArrayList<>();
    private boolean displayParts = false;
    private List<File> files = new ArrayList<>();
    public ButtonBar() {
        setMinimumSize(new Dimension(250, 50));
        setMaximumSize(new Dimension(250, 50));
        setFont(new Font("Cambria", Font.PLAIN, 18));
    }

    public List<Button> getListOfButtons() {
        return listOfButtons;
    }
    public boolean isDisplayParts() {
        return displayParts;
    }
    public void setDisplayParts(boolean displayParts) {
        this.displayParts = displayParts;
    }
    public List<Integer> getListOfIDs() {
        return listOfIDs;
    }
    public List<File> getFiles() {
        return files;
    }

    public int getCurrentIndex() {
        int index = listOfIDs.get(listOfIDs.size()-1);
        return index;
    }
}
