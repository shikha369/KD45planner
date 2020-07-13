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
public class PlanningProblem {

    public static final ArrayList<String> fluentlist = new ArrayList<>();
    public static final ArrayList<String> agentlist = new ArrayList<>();
    public static final ArrayList<KripkeStructure> models = new ArrayList<>();
    public static final ArrayList<KripkeStructure> temp_models = new ArrayList<>();
    public static final ArrayList<KripkeAction> updates = new ArrayList<>();
    public static final HashMap<String, String> action_to_agent = new HashMap<>();
    public static final HashMap<String, String> action_to_explanantion = new HashMap<>();
    public static final ArrayList<KripkeAction> iupdates = new ArrayList<>();
    public static Formula goal = new Formula();
    public static String self;
   }
