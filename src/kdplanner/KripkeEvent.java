/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kdplanner;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author shikha
 */
public class KripkeEvent {

    public Integer eventId;
    public Formula preconditions;
    public ArrayList<String> posteffects;
    public HashMap<String, ArrayList<Integer>> agent_to_outEventsMap;
    public HashMap<String, ArrayList<Integer>> agent_to_inEventsMap; 
    
}
