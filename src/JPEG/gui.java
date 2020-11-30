package JPEG;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class gui {
    private JTextArea textArea1;
    private JButton runButton;
    private JPanel panel1;
    private JButton exitButton;

    public gui() {
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = textArea1.getText();
                try {
                    JPEG obj = new JPEG(text);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    public static void main(String[] args)
    {
        JFrame frame = new JFrame("JPEG APP");
        frame.setContentPane(new gui().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
