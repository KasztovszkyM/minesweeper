package frames;

public class MineTile extends Tile{
    private boolean isTimed;
    

    MineTile(boolean b){
        isTimed = b;
    }

    public int getMinesAround(){
        return -1;
    }

    public boolean getTimed(){
        return isTimed;
    }

    public void setTimed(boolean b){
        isTimed = b;
    }
 
    public void setMinesAround(int m){//doesnt do much, only here bc of abstract class
    }
    
}
