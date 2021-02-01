import java.io.*;
import java.util.*;

/**
 * @author Navya Sogi
 *
 */

public class CheckTrueFalse {
	//1001656413- These to store the symbols and true and false value given in kb 
	// the unqset and negunqset is set when reading the wumpus rule and additional file and statement
	//CHECK the LOGICALEXPRESSION.JAVA .setUniqueSymbol() method will have two new arguments to set these values
	public static HashSet<String> unqset=new HashSet<String>();
	public static HashSet<String> negunqset=new HashSet<String>();
	public static HashSet<String> temp1=new HashSet<String>();
	public static HashSet<String> temp2=new HashSet<String>();
	
	public static int cot=0,tt=0;
	//The flag to check if the alpha or negation of alpha
	public static Boolean negflag=false;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		if( args.length != 3){
			//takes three arguments
			System.out.println("Usage: " + args[0] +  " [wumpus-rules-file] [additional-knowledge-file] [input_file]\n");
			exit_function(0);
		}
		
		//create some buffered IO streams
		String buffer;
		BufferedReader inputStream;
		BufferedWriter outputStream;
		String statementbuffer = null;
		
		//create the knowledge base and the statement and the negation of statement
		LogicalExpression knowledge_base = new LogicalExpression();
		LogicalExpression statement = new LogicalExpression();
		LogicalExpression negstatement = new LogicalExpression();
		

		//open the wumpus_rules.txt
		try {
			inputStream = new BufferedReader( new FileReader( args[0] ) );
			
			//load the wumpus rules
			System.out.println("loading the wumpus rules...");
			knowledge_base.setConnective("and");
		
			while(  ( buffer = inputStream.readLine() ) != null ) 
                        {
				if( !(buffer.startsWith("#") || (buffer.equals( "" )) )) 
                                {
					//the line is not a comment
					LogicalExpression subExpression = readExpression( buffer );
					knowledge_base.setSubexpression( subExpression );
				} 
                else {
					//the line is a comment. do nothing and read the next line
				}
			}		
			
			//close the input file
			inputStream.close();

		} catch(Exception e){
			System.out.println("failed to open " + args[0] );
			e.printStackTrace();
			exit_function(0);
		}
		//end reading wumpus rules
														
		
		//read the additional knowledge file
		try {
			inputStream = new BufferedReader( new FileReader( args[1] ) );
			
			//load the additional knowledge
			System.out.println("loading the additional knowledge...");
			
			// the connective for knowledge_base is already set.  no need to set it again.
			// i might want the LogicalExpression.setConnective() method to check for that
			//knowledge_base.setConnective("and");
			
			while(  ( buffer = inputStream.readLine() ) != null) 
                        {
                                if( !(buffer.startsWith("#") || (buffer.equals("") ))) 
                                {
					LogicalExpression subExpression = readExpression( buffer );
					knowledge_base.setSubexpression( subExpression );
                                } 
                                else 
                                {
				//the line is a comment. do nothing and read the next line
                                }
                          }
			
			//close the input file
			inputStream.close();

		} catch(Exception e) {
			System.out.println("failed to open " + args[1] );
			e.printStackTrace();
			exit_function(0);
		}
		//end reading additional knowledge
		
		
		// check for a valid knowledge_base
		if( !valid_expression( knowledge_base ) ) {
			System.out.println("invalid knowledge base");
			exit_function(0);
		}
		
		// print the knowledge_base
		knowledge_base.print_expression("\n");
		//System.out.println(knowledge_base);
		
		// read the statement file
		try {
			inputStream = new BufferedReader( new FileReader( args[2] ) );
			
			System.out.println("\n\nLoading the statement file...");
			//buffer = inputStream.readLine();
			//negstatement.setConnective("not");
			// actually read the statement file
			// assuming that the statement file is only one line long
			while( ( buffer = inputStream.readLine() ) != null ) {
				if( !buffer.startsWith("#") ) {
					    //the line is not a comment
					    statementbuffer = new String(buffer);
						statement = readExpression(buffer);
						negstatement = readExpression(buffer );

                                                break;
				} else {
					//the line is a commend. no nothing and read the next line
				}
			}
			
			//close the input file
			inputStream.close();

		} catch(Exception e) {
			System.out.println("failed to open " + args[2] );
			e.printStackTrace();
			exit_function(0);
		}
		// end reading the statement file
		
