/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author shikha
 */
public class utility {
    
    public static void main(String[] args){
        ArrayList<String> post = new ArrayList<>();
        post.add("atf");
        post.add("-atm");
        
        Set<String> lits = new HashSet();
        lits.add("hey");
        lits.add("hello");
        lits.add("-atf");
        lits.add("atm");
        
        System.out.println(lits);
        
        for(String p : post){
            if(p.charAt(0)=='-'){
                lits.remove(p.substring(1, p.length()));
                lits.add(p);
            }
            else{
                lits.add(p);
                lits.remove("-".concat(p));
            }
        }
        
        System.out.println(lits);
        
    }
    
}
