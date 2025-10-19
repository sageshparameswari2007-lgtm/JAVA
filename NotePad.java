import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;

import java.awt.print.PrinterException;
import javax.swing.undo.*;
import javax.swing.event.*;

class NotePad extends JFrame implements ItemListener {
    JTextArea textarea;
    private int currentFontSize = 14;
    private String lastSearchTerm = "";
    private int lastSearchIndex = -1;
    JLabel info;
    JFrame frame;
    NotePad() {
        frame=this;
        setTitle("NotePad");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLayout(new BorderLayout());
        info = new JLabel();
        add(info, BorderLayout.SOUTH);
        JMenuBar menubar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem New = new JMenuItem("New");
        JMenuItem open = new JMenuItem("Open");
        JMenuItem save = new JMenuItem("Save");
        JMenuItem saveas = new JMenuItem("Save as");
        JMenuItem print = new JMenuItem("Print");
        JMenuItem exit = new JMenuItem("Exit");
        file.add(New);
        file.add(open);
        file.add(save);
        file.add(saveas);
        file.add(print);
        file.add(exit);
        menubar.add(file);
        textarea = new JTextArea();
        add(textarea);
        New.addActionListener(e -> {
            new NotePad();
        });

        JScrollPane scrollPane = new JScrollPane(textarea);
        add(scrollPane, BorderLayout.CENTER);

        exit.addActionListener(e -> System.exit(0));
        open.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setAcceptAllFileFilterUsed(true);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
            fileChooser.setFileFilter(filter);
            int option = fileChooser.showOpenDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file1 = fileChooser.getSelectedFile();
                try (BufferedReader reader = new BufferedReader(new FileReader(file1))) {
                    textarea.read(reader, null);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error opening file: " + ex.getMessage());
                }
            }
        });
        save.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showSaveDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file1 = fileChooser.getSelectedFile();
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file1))) {
                    textarea.write(writer);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage());
                }
            }
        });
        saveas.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showSaveDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file1 = fileChooser.getSelectedFile();
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file1))) {
                    textarea.write(writer);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage());
                }
            }
        });
        print.addActionListener(e -> {
            try {
                boolean complete = textarea.print();
                if (!complete) {
                    JOptionPane.showMessageDialog(this, "Printing canceled.");
                }
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(this, "Error printing: " + ex.getMessage());
            }
        });
        New.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        saveas.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        print.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));
        exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        JMenu edit = new JMenu("Edit");
        JMenuItem undo = new JMenuItem("Undo");
        JMenuItem cut = new JMenuItem("Cut");
        JMenuItem copy = new JMenuItem("Copy");
        JMenuItem paste = new JMenuItem("Paste");
        JMenuItem delete = new JMenuItem("Delete");
        JMenuItem find = new JMenuItem("Find");
        JMenuItem find_next = new JMenuItem("Find Next");
        JMenuItem find_previous = new JMenuItem("Find Previous");
        JMenuItem replace = new JMenuItem("Repalce");
        JMenuItem go_to = new JMenuItem("GoTo");
        JMenuItem select_all = new JMenuItem("Select All");
        JMenuItem font = new JMenuItem("Font");
        edit.add(undo);
        undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
        UndoManager undoManager = new UndoManager();
        textarea.getDocument().addUndoableEditListener(e -> {
            undoManager.addEdit(e.getEdit());
            undo.setEnabled(undoManager.canUndo());
        });
        undo.addActionListener(e -> {
            if (undoManager.canUndo()) {
                undoManager.undo();
            }
        });
        undo.setEnabled(false);
        edit.add(cut);
        cut.addActionListener(e -> textarea.cut());
        cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
        edit.add(copy);
        copy.addActionListener(e -> textarea.copy());
        copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        edit.add(paste);
        paste.addActionListener(e -> textarea.paste());
        paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
        edit.add(delete);
        delete.addActionListener(e -> {
            int start = textarea.getSelectionStart();
            int end = textarea.getSelectionEnd();
            if (start != end) {
                textarea.replaceRange("", start, end);
            }
        });
        delete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.BUTTON1_DOWN_MASK));
        edit.add(find);
        find.addActionListener(e -> {
            String searchTerm = JOptionPane.showInputDialog(this, "Enter text to find:");
            if (searchTerm != null && !searchTerm.isEmpty()) {
                String content = textarea.getText();
                int index = content.indexOf(searchTerm);
                if (index >= 0) {
                    textarea.setCaretPosition(index);
                    textarea.select(index, index + searchTerm.length());
                    textarea.grabFocus();
                } else {
                    JOptionPane.showMessageDialog(this, "Text not found.");
                }
            }
        });
        find.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
        edit.add(find_next);
        find_next.addActionListener(e -> {
            if (lastSearchTerm.isEmpty())
                return;
            String content = textarea.getText();
            int nextIndex = content.indexOf(lastSearchTerm, lastSearchIndex + 1);
            if (nextIndex >= 0) {
                lastSearchIndex = nextIndex;
                textarea.setCaretPosition(nextIndex);
                textarea.select(nextIndex, nextIndex + lastSearchTerm.length());
                textarea.grabFocus();
            } else {
                JOptionPane.showMessageDialog(this, "No further matches found.");
            }
        });
        find_next.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, InputEvent.BUTTON2_DOWN_MASK));
        edit.add(find_previous);
        find_previous.addActionListener(e -> {
            if (lastSearchTerm.isEmpty())
                return;
            String content = textarea.getText();
            int prevIndex = content.lastIndexOf(lastSearchTerm, lastSearchIndex - 1);
            if (prevIndex >= 0) {
                lastSearchIndex = prevIndex;
                textarea.setCaretPosition(prevIndex);
                textarea.select(prevIndex, prevIndex + lastSearchTerm.length());
                textarea.grabFocus();
            } else {
                JOptionPane.showMessageDialog(this, "No previous matches found.");
            }
        });
        find_previous.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, InputEvent.SHIFT_DOWN_MASK));
        edit.add(replace);
        replace.addActionListener(e -> {
            JPanel panel = new JPanel(new GridLayout(2, 2));
            JTextField findField = new JTextField();
            JTextField replaceField = new JTextField();
            panel.add(new JLabel("Find:"));
            panel.add(findField);
            panel.add(new JLabel("Replace with:"));
            panel.add(replaceField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Replace", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String findText = findField.getText();
                String replaceText = replaceField.getText();
                String content = textarea.getText();
                int index = content.indexOf(findText);
                if (index >= 0) {
                    textarea.select(index, index + findText.length());
                    textarea.replaceSelection(replaceText);
                } else {
                    JOptionPane.showMessageDialog(this, "Text not found.");
                }
            }
        });
        replace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_DOWN_MASK));
        edit.add(go_to);
        go_to.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Enter line number:");
            if (input != null && !input.isEmpty()) {
                try {
                    int lineNumber = Integer.parseInt(input);
                    int startOffset = textarea.getLineStartOffset(lineNumber - 1);
                    textarea.setCaretPosition(startOffset);
                    textarea.grabFocus();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid number format.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Line number out of range.");
                }
            }
        });
        go_to.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK));
        edit.add(select_all);
        select_all.addActionListener(e -> textarea.selectAll());
        select_all.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
        edit.add(font);
        font.addActionListener(e -> {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            String[] fonts = ge.getAvailableFontFamilyNames();
            JComboBox<String> fontBox = new JComboBox<>(fonts);
            JComboBox<String> styleBox = new JComboBox<>(new String[] { "Plain", "Bold", "Italic", "Bold Italic" });
            JTextField sizeField = new JTextField("14");
            JPanel panel = new JPanel(new GridLayout(3, 2));
            panel.add(new JLabel("Font:"));
            panel.add(fontBox);
            panel.add(new JLabel("Style:"));
            panel.add(styleBox);
            panel.add(new JLabel("Size:"));
            panel.add(sizeField);
            int result = JOptionPane.showConfirmDialog(this, panel, "Choose Font", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String fontName = (String) fontBox.getSelectedItem();
                int fontStyle = styleBox.getSelectedIndex();
                int fontSize;
                try {
                    fontSize = Integer.parseInt(sizeField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid font size.");
                    return;
                }
                Font newFont = new Font(fontName, fontStyle, fontSize);
                textarea.setFont(newFont);
            }
        });
        menubar.add(edit);
        JMenu view = new JMenu("View");
        JMenu zoom = new JMenu("Zoom");
        JMenu Theme=new JMenu("Theme");
        JMenuItem zoom_in = new JMenuItem("Zoom in");
        JMenuItem zoom_out = new JMenuItem("Zoom out");
        JMenuItem dark=new JMenuItem("Dark Mode");
        JMenuItem light=new JMenuItem("Light Mode");
        zoom.add(zoom_in);
        Theme.add(dark);
        Theme.add(light);
        zoom_in.addActionListener(e -> {
            currentFontSize += 2;
            Font currentFont = textarea.getFont();
            textarea.setFont(new Font(currentFont.getFamily(), currentFont.getStyle(), currentFontSize));
        });
        zoom_in.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, InputEvent.CTRL_DOWN_MASK));
        zoom.add(zoom_out);
        zoom_out.addActionListener(e -> {
            if (currentFontSize > 6) {
                currentFontSize -= 2;
                Font currentFont = textarea.getFont();
                textarea.setFont(new Font(currentFont.getFamily(), currentFont.getStyle(), currentFontSize));
            }
        });
        zoom_out.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, InputEvent.CTRL_DOWN_MASK));
        view.add(zoom);
        dark.addActionListener(e->{
            textarea.setBackground(Color.BLACK);
            textarea.setForeground(Color.WHITE);
        });
        light.addActionListener(e->{
            textarea.setBackground(Color.WHITE);
            textarea.setForeground(Color.BLACK);
        });
        view.add(Theme);
        menubar.add(view);
        setJMenuBar(menubar);
        setVisible(true);
        updateTextStyle();
    }

    public void itemStateChanged(ItemEvent e) {
        updateTextStyle();
    }

    private void updateTextStyle() {
        textarea.addCaretListener(e -> {
            int caretPos = textarea.getCaretPosition();
            try {
                int line = textarea.getLineOfOffset(caretPos);
                int col = caretPos - textarea.getLineStartOffset(line);
                info.setText("Line: " + (line + 1) + " Column: " + (col + 1));
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        new NotePad();
    }
}
