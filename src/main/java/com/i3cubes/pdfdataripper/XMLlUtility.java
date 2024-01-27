/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i3cubes.pdfdataripper;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author kumar
 */
public class XMLlUtility {
    public int getPageNumber(Element el){
        return (StringtoInt(el.getElementsByTagName("pageNo").item(0).getTextContent().trim())-1);
    }
    public HashMap<String,String> getVarList(Element el){
        HashMap<String,String> vars=new HashMap<String, String>();
        Element e_vars=(Element)el.getElementsByTagName("variables").item(0);
        NodeList var_list=e_vars.getChildNodes();
        for(int k=0; k<var_list.getLength();k++){
            Node n_var=var_list.item(k);
            if(n_var.getNodeType()==Node.ELEMENT_NODE){
                Element e_var=(Element)n_var;
                String var_name=e_var.getAttribute("id");
                String search_txt=e_var.getTextContent();
                vars.put(var_name, search_txt);
            }
        }
        return vars;
    }
    public Rectangle getRectangle(Element el){
        String top_left_xy=el.getElementsByTagName("topLeftXY").item(0).getTextContent().trim();
        String[] xy=top_left_xy.split(",");
        int x=this.StringtoInt(xy[0]);
        int y=this.StringtoInt(xy[1]);
        int w=this.StringtoInt(el.getElementsByTagName("width").item(0).getTextContent().trim());
        int h=this.StringtoInt(el.getElementsByTagName("height").item(0).getTextContent().trim());
        if(x>0 && y>0 && w>0 && h>0){
            return new Rectangle(x, y, w, h);
        }
        else{
            return null;
        }
    }
    private int StringtoInt(String txt){
        try{
            int v=Integer.parseInt(txt);
            return v;
        }
        catch (NumberFormatException ex){
            return -1;
        }
    }
}
