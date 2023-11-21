package frames;

public class SafeTile extends Tile{
    private int minesAround;

    public int getMinesAround(){
        return minesAround;
    }
    public void setMinesAround(int m){
        minesAround = m;
    }

}
