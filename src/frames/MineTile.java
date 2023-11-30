package frames;
import java.util.Random;

public class MineTile extends Tile{
    private boolean isTimed;
    

    public MineTile(){
        Random random = new Random();
        isTimed = random.nextDouble() < 1; //bascially: it has a 10% chance of being timed
        
    }

    public int getMinesAround(){
        return -1;
    }

    public boolean getTimed(){
        return isTimed;
    }


    public void setMinesAround(int m){//doesnt do much, only here bc of abstract class
    }
    
}