		// check for a valid statement
		if( !valid_expression( statement ) ) {
			System.out.println("invalid statement");
			exit_function(0);
		}
		statement.print_expression("\n");
		System.out.println("\n");
		LogicalExpression child= new LogicalExpression();
		//Fetching the subexpressions of the knowledge base
		Vector<LogicalExpression> sentence=knowledge_base.getSubexpressions();
		int k=0;
		//Checking if the symbol is present in kb as a uniquesymbol, then assign true always(optimization) 
		// and if negation of symbol present in kb set false for the symbol
		while(k<sentence.size()){
				child = (LogicalExpression)sentence.get(k);
				String s=child.getConnective();
				//For each uniquesymbol not null and contained in the set of symbols add to the set temp1
				if(!(child.uniqueSymbol == null) && unqset.contains(child.uniqueSymbol)){
						temp1.add(child.uniqueSymbol);
				}
				//For each nconnective which is not and not null and contained in the set of symbols add to the set temp2				
				else if(s!=null  &&(s.equals("not")) ){
					String c=((LogicalExpression)child.getSubexpressions().get(0)).getUniqueSymbol();
					if(c!=null && unqset.contains(c)){
						temp2.add(c);
					}
				}
			k++;
		}
		//Invoke tt_entails for knowledge base and alpha
		Boolean entails1= tt_entails(knowledge_base,statement);
		//Invoke tt_entails for knowledge base and negation of alpha by setting the negflag as true
		negflag=true;
		Boolean entails2= tt_entails(knowledge_base,negstatement);								
		//Copying the result into the result.txt
		createResult(entails1,entails2,statementbuffer);
        
	} //end of main
	/******************Changes 1001656413******************/
	public static void createResult(Boolean entails1,Boolean entails2, String statement){
		try {
		    BufferedWriter output = new BufferedWriter(
							       new FileWriter( "result.txt" ) );
		    //Conditions checked on entails1 and entails2
		    if(entails1==true && entails2==false){
				output.write("Definitely true: \n");
				output.write(statement);
				//output.write("The knowledge base entails the statement, and the knowledge base does not entail the negation of the statement.\n");
			}
			else if(entails1==false && entails2==true){
				output.write("Definitely false: \n");
				output.write(statement);
				//output.write("The knowledge base entails  the negation of the statement, and the knowledge base does not entail the statement.\n");
			}
			else if(entails1==false && entails2==false){
				output.write("Possibly true or false: \n");
				output.write(statement);
				//output.write("The knowledge base entails neither the statement nor the negation of the statement.\n");
			}
			else if(entails1==true && entails2==true){
			 	output.write("Both true and false: \n");
			 	output.write(statement);
			 	//output.write("The knowledge base entails  both the statement and the the negation of the statement. This happens when the knowledge base is always false (i.e., when the knowledge base is false for every single row of the truth table).\n");
			 }	    
		   	output.close();
		    
		} catch( IOException e ) {
		    System.out.println("\nProblem writing to the result file!\n" +
				       "Try again.");
		    e.printStackTrace();
		}
	}
	//TT_entails function
	public static Boolean tt_entails(LogicalExpression kb,LogicalExpression alpha){
		tt++;
		HashMap<String,Boolean> mod=new HashMap<String,Boolean>();
		//Invoking tt_check_all with symbolset and empty model
		if(tt==1){
			//tt_check_all for kb and alpha
			return tt_check_all(kb,alpha,unqset,mod);
		}
		else{
		//tt_check_all for kb and negation alpha
			return tt_check_all(kb,alpha,negunqset,mod);
		}
	}
	//tt_check_all function
	public static Boolean tt_check_all(LogicalExpression kb,LogicalExpression alpha,HashSet symb, HashMap<String,Boolean> model){
		//If symbol set is empty , ie is model assigned values to each symbol then
		if(symb.isEmpty()){
			//check the PL_TRUE and if returned true
			if(PL_TRUE(kb,model)==true){
				cot++;
				//checking the negation flag
				if(!negflag){
					return (PL_TRUE(alpha,model));
				}
				//If negation then opposite of the result, ie true for false or false for true returned
				else{
					return (!PL_TRUE(alpha,model));	
				}
			}
			//KB is false,return true
			else {
				return true;
			}
		}	
		else {
			String p=null;
			int c=0;
			// set to hold the rest(symbols)
			HashSet<String> rest=new HashSet<String>();
			Iterator<String> k=symb.iterator();
			while(k.hasNext()){
				if(c==0){
					//the first element is extracted to p
					p=k.next();
				}
				else{
					//rest in to set rest
					rest.add(k.next());
				}
				c++;
			}
			//if the p is contained in the set temp1 (having the truth value), then
			if(temp1.contains(p)){
				//set the model with p as true
				model.put(p,true);
				//and recursively call ttcheckall with p as true in each model
				return tt_check_all(kb,alpha,rest,EXTEND(p,true,model));
			}

			else if(temp2.contains(p)){
				//set the model with p as false
				model.put(p,false);
				//and recursively call ttcheckall with p as false in each model
				return tt_check_all(kb,alpha,rest,EXTEND(p,false,model));
			}
			else{
				//else recursively call ttcheckall with p set as true and p set as false
				return (tt_check_all(kb,alpha,rest,EXTEND(p,true,model)) && tt_check_all(kb,alpha,rest,EXTEND(p,false,model)));
			}
		}
	}
	//method to add the value of p to the model by extending
	public static HashMap<String,Boolean> EXTEND(String p,Boolean value, HashMap<String,Boolean> model){
		model.put(p,value);
		return model;
	}
	//Computing the PL_TRUE value, either true or false
	public static Boolean PL_TRUE(LogicalExpression sentence,HashMap<String,Boolean> model ){
		LogicalExpression child= new LogicalExpression();
		LogicalExpression left= new LogicalExpression();
		LogicalExpression right= new LogicalExpression();
		//if uniquesymbol then return the value of symbol in model
		if (!(sentence.uniqueSymbol == null)){
			return model.get(sentence.uniqueSymbol);
		}
		//if the connective is and
		else if(sentence.connective.equals("and")){
			//enumerate through all the subexpressions and check for pltrue, if any false then return false
			for( Enumeration e = sentence.subexpressions.elements(); e.hasMoreElements(); ) {
				child = ( LogicalExpression )e.nextElement();
				if(PL_TRUE(child,model)==false){
					return false;
				}
			}
			return true;
		}
		//if the connective is or
		else if(sentence.connective.equals("or")){
			//enumerate through all the subexpressions and check for pltrue, if any true then return true
			for( Enumeration e = sentence.subexpressions.elements(); e.hasMoreElements(); ) {
				child = ( LogicalExpression )e.nextElement();
				if(PL_TRUE(child,model)==true){
					return true;
				}
			}
			return false;
		}
		//if the connective is xor
		else if(sentence.connective.equals("xor")){
			int n=0;
			//enumerate through all the subexpressions and check for pltrue, if any true then increment the counter n
			for( Enumeration e = sentence.subexpressions.elements(); e.hasMoreElements(); ) {
				child = ( LogicalExpression )e.nextElement();
				if(PL_TRUE(child,model)==true){
						//return true;
						n++;
				}
			}
			if(n==1){
				return true;
			}
			return false;
		}
		//if the connective is iff
		else if(sentence.connective.equals("iff")){
			// find the left and right child
			left=( LogicalExpression )sentence.subexpressions.get(0);
			right=( LogicalExpression )sentence.subexpressions.get(1);
			//check the pltrue value, if both same then return true
			if((PL_TRUE(left,model)==true)&&(PL_TRUE(right,model)==true)){
				return true;
			}
			else if((PL_TRUE(left,model)==false)&&(PL_TRUE(right,model)==false)){
				return true;
			}
			else{
				return false;
			}
		}
		//if the connective is if
		else if(sentence.connective.equals("if")){
			left=( LogicalExpression )sentence.subexpressions.get(0);
			right=( LogicalExpression )sentence.subexpressions.get(1);
			//check the pltrue value, if left is true and right is false return false
			if((PL_TRUE(left,model)==true)&&(PL_TRUE(right,model)==false)){
				return false;
			}
			//else always true
			return true;
		}
		//if the connective is not
		else if(sentence.connective.equals("not")){
			child=( LogicalExpression )sentence.subexpressions.get(0);
			//if the value of child is true, return false
			if(PL_TRUE(child,model)==true){
				return false;
			}
			return true;
		}
		return true;
	}
	
	/* this method reads logical expressions
	 * if the next string is a:
	 * - '(' => then the next 'symbol' is a subexpression
	 * - else => it must be a unique_symbol
	 * 
	 * it returns a logical expression
	 * 
	 * notes: i'm not sure that I need the counter
	 * 
	 */
	public static LogicalExpression readExpression( String input_string ){
        LogicalExpression result = new LogicalExpression();
          
          //testing
          //System.out.println("readExpression() beginning -"+ input_string +"-");
          //testing
          //System.out.println("\nread_exp");
          
          //trim the whitespace off
        input_string = input_string.trim();
        
        if( input_string.startsWith("(") ){
            //its a subexpression
          
            String symbolString = "";
            
            // remove the '(' from the input string
            symbolString = input_string.substring( 1 );
            //symbolString.trim();
            
            //testing
            //System.out.println("readExpression() without opening paren -"+ symbolString + "-");
				  
            if( !symbolString.endsWith(")" ) ){
              // missing the closing paren - invalid expression
              System.out.println("missing ')' !!! - invalid expression! - readExpression():-" + symbolString );
              exit_function(0);
              
            }
            else{
              //remove the last ')'
              //it should be at the end
              symbolString = symbolString.substring( 0 , ( symbolString.length() - 1 ) );
              symbolString.trim();
              
              //testing
              //System.out.println("readExpression() without closing paren -"+ symbolString + "-");
              
              // read the connective into the result LogicalExpression object					  
              symbolString = result.setConnective( symbolString );
             // System.out.println("New :\t"+symbolString);
              //testing
              //System.out.println("added connective:-" + result.getConnective() + "-: here is the string that is left -" + symbolString + "-:");
              //System.out.println("added connective:->" + result.getConnective() + "<-");
            }
            
            //read the subexpressions into a vector and call setSubExpressions( Vector );
            result.setSubexpressions( read_subexpressions( symbolString ) );
            
        } 
        else{   	
            // the next symbol must be a unique symbol
            // if the unique symbol is not valid, the setUniqueSymbol will tell us.
            result.setUniqueSymbol( input_string ,unqset,negunqset);
          
            //testing
            //System.out.println(" added:-" + input_string + "-:as a unique symbol: readExpression()" );
        }
          
       return result;
    }
	/* this method reads in all of the unique symbols of a subexpression
	 * the only place it is called is by read_expression(String, long)(( the only read_expression that actually does something ));
	 * 
	 * each string is EITHER:
	 * - a unique Symbol
	 * - a subexpression
	 * - Delineated by spaces, and paren pairs
	 * 
	 * it returns a vector of logicalExpressions
	 * 
	 * 
	 */	
	public static Vector<LogicalExpression> read_subexpressions( String input_string ) {
		Vector<LogicalExpression> symbolList = new Vector<LogicalExpression>();
		LogicalExpression newExpression;// = new LogicalExpression();
		String newSymbol = new String();	
		//testing
		//System.out.println("reading subexpressions! beginning-" + input_string +"-:");
		//System.out.println("\nread_sub");
		input_string.trim();
		while( input_string.length() > 0 ) {
		
		newExpression = new LogicalExpression();
		
		//testing
		//System.out.println("read subexpression() entered while with input_string.length ->" + input_string.length() +"<-");

		if( input_string.startsWith( "(" ) ) {
			//its a subexpression.
			// have readExpression parse it into a LogicalExpression object

			//testing
			//System.out.println("read_subexpression() entered if with: ->" + input_string + "<-");
			
			// find the matching ')'
			int parenCounter = 1;
			int matchingIndex = 1;
				while( ( parenCounter > 0 ) && ( matchingIndex < input_string.length() ) ) {
						if( input_string.charAt( matchingIndex ) == '(') {
							parenCounter++;
						} else if( input_string.charAt( matchingIndex ) == ')') {
							parenCounter--;
						}
					matchingIndex++;
				}
			
				// read untill the matching ')' into a new string
				newSymbol = input_string.substring( 0, matchingIndex );
				
				//testing
				//System.out.println( "-----read_subExpression() - calling readExpression with: ->" + newSymbol + "<- matchingIndex is ->" + matchingIndex );

				// pass that string to readExpression,
				newExpression = readExpression( newSymbol );

				// add the LogicalExpression that it returns to the vector symbolList
				symbolList.add( newExpression );

				// trim the logicalExpression from the input_string for further processing
				input_string = input_string.substring( newSymbol.length(), input_string.length() );

		} 
		else {
			//its a unique symbol ( if its not, setUniqueSymbol() will tell us )

			// I only want the first symbol, so, create a LogicalExpression object and
			// add the object to the vector
			
			if( input_string.contains( " " ) ) {
				//remove the first string from the string
				newSymbol = input_string.substring( 0, input_string.indexOf( " " ) );
				input_string = input_string.substring( (newSymbol.length() + 1), input_string.length() );
				
				//testing
				//System.out.println( "read_subExpression: i just read ->" + newSymbol + "<- and i have left ->" + input_string +"<-" );
			} else {
				newSymbol = input_string;
				input_string = "";
			}
			
			//testing
			//System.out.println( "readSubExpressions() - trying to add -" + newSymbol + "- as a unique symbol with ->" + input_string + "<- left" );
			
			newExpression.setUniqueSymbol( newSymbol,unqset,negunqset );
			
	    	//testing
	    	//System.out.println("readSubexpression(): added:-" + newSymbol + "-:as a unique symbol. adding it to the vector" );

			symbolList.add( newExpression );
			
			//testing
			//System.out.println("read_subexpression() - after adding: ->" + newSymbol + "<- i have left ->"+ input_string + "<-");
			
		}
			
			//testing
			//System.out.println("read_subExpression() - left to parse ->" + input_string + "<-beforeTrim end of while");
			
			input_string.trim();
			
			if( input_string.startsWith( " " )) {
			//remove the leading whitespace
			input_string = input_string.substring(1);
			}
		
		//testing
		//System.out.println("read_subExpression() - left to parse ->" + input_string + "<-afterTrim with string length-" + input_string.length() + "<- end of while");
	}
		return symbolList;
	}


	/* this method checks to see if a logical expression is valid or not 
	 * a valid expression either:
	 * ( this is an XOR )
	 * - is a unique_symbol
	 * - has:
	 *  -- a connective
	 *  -- a vector of logical expressions
	 *  
	 * */
	public static boolean valid_expression(LogicalExpression expression){
		
		// checks for an empty symbol
		// if symbol is not empty, check the symbol and
		// return the truthiness of the validity of that symbol

		if ( !(expression.getUniqueSymbol() == null) && ( expression.getConnective() == null ) ) {
			// we have a unique symbol, check to see if its valid
			return valid_symbol( expression.getUniqueSymbol() );

			//testing
			//System.out.println("valid_expression method: symbol is not empty!\n");
			}

		// symbol is empty, so
		// check to make sure the connective is valid
	  
		// check for 'if / iff'
		if ( ( expression.getConnective().equalsIgnoreCase("if") )  ||
		      ( expression.getConnective().equalsIgnoreCase("iff") ) ) {
			
			// the connective is either 'if' or 'iff' - so check the number of connectives
			if (expression.getSubexpressions().size() != 2) {
				System.out.println("error: connective \"" + expression.getConnective() +
						"\" with " + expression.getSubexpressions().size() + " arguments\n" );
				return false;
				}
			}
		// end 'if / iff' check
	  
		// check for 'not'
		else   if ( expression.getConnective().equalsIgnoreCase("not") ) {
			// the connective is NOT - there can be only one symbol / subexpression
			if ( expression.getSubexpressions().size() != 1)
			{
				System.out.println("error: connective \""+ expression.getConnective() + "\" with "+ expression.getSubexpressions().size() +" arguments\n" ); 
				return false;
				}
			}
		// end check for 'not'
		
		// check for 'and / or / xor'
		else if ( ( !expression.getConnective().equalsIgnoreCase("and") )  &&
				( !expression.getConnective().equalsIgnoreCase( "or" ) )  &&
				( !expression.getConnective().equalsIgnoreCase("xor" ) ) ) {
			System.out.println("error: unknown connective " + expression.getConnective() + "\n" );
			return false;
			}
		// end check for 'and / or / not'
		// end connective check

	  
		// checks for validity of the logical_expression 'symbols' that go with the connective
		for( Enumeration e = expression.getSubexpressions().elements(); e.hasMoreElements(); ) {
			LogicalExpression testExpression = (LogicalExpression)e.nextElement();
			
			// for each subExpression in expression,
			//check to see if the subexpression is valid
			if( !valid_expression( testExpression ) ) {
				return false;
			}
		}

		//testing
		//System.out.println("The expression is valid");
		
		// if the method made it here, the expression must be valid
		return true;
	}
	



	/** this function checks to see if a unique symbol is valid */
	//////////////////// this function should be done and complete
	// originally returned a data type of long.
	// I think this needs to return true /false
	//public long valid_symbol( String symbol ) {
	public static boolean valid_symbol( String symbol ) {
		if (  symbol == null || ( symbol.length() == 0 )) {
			
			//testing
			//System.out.println("String: " + symbol + " is invalid! Symbol is either Null or the length is zero!\n");
			
			return false;
		}

		for ( int counter = 0; counter < symbol.length(); counter++ ) {
			if ( (symbol.charAt( counter ) != '_') &&
					( !Character.isLetterOrDigit( symbol.charAt( counter ) ) ) ) {
				
				System.out.println("String: " + symbol + " is invalid! Offending character:---" + symbol.charAt( counter ) + "---\n");
				
				return false;
			}
		}
		
		// the characters of the symbol string are either a letter or a digit or an underscore,
		//return true
		return true;
	}

        private static void exit_function(int value) {
                System.out.println("exiting from checkTrueFalse");
                  System.exit(value);
                }	
}