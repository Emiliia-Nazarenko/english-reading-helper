import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class ReaderHelper {
    private JPanel basePanel;
    private JTextArea bookText;
    private JTextArea wordsArea;
    private JButton uploadButton;
    private JButton downloadButton;
    private JTextField word;
    private JTextField translation;
    private JButton saveButton;
    private JButton deleteButton;
    private JButton translatedButton;
    private JButton remainingButton;
    private JButton cleanButton;
    private static Queue<String> wordsQ = new ArrayDeque<>();
    private static TreeSet<String> words = new TreeSet<>();
    private static List<String> translatedWords = new ArrayList<>();

    public ReaderHelper() {

        uploadButton.addActionListener(e -> {
                    JFileChooser chooser = new JFileChooser();
                    int ret = chooser.showDialog(null, "Open");
                    if(ret == JFileChooser.APPROVE_OPTION) {
                        File file = chooser.getSelectedFile();
                        Scanner scan = null;
                        try {
                            scan = new Scanner(file);
                        } catch (FileNotFoundException fileNotFoundException) {
                            JOptionPane.showMessageDialog(basePanel, "File not found!");
                        }
                        StringBuilder text = new StringBuilder();
                        while(scan.hasNextLine())
                            text.append(scan.nextLine());
                        String textStr = text.toString();
                        bookText.setLineWrap(true);
                        bookText.setText(textStr);
                        textStr = textStr.replaceAll("\\p{Punct}|[0-9]", "").toLowerCase().trim();
                        words = new TreeSet<>(Arrays.asList(textStr.split("\\s+")));
                        words.removeIf(s -> Pattern.matches("^the|or|a|in|an|at|but|if|and|so|as|not|for$", s));
                        for(String w : words) {
                           wordsArea.append(w + "\n");
                        }
                        scan.close();
                        wordsQ = new ArrayDeque<>(words);
                        word.setText(wordsQ.peek());
                    }
                }
        );
        remainingButton.addActionListener(e3 -> {
            wordsArea.setText(null);
            for(String w : wordsQ) {
                wordsArea.append(w + "\n");
            }
        });
        translatedButton.addActionListener(e4 -> {
            wordsArea.setText(null);
            for(String w : translatedWords) {
                wordsArea.append(w + "\n");
            }
        });
        deleteButton.addActionListener(e1 -> {
            wordsQ.remove(word.getText());
            if(wordsQ.isEmpty()) {
                JOptionPane.showMessageDialog(basePanel, "Words are over!");
                word.setText(null);
            }else
                word.setText(wordsQ.peek());
        });

        saveButton.addActionListener(e2 -> {
            if(wordsQ.isEmpty()) {
                JOptionPane.showMessageDialog(basePanel, "Words are over!");
                word.setText(null);
            }else{
                translatedWords.add(word.getText() + " - " + translation.getText());
                wordsQ.remove(word.getText());
                word.setText(wordsQ.peek());
            }
            translation.setText(null);
        });

        downloadButton.addActionListener(e -> {
            JFileChooser saver = new JFileChooser();
            int result = saver.showSaveDialog(basePanel);
            if(result == JFileChooser.APPROVE_OPTION) {
                File fileToSave = saver.getSelectedFile();
                String path = fileToSave.getPath() + ".txt";
                fileToSave = new File(path);
                try(FileWriter writer = new FileWriter(fileToSave.getPath(), false)) {
                    writer.write(wordsArea.getText());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(basePanel,"File exists");
                }
                JOptionPane.showMessageDialog(basePanel,
                        "File '" + saver.getSelectedFile() + ".txt' is downloaded");
            }
        });
        cleanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bookText.setText(null);
                wordsArea.setText(null);
                word.setText(null);
                translation.setText(null);
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Reader Helper");
        frame.setContentPane(new ReaderHelper().basePanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
