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
public class KEdge {
    
    public Integer edgeId;
    public Integer fromKripkeWorldId;
    public Integer toKripkeWorldId;
    public String agent;

    public KEdge(int edgeId, int fromKripkeWorldId, int toKripkeWorldId, String agent) {
        this.edgeId = edgeId;
        this.agent = agent;
        this.fromKripkeWorldId = fromKripkeWorldId;
        this.toKripkeWorldId = toKripkeWorldId;
    }
    
   
    
}
