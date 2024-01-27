/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i3cubes.pdfdataripper;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripperByArea;

/**
 *
 * @author kumara HHC
 */
public class DataExtractor {
    private String file_path;
    
    public DataExtractor(String file) {
        this.file_path=file;
    }
    /**
     * 
     * @param area Rectangle object that represent the coordinates of the area that need to read
     * @param page_no page number of the pdf document
     * @param vars Hash map of (variable name, search text) 
     * @return Hash map of variable name, value
     * @throws IOException must be handled
     */
    public HashMap<String,String> ExtractByAreaByText(Rectangle area,int page_no,HashMap<String,String> vars) throws IOException{
        HashMap<String,String> vars_values=new HashMap<String,String>();
        PDDocument pdDoc=PDDocument.load(new File(this.file_path));
        PDFTextStripperByArea stripper=new PDFTextStripperByArea();
        stripper.setSortByPosition(true);
        stripper.addRegion("region", area);

        PDPage page=pdDoc.getPage(page_no);
        stripper.extractRegions(page);

        String a1_text=stripper.getTextForRegion("region");
        //System.out.println("AREA-1");
        //System.out.println(a1_text);
        //System.out.println("-------------LINES-----------");
        String[] lines=a1_text.split("\\r?\\n",-1);
        for(String line:lines){
            //System.out.println(line);
        }
        for(HashMap.Entry<String, String> entry:vars.entrySet()){
            String find_txt=entry.getValue().trim().replaceAll("(\\r\\n\\t)", "");
            String v=ExtractVariableWithName(lines, find_txt);
            //System.out.println("VAR::"+entry.getKey()+"{"+find_txt+"}"+"="+v);
            vars.put(entry.getKey(), v);
        }
        return vars;
    }
    /**
     * 
     * @param area Rectangle object that represent the coordinates of the area that need to read
     * @param page_no page number of the pdf document
     * @return Extracted text as string
     * @throws IOException must be handled
     */
    public String ExtractByAreaFullText(Rectangle area,int page_no) throws IOException{        
        PDDocument pdDoc=PDDocument.load(new File(this.file_path));
        PDFTextStripperByArea stripper=new PDFTextStripperByArea();
        stripper.setSortByPosition(true);
        stripper.addRegion("region", area);

        PDPage page=pdDoc.getPage(page_no);
        stripper.extractRegions(page);

        String a1_text=stripper.getTextForRegion("region");
        //System.out.println("PAGE:"+page_no);
        //System.out.println(a1_text);
        return this.formatFullText(a1_text);
    }
    /**
     * 
     * @param lines lines of string extracted from the pdf document
     * @param extract_string search string for the line
     * @return 
     */
    private String ExtractVariableWithName(String[] lines,String extract_string){
        for(String line:lines){
            if(line.indexOf(extract_string)!=-1){
                String val=line.replaceAll(extract_string, "");
                return val.trim();
            }
        }
        return null;
    }
    private String formatFullText(String txt){
        String str="";
        String[] lines=txt.split("\\r?\\n",-1);
        for(String line:lines){
            line=line.replaceAll("(\\r\\n\\t)", "");
            if(!line.trim().equalsIgnoreCase("")){
                str=str+line.trim()+"\r\n";
            }
        }
        return str;
    }
}
