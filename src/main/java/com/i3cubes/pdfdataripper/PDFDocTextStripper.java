/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i3cubes.pdfdataripper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

/**
 *
 * @author kumara HHC
 */
public class PDFDocTextStripper extends PDFTextStripper{
    private SearchField[] s_fields;
    public HashMap<String,String> field_values;
    private boolean reading=false;
    private String text="";
    private String var_name="";
    private String search_text="";
    private float end_X,line_Y;
    private boolean remove_search_text=false;
    
    public PDFDocTextStripper(PDDocument pdd,SearchField[] search_fields) throws IOException {
        super();
        document = pdd;
        this.s_fields=search_fields;
        this.field_values=new HashMap<String,String>();
    }
    public void stripPage(int page_s,int page_e) throws IOException {
        this.setStartPage(page_s);
        this.setEndPage(page_e);
        Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream());
        writeText(document, dummy); // This call starts the parsing process and calls writeString repeatedly.
    }
    @Override
    protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
        //System.out.println("STRING["+string+"]-----"+reading+"   ...LineX="+line_Y+"  :EndX"+end_X);
        if(this.reading){
            for (TextPosition text : textPositions) {
                if(line_Y==text.getYDirAdj()){
                    if(text.getXDirAdj()<this.end_X){
                        this.text=this.text+text.getUnicode();
                    }
                    else{
                        reading=false;
                        if(remove_search_text){
                            this.text=this.text.replaceAll(this.search_text, "");
                        }
                        this.field_values.put(this.var_name, this.text.trim());
                        this.text="";this.line_Y=-1;this.end_X=-1;
                        break;
                    }
                }
                else{
                    reading=false;
                    if(remove_search_text){
                        this.text=this.text.replaceAll(this.search_text, "");
                    }
                    this.field_values.put(this.var_name, this.text.trim());
                    this.text="";this.line_Y=-1;this.end_X=-1;
                    break;
                }

                /*
                System.out.println("String[" + text.getXDirAdj() + "," + text.getYDirAdj() + " fs=" + text.getFontSizeInPt()
                        + " xscale=" + text.getXScale() + " height=" + text.getHeightDir() + " space="
                        + text.getWidthOfSpace() + " width=" + text.getWidthDirAdj() + " ] " + text.getUnicode());
                */
            }
        }
        if(!this.reading){
            for(SearchField field:this.s_fields){
                if(string.contains(field.search_text)){
                    TextPosition position=textPositions.get(0);
                    if(position.getYDirAdj()>field.search_start_y && position.getYDirAdj()<field.search_end_y){
                        //System.out.println(field.search_text+" ==["+position.getXDirAdj()+","+position.getYDirAdj()+"]");
                        this.reading=true;
                        this.line_Y=position.getYDirAdj();
                        this.end_X=position.getXDirAdj()+field.lenght;
                        this.text=string;
                        this.var_name=field.name;
                        this.search_text=field.search_text;
                        this.remove_search_text=field.dropSearchText;
                    }
                }
            }
        }
        /*
        
        */
    }

    public HashMap<String,String> getResult(){
        return field_values;
    }
}
