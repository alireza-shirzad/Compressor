import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;


public class GraphicalUserInterface extends JFrame {
    private JButton buttonBrowse;
    private JButton zipButton;
    private JButton unzipButton;
    private String path;
    private static JFrame frame;
    public GraphicalUserInterface() {
        super("Zipper");
        frame = this;
        setLayout(new FlowLayout());
        path = "";
        buttonBrowse = new JButton("Browse...");
        zipButton = new JButton("Zip");
        unzipButton = new JButton("Unzip");
        buttonBrowse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                showOpenFileDialog();
            }
        });
        zipButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (path.equals("")){
                    JOptionPane.showMessageDialog(frame,
                            "No Input has been selected",
                            "Inane error",
                            JOptionPane.ERROR_MESSAGE);
                }
                else if (path.substring(path.length()-3).equals("txt")){
                    Compressor compressor = new Compressor(5,10);
                    String password = JOptionPane.showInputDialog(frame, "Enter Password (If none, leave it empty):", "Encryption",
                            JOptionPane.WARNING_MESSAGE);
                    try {
                        compressor.compress(path, password);
                        JOptionPane.showMessageDialog(frame,
                                "File Zipped Successfully",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    JOptionPane.showMessageDialog(frame,
                            "Wrong Input Format",
                            "Inane error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        unzipButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (path.equals("")){
                    JOptionPane.showMessageDialog(frame,
                            "No Input has been selected",
                            "Inane error",
                            JOptionPane.ERROR_MESSAGE);
                }
                else if (path.substring(path.length()-3).equals("zad")){
                    Decompressor decompressor = new Decompressor();
                    String password = JOptionPane.showInputDialog(frame, "Enter Password (If none, leave it empty):", "Encryption",
                            JOptionPane.WARNING_MESSAGE);
                    int result = decompressor.decompress(path , password);
                    if (result==0) {
                        JOptionPane.showMessageDialog(frame,
                                "File Unzipped Successfully",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else{
                        JOptionPane.showMessageDialog(frame,
                                "Wrong Password",
                                "Inane error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }

                else {
                    JOptionPane.showMessageDialog(frame,
                            "Wrong Input Format",
                            "Inane error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        getContentPane().add(buttonBrowse);
        getContentPane().add(zipButton);
        getContentPane().add(unzipButton);
        setSize(500, 100);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new GraphicalUserInterface();
            }
        });
    }

    private void showOpenFileDialog() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Zip Files", "shirzad"));

        fileChooser.setAcceptAllFileFilterUsed(true);

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
            path = selectedFile.getAbsolutePath();
        }
    }
}