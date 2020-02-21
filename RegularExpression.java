/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica1;

import java.util.ArrayList;

/**
 *
 * @author LuisMiguel
 */
public class RegularExpression {
    private String name;
    private ArrayList<String> node;
    
    public RegularExpression() {

    }

    public RegularExpression(String name, ArrayList<String> node) {
        this.name = name;
        this.node = node;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the node
     */
    public ArrayList<String> getNode() {
        return node;
    }

    /**
     * @param node the node to set
     */
    public void setNode(ArrayList<String> node) {
        this.node = node;
    }
}
