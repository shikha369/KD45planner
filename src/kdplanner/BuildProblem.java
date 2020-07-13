/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kdplanner;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 *
 * @author shikha
 */
public class BuildProblem {
    
    static File inputFile;
    static DocumentBuilderFactory dbFactory;
    static DocumentBuilder dBuilder;
    static Document doc;
    
    public static void build(String testcase_dir, String testcase_file){
        
        try{
            inputFile = new File("testcases/"+testcase_dir+"/"+testcase_file+".xml");
            dbFactory = DocumentBuilderFactory.newInstance();
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            
            XPath xPath = XPathFactory.newInstance().newXPath();
            
            System.out.println("\n Building fluents");
            build_Propostions(xPath);
            
            System.out.println("\n Building agents");
            build_Agents(xPath);
            
            System.out.println("\n Building initial kripke");
            build_InitialKripke(xPath);
            
            System.out.println("\n Building domain\n");
            build_EventModels(xPath);
            
            System.out.println("\n Building goal\n");
            build_Goal(xPath);
            
            System.out.println("\n Building self\n");
            build_Self(xPath);
            
            System.out.println("Problem built");
            
        }
        
        catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    public static void build_Propostions(XPath xPath) throws XPathExpressionException{
        
        String expression = "/PROGRAM/PROPOSITIONS/PROPOSITION";
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(
            doc, XPathConstants.NODESET);
        
        for(int i = 0; i < nodeList.getLength(); i++){
            Node nNode = nodeList.item(i);
            Element eElement = (Element) nNode;
            PlanningProblem.fluentlist.add(eElement.getAttribute("text"));
        }
        
        System.out.println(PlanningProblem.fluentlist);
        
        
    }
    
    public static void build_Agents(XPath xPath)throws XPathExpressionException{
        
        String expression = "/PROGRAM/AG/AGENT";
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(
            doc, XPathConstants.NODESET);
        
        for(int i = 0; i < nodeList.getLength(); i++){
            Node nNode = nodeList.item(i);
            Element eElement = (Element) nNode;
            PlanningProblem.agentlist.add(eElement.getAttribute("text"));
        }
        
        System.out.println(PlanningProblem.agentlist);
        
    }
    
    public static void build_InitialKripke(XPath xPath) throws XPathExpressionException{
        
        
        /**
         * access nodes
         */
        ArrayList<Kripkeworld> kws = new ArrayList<>();
        
        String nexpression = "/PROGRAM/INITIAL_KRIPKE/NODELIST/N";
        NodeList nodeList = (NodeList) xPath.compile(nexpression).evaluate(
            doc, XPathConstants.NODESET);
        
        for(int i = 0; i < nodeList.getLength(); i++){
            
            Node nNode = nodeList.item(i);
            
            
            
            Element eElement = (Element) nNode;
            
            Kripkeworld kw = new Kripkeworld();
            
            //to align with zero based indexing 
            kw.setKwid(Integer.parseInt(eElement.getAttribute("text"))-1);
            
            Set<String> literals = new HashSet<>();
            /**
             * Initialize map of arraylist for each agent
             */
            HashMap<String, ArrayList<Integer>> agent_to_outlistMap = new HashMap<>();
            HashMap<String, ArrayList<Integer>> agent_to_inlistMap = new HashMap<>();
            
            for (String ag : PlanningProblem.agentlist){
                ArrayList<Integer> inlist = new ArrayList<>();    
                ArrayList<Integer> outlist = new ArrayList<>();
                agent_to_inlistMap.put(ag, inlist);
                agent_to_outlistMap.put(ag, outlist);
                kw.agent_to_inlistMap = agent_to_inlistMap;
                kw.agent_to_outlistMap = agent_to_outlistMap;

            }
            
            
            
            NodeList children = eElement.getElementsByTagName("PROPOSITION");
            
            for (int j = 0; j < children.getLength(); j++){
                Node cnNode = children.item(j);
                Element cElement = (Element) cnNode;
                literals.add(cElement.getAttribute("text"));
            }
            
            kw.literals = literals;
            kw.reverseId = PlanningProblem.models.size();
            
            kws.add(kw);
            
        }
        
        
        /**
         * access edges
         */
        
        ArrayList<KEdge> kes = new ArrayList<>();
        Integer edgeCounter = 0;
        
        String eexpression = "/PROGRAM/INITIAL_KRIPKE/EDGELIST/AE";
        NodeList agList = (NodeList) xPath.compile(eexpression).evaluate(
            doc, XPathConstants.NODESET);
        
        for(int i = 0; i < agList.getLength(); i++){
            
            Node ag = agList.item(i); // (for each world) for each agent, prepare oulist and inlist maps as well
            Element agElement = (Element) ag;
            String agName = agElement.getAttribute("text");
            

            //first get an edge list corresponding to each agent
            NodeList edges = ag.getChildNodes();
            String name;
            for (int j = 0; j < edges.getLength(); j++){
                
                if (edges.item(j).getNodeType() == Node.ELEMENT_NODE){
                    Node cnNode = edges.item(j);
                    Node c1Node = cnNode.getFirstChild().getNextSibling();
                    Node c2Node = cnNode.getLastChild().getPreviousSibling();       

                
                    int fromId = Integer.parseInt(((Element) c1Node).getAttribute("text"))-1;
                    int toId = Integer.parseInt(((Element) c2Node).getAttribute("text"))-1;        
                
                    KEdge ke = new KEdge(edgeCounter, fromId, toId ,agName);
                    kes.add(ke);
                    
                    (kws.get(fromId).agent_to_outlistMap.get(agName)).add(edgeCounter);
                    (kws.get(toId).agent_to_inlistMap.get(agName)).add(edgeCounter);
                
                    edgeCounter++;
                }
            }    
        }        
        
        
  
        
        String dexpression = "/PROGRAM/INITIAL_KRIPKE/DES/ID";
        nodeList = (NodeList) xPath.compile(dexpression).evaluate(
            doc, XPathConstants.NODESET);       
        
        ArrayList<Integer> designated = new ArrayList<>();
        
        for(int i = 0; i < nodeList.getLength(); i++){
            Node nNode = nodeList.item(i);
            Element eElement = (Element) nNode;
            designated.add(Integer.parseInt(eElement.getAttribute("text"))-1);
        }
        
        
        
        /**
         * Initialization 
         */
        KripkeStructure init_kripke = new KripkeStructure(0, kws, kes, PlanningProblem.agentlist, designated);
        PlanningProblem.models.add(init_kripke);
        
    }
    
    public static void build_EventModels(XPath xPath) throws XPathExpressionException{

        
        String expression = "/PROGRAM/EVENTMODELS/CAPABILITIES";
        
        //capList is a list of capabilities
        NodeList capList = (NodeList) xPath.compile(expression).evaluate(
            doc, XPathConstants.NODESET);
        
        for(int cap = 0; cap < capList.getLength(); cap++){
            
            //CAPABILITIES has three children
            NodeList capChildren = capList.item(cap).getChildNodes();
            //Initialise action-name, actor-name, explanation
            String act_name = "mydefault";
            String actor_name;
            String explanation;
            
            for(int i = 0; i < capChildren.getLength(); i++){
                Node nNode = capChildren.item(i);
                
                if(nNode.getNodeName()=="EVENTMODEL"){

            
                        ArrayList<KripkeEvent> kes = new ArrayList<>();
                        ArrayList<ObservEdge> oes = new ArrayList<>();
                        ArrayList<Integer> designated = new ArrayList<>();
                        
                        act_name = ((Element) nNode).getAttribute("text");
                        NodeList eNode = nNode.getChildNodes(); //children: eventlist, edgelist

            
            
            
                        for (int j = 0; j < eNode.getLength(); j++){

                            if ("EVENTLIST".equals(eNode.item(j).getNodeName())){



                                NodeList eventList = eNode.item(j).getChildNodes(); //all events

                                for(int k = 0; k < eventList.getLength(); k++){

                                    if(eventList.item(k).getNodeType() == Node.ELEMENT_NODE)
                                    {
                                        //This is an event: children- pre, post
                                        Node event = eventList.item(k);
                                        KripkeEvent ke = new KripkeEvent();


                                        ke.eventId = Integer.parseInt(((Element) event).getAttribute("text"))-1;

                                        HashMap<String, ArrayList<Integer>> ag_to_out = new HashMap<>();
                                        HashMap<String, ArrayList<Integer>> ag_to_in = new HashMap<>();

                                        for (String ag : PlanningProblem.agentlist){
                                            ArrayList<Integer> inlist = new ArrayList<>();    
                                            ArrayList<Integer> outlist = new ArrayList<>();
                                            ag_to_in.put(ag, inlist);
                                            ag_to_out.put(ag, outlist);
                                            ke.agent_to_inEventsMap = ag_to_in;
                                            ke.agent_to_outEventsMap = ag_to_out;
                                        }


                                        NodeList eventChildren = event.getChildNodes();

                                        for (int c = 0; c < eventChildren.getLength(); c++){
                                           if("PRECONDITIONS".equals(eventChildren.item(c).getNodeName())) {
                                               ke.preconditions = new Formula();
                                               ke.preconditions = buildFormula(eventChildren.item(c));
                                           }

                                           else if("POST".equals(eventChildren.item(c).getNodeName())) {
                                               NodeList postChild = eventChildren.item(c).getChildNodes();
                                               ArrayList<String> pe = new ArrayList<>();
                                               ke.posteffects = pe;
                                               for (int a = 0; a < postChild.getLength(); a++){
                                                   if(postChild.item(a).getNodeType()== Node.ELEMENT_NODE){
                                                       ke.posteffects.add(((Element) postChild.item(a)).getAttribute("text"));
                                                   }


                                               }
                                           }

                                           else continue;   
                                        }       
                                        kes.add(ke);
                                    }


                                }   
                            }

                            else if ("EDGELIST".equals(eNode.item(j).getNodeName())){


                                int edgeCounter = 0;

                                NodeList agList = eNode.item(j).getChildNodes(); //List of AE nodes
                                for(int ae = 0; ae < agList.getLength(); ae++){
                                    if(agList.item(ae).getNodeType() == Node.ELEMENT_NODE){
                                        Node ag = agList.item(ae);
                                        Element agElement = (Element) ag;
                                        String agName = agElement.getAttribute("text");

                                        //first get an edge list corresponding to each agent
                                        NodeList edges = ag.getChildNodes();
                                        String name;

                                        for (int e = 0; e < edges.getLength(); e++){
                                            if (edges.item(e).getNodeType() == Node.ELEMENT_NODE){
                                                Node cnNode = edges.item(e);
                                                Node c1Node = cnNode.getFirstChild().getNextSibling();
                                                Node c2Node = cnNode.getLastChild().getPreviousSibling();

                                                //to enable zero based indexing

                                                int fromId = Integer.parseInt(((Element) c1Node).getAttribute("text"))-1;
                                                int toId = Integer.parseInt(((Element) c2Node).getAttribute("text"))-1;

                                                ObservEdge oe = new ObservEdge(edgeCounter, fromId, toId ,agName);
                                                oes.add(oe);


                                                (kes.get(fromId).agent_to_outEventsMap
                                                        .get(agName)).add(edgeCounter);
                                                (kes.get(toId).agent_to_inEventsMap.
                                                        get(agName)).add(edgeCounter);

                                                edgeCounter++;       
                                            }
                                        }
                                    }
                                }            

                            }

                            else if("DES".equals(eNode.item(j).getNodeName())){

                                NodeList deslist = eNode.item(j).getChildNodes();

                                for(int d = 0; d < deslist.getLength(); d++){
                                    Node dNode = deslist.item(d);
                                    if(dNode.getNodeType() == Node.ELEMENT_NODE){
                                        Element eElement = (Element) dNode;
                                        designated.add(Integer.parseInt(eElement.getAttribute("text"))-1);
                                    }
                                }
                            }

                            else
                                continue;

                            } 
                        KripkeAction kac = new KripkeAction(i, act_name, kes, oes, designated);
                        //actionStore.add(kac);
                        PlanningProblem.updates.add(kac);
                    //}                
                
                }
                else if(nNode.getNodeName()=="NAME"){
                    actor_name = ((Element) nNode).getAttribute("text");
                    PlanningProblem.action_to_agent.put(act_name, actor_name);
                
                }
                else if(nNode.getNodeName()=="EXPLANATION"){
                    explanation = ((Element) nNode).getAttribute("text");
                    PlanningProblem.action_to_explanantion.put(act_name, explanation);
                
                }
                
            }
        }

        
        }
    
    
    public static void build_Goal(XPath xPath) throws XPathExpressionException{
        
        String expression = "/PROGRAM/GOAL";
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(
            doc, XPathConstants.NODESET);
        
        for(int i = 0; i < nodeList.getLength(); i++){
            Node gNode = nodeList.item(i);
            if (gNode.getNodeType() == Node.ELEMENT_NODE){

                PlanningProblem.goal = buildFormula(gNode);
            }
            
        }
    }
    
    public static void build_Self(XPath xPath) throws XPathExpressionException{
        
        String expression = "/PROGRAM/PLANNER/NAME";
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(
            doc, XPathConstants.NODESET);
        
        for(int i = 0; i < nodeList.getLength(); i++){
            Node gNode = nodeList.item(i);
            if (gNode.getNodeType() == Node.ELEMENT_NODE){

                PlanningProblem.self = ((Element) gNode).getAttribute("text");

                
            }
            
        }
    }
   
    public static Formula buildFormula(Node precondition){

        Formula f = new Formula();
        
        NodeList children;
        boolean flagleft;
        
        NodeList pre_children = precondition.getChildNodes();
        
        for(int i = 0; i < pre_children.getLength(); i++){
            if(pre_children.item(i).getNodeType() == Node.ELEMENT_NODE){

                switch(pre_children.item(i).getNodeName()){
                      
                    case "PROPOSITION":
                        
                        f.type = Formula.form_type.LITERAL;

                        f.fluents = new ArrayList<>();
                        f.fluents.add(((Element) pre_children.item(i)).getAttribute("text"));
                        f.num_operands = 0;
                    break;
 
                    case "NOT":
                        
                        f.type = Formula.form_type.NOT;
                        f.num_operands = 1;
                        
                       
                        children = pre_children.item(i).getChildNodes();
                        for (int c = 0; c < children.getLength(); c++){
                            if(children.item(c).getNodeType() == Node.ELEMENT_NODE){
                                f.rightFormula = buildFormulaIn(children.item(c));
                            }
                        }
                        
                        
                    break;
                    
  
                    case "AND":
    
                        f.type = Formula.form_type.AND;
                        f.num_operands = 2;

                        flagleft = true;

                        
                        children = pre_children.item(i).getChildNodes();
                        
                        for (int c = 0; c < children.getLength(); c++){
                            if(children.item(c).getNodeType() == Node.ELEMENT_NODE){
                                if(flagleft){
                                    flagleft = false;
                                    f.leftFormula = buildFormulaIn(children.item(c));
                                }
                                else
                                    f.rightFormula = buildFormulaIn(children.item(c));
                            }   
                        }

                                               
    
                    break;
                    
                    case "OR":
    
                        f.type = Formula.form_type.OR;
                        f.num_operands = 2;
    
                        flagleft = true;
                       
                        
                        children = pre_children.item(i).getChildNodes();
                        
                        for (int c = 0; c < children.getLength(); c++){
                            if(children.item(c).getNodeType() == Node.ELEMENT_NODE){
                                if(flagleft){
                                    flagleft = false;
                                    f.leftFormula = buildFormulaIn(children.item(c));
                                }
                                else
                                    f.rightFormula = buildFormulaIn(children.item(c));
                            }   
                        }                        
    
                    break;
                    
                    case "BELIEVES":
    
                        f.type = Formula.form_type.B;
                        f.num_operands = 2;
                        
                        flagleft = true;
                        
                        
                        children = pre_children.item(i).getChildNodes();
                        
                        for (int c = 0; c < children.getLength(); c++){
                            if(children.item(c).getNodeType() == Node.ELEMENT_NODE){
                                
                                if(flagleft){
                                    flagleft = false;
                                    
                                    f.leftFormula = new Formula();
                                    f.leftFormula.type = Formula.form_type.AGENTNAME;
                                    f.leftFormula.fluents = new ArrayList<>();
                                    f.leftFormula.fluents.add(((Element) children.item(c)).getAttribute("text"));
                                    f.leftFormula.num_operands = 0;
                                    
                                }
                                else
                                    f.rightFormula = buildFormulaIn(children.item(c));
                            }   
                        }                        
    
                    break;                    
  
                    default:
                        System.err.println("failed to build formula");
    
                }
                
                    
            }
                
            
        }
        
        
        
        return f;
    }
     
    public static Formula buildFormulaIn(Node pre_children){

        Formula f = new Formula();
        
        NodeList children;
        boolean flagleft;
        
                switch(pre_children.getNodeName()){
                      
                    case "PROPOSITION":
                        
                        f.type = Formula.form_type.LITERAL;
                        
                        f.fluents = new ArrayList<>();
                        f.fluents.add(((Element) pre_children).getAttribute("text"));
                        f.num_operands = 0;
                    break;
                    
                    case "AGENT":
                        
                        f.type = Formula.form_type.AGENTNAME;
                        
                        f.fluents = new ArrayList<>();
                        f.fluents.add(((Element) pre_children).getAttribute("text"));
                        f.num_operands = 0;
                    break;
 
                    case "NOT":
                        
                        f.type = Formula.form_type.NOT;
                        f.num_operands = 1;
                        
                                                
                        children = pre_children.getChildNodes();
                        for (int c = 0; c < children.getLength(); c++){
                            if(children.item(c).getNodeType() == Node.ELEMENT_NODE){
                                f.rightFormula = buildFormulaIn(children.item(c));
                            }
                        }
                        
                        
                    break;
                    
  
                    case "AND":
    
                        f.type = Formula.form_type.AND;
                        f.num_operands = 2;
                        
                        flagleft = true;
                        
                        
                        children = pre_children.getChildNodes();
                        
                        for (int c = 0; c < children.getLength(); c++){
                            if(children.item(c).getNodeType() == Node.ELEMENT_NODE){
                                if(flagleft){
                                    flagleft = false;
                                    f.leftFormula = buildFormulaIn(children.item(c));
                                }
                                else
                                    f.rightFormula = buildFormulaIn(children.item(c));
                            }   
                        }

                                               
    
                    break;
                    
                    case "OR":
    
                        f.type = Formula.form_type.OR;
                        f.num_operands = 2;
                        
                        flagleft = true;
                        
                        
                        children = pre_children.getChildNodes();
                        
                        for (int c = 0; c < children.getLength(); c++){
                            if(children.item(c).getNodeType() == Node.ELEMENT_NODE){
                                if(flagleft){
                                    flagleft = false;
                                    f.leftFormula = buildFormulaIn(children.item(c));
                                }
                                else
                                    f.rightFormula = buildFormulaIn(children.item(c));
                            }   
                        }                        
    
                    break;
                    
                    case "BELIEVES":
    
                        f.type = Formula.form_type.B;
                        f.num_operands = 2;
                        
                        flagleft = true;
                        
                        
                        children = pre_children.getChildNodes();
                        
                        for (int c = 0; c < children.getLength(); c++){
                            if(children.item(c).getNodeType() == Node.ELEMENT_NODE){
                                
                                if(flagleft){
                                    flagleft = false;
                                    
                                    f.leftFormula = new Formula();
                                    f.leftFormula.type = Formula.form_type.AGENTNAME;
                                    f.leftFormula.fluents = new ArrayList<>();
                                    f.leftFormula.fluents.add(((Element) children.item(c)).getAttribute("text"));
                                    f.leftFormula.num_operands = 0;
                                    
                                }
                                else
                                    f.rightFormula = buildFormulaIn(children.item(c));
                            }   
                        }                        
    
                    break;                    
  
                    default:
                        System.err.println("failed to build formula in build2");
    
                }
        
        
        
        return f;
    }
}
