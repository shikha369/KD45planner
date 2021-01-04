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
 * This planner is a simple bfs planner 
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
                
                System.out.println("Number of models evaluated: " + curr.self.modelId);
                
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
                    System.out.println("checking: "+action.ActionName +" on "+ curr.self.modelId);
                    
                    if(flag_self)//the conditon below will not be checked if self is false;allowing all the actions
                    if(!PlanningProblem.action_to_agent.get(action.ActionName).equals(PlanningProblem.self))
                        continue;
                    
                    /**
                     * the planner should be able to project beliefs of others to check applicability
                     * of actions that are in others' capability-- checking inside applicable method
                     */
                    
                    if(curr.self.applicable(action))
                    {
                        

                        next = curr.self.update(action);
                        
                        //DEBUG
                        System.out.println(next.modelId +" = "+"applying: "+action.ActionName +" on "+ curr.self.modelId);
                        /**
                         * list to store the names of inferencing actions
                         * <Todo: functionality to be added in future>
                         */
                        listInferences = new ArrayList();
                        
                        PlanningProblem.models.add(next);
                        
                        SearchNode child = new SearchNode(curr, action, next, listInferences);
                        open.add(child);
                        
                    }
                    else{
                        //DEBUG
                        //System.out.println("couldn't apply: " + action.ActionName);
                    }
                }
            
            closed.add(curr);
            
        }  
     return act_plan;
    }
    
}


