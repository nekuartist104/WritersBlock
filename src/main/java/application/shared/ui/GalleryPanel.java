package application.shared.ui;

import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.prompt.PromptSupport;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GalleryPanel extends JPanel {

    private JPanel bigPanel;
    private JPanel namePanel;
    private JPanel imagePanel;
    private JTextArea description;
    private JButton leftArrow;
    private JButton rightArrow;
    private JLabel title;
    private JLabel selectedLabel = new JLabel();

    public GalleryPanel() {
        setLayout(new MigLayout("wrap, insets 0 200 0 0"));
        setBackground(Color.white);
        setOpaque(false);

        Icon rightArrowIcon = new ImageIcon(getClass().getClassLoader().getResource("buttonIcons/rightArrow.png"));
        Icon leftArrowIcon = new ImageIcon(getClass().getClassLoader().getResource("buttonIcons/leftArrow.png"));

        title = new JLabel("", SwingConstants.CENTER);
        title.setVerticalAlignment(SwingConstants.CENTER);
        title.setPreferredSize(new Dimension(200, 25));
        title.setFont(new Font("", Font.BOLD, 20));

        leftArrow = new JButton(leftArrowIcon);
        leftArrow.setBackground(Color.lightGray);
        leftArrow.setBorder(BorderFactory.createEmptyBorder());

        rightArrow = new JButton(rightArrowIcon);
        rightArrow.setBackground(Color.lightGray);
        rightArrow.setBorder(BorderFactory.createEmptyBorder());

        namePanel = new JPanel();
        namePanel.setPreferredSize(new Dimension(400, 30));
        namePanel.setBackground(Color.lightGray);
        namePanel.setBorder(BorderFactory.createLineBorder(Color.gray));

        namePanel.add(leftArrow);
        namePanel.add(title, "grow");
        namePanel.add(rightArrow, "right");

        imagePanel = new JPanel();
        imagePanel.add(selectedLabel);
        imagePanel.setBorder(BorderFactory.createLineBorder(Color.gray));
//        imagePanel.setBorder(BorderFactory.createEmptyBorder());
        imagePanel.setPreferredSize(new Dimension(400, 450));
        imagePanel.setLayout(new MigLayout("insets 0"));

        bigPanel = new JPanel();
        bigPanel.setLayout(new MigLayout("wrap, insets 0 0 30 0, gap 0"));
        bigPanel.setBackground(Color.WHITE);
        bigPanel.setOpaque(false);
        add(bigPanel, "grow");
        bigPanel.add(namePanel, "center");
        bigPanel.add(imagePanel, "center");

        description = new JTextArea();
        description.setPreferredSize(new Dimension(400, 100));
//        description.setOpaque(false);
        PromptSupport.setPrompt("Brief description", description);
        PromptSupport.setForeground(Color.GRAY, description);
        description.setBorder(BorderFactory.createLineBorder(Color.gray));
        description.setLineWrap(true);
        add(description);
    }

    public JPanel getImagePanel() {
        return imagePanel;
    }

    public void showChosenImage(List<ImageIcon> list, int index, List<String> texts) {
        if (list.size() > 0) {
            ImageIcon picture = list.get(index);
            Image image = picture.getImage().getScaledInstance(398, 448, Image.SCALE_SMOOTH);
            picture = new ImageIcon(image, picture.getDescription());

            selectedLabel.setIcon(picture);
            title.setText(texts.get(index));

            imagePanel.revalidate();
            imagePanel.repaint();
        }

    }

    public JButton getLeftArrow() {
        return leftArrow;
    }

    public JButton getRightArrow() {
        return rightArrow;
    }

    public JPanel getNamePanel() {
        return namePanel;
    }

    public JLabel getTitle() {
        return title;
    }

    public void setTitle(JLabel title) {
        this.title = title;
    }

    public JLabel getSelectedLabel() {
        return selectedLabel;
    }

    public JTextArea getDescription() {
        return description;
    }

    public void setDescription(JTextArea description) {
        this.description = description;
    }
}
