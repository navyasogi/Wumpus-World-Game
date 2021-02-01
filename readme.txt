Name - Navya R Sogi
UTA ID - 1001753085

Programming Language used: Java



How the code is structured.
 
-In CheckTrueFalse.java added the following functions
 
1)tt_entails()	- The method that returns if the knowledge base entails alpha. The check is done for knowledge base and negation of alpha.
 

2)tt_check_all()- The symbols in the set are assigned values recursively for each model and once all the symbols are assigned value, PL_TRUE() is checked for kb and model. If true then PL_TRUE() is checked for alpha and model and result is returned. If the Kb,model is false then return true.
 

3)EXTEND() - Method to extend the model by assigning the passed boolean value to the symbol
.

4)PL_TRUE() - Here the symbols and connectives are evaluated on the basis of different connectives. The logic for each connective returns different values for each subexpressions. 
 

5)createResult()- Finally the result after checking both the entailment(kb,alpha) and(kb,not(alpha)) is copied to the result.txt. 

 

-In LogicalExpression.java modified 
    
1)setUniqueSymbol()-The hashset unqset and negunqset is used to extract all the  unique symbols present while reading kb and alpha.

   

Files : - Submitted a ZIPPED directory called assignment3_nxs3085.zip. Task 2 code is contained in wumpus_world folder.
   
-This zip directory includes CheckTrueFalse.java, LogicalExpression.java, wumpus_rules.txt, c.txt, c1.txt, c2.txt, b.txt and readme.txt files. We have been asked to test for P_4_4 which is stored in c.txt, P_3_3 stored in c1.txt and M_3_4 which is stored in c2.txt. The results of each is stored in result.txt, result1.txt and result2.txt respectively. 
   
-Keep all input files in the same directory.


  

How to run the code: 	
  
1)Enter the folder which contains the code using the following command:
    
cd /Users/navyasogi/Desktop/wumpus_world (in my case, folder name is wumpus_world)
    
Compile both CheckTrueFalse.java and LogicalExpression.java using the following commands
    
javac CheckTrueFalse.java 
    
javac LogicalExpression.java 
		    	
  

2)Run using the following commands 
    
java CheckTrueFalse wumpus_rules.txt [additional_knowledge_file] [statement_file]
    
b.txt contains the knowledge base sent through email.

Eg: java CheckTrueFalse wumpus_rules.txt b.txt c.txt (for P_4_4)
        
java CheckTrueFalse wumpus_rules.txt b.txt c1.txt (for P_3_3)
        
java CheckTrueFalse wumpus_rules.txt b.txt c2.txt (for M_3_4)

  

3)Checking the efficiency of the program:
   
Run the following commands:
time java CheckTrueFalse wumpus_rules.txt b.txt c.txt (for P_4_4)
   
time java CheckTrueFalse wumpus_rules.txt b.txt c1.txt (for P_3_3)
   
time java CheckTrueFalse wumpus_rules.txt b.txt c2.txt (for M_3_4)


   

For scenario P_4_4			
    
real 0m0.960s
user 0m1.377s < 2 minutes
sys  0m0.196s

   
For scenario P_3_3			
    
real 0m1.125s
user 0m1.584s
sys  0m0.225s 

For scenario M_3_4
    
real 0m1.020s
user 0m1.124s
sys 0m0.183s

