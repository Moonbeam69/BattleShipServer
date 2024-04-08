package battleship.service;

import battleship.dao.*;
import battleship.model.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

public class boardPane extends JPanel implements Runnable {
    int width;
    int height;
    int horizontalGridSize;
    int verticalGridSize;
    int squarewidth;
    int TOPLEFT;
    int TOPDOWN;
    int BRDSEPERATOR;
    public Map<String, List<Ship>> MapOfShips = new HashMap<>();
    public Map<String, Integer> score = new HashMap<>();
    public String currentPlayer;
    GameMap gameMap;
    GameService gs;

    Color dark_grey = new Color(100, 100, 100);
    Color shadow_grey = new Color(50, 50, 50);
    Color yellow_font = new Color(170, 170, 30);
    Color dark_yellow_font = new Color(140, 140, 0);
    Color LCD_green = new Color(0, 64, 00);
    Color orange = new Color (220, 160, 100);
    Color land_brown = new Color(130, 80, 40);

    boardPane(InMemoryDataService dao) {

        Map<String, Object> context = dao.getGameContext();

        width              = 900;
        height             = 1000;
        horizontalGridSize = (int)context.get("horizontalGridSize");
        verticalGridSize   = (int)context.get("verticalGridSize");
        squarewidth        = (int)context.get("squarewidth");
        TOPLEFT            = (int)context.get("TOPLEFT");
        TOPDOWN            = (int)context.get("TOPDOWN");
        BRDSEPERATOR       = (int)context.get("BRDSEPERATOR") + 20;

        this.setPreferredSize(new Dimension(width, height));
    }

    public void setGameService(GameService gs) {
        this.gs = gs;
        gs.gameMap = gameMap;
    }

    public void repaintIt() {
        repaint();
    }

