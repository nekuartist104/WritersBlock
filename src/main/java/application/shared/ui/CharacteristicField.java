package application.shared.ui;

import application.factory.TextAndPopupFactory;

import javax.swing.*;
import java.awt.*;

public class CharacteristicField {

    private JLabel label;
    private JTextArea value;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CharacteristicField() {
        label = new JLabel();
        label.setPreferredSize(new Dimension(150,0));
        value = TextAndPopupFactory.createTextAreaBox();
    }

    public JLabel getLabel() {
        return label;
    }

    public void setLabel(JLabel label) {
        this.label = label;
    }

    public JTextArea getValue() {
        return value;
    }

    public void setValue(JTextArea value) {
        this.value = value;
    }
}
