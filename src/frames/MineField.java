package frames;

import java.io.Serializable;
import java.util.Random;

public class MineField implements Serializable{
    private Random random = new Random(); 

    private int cols;
    private int rows;
    private int minesLeft; 

    private boolean firstMove; //true if all the tiles are unrevealed
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
        firstMove = true;

        //creating the field to fill it up:
        field = new Tile[rows][cols];

        //first filling up with safeTiles:
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                field[i][j] = new SafeTile();
            }
        }

        /* // Setting minesAround for safeTiles:
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // If the cell is not a mine, calculate the number of mines around it
                if (!(field[i][j] instanceof MineTile)) {
                    field[i][j].setMinesAround(countMinesAround(i, j));
                }
            }
        } */
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
//sets revealed to true on every tile
 public void revealAll(){
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                field[i][j].setRevealed(true);
            }
        }
    }

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
            if(firstMove && !flagMode){
                firstReveal(row, col);
            }

            else if(!flagMode){
                noFlagReveal(row, col);
            }

            else{
                flagModeReveal(row, col);
            }
    }
    }


    public void firstReveal(int row, int col){
 
            int[][] cellsToReplace = {
                {row, col},
                {row - 1, col - 1}, {row - 1, col}, {row - 1, col + 1},
                {row, col - 1}, {row, col + 1},
                {row + 1, col - 1}, {row + 1, col}, {row + 1, col + 1}
        };
        
        //placing the mines:
        int minesPlaced = 0;
        
        while (minesPlaced < minesLeft) {
            int row2 = random.nextInt(rows);
            int col2 = random.nextInt(cols);

            boolean isprotected = false;
            for (int[] cell : cellsToReplace) {
            int r = cell[0];
            int c = cell[1];

            //Check if the cell is within bounds AND has a mine AND give it a random chance to swap
            if ((r >= 0 && r < rows && c >= 0 && c < cols) && (r == row2 && c == col2)) isprotected = true; 
            }


            if ((!(field[row2][col2] instanceof MineTile)) && (!isprotected)) {
                boolean timed = random.nextDouble() < 0.1; //bascially: it has a 10% chance of being timed
                field[row2][col2] = new MineTile(timed); 
                minesPlaced++;
            }
        }
        // Replace mines in the specified cells with non-mine cells randomly
        

            // Setting minesAround for safeTiles:
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    // If the cell is not a mine and not the swapped empty tile,
                    // calculate the number of mines around it
                    if (!(field[i][j] instanceof MineTile) ) {
                        field[i][j].setMinesAround(countMinesAround(i, j));
                    }
                }
            } 
            firstMove = false;
        

            noFlagReveal(row, col);
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
                //if its the first timed bomb you get a timer
                if(!isOnTimer){
                isOnTimer = true;  
                }
                //if its the second timed bomb you just lose
                else{
                endOfGame = true;
                }
                //reveal anyway
                tile.setRevealed(true);
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
    
    //return: -1 if the player lost, 0 if the game hasnt ended yet, 1 if the player won 
    public int checkEndOutcome(){
    
        if(!endOfGame){return 0;}

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
