/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i3cubes.pdfdataripper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 *
 * @author kumara HHC
 */
public class TableRow {
    public int row_number;
    public TableCell[] cells;
    public int size;

    /**
     * 
     * @param cols no of columns
     */
    public TableRow(int cols) {
        this.cells=new TableCell[cols];
        this.size=cols;
    }

    public int getSize() {
        return size;
    }

    public TableCell[] getCells() {
        return cells;
    }
    public String[] getAsArray(){
        String[] ary_cell=new String[cells.length];
        for(int i=0;i<cells.length;i++){
            ary_cell[i]=cells[i].value;
        }
        return ary_cell;
    }
    public Iterator<TableCell> getIterator(){
        return Arrays.asList(cells).iterator();
    }
    public String toString(){
        String str="";
        for(TableCell c:cells){
            str=str+"|"+c.value;
        }
        return str;
    }
}
