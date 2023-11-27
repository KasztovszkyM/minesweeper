package junittest;

import org.junit.*;
import frames.LeaderBoard;

public class LeaderBoardListTest {

    private LeaderBoard lb;

    @Before
    public void setUp() {
        lb = new LeaderBoard();
    }

    @Test
    public void testAddEntry() {
        lb.add(50, "Player1");

        Assert.assertEquals(1, lb.getRowCount());
        Assert.assertEquals("Player1", lb.getValueAt(0, 0));
        Assert.assertEquals(50, lb.getValueAt(0, 1));
    }

    @Test
    public void testAddEntryDoesNotFit() {
        // Adding 5 entries to fill the leaderboard
        for (int i = 0; i < 5; i++) {
            lb.add(i * 10, "Player" + i);
        }

        // Trying to add a new entry that doesn't fit
        lb.add(30, "PlayerX");

        // Ensure the leaderboard remains unchanged
        Assert.assertEquals(5, lb.getRowCount());
    }

    @Test
    public void testAddEntryFits() {
        // Adding 5 entries to fill the leaderboard
        for (int i = 0; i < 4; i++) {
            lb.add(i * 10, "Player" + i);
        }

        // Adding a new entry that fits
        lb.add(25, "PlayerX");

        // Ensure the new entry is added and the leaderboard is sorted
        Assert.assertEquals(5, lb.getRowCount());
        Assert.assertEquals("PlayerX", lb.getValueAt(3, 0)); // Assuming it's in the middle after sorting
    }

    @Test
    public void testSort() {
        lb.add(30, "Player3");
        lb.add(20, "Player2");
        lb.add(10, "Player1");

        // Sorting should rearrange the entries in ascending order
        lb.sort();

        Assert.assertEquals(10, lb.getValueAt(0, 1));
        Assert.assertEquals(20, lb.getValueAt(1, 1));
        Assert.assertEquals(30, lb.getValueAt(2, 1));
    }

    @Test
    public void testFitsOnList() {
    	lb.add(50, "Player5");
    	lb.add(40, "Player4");
        lb.add(30, "Player3");
        lb.add(20, "Player2");
        lb.add(10, "Player1");

        // Adding a score that fits on the list
        Assert.assertTrue(lb.fitsOnList(15));

        // Adding a score that does not fit on the list
        Assert.assertFalse(lb.fitsOnList(60));
    }
    
    @Test
    public void testGetColumnName() {
        Assert.assertEquals("Name", lb.getColumnName(0));
        Assert.assertEquals("Time (s)", lb.getColumnName(1));
    }
    
    @Test
    public void testGetColumnClass() {
        Assert.assertEquals(String.class, lb.getColumnClass(0));
        Assert.assertEquals(Integer.class, lb.getColumnClass(1));
        Assert.assertEquals(Object.class, lb.getColumnClass(5));
    }

    @Test
    public void testGetColumnCount() {
        Assert.assertEquals(2, lb.getColumnCount());
    }
    
    @Test
    public void testGetValueAt() {
        lb.add(30, "Player1");
        lb.add(50, "Player2");

        // Testing the getValueAt method
        Assert.assertEquals("Player1", lb.getValueAt(0, 0)); 
        Assert.assertEquals(30, lb.getValueAt(0, 1));
        Assert.assertEquals("Player2", lb.getValueAt(1, 0));
        Assert.assertEquals(50, lb.getValueAt(1, 1));
        Assert.assertEquals(null, lb.getValueAt(1, 5));
    }
}