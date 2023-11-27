package junittest;

import org.junit.*;
import frames.MineTile;
import frames.SafeTile;


public class TileTest {
	MineTile mt;
	SafeTile st;
	
	@Before
	public void SetUp() {
		mt = new MineTile();
		st = new SafeTile();
	}
	
	@Test
	public void MineTileGetMinesAroundTest() {
		mt.setMinesAround(1);
		Assert.assertEquals(mt.getMinesAround(), -1);
	}
	
	@Test
	public void SafeTileGetMinesAroundTest() {
		st.setMinesAround(5);
		Assert.assertEquals(st.getMinesAround(), 5);
	}
	
	@Test
	public void TileFlaggedTest() {
		mt.setFlagged(true);
		Assert.assertEquals(mt.isFlagged(), true);
	}
	
	@Test 
	public void TileRevealedTest() {
		mt.setRevealed(true);
		Assert.assertEquals(true, true);
	}
	
}
