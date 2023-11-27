package frames;

import java.util.Random;

public class SafeTile extends Tile{
    private int minesAround;
    private boolean special;

    public SafeTile(){
        Random random = new Random();
        special = random.nextDouble() < 0.03;
    }

    public int getMinesAround(){
        return minesAround;
    }
    public void setMinesAround(int m){
        minesAround = m;
    }

    public boolean getSepcial(){
        return special;
    }

}
