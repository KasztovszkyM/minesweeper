package frames;

import java.util.*;


import javax.swing.table.AbstractTableModel;

public class LeaderBoard extends AbstractTableModel{
    private List<Map.Entry<Integer, String>> list;
    private String[] colName = {"Name", "Time (s)"};


    public LeaderBoard(){
        list = new ArrayList<>();
    }

    public void sort() {
        //sort in ascending order
        Collections.sort(list, Comparator.comparingInt(Map.Entry::getKey));
        
        fireTableDataChanged(); // Notify the table model that the data has changed
    }

    public void add(int score, String name){
        if (fitsOnList(score) && !name.equals("")) {
            if (list.size() >= 5) {
                Map.Entry<Integer, String> lowestEntry = list.get(4);
                if (lowestEntry.getKey() >= score) {
                    // Replace the lowest entry with the new entry
                    list.remove(4);
                    list.add(new AbstractMap.SimpleEntry<>(score, name));
                    this.sort();
                    fireTableDataChanged(); // Notify the table model that the data has changed
                }
            } else {
                // If the list has less than 5 elements, just add the new entry
                list.add(new AbstractMap.SimpleEntry<>(score, name));
                this.sort();
                fireTableRowsInserted(0, list.size() - 1);
            }
        }
    }

    public boolean fitsOnList(int score){
         return (list.size()<5 || list.get(4).getKey()>=score);
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
        if (col == 0) {
            return String.class; // Name column
        } else if (col == 1) {
            return Integer.class; // Score column
        } else {
            return Object.class;
        }
    }
}
