package frames;

import java.util.Random;

public class MineField {

    private int cols;
    private int rows;
    private int minesLeft; 

    private boolean flagMode; //true if we are in flagmode
    private boolean endOfGame; //true if the game has ended
    private boolean isOnTimer; //true if a timed bomb has been clicked and not yet disarmed

    private Tile[][] field;
    ///////////////////////////////////
    //Constructor:
    public MineField(int r, int c){
        rows =r;
        cols = c;
        minesLeft = 40;

        //creating the field to fill it up:
        field = new Tile[rows][cols];

        //first filling up with safeTiles:
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                field[i][j] = new SafeTile();
            }
        }

        //placing the mines:
        Random random = new Random();
        int minesPlaced = 0;

        while (minesPlaced < minesLeft) {
            int row = random.nextInt(rows);
            int col = random.nextInt(cols);

            if (!(field[row][col] instanceof MineTile)) {
                boolean timed = random.nextDouble() < 0.1; //bascially: it has a 10% chance of being timed
                field[row][col] = new MineTile(timed); 
                minesPlaced++;
            }
        }


        // Setting minesAround for safeTiles:
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // If the cell is not a mine, calculate the number of mines around it
                if (!(field[i][j] instanceof MineTile)) {
                    field[i][j].setMinesAround(countMinesAround(i, j));
                }
            }
        }
    }

/////////////////////////////////////////////////////////
    public int getMinesLeft(){
        return minesLeft;
    }
    public Tile getTile(int r, int c){
        return field[r][c];
    }

    public void setFlagMode(boolean b){
        flagMode = b;
    }

    public boolean isFlagMode(){
        return flagMode;
    }

    public void setTimer(boolean b){
        isOnTimer = b;
    }

    public boolean hasTimer(){
        return isOnTimer;
    }    


///////////////////////////////////////////////////////////////////
//counts the Mines around a safeTile:
    private int countMinesAround(int row, int col) {
        int count = 0; //n.o. mines

        //offset otimalization insted of a bunch of if else-s
        int[] rowOffsets = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] colOffsets = {-1, 0, 1, -1, 1, -1, 0, 1};

        // Check each neighboring cell
        for (int i = 0; i < 8; i++) {
            int newRow = row + rowOffsets[i];
            int newCol = col + colOffsets[i];

            // Check if the neighboring cell is within bounds and contains a mine
            if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols && (field[newRow][newCol] instanceof MineTile)) {
                count++; //if yes then add to the cound
            }
        }

        return count;
    }

/////////////////////////////////////////////////////////////
    //Reveal functions:
    public void reveal(int row, int col){
        if(!(field[row][col].isRevealed())){
            if(!flagMode){
                noFlagReveal(row, col);
            }

            else{
                flagModeReveal(row, col);
            }
    }
    }

    //reveal in NON flag mode: if simple mine lose, if timed bomb start timer, if empty reveal recursively, else simple reveal
    public void noFlagReveal(int row, int col){
        
            Tile tile = field[row][col]; //we will be working with this tile
            if(!tile.isFlagged()){
            if((tile.getMinesAround()==-1) && (!((MineTile)tile).getTimed())){
                endOfGame = true;
                tile.setRevealed(true);
            }
            
            else if((tile.getMinesAround()==-1) && ((MineTile)tile).getTimed()){
                if(!isOnTimer){
                    isOnTimer = true;
                    tile.setRevealed(true);
                }
            }
            
            else{
               if(tile.getMinesAround() == 0){
                    tile.setRevealed(true);
                    int[] rowOffsets = {-1, -1, -1, 0, 0, 1, 1, 1};
                    int[] colOffsets = {-1, 0, 1, -1, 1, -1, 0, 1};

                    for (int i = 0; i < 8; i++) {
                        int newRow = row + rowOffsets[i];
                        int newCol = col + colOffsets[i];

                        // Check if the neighboring cell is within bounds and contains a mine
                        if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols) {
                            reveal(newRow,newCol);
                        }
                     }
                }

                else{
                    tile.setRevealed(true);
                    
                }
            }
        }
    }
        

    //reveal in flagmode: if defused then timer dissappears, if not flagged flag, if flagged unflag
    public void flagModeReveal(int row, int col){
        
        Tile tile = field[row][col]; //we will be working with this tile

        
        if(isOnTimer && tile.getMinesAround()==-1 && !tile.isFlagged() && minesLeft != 0){
            tile.setFlagged(true); //we flagged a tile
            isOnTimer = false; //we defused the timed bomb

            if((--minesLeft) == 0){endOfGame = true;}
        }

        //flags if not flagged and has flags remaining
        else if(!tile.isFlagged() && minesLeft != 0){
            tile.setFlagged(true);
            if((--minesLeft) == 0){endOfGame = true;}
        }
        
        //unflags if it is flagged
        else if(tile.isFlagged()){
            tile.setFlagged(false);
            minesLeft++;
            if(endOfGame){endOfGame = false;}
        }
    
    }
///////////////////////////////////////////////////////////////////////////////////////
    //CHECK ENDING OUTCOME
    //param: true if the ending is bc of the timed bomb timer
    //return: -1 if the player lost, 0 if the game hasnt ended yet, 1 if the player won 
    public int checkEndOutcome(boolean timer){
        if(timer){return -1;}
        
        else if(!endOfGame){return 0;}

        if(minesLeft != 0){ //this means the player clicked on a mine
            return -1; 
        }

        
        //checking if all mines are flagged and all safe tiles are revealed
        int outcome = 1;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Tile tile = field[i][j];
                if(tile instanceof MineTile && !(tile.isFlagged())){outcome = 0;} //if a mine is not flagged
                if(tile instanceof SafeTile && !(tile.isRevealed())){outcome = 0;} //if a safe tile is not revealed
            }
        }
        return outcome;
    }

}
