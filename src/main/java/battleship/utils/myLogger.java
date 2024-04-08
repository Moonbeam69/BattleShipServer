package battleship.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;

public class myLogger extends JFrame {

    JTextArea display = new JTextArea();
    JScrollPane scroll = new JScrollPane(display);
    JPopupMenu pop;
    boolean allowScrolling = true;
    JLabel logSize;
    Font font = new Font(Font.MONOSPACED, Font.PLAIN, 15);

    public myLogger() {
        this.setTitle("Server Log View");
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        JPanel statusPane = new JPanel(new BorderLayout());
        JLabel upTimeSince = new JLabel("Game server active since: " + getFormattedDate());
        logSize = new JLabel();

        statusPane.add(upTimeSince, BorderLayout.WEST);
        statusPane.add(logSize, BorderLayout.EAST);

        pop = new JPopupMenu();
        JMenuItem clearAllMenuItem = new JMenuItem("Clear all");
        JMenuItem allowScrollingMenuItem = new JMenuItem("Stop scrolling"); // because default value = true
        pop.add(clearAllMenuItem);
        pop.add(allowScrollingMenuItem);

        this.add(statusPane, BorderLayout.SOUTH);

        display.setFont(font);
        display.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                if(e.getButton()==3) { // right mouse click
                    pop.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        clearAllMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                display.setText("");
                log("Log window manually cleared");
            }
        });

        allowScrollingMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(allowScrolling) {
                    allowScrolling = false;
                    allowScrollingMenuItem.setText("Start scrolling");
                } else {
                    allowScrolling = true;
                    allowScrollingMenuItem.setText("Stop scrolling");
                }
            }
        });

        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        scroll.setPreferredSize(new Dimension(900, 1000));

        this.getContentPane().add(scroll);
        this.pack();
        this.setVisible(true);
        this.setLocation((int)(.75*dim.width), (int)500);
        this.log("Logger initialised");
    }


    public void log(String text) {

        if (!text.contains("ping") && !text.contains("getPlayers")) { // exclude ping to reduce clutter

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss:SSS");
            Date date = new Date(System.currentTimeMillis());
            display.append(getFormattedDate() + " " + text + "\n");

            logSize.setText(display.getLineCount() + " lines");

            if (allowScrolling) {
                int max = scroll.getVerticalScrollBar().getMaximum();
                scroll.getVerticalScrollBar().setValue(max);
            }
        }
    }

    String getFormattedDate(){
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss:SSS");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }
}
