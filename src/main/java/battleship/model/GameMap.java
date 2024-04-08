package battleship.model;

import battleship.utils.*;

import java.io.*;

public class GameMap {
    public int gridsize_x;
    public int gridsize_y;
    public GridField[][] boardArray;
    public GameMap(){}
    myLogger logger;

    public GameMap(myLogger logger, int gridsize_x, int gridsize_y){
        this.logger = logger;
        this.gridsize_x = gridsize_x;
        this.gridsize_y = gridsize_y;

        boardArray = new GridField[gridsize_x][gridsize_y];

        // initialise map
        for (int i=0; i< gridsize_x; i++) {
            for (int j=0; j< gridsize_y; j++) {
                boardArray[i][j] = new GridField(i, j, true);
            }
        }

        AddIslands(1);
    }

    private void AddIslands(int no)  {

        // TODO implement no and random island shapes
        BufferedReader br;
        int x=0;
        int y=0;


        try {
            String islandFileName = "/island.csv";
            InputStream inputStream = getClass().getResourceAsStream(islandFileName);
            br = new BufferedReader(new InputStreamReader(inputStream));

            logger.log("Reading island map data from " + islandFileName);
            logger.log("   ABCDEFGHIJKLMNOPQRST");
            String line;
            String spacer = "  ";

            while ((line = br.readLine()) != null) {

                if(y>9) spacer = " ";
                logger.log(y + spacer + line.replace(",", ""));

                String[] grid = line.split(",");
                for (x=0; x< grid.length; x++) {

                    if(grid[x].equals("1")) {
                        boardArray[x][y].setMoveable(false);
                    }
                }
               y++;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public GridField[][] getBoardArray() {
        return boardArray;
    }

    public int getGridsize_x() {
        return gridsize_x;
    }

    public int getGridsize_y() {
        return gridsize_y;
    }
}