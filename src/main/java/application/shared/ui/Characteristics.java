package application.shared.ui;

import application.factory.TextAndPopupFactory;
import application.shared.domain.CheckChars;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class Characteristics extends JPanel {
    private List<CharacteristicField> listOfCharacteristicFields = new ArrayList<>();
    private List<String> listOfNames = new ArrayList<>();
    private String type;

    public List<String> getListOfNames() {
        return listOfNames;
    }

    public Characteristics(String type) {
        setLayout(new MigLayout("wrap 2, insets 0 5 25 0", "grow", "grow"));
        setBackground(Color.WHITE);
        setOpaque(false);

        this.type = type;
    }

    public List<CharacteristicField> getListOfCharacteristicFields() {
        return listOfCharacteristicFields;
    }

    public void setListOfCharacteristicFields(List<CharacteristicField> listOfCharacteristicFields) {
        this.listOfCharacteristicFields = listOfCharacteristicFields;
    }

    public void addCharacteristicField() {
        String newCharacteristicNameInput = TextAndPopupFactory.createPopupWindow("What " + type + " would you like to add?");

        if (newCharacteristicNameInput != null) {
            if (!newCharacteristicNameInput.equals("")) {
                int spaceCount = CheckChars.checkSpaceChars(newCharacteristicNameInput);
                boolean preexists = CheckChars.checkPrexists(listOfNames, newCharacteristicNameInput);

                if (spaceCount == newCharacteristicNameInput.length()) {
                    JOptionPane.showMessageDialog(null,
                            "Sorry! A blank space is not a title!",
                            "Name error",
                            JOptionPane.ERROR_MESSAGE);
                }
                else if (preexists) {
                    JOptionPane.showMessageDialog(null,
                            "Sorry! It looks like you already have a " + type + " with this name!",
                            "Name error",
                            JOptionPane.ERROR_MESSAGE);
                }
                else {
                    CharacteristicField field = new CharacteristicField();
                    field.getLabel().setText(newCharacteristicNameInput + ":");
                    addCharacteristicToPanel(field);
                }


            }
            else {
                JOptionPane.showMessageDialog(null,
                        "Sorry! It looks like you didn't enter a name!",
                        "Name error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        else {
            JOptionPane.showMessageDialog(null,
                        "Sorry! It looks like you didn't enter a name!",
                        "Name error",
                        JOptionPane.ERROR_MESSAGE);
        }
    }

    public void addCharacteristicFieldToFixedPanel() {
        String newCharacteristicNameInput = TextAndPopupFactory.createPopupWindow("What " + type + " would you like to add?");

        if (newCharacteristicNameInput != null) {
            if (!newCharacteristicNameInput.equals("")) {
                if (!newCharacteristicNameInput.equalsIgnoreCase("Name")) {
                    int spaceCount = CheckChars.checkSpaceChars(newCharacteristicNameInput);
                    boolean preexists = CheckChars.checkPrexists(listOfNames, newCharacteristicNameInput);

                    if (spaceCount == newCharacteristicNameInput.length()) {
                        JOptionPane.showMessageDialog(null,
                                "Sorry! A blank space is not a title!",
                                "Name error",
                                JOptionPane.ERROR_MESSAGE);
                    }

                    else if (preexists) {
                        JOptionPane.showMessageDialog(null,
                                "Sorry! It looks like you already have a " + type + " with this name!",
                                "Name error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    else {
                        CharacteristicField field = new CharacteristicField();
                        field.getLabel().setText(newCharacteristicNameInput + ":");
                        addCharacteristicToPanel(field);
                    }
                }
                else {
                    JOptionPane.showMessageDialog(null,
                            "Sorry! It looks like you already have a " + type + " with this name!",
                            "Name error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
            else {
                JOptionPane.showMessageDialog(null,
                        "Sorry! It looks like you didn't enter a name!",
                        "Name error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        else {
            JOptionPane.showMessageDialog(null,
                    "Sorry! It looks like you didn't enter a name!",
                    "Name error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void addCharacteristicToPanel(CharacteristicField field) {
        listOfCharacteristicFields.add(field);
        add(field.getLabel());
        add(field.getValue());
        String itemName = field.getLabel().getText().substring(0,field.getLabel().getText().length()-1);
        listOfNames.add(itemName);
        PopupMenu pop = new PopupMenu();
        pop.getRemoveMenuItem().setAction(new AbstractAction("Remove", pop.getRemoveMenuItem().getIcon()) {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] options = {"Continue",
                        "Cancel"};
                int confirmation = JOptionPane.showOptionDialog(null,
                        "Are you sure you want to remove this " + type + "? \n"
                                + "This action cannot be undone.",
                        "Warning",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE,
                        null,
                        options,
                        options[0]);

                if (confirmation == 0) {
                    remove(field.getLabel());
                    remove(field.getValue());
                    String n = field.getLabel().getText().substring(0,field.getLabel().getText().length()-1);
                    listOfCharacteristicFields.remove(field);
                    listOfNames.remove(n);
                    revalidate();
                    repaint();
                }
            }
        });

        pop.getRenameMenuItem().setAction(new AbstractAction("Rename", pop.getRenameMenuItem().getIcon()) {
            @Override
            public void actionPerformed(ActionEvent e) {
                String oldCharacteristicName = field.getLabel().getText().substring(0, field.getLabel().getText().length()-1);
                String newCharacteristicNameInputChange = TextAndPopupFactory.createPopupWindow("What would you like to rename " + oldCharacteristicName + " to?");

                if (newCharacteristicNameInputChange != null) {
                    if (!newCharacteristicNameInputChange.equals("")) {
                        int spaceCount = CheckChars.checkSpaceChars(newCharacteristicNameInputChange);
                        boolean preexists = CheckChars.checkPrexists(listOfNames, newCharacteristicNameInputChange);

                        if (spaceCount == newCharacteristicNameInputChange.length()) {
                            JOptionPane.showMessageDialog(null,
                                    "Sorry! A blank space is not a title!",
                                    "Name error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                        else if (newCharacteristicNameInputChange.equalsIgnoreCase(oldCharacteristicName)) {
                            field.getLabel().setText(newCharacteristicNameInputChange + ":");
                            listOfNames.remove(oldCharacteristicName);
                            listOfNames.add(newCharacteristicNameInputChange);
                        }
                        else if (preexists) {
                            JOptionPane.showMessageDialog(null,
                                    "Sorry! It looks like you already have a " + type + " with this name!",
                                    "Name error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                        else {
                            field.getLabel().setText(newCharacteristicNameInputChange + ":");
                            listOfNames.remove(oldCharacteristicName);
                            listOfNames.add(newCharacteristicNameInputChange);
                        }
                    }
                }
            }
        });
        field.getLabel().addMouseListener(new PopClickListener(pop));
        revalidate();
        repaint();
    }
}
