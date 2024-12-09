package application.character.ui;

import application.character.domain.ExtraCharacterTypes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CharacterDialog extends JDialog {

    private JButton continueBtn;
    private JButton cancelBtn;
    private List<JCheckBox> checkBoxes = new ArrayList<>();
    private JPanel removeTypePopup = new JPanel();
    private JPanel panel;
    private List<String> removalList = new ArrayList<>();
    private static final String usersDir = System.getProperty("user.home");

    public CharacterDialog(ExtraCharacterTypes types) {
        setTitle("Remove Types");
        setModal(true);
        getContentPane().add(removeTypePopup);
        setResizable(false);

        removeTypePopup.setLayout(new MigLayout("wrap, insets 15 15 15 15"));
        removeTypePopup.add(new JLabel("Select which types you would like to remove:"));

        panel = new JPanel();
        removeTypePopup.add(panel);
        panel.setLayout(new MigLayout("wrap 3, insets 10 0 0 0"));
        for (String name : types.getTypes()) {
            File file = new File(usersDir + "\\WritersBlock\\Character\\CharacterExtraLeafIcons\\" + name + ".png");
            Icon imageIcon = new ImageIcon(file.getAbsolutePath());
            JCheckBox checkBox = new JCheckBox();
            checkBoxes.add(checkBox);

            JLabel label = new JLabel(name);
            label.setMinimumSize(new Dimension(70,0));

            panel.add(new JLabel(imageIcon));
            panel.add(label);
            panel.add(checkBox);
        }

        continueBtn = new JButton("Continue");
        cancelBtn = new JButton("Cancel");
        panel.add(continueBtn, "skip");
        panel.add(cancelBtn);

        continueBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i <= checkBoxes.size()-1; i++) {
                    if (checkBoxes.get(i).isSelected()) {
                        String name = types.getTypes().get(i);
                        removalList.add(name);
                    }
                }

                if (removalList.size() > 0) {
                    if (removalList.size() == 1) {
                        removeTypes("this character type", types);
                    }
                    else if (removalList.size() > 1) {
                        removeTypes("these character types", types);
                    }
                }
                dispose();
                setVisible(false);
            }
        });

        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                setVisible(false);
            }
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void savesExtraTypesJson(ExtraCharacterTypes types) {
        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        try {
            json = mapper.writeValueAsString(types);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }

        File jsonTypes = new File(usersDir + "\\WritersBlock\\Character\\CharacterExtraTypes\\ExtraTypes.json");
        File folder = new File(usersDir + "\\WritersBlock\\Character\\CharacterExtraTypes");

        FilenameFilter fi = new FilenameFilter() {
            public boolean accept(File f, String name) {
                return name.equals("ExtraTypes.json");
            }
        };

        try {
            File[] f = folder.listFiles(fi);
            if (f != null && f.length!=0) {
                f[0].renameTo(jsonTypes);
            }
            else {
                jsonTypes.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(jsonTypes);
            fileWriter.write(json);
            fileWriter.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void removeTypes(String text, ExtraCharacterTypes types) {
        Object[] options = {"Continue",
                "Cancel"};
        int confirmation = JOptionPane.showOptionDialog(null,
                "Are you sure you want to remove " + text + "? \n"
                        + "This action cannot be undone.",
                "Warning",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                options,
                options[0]);

        if (confirmation == 0) {
            for (String name : removalList) {
                File file = new File(usersDir + "\\WritersBlock\\Character\\CharacterExtraLeafIcons\\" + name + ".png");
                if (file.exists()) {
                    file.delete();
                }
                types.getTypes().remove(name);
                savesExtraTypesJson(types);
            }
        }
    }
}
