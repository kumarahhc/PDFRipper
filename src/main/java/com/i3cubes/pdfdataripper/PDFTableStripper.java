/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i3cubes.pdfdataripper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

/**
 *
 * @author kumara HHC
 */
public class PDFTableStripper extends PDFTextStripper{
    private TableHeader[] h_fields;
    private int key_column_no;
    public ArrayList<String[]> table_rows;
    private float startY=0;
    private String tb_end_method,tb_end_data;
    private boolean has_header;
    private String row_format;
    
    private boolean table_header=false;
    private boolean table_body=false;
    private float start_X,line_Y,end_X,row_Y=0;
    private int header_col=0;
    
    private String text="";
    private String var_name="";
    private String search_text="";    
    private boolean remove_search_text=false;
    
    private float table_StartX,table_EndX;
    private int row_no=0;
    private TableHeader current_col_header;
    private int current_column_no=0;
    private TableRow current_table_row;
    private ArrayList<TableRow> temp_table_rows;
    private boolean flg_start_row=true;
    private boolean end_of_table=false;
    
    public Table table;
    public PDFTableStripper(PDDocument pdd,TableHeader[] header_fields,boolean has_header,float s_Y,int key_col,String end_method,String end_data,String rformat) throws IOException {
        super();
        document = pdd;
        this.h_fields=header_fields;
        this.startY=s_Y;
        this.key_column_no=key_col;
        this.tb_end_method=end_method;
        this.tb_end_data=end_data;
        this.has_header=has_header;
        this.row_format=rformat;
        //this.table_rows=new ArrayList<String[]>();
        //Create table
        this.table=new Table(header_fields.length);
        this.table.setTableHeader(h_fields);
        this.temp_table_rows=new ArrayList<TableRow>();
    }
    public void stripPage(int page_s,int page_e) throws IOException {
        this.setStartPage(page_s);
        this.setEndPage(page_e);
        //System.out.println("PAGE::["+page_s+","+page_e+"]");
        Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream());
        writeText(document, dummy); // This call starts the parsing process and calls writeString repeatedly.
        //
        //this.table.printTable();
        //System.out.println("TABLE END BY:"+this.tb_end_method+" === "+this.tb_end_data);
        //IF remaining temp row
        if(this.temp_table_rows.size()>0){
            table.addRow(this.formTableRow(temp_table_rows));
            this.row_no++;
            //clear temp_rows adn push this row to temp_rows
            this.temp_table_rows.clear();
        }
    }
    @Override
    protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
        //System.out.println("STRING["+string+"]-----"+table_header+"||"+table_body+"   ...LineY="+line_Y+"  :EndX"+end_X+"---->x,y["+textPositions.get(0).getXDirAdj()+","+textPositions.get(0).getYDirAdj()+"]"+textPositions.get(textPositions.size()-1).getXDirAdj());
        if(!this.end_of_table){
            if(this.table_body){
                if(row_Y==0){
                    //System.out.println("INSIDE TB");
                    //Start tracing table bosy
                    this.table_StartX=h_fields[0].startX;
                    this.table_EndX=h_fields[h_fields.length-1].endX;
                    //First row
                    this.row_no=0;//First row
                    this.current_column_no=0;// first column
                    //this.current_row_cells=new TableCell[h_fields.length];
                    this.current_table_row=this.table.newRow();
                    row_Y=textPositions.get(0).getYDirAdj();
                }
                //? End of Table
                switch(tb_end_method){
                    case "EndByText":
                        if(string.contains(tb_end_data)){
                            //End of table
                            this.end_of_table=true;
                        }
                        break;
                }
                if(this.end_of_table){
                    //System.out.println("END OF TABLE");
                    //Table has Ended
                    //table.addRow(this.formTableRow(temp_table_rows));
                    //this.row_no++;
                    //clear temp_rows adn push this row to temp_rows
                    //this.temp_table_rows.clear();
                }

                String sb="";

                for (TextPosition text : textPositions) {
                    //System.out.println("String[" + text.getXDirAdj() + "," + text.getYDirAdj() + " fs=" + text.getFontSizeInPt()
                    //        + " xscale=" + text.getXScale() + " height=" + text.getHeightDir() + " space="
                    //       + text.getWidthOfSpace() + " width=" + text.getWidthDirAdj() + " ] " + text.getUnicode());


                    if(text.getXDirAdj()<this.table_StartX){
                        //move forward as still table not started
                    }
                    else{
                        if(Math.abs(this.row_Y-text.getYDirAdj())<(text.getHeightDir()/2)){//Same row
                            if(text.getXDirAdj()>this.table_EndX){
                                //table ended, then just move forward
                                //System.out.println("GAP till TB Start");
                            }
                            else{
                                for(int i=this.current_column_no;i<h_fields.length;i++){
                                    if(text.getXDirAdj()>h_fields[current_column_no].startX){ // Char if withing the column width
                                        if(text.getXDirAdj()<h_fields[current_column_no].endX){
                                            //System.out.println("col_startX < x <col_endX"+h_fields[current_column_no].endX);
                                            //Char is within the column
                                            sb=sb+text.getUnicode();
                                            this.current_table_row.cells[current_column_no].value=sb;
                                            //System.out.println("Appending:"+text.getUnicode()+"{"+sb+"}");
                                            i=h_fields.length;
                                        }
                                        else{
                                            //System.out.println("col_startX < x >col_endX");
                                            this.current_column_no++;
                                            //this.flg_new_cell=true;
                                            sb="";
                                        }
                                    }
                                    else{
                                        //System.out.println("x <col_startX"+h_fields[current_column_no].startX);
                                        i=h_fields.length;
                                    }
                                }
                            }
                        }
                        else{
                            //new row of text, push last table row to temp_list
                            //System.out.println("New Row");
                            //System.out.println("ROW["+this.row_no+"]"+current_table_row.toString()+"]---->"+current_table_row.cells[key_column_no].value+" | "+this.flg_start_row);
                            if(this.row_format.equalsIgnoreCase("multiple")){
                                if((current_table_row.cells[key_column_no].value!="" && !this.flg_start_row) ||this.end_of_table){
                                    //System.out.println("Concat rows");
                                    if(this.end_of_table){
                                        this.temp_table_rows.add(current_table_row);
                                        table.addRow(this.formTableRow(temp_table_rows));
                                        this.row_no++;
                                        //clear temp_rows adn push this row to temp_rows
                                        this.temp_table_rows.clear();
                                    }
                                    else{
                                        table.addRow(this.formTableRow(temp_table_rows));
                                        this.row_no++;
                                        //clear temp_rows adn push this row to temp_rows
                                        this.temp_table_rows.clear();
                                        this.temp_table_rows.add(current_table_row);
                                    }
                                }
                                else{
                                    this.temp_table_rows.add(current_table_row);
                                    //this.current_table_row=this.table.newRow();// define new row
                                    //this.current_column_no++;
                                }
                            }
                            else{
                                if(current_table_row.cells[key_column_no].value.equalsIgnoreCase("")){
                                    //System.out.println("KEY EMPTY");
                                    //Discard the row, if there are temp row add it to table
                                }
                                else{
                                    table.addRow(this.cleanTableRowData(current_table_row));
                                    this.row_no++;
                                    //clear temp_rows adn push this row to temp_rows
                                    //this.temp_table_rows.clear();
                                }
                                //this.current_table_row=this.table.newRow();// define new row
                                //this.current_column_no=0;
                            }
                            this.flg_start_row=false;   
                            //this.temp_table_rows.add(current_table_row);
                            this.current_table_row=this.table.newRow();// define new row
                            this.current_column_no=0;
                            //this.row_no++;
                            this.row_Y=text.getYDirAdj(); // set the new row Y
                            //this.current_table_row.cells[current_column_no].value=text.getUnicode();
                            sb=text.getUnicode();
                        }
                    }


                }
            }
            if(this.has_header){
                if(!this.table_header && !this.table_body){
                    TableHeader head=h_fields[0];
                        if(string.contains(head.name)){
                            TextPosition position=textPositions.get(0);
                            if(position.getYDirAdj()>startY){
                                //System.out.println(head.name+" ==1["+position.getXDirAdj()+","+position.getYDirAdj()+"]");
                                this.table_header=true;
                                this.line_Y=position.getYDirAdj();
                                head.startX=position.getXDirAdj()-head.margin_left;
                                this.start_X=position.getXDirAdj()-head.margin_left;
                                h_fields[0]=head;
                                this.header_col++;
                            }
                        }
                }
                if(this.table_header && !this.table_body){
                    if(this.header_col>h_fields.length-1){
                        //End of table header row
                        this.end_X=h_fields[this.header_col-1].margin_right+this.end_X;
                        //System.out.println("END-X:"+this.end_X+"--Mar-right:>>>"+h_fields[this.header_col-1].margin_right);
                        //End of table header
                        //System.out.println("--END ==["+textPositions.get(0).getXDirAdj()+","+textPositions.get(0).getYDirAdj()+"]"+end_X);
                        this.table_header=false;
                        h_fields[this.header_col-1].endX=this.end_X; // set end x of previous col to start of this
                        this.table_body=true;
                        //this.printHeaders();
                        this.writeString(string, textPositions);
                    }
                    else{
                        TableHeader head=h_fields[this.header_col];
                            TextPosition position=textPositions.get(0);
                            if(position.getYDirAdj()==this.line_Y){
                                if(string.contains(head.name)){
                                    head.startX=position.getXDirAdj()-head.margin_left;
                                    this.end_X=textPositions.get(textPositions.size()-1).getXDirAdj();
                                    head.endX=this.end_X;
                                    //System.out.println(head.name+" ==["+position.getXDirAdj()+","+position.getYDirAdj()+"]"+end_X);

                                    h_fields[this.header_col-1].endX=head.startX; // set end x of previous col to start of this
                                    h_fields[this.header_col]=head;
                                    this.header_col++;
                                }

                            }
                            else{
                                this.end_X=h_fields[this.header_col-1].margin_right+this.end_X;
                                //System.out.println("END-X:"+this.end_X+"--Mar-right:"+h_fields[this.header_col-1].margin_right);
                                //End of table header
                                //System.out.println(head.name+"--END ==["+position.getXDirAdj()+","+position.getYDirAdj()+"]"+end_X);
                                this.table_header=false;
                                h_fields[this.header_col-1].endX=this.end_X; // set end x of previous col to start of this
                                this.table_body=true;

                                //IF header end identified by end of line, just process the chunk of text for next row
                                //this.printHeaders();
                                this.writeString(string, textPositions);
                            }
                    }
                }
            }
            else{
                //No header, check for Y from top
                if(textPositions.get(0).getYDirAdj()>this.startY && !table_body){
                    //System.out.println("Y:"+textPositions.get(0).getYDirAdj()+" OK STRING:"+string);
                    //table bosy started
                    this.table_body=true;
                    //this.printHeaders();
                    this.writeString(string, textPositions);
                }
                else{
                    //System.out.println("Y:"+textPositions.get(0).getYDirAdj()+" STRING:"+string);
                }
            }
        }
        /*
        
        */
    }
    
    private TableRow formTableRow(ArrayList<TableRow> temp_rows){
        TableRow new_row=null;
        if(temp_rows.size()>0){
            new_row=this.table.newRow();
            for(TableRow row:temp_rows){
                //System.out.println("CONCAT ROW["+row.toString()+"]");
                for(int j=0;j<row.size;j++){
                    if(row.cells[j].value!=""){
                        if(new_row.cells[j].value==""){
                            new_row.cells[j].value=row.cells[j].value.trim();
                        }
                        else{
                            new_row.cells[j].value=new_row.cells[j].value+"\r\n"+row.cells[j].value.trim();
                        }
                    }
                }
            }
        }
        return new_row;
    }
    private TableRow cleanTableRowData(TableRow row){
        for(int j=0;j<row.cells.length;j++){
            row.cells[j].value=row.cells[j].value.trim();
        }
        return row;
    }
    public Table getTable(){
        return this.table;
    }
    private void printHeaders(){
        for(TableHeader h:h_fields){
            System.out.println("H::"+h.name+"{"+h.startX+","+h.endX+"}");
        }
    }
}
