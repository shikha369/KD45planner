/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kdplanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 *
 * @author shikha
 */
public class KripkeStructure {
    
    
    public Integer modelId;
    public  ArrayList<Kripkeworld> statelist;
    public  ArrayList<KEdge> kedgelist;
    public ArrayList<String> agents;
    public ArrayList<Integer> designated;
    


    public KripkeStructure(Integer modelId, ArrayList<Kripkeworld> statelist, ArrayList<KEdge> kedgelist, ArrayList<String> agents, ArrayList<Integer> designated) {
        this.modelId = modelId;
        this.statelist = statelist; 
        this.kedgelist = kedgelist;
        this.agents = agents;
        this.designated = designated;
    }
    
    
    /**
     * Procedure to do product update
     */
    
    
    public KripkeStructure update(KripkeAction kaction)        
    {
        KripkeStructure upd_model = null;
                
        
        
        ArrayList<Kripkeworld> curr_worlds = this.statelist;
        ArrayList<KripkeEvent> curr_events = kaction.eventlist;
        

        
        ArrayList<Kripkeworld> new_statelist;
        ArrayList<KEdge> new_edgelist;
        ArrayList<Integer> new_des = new ArrayList<>();
        
        //Initialise identifiers
        int numM = PlanningProblem.models.size();
        int numW = 0;
        int numE = 0;
        
        ArrayList<Kripke_state_event_tuple> kse_tuples = new ArrayList<>();
        
        ArrayList<KEdge> temp_new_kedges = new ArrayList<>();


        for(int w_itr = 0; w_itr < curr_worlds.size(); w_itr++)
        {
            Kripkeworld curr_world = this.statelist.get(w_itr);
            
            for(int e_itr = 0; e_itr < curr_events.size(); e_itr++)
            {

                KripkeEvent curr_event = kaction.eventlist.get(e_itr);

                if(curr_world.isEntailed(curr_event.preconditions))
                {
                    /**
                     * 1- create a new state with updated valuation and add the w-id and e-id pair to twmp list
                     */
                    Kripkeworld k = new Kripkeworld();
                    k.kripkeworldId = numW;
                    k.reverseId = numM;
                    
                    /**
                     * Check if both kw and ke are designated then make k too designated
                     */
                    
                    if(this.designated.contains(curr_world.kripkeworldId) && kaction.designated.contains(curr_event.eventId))
                        new_des.add(numW);
                    
                    
                    Set<String> new_valuation = new HashSet<>();
                    new_valuation.addAll(curr_world.literals);
                    ArrayList<String> post =  new ArrayList<>();
                            post = curr_event.posteffects;
                    
                    for (String lit: post){
                        if(lit.charAt(0)=='-'){
                            new_valuation.remove(lit.substring(1, lit.length()));
                            new_valuation.add(lit);
                        }
                        else if (lit.equals("nil"))
                            continue;
                        else{
                            new_valuation.add(lit);
                            new_valuation.remove("-".concat(lit));
                        }
                            
                    }

                    k.literals = new_valuation;
                    
                    k.agent_to_outlistMap = new HashMap<>();
                    k.agent_to_inlistMap = new HashMap<>();
                    
                    
                    Kripke_state_event_tuple kse1 = new Kripke_state_event_tuple();
                    kse1.kworld = k;
                    kse1.sepair = new state_event_pair(curr_world.kripkeworldId, curr_event.eventId);
                    kse_tuples.add(kse1);
                    
                    numW++;
                    
                }
            }
        }
        
        for(int ag = 0; ag < this.agents.size(); ag++)
        {
            String agent = this.agents.get(ag);
            
            for(int w1 = 0; w1 < kse_tuples.size(); w1++) /*from world*/
            {
                
                for(int w2 = 0; w2 < kse_tuples.size(); w2++)  /*to world*/
                {
                
                    state_event_pair p1 = kse_tuples.get(w1).sepair; //w1,e1
                    state_event_pair p2 = kse_tuples.get(w2).sepair; //w2,e2
                    
                    ArrayList<Integer> out_wedges = 
                            
                            KripkeStructure.pull_a_state_by_id(this.statelist, p1.state_id).agent_to_outlistMap.get(agent);
                    
                    
                    for(int eid : out_wedges)
                    {
                        
                        if(KripkeStructure.pull_an_edge_by_id(this.kedgelist, eid).toKripkeWorldId == p2.state_id)
                        {
                            ArrayList<Integer> out_oedges = 
                            
                                    KripkeStructure.pull_an_event_by_id(kaction.eventlist, p1.event_id).agent_to_outEventsMap.get(agent);
                            
                            
                            for(int oid : out_oedges)
                            {
                                
                                if(KripkeStructure.pull_an_oedge_by_id(kaction.observelist, oid).toEventId == p2.event_id)
                                {

                                    
                                    KEdge knew = new KEdge(numE, kse_tuples.get(w1).kworld.kripkeworldId, kse_tuples.get(w2).kworld.kripkeworldId, agent);
                                    temp_new_kedges.add(knew);

                                    if(kse_tuples.get(w1).kworld.agent_to_outlistMap.containsKey(agent))
                                    {
                                        ArrayList<Integer> agent_out = kse_tuples.get(w1).kworld.agent_to_outlistMap.get(agent);
                                        agent_out.add(numE);
                                        kse_tuples.get(w1).kworld.agent_to_outlistMap.put(agent, agent_out);
                                    }
                                    else
                                    {
                                        ArrayList<Integer> agent_out = new ArrayList<>();
                                        agent_out.add(numE);
                                        kse_tuples.get(w1).kworld.agent_to_outlistMap.put(agent, agent_out);
                                        
                                    }
                                    

                                    
                                    if(kse_tuples.get(w2).kworld.agent_to_inlistMap.containsKey(agent))
                                    {
                                        ArrayList<Integer> agent_in = kse_tuples.get(w2).kworld.agent_to_inlistMap.get(agent);
                                        agent_in.add(numE);
                                        kse_tuples.get(w2).kworld.agent_to_inlistMap.put(agent, agent_in);
                                    }
                                    else
                                    {
                                        ArrayList<Integer> agent_in = new ArrayList<>();
                                        agent_in.add(numE);
                                        kse_tuples.get(w2).kworld.agent_to_inlistMap.put(agent, agent_in);
                                        
                                    }
                                    
                                    numE++;
                                }
                            }    
                        }
                    }
                }    
            }
        }
        
        
        new_statelist = new ArrayList<>();
        for(int w = 0; w < kse_tuples.size(); w++)
        {
            new_statelist.add(kse_tuples.get(w).kworld);

        }
        
   
        upd_model = new KripkeStructure(numM, new_statelist , temp_new_kedges, this.agents, new_des);
        
        return upd_model;
    }
    
