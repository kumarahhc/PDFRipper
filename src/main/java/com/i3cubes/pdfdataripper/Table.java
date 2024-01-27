/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i3cubes.pdfdataripper;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 
 * @author kumara HHC
 */
public class Table {
    private TableHeader[] headers;
    private ArrayList<TableRow> rows;

    /**
     * 
     * @param cols no of columns
     */
    public Table(int cols) {
        this.headers=new TableHeader[cols];
        this.rows=new ArrayList<TableRow>();
    }
    /**
     * 
     * @param row table row
     */
    public void addRow(TableRow row){
        if(row!=null){
            rows.add(row);
        }
    }
    /**
     * 
     * @param i row number
     * @return TableRow object
     */
    public TableRow getRow(int i){
        return this.rows.get(i);
    }
    /**
     * 
     * @param h array of TableHeader
     */
    public void setTableHeader(TableHeader[] h){
        this.headers=h;
    }
    /**
     * 
     * @return array of TableHeader
     */
    public TableHeader[] getHeaders(){
        return this.headers;
    }
    public TableRow newRow(){
        TableRow r=new TableRow(this.headers.length);
        if(headers.length>0){
            for(int i=0;i<headers.length;i++){
                r.cells[i]=new TableCell();
            }
        }
        return r;
    }
    /**
     * This will return the Iterator that represent the rows
     * @return Iterator
     */
    public Iterator<TableRow> getIterator(){
        return rows.iterator();
    }
    /**
     * this will return arrayList of string arrays that represent each cell of the row
     * @return ArrayList of String arrays
     */
    public ArrayList<String[]> getTableRows(){
        ArrayList<String[]> map=new ArrayList<String[]>();
        for(TableRow r:rows){
            map.add(r.getAsArray());
        }
        return map;
    }
    /**
     * print all headers in to out put
     */
    public void printHeader(){
        String str="";
        for(TableHeader h:this.headers){
            str=str+"|"+h.name;
        }
        //System.out.println(str);
    }
    public void printTable(){
        this.printHeader();
        String str="";
        for(TableRow r:this.rows){
            System.out.println("["+r.toString()+"]");
        }
    }
    public void MergeTable(Table t){
        for(TableRow tr:t.rows){
            this.addRow(tr);
        }
    }
    public String toString(){
        String str="";
        for(TableRow r:this.rows){
            str=str+r.toString()+"\r\n";
        }
        return str;
    }
}
