package application;

import application.character.ui.CharacterPanel;
import application.world.ui.WorldPanel;
import application.write.ui.WriteMenu;
import application.write.ui.WritePanel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class Window extends JPanel {

    private JTabbedPane tabbedPane;
    private JLabel writeLabel;
    private JLabel characterLabel;
    private JLabel worldLabel;
    private CharacterPanel characterPanel;
    private WorldPanel worldPanel;
    private WriteMenu writePanel;

    public Window() {
        setLayout(new MigLayout("insets 0", "grow", "grow"));
        setBackground(Color.MAGENTA);
        tabbedPane = new JTabbedPane();

        tabbedPane.setBorder(BorderFactory.createEmptyBorder());
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setTabPlacement(JTabbedPane.TOP);

        writeLabel = new JLabel("WRITE");
        writeLabel.setPreferredSize(new Dimension(100, 30));

        writeLabel = new JLabel("WRITE", JLabel.CENTER);
        writeLabel.setPreferredSize(new Dimension(250, 30));
        writeLabel.setFont(new Font("Comic Sans", Font.BOLD, 20));

        characterLabel = new JLabel("CHARACTERS", JLabel.CENTER);
        characterLabel.setPreferredSize(new Dimension(250, 30));
        characterLabel.setFont(new Font("Comic Sans", Font.BOLD, 20));

        worldLabel = new JLabel("WORLD", JLabel.CENTER);
        worldLabel.setPreferredSize(new Dimension(250, 30));
        worldLabel.setFont(new Font("Comic Sans", Font.BOLD, 20));

        writePanel = new WriteMenu();
        characterPanel = new CharacterPanel();
        worldPanel = new WorldPanel();

//        tabbedPane.setBounds(50,50,200,200);
        tabbedPane.add("WRITE", writePanel);
        tabbedPane.add("CHARACTERS", characterPanel);
        tabbedPane.add("WORLD", worldPanel);
        tabbedPane.setTabComponentAt(0, writeLabel);
        tabbedPane.setTabComponentAt(1, characterLabel);
        tabbedPane.setTabComponentAt(2, worldLabel);
        tabbedPane.setBackground(Color.WHITE);

        add(tabbedPane,"grow");
    }
}
