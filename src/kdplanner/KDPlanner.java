/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kdplanner;

import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author shikha
 * Date: 
 * version 1: 02-01-2020
 * version 2: 12-04-2020  
 */
public class KDPlanner {

    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {
        
        
        Scanner input = new Scanner(System.in);
        
        System.out.println("Enter testcase directory:");
        String testcase_dir = input.nextLine();
        
        System.out.println("Enter testcase file:");
        String testcase_file = input.nextLine();
        
        
        /**
         * FIRST STEP 
         * Invoke the module to convert the text input into xml input
         * java -jar "KRR.jar" -KD45  kd45.txt 1>kd45.xml
         */
        
        System.out.println("Converting input input to XML document");
        //BuildXML.build(testcase);
        
        double start0 = System.nanoTime();
        BuildXML.build(testcase_dir, testcase_file);

        
        
        
        /**
         * SECOND STEP
         * Traverse the tree and populate the initial model and action structures
         */
        
        BuildProblem.build(testcase_dir, testcase_file);
        double end0 = System.nanoTime();
        double elapsedTime0 = (end0 - start0)/1000000000;
        


        /**
         * THIRD STEP
         * Invoke planner
         */

       
        boolean isPlanFound = false;
        ArrayList<String> Solution_plan = null;
        System.out.println("Compute or Validate?\n\t1.Compute\n\t2.Validate");
        int choice_compute = input.nextInt();
        switch (choice_compute){
            case 1:
            {
                System.out.println("Type?\n\t1.Strong\n\t2.Weak"); 
                int choice_plantype = input.nextInt();
                switch (choice_plantype){
                    case 1:
                    {
                        //Strong: plan with only self actions
                        double start = System.nanoTime();
                        Solution_plan = planner.constructPlanSearchTree(true);
                        double end = System.nanoTime();
                        double elapsedTime = (end - start)/1000000000;
                        
                        if(!Solution_plan.isEmpty())
                        {
                            System.out.println("Search time: "+ elapsedTime);
                            System.out.println("Total time: "+ elapsedTime0 + elapsedTime);
                            
                            System.out.print("\nPlan found is : [");
                            for(int i = Solution_plan.size()-1; i >= 0; i--){
                                System.out.print(Solution_plan.get(i));
                                if(i!=0)
                                    System.out.print(", ");
                            }

                            System.out.println("]\n");
                            isPlanFound = true;
                        }

                        else
                            System.out.println("\nNo plan found!\n");    
                        
                    }
                    break;
                    
                    case 2:
                    {
                        //Weak: default mode
                        double start = System.nanoTime();
                        Solution_plan = planner.constructPlanSearchTree(false);
                        double end = System.nanoTime();
                        double elapsedTime = 1.0*(end - start)/1000000000;                        
                        if(!Solution_plan.isEmpty())
                        {
                            System.out.println("Search time: "+ elapsedTime);
                            System.out.println("Total time: "+ (elapsedTime0 + elapsedTime));
                            System.out.print("\nPlan found is : [");
                            for(int i = Solution_plan.size()-1; i >= 0; i--){
                                System.out.print(Solution_plan.get(i));
                                if(i!=0)
                                    System.out.print(", ");
                            }

                            System.out.println("]\n");
                            isPlanFound = true;
                        }

                        else
                            System.out.println("\nNo plan found!\n");    
                    }
                    break;
                    
                    default:
                        System.out.println("Invalid choice!");
                } 
                
                if(isPlanFound){
                    System.out.println("Generate explanations?\n\t1.Y\n\t2.N"); 
                    String ch = input.next();
                    switch(ch){

                        case "Y":
                        case "y":
                        {
                            //Y: action explanation and listinferences
                            for (int i = Solution_plan.size()-1; i >= 0; i--){
                                System.out.println(PlanningProblem.action_to_explanantion.get(Solution_plan.get(i).split(" ")[0]));
                                System.out.println(Solution_plan.get(i));
                            }
                        }
                        
                        break;

                        case "N":
                        case "n":
                        {
                            System.out.println("You chose not to!");

                        }
                        
                        break;

                        default:
                            System.out.println("Invalid choice!");   
                    }
                }
            }    
            break;
           
            case 2:
            {
                ArrayList<String> plan = new ArrayList<>();
                boolean cont = true;
                
                while(cont){
                    
                    System.out.println("add action and enter");
                    plan.add(input.next());
                    System.out.println("Press Y to add more");
                    //if(!input.hasNext("Y")||(!input.hasNext("y")))
                      //  cont = false;
                    //input.next();
                    if(input.hasNext("Y")||(input.hasNext("y")))
                    {
                        input.next();
                        continue;
                        
                               
                    }
                        else
                        cont = false;
                    
                }
                
                System.out.println("VALIDATING PLAN: "+plan+"  ...");
                boolean isPlanValid = PlanValidation.validate(plan);
                System.out.println("Plan validation status: " + isPlanValid);
            }
            
            break;
            
            default:
                System.out.println("Invalid choice!");
                
       }
        
       System.out.println("Exiting...");
            
    }
}
        
        
        
        
        
        
        
      
    