    public void paintComponent(Graphics g) {
        int i=0;
        int j=0;
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;

        int y=50;
        g.drawString ("Current player: "+ currentPlayer, 500, y);

        y +=20;
        for (Map.Entry<String, Integer> entry : score.entrySet()) {
            g.drawString("->"+entry.getKey().toString() + ": " + entry.getValue().toString(), 500, y );
            y +=20;
        }

        {
            // draw defence LCD
            g.setColor(dark_grey);
            g.fillRect(TOPLEFT - 20, TOPDOWN - 20, horizontalGridSize * squarewidth + 40, verticalGridSize * squarewidth + 40);
            g.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            // shadow borders
            g.drawLine(TOPLEFT - 20 + 1, TOPDOWN - 20 + verticalGridSize * squarewidth + 40, TOPLEFT - 20 + horizontalGridSize * squarewidth + 40, TOPDOWN - 20 + verticalGridSize * squarewidth + 40);
            g.drawLine(TOPLEFT - 20 + horizontalGridSize * squarewidth + 40, TOPDOWN - 20 + 1, TOPLEFT - 20 + horizontalGridSize * squarewidth + 40, TOPDOWN - 20 + verticalGridSize * squarewidth + 40);

        }

        // draw islands
        for (i = 0; i< horizontalGridSize; i++ ) {
            for (j = 0; j < verticalGridSize; j++) {
                if (gameMap.boardArray[i][j].isMoveable() == false) {
                    g.setColor(land_brown);
                    g.fillRect(i * squarewidth + TOPLEFT, j * squarewidth + TOPDOWN, squarewidth, squarewidth);
                }
            }
        }
        // draw defence grid
        g2.setStroke(new BasicStroke(1));

        for (i = 0; i< horizontalGridSize; i++ ){
            for (j = 0; j< verticalGridSize; j++ ){
                g.setColor(yellow_font);
                g.drawRect(i * squarewidth + TOPLEFT, j * squarewidth + TOPDOWN, squarewidth, squarewidth);
                if (gameMap.boardArray[i][j].isMoveable() == false) {
                    g.setColor(land_brown);
                } else {
                    g.setColor(LCD_green);
                }
                g.fillRect(i * squarewidth + TOPLEFT+1, j * squarewidth + TOPDOWN+1, squarewidth-2, squarewidth-2);
            }
        }

        {
            // draw attack LCD

            // draw backgroumd
            g.setColor(dark_grey);
            g.fillRect(TOPLEFT - 20, TOPDOWN - 20 + BRDSEPERATOR, horizontalGridSize * squarewidth + 40, verticalGridSize * squarewidth + 40);
            g.setColor(Color.BLACK);

            // draw shadow
            g2.setStroke(new BasicStroke(2));
            g.drawLine(TOPLEFT - 20 + 1, TOPDOWN - 20 + verticalGridSize * squarewidth + 40 + BRDSEPERATOR, TOPLEFT - 20 + horizontalGridSize * squarewidth + 40, TOPDOWN - 20 + verticalGridSize * squarewidth + 40 + BRDSEPERATOR);
            g.drawLine(TOPLEFT - 20 + horizontalGridSize * squarewidth + 40, TOPDOWN - 20 + 1 + BRDSEPERATOR, TOPLEFT - 20 + horizontalGridSize * squarewidth + 40, TOPDOWN - 20 + verticalGridSize * squarewidth + 40 + BRDSEPERATOR);

        }

        // draw islands
        for (i = 0; i< horizontalGridSize; i++ ) {
            for (j = 0; j < verticalGridSize; j++) {
                if (gameMap.boardArray[i][j].isMoveable() == false) {
                    g.setColor(land_brown);
                    g.fillRect(i * squarewidth + TOPLEFT, j * squarewidth + TOPDOWN+ BRDSEPERATOR, squarewidth, squarewidth);
                }
            }
        }
        // draw attack grid
        g2.setStroke(new BasicStroke(1));
        for (i = 0; i< horizontalGridSize; i++ ){
            for (j = 0; j< verticalGridSize; j++ ){
                g.setColor(yellow_font);
                g.drawRect(i * squarewidth + TOPLEFT, j * squarewidth + TOPDOWN + BRDSEPERATOR, squarewidth, squarewidth);
                if (gameMap.boardArray[i][j].isMoveable() == false) {
                    g.setColor(land_brown);
                } else {
                    g.setColor(LCD_green);
                }
                g.fillRect(i * squarewidth + TOPLEFT+1, j * squarewidth + TOPDOWN+1 + BRDSEPERATOR, squarewidth-2, squarewidth-2);
            }
        }



        // Draw ships with damage
        g2.setStroke(new BasicStroke(3));

        int v_offset = 0;

        for (Map.Entry<String, List<Ship>> entry : MapOfShips.entrySet()) {

            for (Ship ship: entry.getValue()){

                g.setColor(Color.MAGENTA);
                g.drawString(entry.getKey(), TOPLEFT, TOPDOWN - 25 + v_offset);


                for (Section s: ship.getSections()) {
                    if (s.getState() == State.UNDAMAGED) {

                        if (ship.type!=ShipType.FLAG) {
                            g.setColor(Color.BLACK);
                        } else {
                            g.setColor(Color.YELLOW);
                        }
                        g.drawRect((int) s.getX() * squarewidth + TOPLEFT, (int) s.getY() * squarewidth + TOPDOWN + v_offset, squarewidth, squarewidth);
                    } else {
                        g.setColor(Color.RED);
                        g.fillRect((int) s.getX() * squarewidth + TOPLEFT, (int) s.getY() * squarewidth + TOPDOWN + v_offset, squarewidth, squarewidth);
                    }
                }
            }

            v_offset = BRDSEPERATOR;
        }
        g.setColor(Color.BLACK);
        g.drawString("Game state booleans:", 700, 680);
        g.drawString("gameOver=      " + gs.gameOver, 700, 700);
        g.drawString("gameCanStart=  " + gs.gameCanStart, 700, 720);


    }

    public void setScore(Map<String, Integer> score) {
        this.score = score;
    }

    public void setMapOfShips(Map<String, List<Ship>> mapOfShips) {
        this.MapOfShips = mapOfShips;
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    @Override
    public void run() {

        while (true) {

            repaint();

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
