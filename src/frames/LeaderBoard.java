package frames;
import java.util.*;


import javax.swing.table.AbstractTableModel;




public class LeaderBoard extends AbstractTableModel{
    private List<Map.Entry<Integer, String>> list;
    private String[] colName = {"Name", "Time"};


    LeaderBoard(){
        list = new ArrayList<>();
    }

    public void sort() {
        //sort in reversed order:
        Collections.sort(list, Collections.reverseOrder(Comparator.comparingInt(Map.Entry::getKey)));
        
        fireTableDataChanged(); // Notify the table model that the data has changed
    }

    public void add(int score, String name){
        if(fitsOnList(score)){
            if(list.size()>=5){list.remove(0);} //if we can add a higher score remove the lowest
        list.add(new AbstractMap.SimpleEntry<>(score,name));
        this.sort();
        }
    }

    public boolean fitsOnList(int score){
         return (list.size()<5 || list.get(0).getKey()<=score);
         //we can add the new score if: 
         //the list has less than 5 elements
         //the score is higher than the lowest score (the sorting happens elsewhere)
    }

    @Override
    public int getRowCount() {
        return list.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Map.Entry<Integer, String> entry = list.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return entry.getValue(); // Name
            case 1:
                return entry.getKey();   // Score
            default:
                return null;
        }
    }

    @Override
    public String getColumnName(int index) {
         return colName[index];
    }

    @Override
    public Class<?> getColumnClass(int col) {
        return String.class;
            
    }
}
