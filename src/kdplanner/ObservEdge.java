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
public class ObservEdge {
    
    public Integer id;
    public Integer fromEventId;
    public Integer toEventId;
    public String agent;

    public ObservEdge(int id, int fromEventId, int toEventId, String agent) {
        
        this.id = id;
        this.fromEventId = fromEventId;
        this.toEventId = toEventId;
        this.agent = agent;
        
    }
    
    
}
