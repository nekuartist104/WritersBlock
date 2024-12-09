package application.write.ui;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class WritePanelLeftComponent extends JPanel {
    List<Button> listOfButtons = new ArrayList<>();
    public WritePanelLeftComponent() {
        setLayout(new MigLayout("wrap", "grow"));
        setBackground(Color.WHITE);
    }

    public List<Button> getListOfButtons() {
        return listOfButtons;
    }
}
