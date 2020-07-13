/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kdplanner;

import java.util.ArrayList;

/**
 *
 * @author shikha
 * 
 * This planner is a simple bfs planner with 0-level stratified axioms incorporated as inferencing actions
 * in the system
 * 
 */
public class planner {
    
    
    public static ArrayList<String> constructPlanSearchTree(boolean flag_self)
    {
        
        /**<Todo>
         * listInferences: Future work with mental actions
         */
        ArrayList<String> listInferences = new ArrayList<>();
        
        KripkeStructure init_model = PlanningProblem.models.get(0);
        KripkeStructure next;

        
        SearchNode curr = new SearchNode(null, null, init_model,listInferences);
        
        ArrayList<SearchNode> closed = new ArrayList<>();
        ArrayList<SearchNode> open = new ArrayList<>();
        
        open.add(curr);
        
        ArrayList<String> act_plan = new ArrayList<>();
        
        while (!open.isEmpty())
        {
            curr = open.remove(0);

            if(curr.self.entails(PlanningProblem.goal))
            {
               /**
                * compute plan and return
                */ 
                
                while(!(curr.ParentNode == null))
                {
                    act_plan.add(curr.Action.ActionName + " [" + curr.listIactions+" ]");
                    curr = curr.ParentNode;
                }
                
                
                
                
                
                return act_plan;
            }
                
                
            
            for(int a = 0; a < PlanningProblem.updates.size(); a++)
                {
                    //has to explore all sucessors
                    KripkeAction action = PlanningProblem.updates.get(a);
                    
                    if(flag_self)//the conditon below will not be checked if self is false;allowing all the actions
                    if(!PlanningProblem.action_to_agent.get(action.ActionName).equals(PlanningProblem.self))
                        continue;
                    
                    /**
                     * the planner should be able to project beliefs of others to check applicability
                     * of actions that are in others' capability-- checking inside applicable method
                     */
                    
                    if(curr.self.applicable(action))
                    {
                        //DEBUG
                        //System.out.println("applying: "+action.ActionName +" on "+ curr.self.modelId);
                        //if(action.ActionName.equals("tell_b_secret"))
                          //  System.out.println("ye kya");
                        next = curr.self.update(action);
                        /**
                         * list to store the names of inferencing actions
                         */
                        listInferences = new ArrayList();
                        
                        /***
                         * apply all the iupdates (inferencing actions on the next)
                         */
                        PlanningProblem.temp_models.add(next);
                        
                        PlanningProblem.models.add(next);
                        PlanningProblem.temp_models.clear();
                        SearchNode child = new SearchNode(curr, action, next, listInferences);
                        open.add(child);
                        
                    }
                    else{
                        //System.out.println("couldn't apply: " + action.ActionName);
                    }
                }
            
            closed.add(curr);
            //curr = open.remove(0);
            
        }  
     return act_plan;
    }
    
}


