/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i3cubes.pdfdataripper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import org.apache.pdfbox.pdmodel.PDDocument;

/**
 *
 * @author kumara HHC
 */
public class PDFCotentExtractor{
    private String file_path="";
    private SearchField[] search_fields;

    public PDFCotentExtractor(String f) {
        this.file_path=f;        
    }
    /**
     * 
     * @param page_s start page
     * @param page_e end page
     * @param searh_fiels array of searchField
     * @return HashMap of (variable name, value)
     */
    public HashMap<String,String> ExtractTextFromDocument(String page_s,String page_e,SearchField[] searh_fiels){
        //System.out.println("PAGES["+page_s+":"+page_e+"]");
        //for(SearchField f:searh_fiels){
            //System.out.println("F<"+f.search_text+">");
        //}
        int page_start,page_end;
        HashMap<String,String> result=null;
        File f = new File(file_path);
        FileInputStream fis = null;
        PDFDocTextStripper stripper=null;
        PDDocument pdd = null;
        try {
            fis = new FileInputStream(f);
            pdd = PDDocument.load(fis);
            if(page_s.equalsIgnoreCase("")){page_start=1;}else{page_start=Integer.parseInt(page_s);};
            if(page_e.equalsIgnoreCase("")){page_end=pdd.getNumberOfPages();}else{page_end=Integer.parseInt(page_e);};
            //System.out.println("PAGES["+page_start+":"+page_end+"]");
            stripper = new PDFDocTextStripper(pdd,searh_fiels);
            stripper.setSortByPosition(true);
            stripper.stripPage(page_start,page_end);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            result=stripper.getResult();
        }
        return result;
    }
    
    /**
     * 
     * @param page_s start page 
     * @param page_e end page
     * @param header_fiels array of SearchField
     * @param has_header whether table has identifiable header
     * @param startY start Y coordinated (points)
     * @param key_col column number that can be used to identify row breaks
     * @param table_end_method how to identify end of the table
     * @param end_data if the table end by text, this is the identify string
     * @param rformat row type
     * @param table_type table type (continous table from start to end though out the pages, or repeat same table every page)
     * @return Table object
     */
    public Table ExtractTableFromDocument(String page_s,String page_e,TableHeader[] header_fiels,boolean has_header,float startY,int key_col,String table_end_method,String end_data,String rformat,String table_type){
        //System.out.println("PAGES["+page_s+":"+page_e+"]");
        //for(TableHeader h:header_fiels){
            //System.out.println("H<"+h.name+">");
        //}
        int page_start,page_end;
        Table result=null;
        File f = new File(file_path);
        FileInputStream fis = null;
        PDFTableStripper stripper=null;
        PDDocument pdd = null;
        try {
            fis = new FileInputStream(f);
            pdd = PDDocument.load(fis);
            if(page_s.equalsIgnoreCase("")){page_start=1;}else{page_start=Integer.parseInt(page_s);};
            if(page_e.equalsIgnoreCase("")){page_end=pdd.getNumberOfPages();}else{page_end=Integer.parseInt(page_e);};
            //System.out.println("PAGES["+page_start+":"+page_end+"]");
            
            if(table_type.equalsIgnoreCase("EachPage")){
                result=new Table(header_fiels.length);
                result.setTableHeader(header_fiels);
                ///Read page by page
                for(int i=page_start;i<=page_end;i++){
                    stripper = new PDFTableStripper(pdd,header_fiels,has_header,startY,key_col,table_end_method,end_data,rformat);
                    stripper.setSortByPosition(true);
                    stripper.stripPage(i,i);
                    result.MergeTable(stripper.getTable());// append table rows to table
                }
            }
            else{
                //read all at once
                stripper = new PDFTableStripper(pdd,header_fiels,has_header,startY,key_col,table_end_method,end_data,rformat);
                stripper.setSortByPosition(true);
                stripper.stripPage(page_start,page_end);
                result=stripper.getTable();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
}
