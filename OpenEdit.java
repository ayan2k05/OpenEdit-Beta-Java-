import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class OpenEdit extends JFrame {

    private JTextArea textArea;
    private JTextArea lineNumbers;
    private JPanel buttonPanel;
    private boolean darkMode = true;  // Using a more common term instead of "darkTheme"

    public OpenEdit() {
        setTitle("OpenEdit-Java");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centering the window

        textArea = new JTextArea();
        textArea.setFont(new Font("Consolas", Font.PLAIN, 18)); // Standard font size for readability
        textArea.setTabSize(4);

        lineNumbers = new JTextArea("1");
        lineNumbers.setBackground(Color.LIGHT_GRAY);
        lineNumbers.setEditable(false);
        lineNumbers.setFont(new Font("Consolas", Font.PLAIN, 18));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setRowHeaderView(lineNumbers);

        add(scrollPane, BorderLayout.CENTER);

        buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        add(buttonPanel, BorderLayout.NORTH);

        addButton("New", e -> newFile());
        addButton("Save", e -> saveFile());
        addButton("Open", e -> openFile());
        addButton("Terminal", e -> openTerminal());
        addButton("Toggle Theme", e -> toggleTheme());

        JButton increaseSize = new JButton("+");
        JButton decreaseSize = new JButton("-");
        JLabel sizeLabel = new JLabel(String.valueOf(textArea.getFont().getSize()));

        increaseSize.addActionListener(e -> {
            changeTextSize(textArea.getFont().getSize() + 1);
            sizeLabel.setText(String.valueOf(textArea.getFont().getSize()));
        });

        decreaseSize.addActionListener(e -> {
            int newSize = Math.max(12, textArea.getFont().getSize() - 1); // Preventing too small fonts
            changeTextSize(newSize);
            sizeLabel.setText(String.valueOf(newSize));
        });

        buttonPanel.add(new JLabel("Size: "));
        buttonPanel.add(decreaseSize);
        buttonPanel.add(sizeLabel);
        buttonPanel.add(increaseSize);

        textArea.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                autoCloseBrackets(e);
            }
        });

        textArea.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateLineNumbers(); }
            public void removeUpdate(DocumentEvent e) { updateLineNumbers(); }
            public void changedUpdate(DocumentEvent e) { updateLineNumbers(); }
        });

        toggleTheme();
    }

    private void addButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.addActionListener(action);
        buttonPanel.add(button);
    }

    private void newFile() {
        if (!textArea.getText().isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(this, "Discard changes?", "New File", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                textArea.setText("");
            }
        } else {
            textArea.setText("");
        }
    }

    private void saveFile() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                FileWriter writer = new FileWriter(file);
                writer.write(textArea.getText());
                writer.close();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Could not save file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                textArea.read(reader, null);
                reader.close();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Could not open file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openTerminal() {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                Runtime.getRuntime().exec("cmd.exe");
            } else {
                Runtime.getRuntime().exec("x-terminal-emulator");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Cannot open terminal.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void toggleTheme() {
        // Less optimized but more readable approach
        if (darkMode) {
            textArea.setBackground(new Color(40, 44, 52));
            textArea.setForeground(Color.WHITE);
            textArea.setCaretColor(Color.RED);
            lineNumbers.setBackground(new Color(50, 54, 62));
            lineNumbers.setForeground(Color.LIGHT_GRAY);
        } else {
            textArea.setBackground(Color.WHITE);
            textArea.setForeground(Color.BLACK);
            textArea.setCaretColor(Color.BLACK);
            lineNumbers.setBackground(Color.LIGHT_GRAY);
            lineNumbers.setForeground(Color.DARK_GRAY);
        }
        darkMode = !darkMode;
    }

    private void changeTextSize(int size) {
        textArea.setFont(new Font("Consolas", Font.PLAIN, size));
        lineNumbers.setFont(new Font("Consolas", Font.PLAIN, size));
    }

    private void autoCloseBrackets(KeyEvent e) {
    char c = e.getKeyChar();
    
    // Only close brackets if the next character is NOT already the expected closing character
    int pos = textArea.getCaretPosition();
    
    // Get the next character after the caret, if available
    String text = textArea.getText();
    char nextChar = (pos < text.length()) ? text.charAt(pos) : '\0';

    if (c == '(' && nextChar != ')') {
        textArea.insert(")", pos);
        textArea.setCaretPosition(pos); // Move caret back inside
    } 
    else if (c == '{' && nextChar != '}') {
        textArea.insert("}", pos);
        textArea.setCaretPosition(pos);
    } 
    else if (c == '[' && nextChar != ']') {
        textArea.insert("]", pos);
        textArea.setCaretPosition(pos);
    } 
    else if (c == '"' && nextChar != '"') {
        textArea.insert("\"", pos);
        textArea.setCaretPosition(pos);
    } 
    else if (c == '\'' && nextChar != '\'') {
        textArea.insert("'", pos);
        textArea.setCaretPosition(pos);
    }
}

    private void updateLineNumbers() {
        int lines = textArea.getText().split("\n").length;
        StringBuilder numbers = new StringBuilder();
        for (int i = 1; i <= lines; i++) {
            numbers.append(i).append("\n");
        }
        lineNumbers.setText(numbers.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            OpenEdit editor = new OpenEdit();
            editor.setVisible(true);
        });
    }
}
