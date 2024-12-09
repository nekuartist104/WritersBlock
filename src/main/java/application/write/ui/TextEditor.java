package application.write.ui;

import com.youbenzi.mdtool.tool.MDTool;
import io.github.furstenheim.CopyDown;
import net.miginfocom.swing.MigLayout;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TextEditor extends JPanel {

    private List<String> histories = new ArrayList<>();
    private int index;
    private TextToolbar toolbar;
    private JPanel mainPanel;
    private JScrollPane scrollPane;
    private JTextPane pane;
    private JPanel titlePanel;
    private JTextPane titlePane;
    private boolean ignoreHistoryBuilder = false;
    private boolean keepRedo = false;
    private boolean readOnly = false;

//    class PasteAction extends AbstractAction {
//
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            try {
//                int offset = pane.getSelectionStart();
//                Document sd=pane.getDocument();
//                String value = getClipboard();
//                String cleanText = html2text(value);
//                sd.remove(pane.getSelectionStart(), pane.getSelectionEnd()-pane.getSelectionStart());
//                HTMLEditorKit kit = new HTMLEditorKit();
//                kit.insertHTML((HTMLDocument) pane.getDocument(), offset, cleanText, 0, 0, HTML.Tag.SPAN);
//
////                ((HTMLDocument) pane.getDocument()).insertString(offset, value , null);
////                if (value != null) {
////                    pane.setCaretPosition(offset + value.length());
////                }
//            } catch (Exception exc) {
//                exc.printStackTrace();
//            }
//        }
//
//        public static String html2text(String html) {
//            return "<span>" + Jsoup.clean(html,  (new Safelist()).addTags("b", "i")) + "</span>";
//        }
//    }

//    class EmptyPasteAction extends AbstractAction {
//
//        @Override
//        public void actionPerformed(ActionEvent e) {
//
//        }
//    }

    public TextEditor() {
        setLayout(new MigLayout("wrap 2, insets 5 5 5 5", "grow", "[][][grow]"));
        setBackground(Color.blue);

        toolbar = new TextToolbar();
        toolbar.setBackground(Color.cyan);
        enablePaste(Toolkit.getDefaultToolkit().getSystemClipboard());

        titlePanel = new JPanel();
        titlePanel.setBackground(Color.GREEN);
        titlePanel.setLayout(new MigLayout("fillx"));

        titlePane = new JTextPane();
        StyledDocument doc = titlePane.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
        titlePane.setText("Untitled");
        titlePane.setFont(new Font("Cambria", Font.PLAIN, 24));
        titlePanel.add(titlePane, "grow");

        add(titlePanel, "grow, span");
        add(toolbar, "grow, span");

        mainPanel = new JPanel();
        mainPanel.setBackground(Color.MAGENTA);
        mainPanel.setLayout(new MigLayout("wrap 2, insets 0 0 0 0", "grow", "grow"));
        pane = new JTextPane();

        scrollPane = new JScrollPane(pane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        pane.setContentType("text/html");
//        pane.getActionMap();
//        pane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK), "paste");

//        pane.getActionMap().put("paste", new PasteAction());
//        toolbar.getPasteBtn().setAction(new PasteAction());

        toolbar.getPasteBtn().setIcon(new ImageIcon(TextEditor.class.getClassLoader().getResource("buttonIcons/pasteIcon.png")));
        toolbar.getPasteBtn().setToolTipText("Paste");
        enablePaste(Toolkit.getDefaultToolkit().getSystemClipboard());

        pane.setText("""
                    <style>
                        body {
                            font-family: 'Papyrus';
                        }
                    </style>
                """);

        pane.setBackground(Color.pink);
        mainPanel.add(scrollPane, "grow, span");

        histories.add(pane.getText());
        index = histories.size() - 1;
        enableUndoBtn();
        enableRedoBtn();

        add(mainPanel, "grow, span");

        pane.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                System.out.println(pane.getText());
                if (keepRedo == false) {
                    if (ignoreHistoryBuilder == false) {
                        try {
                            String newText = e.getDocument().getText(e.getOffset(), e.getLength());
                            if (newText.contains(" ")) {
                                historyBuilder();
                            }
                        } catch (BadLocationException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                } else {
                    if (ignoreHistoryBuilder == false) {
                        keepRedo = true;
                        deleteRedoHistory();
                        enableRedoBtn();
                    }
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (ignoreHistoryBuilder == false) {
                    historyBuilder();
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (ignoreHistoryBuilder == false) {
                    historyBuilder();
                }
            }
        });

        pane.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                if (readOnly == false) {
                    if (pane.getSelectedText() != null) {
                        toolbar.getCopyBtn().setEnabled(true);
                    } else {
                        toolbar.getCopyBtn().setEnabled(false);
                    }
                }
                else {
                    toolbar.getCopyBtn().setEnabled(false);
                }
            }
        });

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.addFlavorListener(new FlavorListener() {
            @Override
            public void flavorsChanged(FlavorEvent e) {
                enablePaste(clipboard);
            }
        });

        toolbar.getUndoBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                historyBuilder();

                ignoreHistoryBuilder = true;
                index = index - 1;
                displayText(index);

                enableUndoBtn();
                enableRedoBtn();
                ignoreHistoryBuilder = false;

                keepRedo = true;
            }
        });

        toolbar.getRedoBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                index = index + 1;
                ignoreHistoryBuilder = true;
                displayText(index);

                enableUndoBtn();
                enableRedoBtn();
                ignoreHistoryBuilder = false;
            }
        });

        toolbar.getPreviewBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (readOnly == false) {
                    for (Component com : toolbar.getComponents()) {
                        if (com != toolbar.getPreviewBtn()) {
                            com.setEnabled(false);
                        }
                    }
                    pane.setEditable(false);
                    titlePane.setEditable(false);
//                    pane.getActionMap().put("paste", new EmptyPasteAction());
                    readOnly = true;
                }
                else {
                    for (Component com : toolbar.getComponents()) {
                        if (com != toolbar.getPreviewBtn()) {
                            com.setEnabled(true);
                        }
                    }
                    pane.setEditable(true);
                    titlePane.setEditable(true);
                    enableUndoBtn();
                    enableRedoBtn();
//                    pane.getActionMap().put("paste", new PasteAction());

                    readOnly = false;
                }
            }
        });
    }

    public String getClipboard() throws ClassNotFoundException, UnsupportedFlavorException {
        Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        DataFlavor htmlStringFlavor = new DataFlavor("text/html; class=java.lang.String");
        try {
            if (t != null && t.isDataFlavorSupported(htmlStringFlavor)) {
                String text = (String) t.getTransferData(htmlStringFlavor);
                return text;
            }
        } catch (UnsupportedFlavorException e) {
        } catch (IOException e) {
        }
        return null;
    }

    public String markdownToHtml(String text) {
        String html = MDTool.markdown2Html(text);
        html = html.replaceAll("<strong>", "<b>");
        html = html.replaceAll("</strong>", "</b>");
        html = html.replaceAll("<em>", "<i>");
        html = html.replaceAll("</em>", "</i>");
        html = html.replaceAll("<p>", "<p style=\"margin-top: 0\">");

        return html;
    }

    public String htmlToMarkdown(String text) {
        CopyDown copyDown = new CopyDown();
        return copyDown.convert(text);
    }

    public String getTextFromFile(File file) throws FileNotFoundException {
        Scanner myReader = new Scanner(file);
        List<String> lines = new ArrayList<>();
        while (myReader.hasNextLine()) {
            lines.add(myReader.nextLine());
        }
        myReader.close();

        if (lines.isEmpty()) {
            return "";
        }
        return String.join("\n", lines);
    }

    public void displayText(int number) {
        pane.setText(histories.get(number));
    }

    public void historyBuilder() {
        if (!histories.isEmpty()) {
            if (!htmlToMarkdown(pane.getText()).equals(htmlToMarkdown(histories.get(index)))) {
                deleteRedoHistory();
                histories.add(pane.getText());
                index = index + 1;
            }
        } else {
            histories.add(pane.getText());
            index = index + 1;
        }
        enableUndoBtn();
        enableRedoBtn();
    }

    public void enableUndoBtn() {
        if (index == 0) {
            toolbar.getUndoBtn().setEnabled(false);
        } else {
            toolbar.getUndoBtn().setEnabled(true);
        }
    }

    public void enableRedoBtn() {
        if (index == histories.size() - 1) {
            toolbar.getRedoBtn().setEnabled(false);
        } else {
            toolbar.getRedoBtn().setEnabled(true);
        }
    }

    public void enablePaste(Clipboard clipboard) {
        if (readOnly == false) {
            Transferable t = clipboard.getContents(null);
            if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                toolbar.getPasteBtn().setEnabled(true);
            }
            else {
                toolbar.getPasteBtn().setEnabled(false);
            }
        }
        else {
            toolbar.getPasteBtn().setEnabled(false);
        }
    }

    public void deleteRedoHistory() {
        if (histories.size() > index + 1) {
            int number = index + 1;
            while (number < histories.size()) {
                histories.remove(number);
            }
        }
    }

    public JTextPane getPane() {
        return pane;
    }

    public JTextPane getTitlePane() {
        return titlePane;
    }

    public TextToolbar getToolbar() {
        return toolbar;
    }

    public List<String> getHistories() {
        return histories;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
