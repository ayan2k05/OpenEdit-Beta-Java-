import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class OpenEdit extends JFrame 
{
    private JTextArea textArea;
    private JTextArea lineNumbers;
    private JPanel buttonPanel;
    private boolean darkTheme = true;

    public OpenEdit() 
    {
        //main window
        setTitle("OpenEdit-Java");//i want to name it DiddyEdit but sounds little concerning :{}
        setSize(800, 600);//
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//Rage Quitting is allowed ;) still DO_NOTHING_ON_CLOSE would be better so you could be productive
        setLocationRelativeTo(null);//Centres the window

        // Create the text area Syntax highlighting will soon not be a dream, i'm learning Jtextpane
        // Create the text area
        textArea = new JTextArea();
        textArea.setFont(new Font("Consolas", Font.PLAIN, 20)); // Consolas is better for coding
        textArea.setTabSize(4);

        // Create line numbers area
        lineNumbers = new JTextArea("1");
        lineNumbers.setBackground(Color.LIGHT_GRAY);
        lineNumbers.setEditable(false);
        lineNumbers.setFont(new Font("Consolas", Font.PLAIN, 20));
        lineNumbers.setMargin(new Insets(5, 5, 5, 10)); // Adds space inside the line number box
        lineNumbers.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 10)); // Adds space on the right side


        // Wrap both in a JScrollPane
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setRowHeaderView(lineNumbers); // This ensures they scroll together

        add(scrollPane, BorderLayout.CENTER);

        // Create button panel
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        add(buttonPanel, BorderLayout.NORTH);//switch to SOUTH if you are gay

        // Add buttons
        addButton("New", e -> newFile());//New day new start
        addButton("Save ✔", e -> saveFile());//Save your code, it looks beautiful
        addButton("Open", e -> openFile());//Have you seen your lost ideas?
        addButton("Terminal", e -> openTerminal());//Windows sucks haahahaha cry about it
        addButton("Toggle Theme", e -> toggleTheme());//I swear if you use LightMode i will call Police on you

        // Create text size buttons
        JButton increaseSize = new JButton("+");
        JButton decreaseSize = new JButton("−");

        // Create a label to show the current font size
        JLabel sizeLabel = new JLabel(String.valueOf(textArea.getFont().getSize()));

        // Update font size label whenever the text size changes
        increaseSize.addActionListener(e -> {
        adjustTextSize(textArea.getFont().getSize() + 1);
        sizeLabel.setText(String.valueOf(textArea.getFont().getSize())); // Update label
        });

        decreaseSize.addActionListener(e -> {
        adjustTextSize(Math.max(15, textArea.getFont().getSize() - 1));
        sizeLabel.setText(String.valueOf(textArea.getFont().getSize())); // Update label
        });

        // Add components to the panel
        buttonPanel.add(new JLabel("Text Size: "));
        buttonPanel.add(decreaseSize);
        buttonPanel.add(sizeLabel);  // Display the number
        buttonPanel.add(increaseSize);


        // Add auto-closing brackets
        textArea.addKeyListener(new KeyAdapter() 
        {
            @Override
            public void keyTyped(KeyEvent e) 
            {
                autoClose(e);
            }
        });


        // I don't want to start counting lines while debugging hehe
        textArea.getDocument().addDocumentListener(new DocumentListener() 
        {
            @Override
            public void insertUpdate(DocumentEvent e) 
            {
                updateLineNumbers();
            }

            @Override
            public void removeUpdate(DocumentEvent e) 
            {
                updateLineNumbers();
            }

            @Override
            public void changedUpdate(DocumentEvent e) 
            {
                updateLineNumbers();
            }
        });

        toggleTheme();
    }



    private void addButton(String text, ActionListener action) 
    {
        JButton button = new JButton(text);
        button.addActionListener(action);
        buttonPanel.add(button);
    }



    private void newFile() 
    {
        if (!textArea.getText().isEmpty()) 
        {
            int confirm = JOptionPane.showConfirmDialog(this, "Unsaved changes will be lost. Do you want to continue?", "Confirm New File", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;
        }
        textArea.setText("");
    }



    private void saveFile() 
    {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text Files", "txt", "py", "c", "cpp", "java"));
        //i don't know why but it don't work and i don't care
        int option = fileChooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            try (FileWriter writer = new FileWriter(fileChooser.getSelectedFile())) 
            {
                writer.write(textArea.getText());
            } 
            catch (IOException e) 
            {
                JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//don't even to try to touch anything here



    private void openFile() 
    {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text Files", "txt", "py", "c", "cpp", "java"));
        int option = fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) 
        {
            try (BufferedReader reader = new BufferedReader(new FileReader(fileChooser.getSelectedFile()))) 
            {
                textArea.read(reader, null);
            } 
            catch (IOException e) 
            {
                JOptionPane.showMessageDialog(this, "Error opening file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }



    private void openTerminal() 
    {
        try 
        {
            if (System.getProperty("os.name").toLowerCase().contains("win")) 
            {
                Runtime.getRuntime().exec("cmd.exe");
            } 
            else 
            {
                Runtime.getRuntime().exec("x-terminal-emulator");
            }
        } 
        catch (IOException e) 
        {
            JOptionPane.showMessageDialog(this, "Error opening terminal: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    private void toggleTheme() 
    {
        // Dark theme looks Cool i swear
        //stackoverflow sucks
        //rahilsha2001 code sucks i wonder if you ever found a place on earth with peace 
        Color bgColor = darkTheme ? new Color(245, 245, 245) : new Color(40, 44, 52); // Soft gray / Dark slate
        Color fgColor = darkTheme ? new Color(50, 50, 50) : new Color(220, 220, 220); // Dark gray / Light gray
        Color caretColor = new Color(255, 107, 107); // Soft red cursor
        Color lineBgColor = darkTheme ? new Color(230, 230, 230) : new Color(50, 54, 62); // Light gray / Dark blue-gray
        Color lineFgColor = darkTheme ? new Color(90, 90, 90) : new Color(200, 200, 200); // Muted gray tones
        Color panelBgColor = darkTheme ? new Color(235, 235, 235) : new Color(50, 54, 62); // Light gray / Dark blue-gray

        // Applying the theme to components
        getContentPane().setBackground(bgColor);
        textArea.setBackground(bgColor);
        textArea.setForeground(fgColor);
        textArea.setCaretColor(caretColor);
        lineNumbers.setBackground(lineBgColor);
        lineNumbers.setForeground(lineFgColor);
        buttonPanel.setBackground(panelBgColor);

        // Toggle theme because i don't want my fellow programmers to go blind, i know you aren't married
        darkTheme = !darkTheme;
    }



    private void adjustTextSize(int size) 
    {
        textArea.setFont(new Font("Arial", Font.PLAIN, size));
        lineNumbers.setFont(new Font("Arial", Font.PLAIN, size));
    }



    private void autoClose(KeyEvent e) 
    {
        char c = e.getKeyChar();
        String pairs = "(){}[]\"\"''";
        int index = pairs.indexOf(c);
        if (index != -1 && index % 2 == 0) 
        {
            textArea.insert(String.valueOf(pairs.charAt(index + 1)), textArea.getCaretPosition());
            textArea.setCaretPosition(textArea.getCaretPosition() - 1);
        }
    }//touch anything it will break, you are at your own risk, if you ever wonder if i'm onto something, I AM SOMETHING 



    private void updateLineNumbers() 
    {
        String[] lines = textArea.getText().split("\n");
        StringBuilder numbers = new StringBuilder();
        for (int i = 1; i <= lines.length; i++) 
        {
            numbers.append(i).append("\n");
        }
        lineNumbers.setText(numbers.toString());
    }



    public static void main(String[] args) 
    {
        SwingUtilities.invokeLater(() -> new OpenEdit().setVisible(true));
    }
}/*you know the code is trash, i know the code is trash but it works :)*/
/*Just accept the fact you aren't gonna use this text editor ! '*/
