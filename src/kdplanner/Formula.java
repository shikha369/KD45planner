/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kdplanner;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 *
 * @author shikha
 */
public class Formula {
    
    enum form_type
    {
        AND, OR, NOT, B, ATOMIC, AGENTNAME, LITERAL, NIL
        
    }
    
    public form_type type;
    public Integer num_operands;
    public Formula leftFormula;
    public Formula rightFormula;
    public ArrayList<String> fluents;
    
}