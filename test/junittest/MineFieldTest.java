package junittest;

import org.junit.*;
import frames.MineField;
import frames.MineTile;

public class MineFieldTest {
	MineField mf;
	
	@Before
	public void SetUp() {
		mf = new MineField(16,16);
	}

	@Test
    public void testMinePlacement() {
        mf.firstReveal(0, 0);

        int mineCount = 0;
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                if (mf.getTile(i, j) instanceof MineTile) {
                    mineCount++;
                }
            }
        }

        Assert.assertEquals(40, mineCount); // Assuming 40 mines are placed
    }

	 @Test
	 public void testFlagging() {
		 mf.firstReveal(0, 0);
		 mf.setFlagMode(true);

	     int initialMinesLeft = mf.getMinesLeft();
	     mf.flagModeReveal(0, 0);

	     Assert.assertEquals(initialMinesLeft - 1, mf.getMinesLeft());
	    }
	
	 @Test
	 public void testFirstRevealing() {
	     mf.firstReveal(0, 0);
	     Assert.assertEquals(0, mf.getTile(0, 0).getMinesAround());
	    }
	 
	 
	 @Test
	 public void testEndOutcomeWin() {
		 mf.firstReveal(0, 0);
		 for (int i = 0; i < 16; i++) {
			 for (int j = 0; j < 16; j++) {
				 if (!(mf.getTile(i, j) instanceof MineTile)) {
					 	mf.noFlagReveal(i, j);
	                }
	            }
	        }
		 
		 //this is because some safeTiles make flags disappear
		 for (int i = 0; i < 16; i++) {
			 for (int j = 0; j < 16; j++) {
				 if (mf.getTile(i, j) instanceof MineTile) {
					 	mf.flagModeReveal(i, j);
	                }
	            }
	        }

	    Assert.assertEquals(1, mf.checkEndOutcome());
	    }
	 
	 @Test
	 public void testEndOutcomeLose() {
		 mf.firstReveal(0, 0);
		 for (int i = 0; i < 16; i++) {
			 for (int j = 0; j < 16; j++) {
				 if (mf.getTile(i, j) instanceof MineTile) {
	                    mf.noFlagReveal(i, j);
	           }
	        }
		 }
	    Assert.assertEquals(-1, mf.checkEndOutcome());
	    }
	 
	 @Test
	 public void testEndOutcomeNeither() {
		 mf.firstReveal(0, 0);
		 for (int i = 0; i < 16; i++) {
			 for (int j = 0; j < 16; j++) {
				 if (mf.getTile(i, j) instanceof MineTile) {
	                    mf.flagModeReveal(i, j);
	           }
	        }
		 }
	    Assert.assertEquals(0, mf.checkEndOutcome());
	    }
	 
	 @Test
	 public void testRevealAll() {
		 mf.revealAll();
	        for (int i = 0; i < 16; i++) {
	            for (int j = 0; j < 16; j++) {
	                Assert.assertTrue(mf.getTile(i, j).isRevealed());
	            }
	        }
	    }
	 
	 @Test
	 public void testFlagModeRevealUnflag() {
	     mf.firstReveal(0, 0);
	     mf.setFlagMode(true);

	     // Flag a tile and then unflag it
	     mf.flagModeReveal(1, 1);
	     int initialMinesLeft = mf.getMinesLeft();
	     mf.flagModeReveal(1, 1);

	     // Ensure the tile is unflagged and minesLeft is incremented
	     Assert.assertEquals(initialMinesLeft + 1, mf.getMinesLeft());
	     Assert.assertFalse(mf.getTile(0, 0).isFlagged());
	    }
	 
	 @Test
	 public void testCountMinesAround() {
	     mf.firstReveal(2, 2);

	     // Assuming no mines around the revealed tile at (2, 2)
	     Assert.assertEquals(0, mf.getTile(2,2).getMinesAround());
	    }
}
