package View;

import battleship.service.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

public class UserManagement extends JFrame implements Runnable{
    DefaultTableModel model;
    JPanel listPane;
    GameService gameService;
    Object data[][] = null;
    PlayerState state;

    public UserManagement(GameService gameService) {

        this.setTitle("User Management");
        this.gameService = gameService;
        this.getContentPane().setPreferredSize(new Dimension(600, 300));

        String column[]={"username","uuid", "status", "last update"};
        model = new DefaultTableModel(data, column);

        JTable jt=new JTable(model);
        jt.setRowSelectionAllowed(true);
        jt.setFillsViewportHeight(true);

        jt.getColumnModel().getColumn(0).setPreferredWidth(50);
        jt.getColumnModel().getColumn(1).setPreferredWidth(200);
        jt.getColumnModel().getColumn(2).setPreferredWidth(40);

        ListSelectionModel select= jt.getSelectionModel();
        select.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        jt.setBounds(30,40,200,300);
        JScrollPane scroll=new JScrollPane(jt);

        listPane = new JPanel();
        listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
        listPane.add(Box.createRigidArea(new Dimension(0,5)));
        listPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        listPane.add(scroll);

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JButton kickButton = new JButton("Kick");
        JButton kickAllButton = new JButton("Kick all");
        JButton addDummyPlayer = new JButton ("Add tester");

        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(addDummyPlayer);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(kickButton);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(kickAllButton);

        this.add(listPane, BorderLayout.CENTER);
        this.add(buttonPane, BorderLayout.PAGE_END);

        this.pack();
        this.setVisible(true);

        kickButton.addActionListener(e->{

                gameService.logger.log("UserManagement: Clicked KickUser");

                if (jt.getSelectedRow()>=0) {
                    String Stringuid = model.getValueAt(jt.getSelectedRow(), 1).toString(); // uuid

                    model.removeRow(jt.getSelectedRow());

                    UserManagement.this.gameService.kickUser(UUID.fromString(Stringuid));
                }
            }
        );

        kickAllButton.addActionListener(e->{

            gameService.logger.log("UserManagement: Clicked KickAll");

            while (model.getRowCount()>0) {
                String Stringuid = model.getValueAt(0,1).toString(); // uuid
                model.removeRow(0);
                this.gameService.kickUser(UUID.fromString(Stringuid));
                this.gameService.kickAll(); // clearing out miscellaneous users
            }

            gameService.logger.log("Users left in game: (calling gameService.getPlayers().size()) = " + gameService.getPlayers().size() );
        });

        addDummyPlayer.addActionListener(e->{

            UUID uuid = gameService.addUser("tester");

            gameService.playerJoinedGame(uuid.toString());

            gameService.testMode = true;
        });

        Thread thread = new Thread(this);
        thread.start();
    }

    public void addUser(String username, String Stringuid, PlayerState state) {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        Object[] rowdata = {username, Stringuid, state, now.format(dtf)};
        model.addRow(rowdata);

        model.fireTableDataChanged();
    }

    public void updateUserStatus(String Stringuid, PlayerState state) {
        this.state = state;
        for (int row = 0; row < model.getRowCount(); row++) {
            if (model.getValueAt(row, 1).equals(Stringuid)) {
                model.setValueAt(state.toString(), row, 2);
            }
        }

        model.fireTableDataChanged();
    }

    public void ping (String Stringuid) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        for (int row = 0; row < model.getRowCount(); row++) {
            if (model.getValueAt(row,1).equals(Stringuid) ) {
                model.setValueAt(now.format(dtf), row, 3);
            }
        }
    }

    @Override
    public void run() {
        int delta = 0;
        String stateString = "";

        while (true) {

            if (model.getRowCount() > 0) {

                for (int row = 0; row <model.getRowCount() ; row++) {


                    String oldtime = model.getValueAt(row, 3).toString();

                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    String nowtime = now.format(dtf);

                    int h1 = Integer.valueOf(nowtime.split(":")[1]);
                    int m1 = Integer.valueOf(nowtime.split(":")[2]);
                    int h2 = Integer.valueOf(oldtime.split(":")[1]);
                    int m2 = Integer.valueOf(oldtime.split(":")[2]);

                    if (h1 >= h2) {
                        delta = (h1 - h2) * 60 + (m1 - m2);
                    }

                    if (h1 < h2) {
                        delta = h1 * 60 + m1 + (60 - h2 - 1) * 60 + 60 - m2;
                    }

                    stateString = model.getValueAt(row, 2).toString();

                    if (delta > 5 && stateString.equals(PlayerState.INGAME.toString())) {
                        stateString = PlayerState.LOGGEDIN.toString();
                        model.setValueAt(stateString, row, 2);
                        delta = 0;
                    }
                    if (delta > 5 && stateString.equals(PlayerState.LOGGEDIN.toString())) {
                        stateString = PlayerState.NOTLOGGEDIN.toString();
                        model.setValueAt(stateString, row, 2);
                        delta = 0;
                    }

                    System.out.println(model.getValueAt(row, 0) + model.getValueAt(row, 2).toString() +  ", delta:" + delta);
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
