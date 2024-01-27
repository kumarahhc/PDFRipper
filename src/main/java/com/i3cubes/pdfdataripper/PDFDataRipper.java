/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

package com.i3cubes.pdfdataripper;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author kumara HHC
 */
public class PDFDataRipper {
    public String instruction_file;
    private DataExtractor extractor;
    private XMLlUtility xml_util;
    public static PDFData data;
    
    public static void main(String[] args) {
        
    }
    /**
     * 
     * @param config_file  file path to the config file (xml)
     */
    public PDFDataRipper(String config_file) {
        this.instruction_file = instruction_file;
        this.instruction_file=config_file;
        
        this.xml_util=new XMLlUtility();
        this.data=new PDFData();
    }
    
    /**
     * 
     * @param pdf_file file path to the pdf that needs to extract data from
     * @return PDFData object containing all extracted data
     * @throws IOException must be handled
     */
    public PDFData ExtractPDFData(String pdf_file) throws IOException{
        this.extractor=new DataExtractor(pdf_file);
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            // parse XML file
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(instruction_file));
            doc.getDocumentElement().normalize();
            //System.out.println("Root Element :" + doc.getDocumentElement().getNodeName());
            Element root=(Element)doc.getDocumentElement();
            NodeList dev_list=root.getChildNodes();
            Element el;
            for(int j=0; j<dev_list.getLength();j++){
                Node r_node=dev_list.item(j);
                if(r_node.getNodeType()==Node.ELEMENT_NODE){
                    el=(Element)r_node;
                    //System.out.println("TAG:"+el.getTagName());
                    switch(el.getTagName()){
                        case "readByArea":
                            NodeList area_list=el.getChildNodes();
                            //System.out.println("AREA Nos:"+area_list.getLength());
                            for(int k=0; k<area_list.getLength();k++){
                                Node area_node=area_list.item(k);
                                //System.out.println("TYE::::::"+k+"-->"+area_node.getNodeType());
                                if(area_node.getNodeType()==Node.ELEMENT_NODE){
                                    Element el2=(Element)area_node;
                                    //System.out.println("TAG:"+el2.getTagName());
                                    
                                    Element el2_type=(Element) el2.getElementsByTagName("readType").item(0);
                                    //System.out.println("readType:"+el2_type.getTextContent());
                                    //System.out.println("var_name:"+el2.getAttribute("id"));
                                    Rectangle area;
                                    int page_no;
                                    String var_name;
                                    Object value;
                                    
                                    switch(el2_type.getTextContent()){
                                        case "FullText":
                                            //String val="";
                                            area=this.xml_util.getRectangle(el2);
                                            page_no=this.xml_util.getPageNumber(el2);
                                            var_name=el2.getAttribute("id");
                                            if(area!=null){
                                                if(page_no!=-1){
                                                    value=this.extractor.ExtractByAreaFullText(area, 0);
                                                }
                                                else{
                                                    value=new String("page number is not valid");
                                                }
                                            }
                                            else{
                                                value=new String("area definition is not valid");
                                            }
                                            this.data.put(var_name,value);
                                            break;
                                        case "FindByText":
                                            area=this.xml_util.getRectangle(el2);
                                            page_no=this.xml_util.getPageNumber(el2);
                                            var_name=el2.getAttribute("id");
                                            HashMap<String,String> vars=this.xml_util.getVarList(el2);
                                            if(area!=null){
                                                if(page_no!=-1){
                                                    if(vars.size()>0){
                                                        value=this.extractor.ExtractByAreaByText(area, page_no, vars);
                                                    }
                                                    else{
                                                        value=new String("No variables defined");
                                                    }
                                                }
                                                else{
                                                    value=new String("page number is not valid");
                                                }
                                            }
                                            else{
                                                value=new String("area definition is not valid");
                                            }
                                            this.data.put(var_name,value);
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            }
                            break;
                        case "readInDodument":
                            String var_name=el.getAttribute("id");
                            if(var_name.equals(""))var_name="document_values"+j;
                            String page_s=el.getElementsByTagName("startPageNo").item(0).getTextContent().trim();
                            String page_e=el.getElementsByTagName("endPageNo").item(0).getTextContent().trim();
                            //System.out.println("Pages:"+page_s+"-"+page_e);
                            NodeList field_list=el.getElementsByTagName("field");
                            //System.out.println("FIELD Nos:"+field_list.getLength());
                            SearchField[] search_fields=new SearchField[field_list.getLength()];
                            for(int k=0; k<field_list.getLength();k++){
                                Node field_node=field_list.item(k);
                                if(field_node.getNodeType()==Node.ELEMENT_NODE){
                                    Element el2=(Element)field_node;
                                    //System.out.println("TAG:"+el2.getTagName());
                                    SearchField field=new SearchField();
                                    field.search_text=el2.getElementsByTagName("searchText").item(0).getTextContent();
                                    String len=el2.getElementsByTagName("searchLenght").item(0).getTextContent();
                                    if(!len.trim().equalsIgnoreCase(""))field.lenght=Integer.parseInt(len.trim());
                                    String s_s_y=el2.getElementsByTagName("searchStartY").item(0).getTextContent();
                                    if(!s_s_y.equalsIgnoreCase("")) field.search_start_y=Float.parseFloat(s_s_y.trim());
                                    String s_e_y=el2.getElementsByTagName("searchEndY").item(0).getTextContent();
                                    if(!s_e_y.equalsIgnoreCase("")) field.search_end_y=Float.parseFloat(s_e_y.trim());
                                    
                                    field.name=el2.getAttribute("id");
                                    if(el2.getElementsByTagName("dropSearchText").item(0).getTextContent().equalsIgnoreCase("YES")){
                                        field.dropSearchText=true;
                                    }
                                    else{
                                        field.dropSearchText=false;
                                    }
                                    search_fields[k]=field;
                                }
                            }
                            //
                            PDFCotentExtractor pdf_search=new PDFCotentExtractor(pdf_file);
                            HashMap<String,String> values=pdf_search.ExtractTextFromDocument(page_s, page_e, search_fields);
                            this.data.put(var_name, values);
                            break;
                        case "readTable":
                            String var_name_t=el.getAttribute("id");
                            if(var_name_t.equals(""))var_name="table"+j;
                            String table_type=el.getElementsByTagName("tableType").item(0).getTextContent().trim();
                            String row_format=el.getElementsByTagName("rowFormat").item(0).getTextContent().trim();
                            if(row_format.equalsIgnoreCase(""))row_format="single";
                            String startY=el.getElementsByTagName("startY").item(0).getTextContent().trim();
                            if(startY.equalsIgnoreCase(""))startY="0";
                            float start_Y=Float.parseFloat(startY);
                            String startPage=el.getElementsByTagName("startPage").item(0).getTextContent().trim();
                            String endPage=el.getElementsByTagName("endPage").item(0).getTextContent().trim();
                            String header_present=el.getElementsByTagName("headerPresent").item(0).getTextContent().trim();
                            String row_identify_col=el.getElementsByTagName("rowIdentificationColumn").item(0).getTextContent().trim();
                            if(row_identify_col.equalsIgnoreCase(""))row_identify_col="0";
                            int row_identify_col_no=Integer.parseInt(row_identify_col)-1;//table row starts from 0
                            
                            Element el3=(Element)el.getElementsByTagName("endOfTable").item(0);
                            String table_end_method=el3.getElementsByTagName("method").item(0).getTextContent().trim();
                            String table_end_text="";
                            if(table_end_method.equalsIgnoreCase("EndByText")){
                                table_end_text=el3.getElementsByTagName("endText").item(0).getTextContent().trim();
                            }
                            //System.out.println("HEADER:"+header_present);
                            if(header_present.equalsIgnoreCase("YES")){
                                NodeList cell_list=el.getElementsByTagName("cell");
                                //System.out.println("AREA Nos:"+cell_list.getLength());
                                TableHeader[] header_fields=new TableHeader[cell_list.getLength()];

                                for(int k=0; k<cell_list.getLength();k++){
                                    Node cell_node=cell_list.item(k);
                                    if(cell_node.getNodeType()==Node.ELEMENT_NODE){
                                        Element el4=(Element)cell_node;
                                        TableHeader h=new TableHeader();
                                        h.name=el4.getElementsByTagName("text").item(0).getTextContent();
                                        String marL=el4.getElementsByTagName("leftMargin").item(0).getTextContent();
                                        if(marL.equalsIgnoreCase("")) marL="0";
                                        h.margin_left=Float.parseFloat(marL);
                                        //non mandatory right margin
                                        NodeList mar_nodes=el4.getElementsByTagName("rightMargin");
                                        if(mar_nodes.getLength()>0){
                                            String marR=el4.getElementsByTagName("rightMargin").item(0).getTextContent();
                                            h.margin_right=Float.parseFloat(marR);
                                        }
                                        header_fields[k]=h;
                                    }
                                }
                                //for(TableHeader h:header_fields){
                                    //System.out.println("H:"+h.name+"["+h.margin_left+"]");
                                //}

                                PDFCotentExtractor pdf_table=new PDFCotentExtractor(pdf_file);
                                Table table=pdf_table.ExtractTableFromDocument(startPage, "", header_fields,true,start_Y,row_identify_col_no,table_end_method,table_end_text,row_format,table_type);
                                //table.printTable();
                                this.data.put(var_name_t, table);
                            }
                            else{
                                String startX=el.getElementsByTagName("startX").item(0).getTextContent().trim();
                                if(startX.equalsIgnoreCase(""))startX="0";
                                float start_X=Float.parseFloat(startX);
                                NodeList cell_list=el.getElementsByTagName("cell");
                                //System.out.println("AREA Nos:"+cell_list.getLength());
                                TableHeader[] header_fields=new TableHeader[cell_list.getLength()];

                                for(int k=0; k<cell_list.getLength();k++){
                                    Node cell_node=cell_list.item(k);
                                    if(cell_node.getNodeType()==Node.ELEMENT_NODE){
                                        Element el5=(Element)cell_node;
                                        TableHeader h=new TableHeader();
                                        //h.name=el3.getElementsByTagName("text").item(0).getTextContent();
                                        String w=el5.getElementsByTagName("width").item(0).getTextContent().trim();
                                        if(k==0){
                                            //First Col
                                            h.startX=start_X;
                                            h.endX=start_X+Float.parseFloat(w);
                                        }
                                        else{
                                            h.startX=header_fields[k-1].endX;
                                            h.endX=header_fields[k-1].endX+Float.parseFloat(w);
                                        }
                                        header_fields[k]=h;
                                    }
                                }
                                PDFCotentExtractor pdf_table=new PDFCotentExtractor(pdf_file);
                                Table table=pdf_table.ExtractTableFromDocument(startPage, endPage, header_fields,false,start_Y,row_identify_col_no,table_end_method,table_end_text,row_format,table_type);
                                //table.printTable();
                                this.data.put(var_name_t, table);
                            }
                            break;
                    }
                }
            }
            //Print DATA
            //System.out.println(data.toString());
            
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(PDFDataRipper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(PDFDataRipper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return data;
    }
}
