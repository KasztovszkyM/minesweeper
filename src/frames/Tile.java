package frames;

public abstract class Tile {
    private boolean revealed;

    private boolean flagged;

    public boolean isRevealed(){
        return revealed;
    }

    public void setRevealed(boolean b){
        revealed = b;
    }

    public boolean isFlagged(){
        return flagged;
    }

    public void setFlagged(boolean b){
        flagged = b;
    }
    
    public abstract int getMinesAround(); //if mine returns -1, else 0-8

    public abstract void setMinesAround(int m);
}

