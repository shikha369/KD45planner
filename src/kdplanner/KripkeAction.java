/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kdplanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author shikha
 */
public class KripkeAction {

    
    public Integer ActionId;
    public String ActionName;
    public ArrayList<KripkeEvent> eventlist;
    public ArrayList<ObservEdge> observelist;
    //public Set agents;
    public ArrayList<Integer> designated;
    

    public KripkeAction(Integer actionid, String Actionname, ArrayList<KripkeEvent> eventlist, 
            ArrayList<ObservEdge> observelist, ArrayList<Integer> designated) {
        
        this.ActionId = actionid;
        this.ActionName = Actionname;
        this.eventlist = eventlist;
        this.observelist = observelist;
        this.designated = designated;
    }
    
    
    
    
}
