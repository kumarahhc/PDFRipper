/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i3cubes.pdfdataripper;

import java.util.HashMap;

/**
 *
 * @author kumar
 */
public class PDFData {
    public HashMap<String,Object> pdf_data;
    /**
    *Constructor
    */
    public PDFData() {
        this.pdf_data=new HashMap<String,Object>();
    }
    /**
     * 
     * @param key parameter name defined in the configuration xml
     * @param d  object which represent the value of the parameter
     */
    public void put(String key,Object d){
        this.pdf_data.put(key, d);
    }
    public String getString(String key){
        return this.pdf_data.get(key).toString();
    }
    /**
     * 
     * @return Return the string that represent all the values in the class in Key-value format
     */
    public String toString(){
        String str="";
        for(HashMap.Entry<String, Object> entry:this.pdf_data.entrySet()){
            
            if(entry.getValue() instanceof String){
                str="STRING DATA--------------\r\n";
                str=str+""+entry.getKey()+"=>"+(String)entry.getValue()+"\r\n";
            }
            else if(entry.getValue() instanceof HashMap){
                str=str+"KEY-VALUE PAIRS--------------\r\n";
                str=str+""+entry.getKey()+"=>[\r\n";
                HashMap<Object,Object> hm=(HashMap)entry.getValue();
                //System.out.println("HASH MAPPPPP");
                for(HashMap.Entry<Object, Object> entry2:hm.entrySet()){
                    if(entry2.getValue() instanceof String){
                        str=str+"{"+entry2.getKey()+"=>"+entry2.getValue()+"}\r\n";
                    }
                }
                str=str+"]\r\n";
            }
            else if(entry.getValue() instanceof Table){
                str=str+"TABLE DATA--------------\r\n";
                str=str+entry.getValue().toString();
            }
        }
        return str;
    }
}
