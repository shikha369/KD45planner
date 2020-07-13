/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kdplanner;

/**
 *
 * @author shikha
 */
public class state_event_pair {
    /*
     * temp class to store: world_id, edge_id
     */
    
    int state_id;
    int event_id;

    public state_event_pair(int sid, int eid) {
        this.state_id = sid;
        this.event_id = eid;
    }
    
    
}