    /**
     * Procedure to check whether all the designated states in the structure entail the goal
     */
    
    public boolean entails (Formula goal){
        boolean goal_condition_met = true;
        
        for( int d : this.designated ){
            goal_condition_met = goal_condition_met && KripkeStructure.pull_a_state_by_id(this.statelist, d).isEntailed(PlanningProblem.goal);
        }
        return goal_condition_met;
    }
    
    public boolean applicable (KripkeAction action){
        
        Formula checkForApplicability;
        boolean isapplicable = false;
        
        ArrayList<Kripkeworld> curr_worlds = this.statelist;
        ArrayList<KripkeEvent> curr_events = action.eventlist;
        
        ArrayList<Integer> new_des = new ArrayList<>();
        
        //Initialise identifiers
        int numM = PlanningProblem.models.size();
        int numW = 0;
        int numE = 0;

        
        for(int w_itr = 0; w_itr < curr_worlds.size(); w_itr++)
        {
            Kripkeworld curr_world = this.statelist.get(w_itr);
            
            for(int e_itr = 0; e_itr < curr_events.size(); e_itr++)
            {

                KripkeEvent curr_event = action.eventlist.get(e_itr);

                /**
                 * if capability of the action is self, then check entailment of preconditions 
                 * otherwise check the entailment of the planner's beliefs about the preconditions 
                 */
                
                if(PlanningProblem.self.equals(PlanningProblem.action_to_agent.get(action.ActionName)))
                    checkForApplicability = curr_event.preconditions;
                else{
                    
                    checkForApplicability = new Formula();
                    checkForApplicability.type = Formula.form_type.B;
                    checkForApplicability.leftFormula = new Formula();
                    checkForApplicability.leftFormula.type = Formula.form_type.AGENTNAME;
                    checkForApplicability.leftFormula.fluents = new ArrayList<>();
                    checkForApplicability.leftFormula.fluents.add(PlanningProblem.self);
                    checkForApplicability.rightFormula = curr_event.preconditions;
                    
                }
                    
                
                
                if(curr_world.isEntailed(checkForApplicability))
                {
                    
                    /**
                     * Check if both kw and ke are designated then make k too designated
                     */
                    
                    if(this.designated.contains(curr_world.kripkeworldId) && action.designated.contains(curr_event.eventId))
                        new_des.add(numW);
                    
                    numW++;
                    
                }
            }
        }

        
        if(!new_des.isEmpty())
            isapplicable = true;
        
        return isapplicable;
    }
    
    public void correctModelId(int retainId){
        this.modelId = retainId;
        for (Kripkeworld kw : this.statelist){
            kw.reverseId = modelId;
    }
    
}
    
    public boolean isEquivalentTo(KripkeStructure kmodel){
        boolean isEq = false;
        
        return isEq; 
    }
    
    public static KEdge pull_an_edge_by_id(ArrayList<KEdge> listedges, int edgeId) {
    for(KEdge e : listedges)
        if(e.edgeId == edgeId)
            return e;
    return null;
        
    }
    
    public static Kripkeworld pull_a_state_by_id(ArrayList<Kripkeworld> liststates, int stateId) {
        for(Kripkeworld s : liststates)
            if(s.kripkeworldId == stateId)
                return s;
        return null;
        
    }
    
    public static ObservEdge pull_an_oedge_by_id(ArrayList<ObservEdge> listedges, int edgeId) {
    for(ObservEdge e : listedges)
        if(e.id == edgeId)
            return e;
    return null;
        
    }
    
    public static KripkeEvent pull_an_event_by_id(ArrayList<KripkeEvent> listevents, int eventId) {
        for(KripkeEvent e : listevents)
            if(e.eventId == eventId)
                return e;
        return null;
        
    }
}
    

