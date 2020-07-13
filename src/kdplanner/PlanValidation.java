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
 */
public class PlanValidation {
    
    public static boolean validate(ArrayList<String> plan) {
        
        boolean goal_condition_met = true;
        boolean break_from_plan = false;
        
        
        KripkeStructure tks;
        //KripkeStructure init_model = PlanningProblem.models.get(0);
        //int retain_model_id;
    
        
                
        for(String a: plan){
        for (KripkeAction ka: PlanningProblem.updates){
            if (a.equals(ka.ActionName)){
                
                //System.err.println("Does updated Model by "+ ka.ActionName+" entails: the goal?");
                
                
                tks = PlanningProblem.models.get(PlanningProblem.models.size()-1).update(ka);
                
                if(tks.designated.isEmpty()){
                    System.err.println("updated model has no designated states");
                    break_from_plan = true;
                    System.out.println("Breaking after applying : " + a);
                    break; //break from plan
                }
                
                if (break_from_plan)
                    break;   
            }
        }
        }
        
        if(!break_from_plan){
            tks = PlanningProblem.models.get(PlanningProblem.models.size()-1);
            for( int d : tks.designated ){
                        goal_condition_met = goal_condition_met && tks.statelist.get(d).isEntailed(PlanningProblem.goal);
            }
            //System.out.println("\nis goal satisfied?");
            //System.out.println(goal_condition_met);
        }
    
        return goal_condition_met;
    }
        
        
    }
    

