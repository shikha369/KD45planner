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
public class Kripkeworld {
    
    public Integer kripkeworldId;
    public Integer reverseId;
    public Set<String> literals;
    public HashMap<String, ArrayList<Integer>> agent_to_outlistMap;
    public HashMap<String, ArrayList<Integer>> agent_to_inlistMap;
    

    public void setKwid(int i) {
        this.kripkeworldId = i;
    }
   
          
    
    /**
     * methods to check whether a formula holds true in this world
     */
    
    public boolean isEntailed(Formula formula) {   
        boolean isEnt = false;
        if(formula.type == null){
            System.out.println("here comes the demon");
            System.out.println(PlanningProblem.updates.size());
        }
        switch(formula.type)
        {
            
            case LITERAL:
                /**
                 * input is a string
                 * 
                 */
                
                isEnt = isLitEntailed(formula.fluents.get(0));
                break;
                
            case AND:
                isEnt = isEntailed(formula.leftFormula) && isEntailed(formula.rightFormula);
                break;
                
            case OR:
                isEnt = isEntailed(formula.leftFormula) || isEntailed(formula.rightFormula);                
                break;
                
            case NOT:
                /**
                 * rightmost character '-'
                 */
                isEnt = !(isEntailed(formula.rightFormula));
                break;
                
            case B:
                 
                ArrayList<String> agents = formula.leftFormula.fluents;
                
                for (String ag : agents)
                {
                    ArrayList<Integer> to_world_edge_ids = this.agent_to_outlistMap.get(ag);
                    

                    for (Integer e_id : to_world_edge_ids)
                    {
                        
                        
                        int to_world_id = KripkeStructure.pull_an_edge_by_id(PlanningProblem.models.get(this.reverseId).kedgelist, e_id).toKripkeWorldId;
                        
                        Kripkeworld to_world = KripkeStructure.pull_a_state_by_id(PlanningProblem.models.get(this.reverseId).statelist, to_world_id);
                        isEnt = to_world.isEntailed(formula.rightFormula);
                        
                        if(!isEnt)
                            break;
                    }
                    
                    if(!isEnt)
                        break;
                }
                
                break;
                
            default:
                System.out.println("can't decide the type of the formula");
                break;
        
        }
        
        if(isEnt)
            return true;
        else
            return false;
    }
    
    public boolean isLitEntailed(String fluent){
        if("top".equals(fluent))
            return true;
        if("bottom".equals(fluent))
            return false;
        if (this.literals.contains(fluent))
            return true;
        else 
            return false;
    }
}
