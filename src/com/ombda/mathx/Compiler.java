package com.ombda.mathx;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ombda.mathx.expressions.*;
import com.ombda.mathx.statements.*;
import com.ombda.mathx.values.*;
import com.ombda.mathx.errors.Error;
public class Compiler{
	/*
	 * TODO
	 * ✔ if statement
	 * ✔ with statement
	 * ✔ conditional operator, inline if-else
	 * ✔ in operator
	 * ✔ bit-shift operators
	 * ✔ new expressions
	 * ✔ arrays
	 * (?) crazy math with infinity and zero
	 * ✔ variable initializer/creation statements (+constant variables as well)
	 * ✔ <> compareTo operator
	 * ✔ comments
	 * ✔ functions - statements, literals
	 * ✔ const(), finalize(), seal(), and immutable()
	 * ✔ while, do...while statements
	 * ✔ for statement - for(init; condition; increment)
	 * ✔ for statement - for(variable in container)
	 * ✔ for statement - for(integer)
	 * ✔ define directives
	 * ✔ if-elseif-else-end directives
	 * ✔ inline for 
	 * ✔ inline if - else
	 * ✔ ?. null-catch operator
	 * x when expr* else y 	 where expr* = an expression such that (x expr*) will work syntactically -> will work like this: if(x expr*) then x else y
	 * custom Type creation - classes, enums, structs
	 * try-catch, throw, & error type
	 * ✔ += -= *= /= %= &= |= &&= ||= ^^= <<= >>= >>>= assignment operators
	 * ✔ ++= --= !!= absolute value assignment, negative value assignment, logical not assignment
	 * improve .= operator
	 * string \n \r \b \t etc.
	 * .==(vals) .==&(vals) .!=(vals) .!=|(vals) operators
	 * 'global' object that just contains all the top-level variables in the scope.
	 * native - Java?
	 * real error support - with lines and position within them
	 * make postfix ++ -- operators support indexing
	 */
	private List<String> tokens = new ArrayList<>();
	private List<Position> positions = new ArrayList<>();
	private int pos;
	private String token;
	private Position position, lastPosition = null;
	private boolean inFunction = false, inLoop = false;
	private Compiler(File f) throws FileNotFoundException{	
		Scanner scan = new Scanner(f);
		List<String> lines = new ArrayList<>();
		while(scan.hasNext()){
			String line = scan.nextLine().trim();
			if(line.startsWith("@"))
				line += '\u0001';
			line = line.replaceAll("/([\\*+]).*\\1/", "").replaceAll("//.*", "")/*.replaceAll("/\\+.*\\+/", "")*/;
			//xxxxxSystem.out.println("line = "+line);
			lines.add(line);
		}
		scan.close();
		
		evalTokens(lines);
	}
	private Compiler(String program){
		evalTokens(Arrays.asList(program.replaceAll("/\\*.*\\*/", "").replaceAll("//.*\\n", "")));
	}
	private Compiler(List<String> lines){
		
		for(String line : lines){
			if(line.startsWith("@"))
				line += '\u0001';
		}
		
		evalTokens(lines);
	}
	private void evalTokens(List<String> lines){
		List<Token> tokens = Tokenizer.tokenize(lines);
		tokens = directives(tokens);
		
		for(Token t : tokens){
			this.tokens.add(t.toString());
			this.positions.add(Position.get(t.line, t.pos));
		}
	}
	static List<Token> directives(List<Token> tokens){
		List<Token> result = new ArrayList<>();
		Map<String,Macro> defined = new HashMap<>();
		
		boolean changed;
		do{
			changed = false;
			for(int i = 0; i < tokens.size(); i++){
				Token token = tokens.get(i);
				if(token.startsWith("@")){
					String directiveStr = token.toString();
					
					if(directiveStr.length() <= 2) throw new Error(token.position(),"Invalid directive "+directiveStr);
					//if(c != '\'' && c != '"' || c != directiveStr.charAt(directiveStr.length()-1)) throw new Error(position,"Invalid directive: requires a string, got token "+directiveStr);
					//directiveStr = directiveStr.substring(1, directiveStr.length()-1).trim();
					Matcher m = Pattern.compile("\\s").matcher(directiveStr);
					String cmd = directiveStr.substring(1,m.find()? m.start() : directiveStr.length());
					//System.out.println("directiveStr = "+directiveStr);
					directiveStr = directiveStr.substring(m.start()+1).trim();
					if(cmd.equals("define")){
						List<Token> definition;
						Matcher m2 = Pattern.compile("\\s+as\\s+").matcher(directiveStr);
						boolean found = m2.find();
						String name = directiveStr.substring(0,found? m2.start() : directiveStr.length()).trim();
						if(defined.containsKey(name)) throw new Error(token.position(),"Error: macro "+name+" is already defined");
						if(!found){
							m.reset(directiveStr);
							if(m.find()) throw new Error(token.position().add(0, 8),"Invalid macro definition: Invalid name: "+directiveStr);
							if(directiveStr.startsWith("#") || directiveStr.matches("%+#")) directiveStr = directiveStr.substring(1);
							defined.put(directiveStr, new Definition(directiveStr));
						}else{
							directiveStr = directiveStr.substring(m2.start()+m2.group().length()).trim();//.replaceAll("\\\\as", "as");
							if(directiveStr.startsWith("pattern(")){
								if(!name.matches("[a-zA-Z_][a-zA-Z_0-9]*")) throw new Error(token.position().add(0, 8),"Invalid macro definition: Invalid name for pattern macro: "+name);
								directiveStr = directiveStr.substring(8);
								int depth = 0;
								int last = 0;
								int j;
								for(j = 0; j < directiveStr.length(); j++){
									char c = directiveStr.charAt(j);
									int next = j < directiveStr.length()-1? directiveStr.charAt(j+1) : 0;
									if(c == '(' && (!Character.isWhitespace(last) || !Character.isWhitespace(next)))
										depth++;
									else if(c == ')' && (!Character.isWhitespace(last) || !Character.isWhitespace(next)))
										depth--;
									if(depth == -1) break;
									last = c;
								}
								if(j == directiveStr.length()-1 && directiveStr.charAt(j) != ')')
									throw new Error(token.position().add(0, j+name.length()+12),"Invalid macro definition: Expected ), got "+directiveStr.charAt(j));
								String pattern = directiveStr.substring(0,j);
								directiveStr = directiveStr.substring(j+1).trim();
								if(directiveStr.startsWith("{") != directiveStr.endsWith("}"))
									throw new Error(token.position().add(0, j+name.length()+12),"Invalid macro definition: Definition of a pattern must both start AND end with curly brackets {}");
								else if(!(directiveStr.startsWith("{") && directiveStr.endsWith("}"))){
									if(directiveStr.startsWith("->")){
										directiveStr = directiveStr.substring(2).trim();
										if(!Compiler.isString(directiveStr)){
											char end = directiveStr.charAt(0) == '"'? '\'' : '"';
											directiveStr = end + directiveStr + end;
										}
										directiveStr = "->"+directiveStr;
									}
									directiveStr = "{"+directiveStr+"}";
									//System.out.println("directiveStr = "+directiveStr);
								}
								definition = Tokenizer.tokenize(Arrays.asList(directiveStr),token.line,token.pos+12+name.length());
								Macro def = new PatternMacro(name,compilePatternString(pattern),definition);
								if(defined.containsKey(def.getName())) throw new Error(token.position(),"Error: macro "+def.getName()+" is already defined");
								defined.put(def.getName(), def);
								//System.out.println("Created new definition "+def.getName());
							}else{
								definition = Tokenizer.tokenize(Arrays.asList(directiveStr),token.line,token.pos);
								Macro def;
								if(name.matches("[a-zA-Z_][a-zA-Z_0-9]*"))
									def = new SimpleMacro(name,definition);
								else{
									if(name.matches("[a-zA-Z_][a-zA-Z_0-9]*\\([a-zA-Z_][a-zA-Z_0-9]*(,[\\w_][a-zA-Z_0-9]*)*\\)")){
										int idx = name.indexOf('(');
										String params = name.substring(idx+1,name.length()-1);
										name = name.substring(0,idx);
										String[] paramNames = params.split(",");
										def = new MacroFunction(name,paramNames,definition);
										//System.out.println("new function macro created: "+def);
									}else throw new Error(token.position().add(0, 8),"Invalid macro definition: Invalid macro name "+name);
								}
								defined.put(def.getName(), def);
							}
							tokens.remove(i--);
						}
					}else if(cmd.equals("undef")){
						String name = directiveStr;
						if(!defined.containsKey(name)) throw new Error(token.position(),"\""+name+"\" is not defined, cannot undefine it");
						defined.remove(name);
					}else if(cmd.equals("if")){
						String statement = directiveStr;
						tokens.remove(i-1);
						tokens.remove(i-1);
						i--;
						doIf(evalIfStatement(statement,defined),tokens,i,defined);
						
						result.add(tokens.get(i));
					}else throw new Error(token.position(),"Invalid directive "+cmd);
					changed = true;
				}else{
					boolean replaced = false;
					for(Macro def : defined.values()){
						int skip = def.matches(tokens, i);
						//System.out.println("Testing definition "+def+" against "+tokens.subList(i,tokens.size()));
						if(skip != -1){
							//System.out.println("matched");
							replaced = true;
							changed = true;
							//System.out.println("replaced "+token+" with "+def.replacement()+"; skip = "+skip);
							for(Token t : def.replacement()){
								result.add(t);
							}
							//System.out.println("resulting tokens = "+result);
							//System.out.println("skip = "+skip+" "+tokens.get(i+skip));
							i += skip-1;
							
						}
					}
					if(!replaced)
						result.add(token);
				}
			}
			for(int i = result.size()-1; i >= 0; i--){
				if(result.get(i).toString().isEmpty())
					result.remove(i);
			}
			if(changed){
				tokens = result;
				result = new ArrayList<>();
			}
		}while(changed);
		//System.out.println("final result = "+result);
		return result;
	}
	private static boolean evalIfStatement(final String statement, final Map<String, Macro> defined){
		final List<Token> tokens = Tokenizer.tokenize(Arrays.asList(statement));
		return new Object(){
			private int pos = -1;
			private Token token;
			private void next(){
				pos++;
				if(pos >= tokens.size()){
					token = tokens.get(tokens.size()-1);
					pos = tokens.size();
				}else token = tokens.get(pos);
			}
			private boolean eat(String s){
				if(token.equals(s)){
					next();
					return true;
				}else return false;
			}
			public boolean parse(){
				next();
				return or();
			}
			private boolean or(){
				boolean b = and();
				while(eat("||") || eat("|") || eat("or"))
					b = b || and();
				return b;
			}
			private boolean and(){
				boolean b = equal();
				while(eat("&&") || eat("&") || eat("and"))
					b = b && equal();
				return b;
			}
			private boolean equal(){
				boolean b = factor();
				for(;;){
					if(eat("==") || eat("==="))
						return b == factor();
					else if(eat("!=") || eat("!=="))
						return b != factor();
					else return b;
				}
			}
			private boolean factor(){
				if(eat("!") || eat("not")) return !factor();
				else if(eat("true")) return true;
				else if(eat("false")) return false;
				else if(eat("(")){
					boolean b = or();
					if(!eat(")")) throw new Error(token.position(),"Invalid if directive expression "+statement+": expected ) here.");
					return b;
				}
				else{
					String name = token.toString();
					if(isString(name))
						name = name.substring(1,name.length()-1);
					next();
					boolean not;
					if(eat("not")){
						if(!eat("defined")) throw new Error(token.position(),"Invalid if directive expression "+statement+": expected 'defined' here.");
						not = true;
					}else{
						not = eat("!defined");
						if(!not) eat("defined");
					}
					boolean result = defined.containsKey(name);
					if(not) result = !result;
					return result;
				}
			}
		}.parse();
	}

	private static void doIf(boolean ifresult, List<Token> tokens2, int i, Map<String, Macro> defined){
		//System.out.println("ifresult = "+ifresult);
		if(ifresult){
			int depth = 1;
			for(int j = i; j < tokens2.size() && depth > 0; j++){
				Token str = tokens2.get(j);
				if(j > 1 && tokens2.get(j-1).equals("@") && (str.startsWith("'if") || str.startsWith("\"if"))){
					depth++;
				}else if(j > 1 && tokens2.get(j-1).equals("@") && (str.startsWith("'end") || str.startsWith("\"end"))){
					depth--;
					//System.out.println("removing "+str);
					if(depth == 0){
						tokens2.remove(j-1);
						tokens2.remove(j-1); //removes end
						break;
					}
				}else if(depth == 1 && j > 1 && tokens2.get(j-1).equals("@") && (str.startsWith("'else") || str.startsWith("\"else"))){
					j--;
					tokens2.remove(j);
					tokens2.remove(j);
					int depth2 = 1;
					//removes else/else if
					inner:for(int q = j; q < tokens2.size();){
						Token str2 = tokens2.get(q);
						if(q+1 < tokens2.size() && str2.equals("@") && (tokens2.get(q+1).equals("'if'") || tokens2.get(q+1).equals("\"if\""))){
							depth2++;
							//System.out.println("str = "+str);
						}else if(q+1 < tokens2.size() && str2.equals("@") && (tokens2.get(q+1).equals("'end'") || tokens2.get(q+1).equals("\"end\""))){
							depth2--;
							//System.out.println("str = "+str2);
							if(depth2 == 0){
								break inner;
							}
						}
						tokens2.remove(q);
					}
				}
			}
		}else{
			
			int depth = 1;
			for(int j = i; j < tokens2.size();){
				Token str = tokens2.get(j);
				if(str.equals("@") && j+1 < tokens2.size() && (tokens2.get(j+1).startsWith("'if ") && tokens2.get(j+1).endsWith("'") || tokens2.get(j+1).startsWith("\"if ") && tokens2.get(j+1).endsWith("\""))){
					tokens2.remove(j);
					depth++;
				}else if(str.equals("@") && j+1 < tokens2.size() && (tokens2.get(j+1).equals("'end'") || tokens2.get(j+1).equals("\"end\""))){
					depth--;
					if(depth == 0){
						tokens2.remove(j+1);
						tokens2.remove(j);
						//System.out.println("result tokens = "+tokens);
						return;
					}
				}else if(str.equals("@") && j+1 < tokens2.size() && (tokens2.get(j+1).startsWith("'elseif ") && tokens2.get(j+1).endsWith("'")|| tokens2.get(j+1).startsWith("\"elseif ") && tokens2.get(j+1).endsWith("\""))){
					tokens2.remove(j);
					String statement = tokens2.remove(j).substring(8);
					
					doIf(evalIfStatement(statement,defined),tokens2,i,defined);
					//System.out.println("result tokens = "+tokens);
					return;
				}else if(str.equals("@") && j+1 < tokens2.size() && (tokens2.get(j+1).equals("'else'") || tokens2.get(j+1).equals("\"else\""))){
					tokens2.remove(j);
					tokens2.remove(j);
					int depth2 = 1;
					for(int q = j; q < tokens2.size(); q++){
						Token str2 = tokens2.get(q);
						//
						//System.out.println("str2 = "+str2);
						if(q+1 < tokens2.size() && str2.equals("@") && (tokens2.get(q+1).equals("'if'") || tokens2.get(q+1).equals("\"if\""))){
							depth2++;
							//System.out.println("str = "+str);
						}else if(q+1 < tokens2.size() && str2.equals("@") && (tokens2.get(q+1).equals("'end'") || tokens2.get(q+1).equals("\"end\""))){
							depth2--;
							if(depth2 == 0){
								tokens2.remove(q);
								tokens2.remove(q);
								//System.out.println("result tokens = "+tokens);
								return;
							}
						}
					}
				}
				tokens2.remove(j);
			}
		}
	}
	static List<String> compilePatternString(String pattern){
		List<String> patternTokens = new ArrayList<>();
		
		int pdepth = 0;
		char string = (char)0;
		
		String token = "";
		
		for(int i = 0; i < pattern.length(); i++){
			char c = pattern.charAt(i);
			if(Character.isWhitespace(c) && !token.isEmpty() && pdepth == 0){
				patternTokens.add(token);
				token = "";
			}else{
				if(c == '(' && (i == pattern.length()-1 || !Character.isWhitespace(pattern.charAt(i+1)))) pdepth++;
				else if(c == ')' && pdepth != 0 && (i == 0 || !Character.isWhitespace(pattern.charAt(i-1)))) pdepth--;
				else if(c == '\'' || c == '"'){
					if(string == 0) string = c;
					else if(string == c){
						if(pattern.charAt(i-1) != '\\') string = (char)0;
					}
				}
				token += c;
			}
		}
		patternTokens.add(token);
		return patternTokens;
	}
	
	private void next(){
		pos++;
		if(pos >= tokens.size()){
			pos = tokens.size();
			token = "";
			lastPosition = position;
			position = null;
		}else{
			token = tokens.get(pos);
			lastPosition = position;
			position = positions.get(pos);
		}
	}
	private boolean eat(String str){
		if(token.equals(str)){
			next();
			return true;
		}else return false;
	}
	private boolean assert_eat(String str){
		if(!eat(str)) throw new Error(position,"Expected ), got "+token);
		return true;
	}
	private boolean eat(String...strings){
		int temp_pos = pos;
		for(String str : strings){
			if(!eat(str)){
				pos = temp_pos-1;
				next();
				return false;
			}
		}
		return true;
	}
	
	private List<Statement> compile(){
		List<Statement> statements = new ArrayList<>();
		pos = -1;
		next();
		
		while(pos < tokens.size()){
			Statement statement = Statement();
			if(statement != EmptyStatement.INSTANCE)
				statements.add(statement);
		}
		
		return statements;
	}
	private Statement Statement(){
		if(eat("{")){
			Position p = lastPosition;
			List<Statement> statements = new ArrayList<>();
			while(!eat("}")){
				statements.add(Statement());
			}
			return new BodyStatement(p,statements);
		}else if(eat("stop")){
			Position p = position;
			int exit_status = 0;
			if(token.matches("0*\\d+(\\.0*)?")) exit_status = Integer.parseInt(token.replaceAll("\\.0*", ""));
			eat(";");
			return new Stop(p,exit_status);
		}else if(eat("if")){
			Position p = position;
			assert_eat("(");
			Expression condition = Expression();
			assert_eat(")");
			Statement t = Statement(), f = null;
			if(eat("else")){
				f = Statement();
			}
			return new IfStatement(p,condition,t,f);
		}else if(eat(";")){
			return EmptyStatement.INSTANCE;
		}else if(eat("for")){
			Position p = position;
			return ForLoop(p,false);
		}else if(eat("do")){
			Position p = lastPosition;
			Statement statement = Statement();
			boolean until = eat("until");
			if(!until) assert_eat("while");
			
			assert_eat("(");
			
			Expression cond = Expression();
			
			assert_eat(")");
			eat(";");
			
			return new DoWhileStatement(p,cond,statement,until);
		}else if(token.equals("while") || token.equals("until")){ 
			boolean until = token.equals("until");
			Position p = position;
			next();
			assert_eat("(");
			Expression condition = Expression();
			assert_eat(")");
			boolean b = inLoop;
			inLoop = true;
			Statement statement = Statement();
			eat(";");
			inLoop = b;
			return new WhileStatement(p,condition,statement,until);
		}else if(eat("break")){
			Position p = lastPosition;
			if(!inLoop) throw new Error(p,"break statement out of loop");
			eat(";");
			return new BreakStatement(p);
		}else if(eat("continue")){
			Position p = lastPosition;
			if(!inLoop) throw new Error(p,"continue statement out of loop");
			eat(";");
			return new ContinueStatement(p);
		}else if(eat("return")){
			Position p = lastPosition;
			if(!inFunction) throw new Error(p,"return statement out of function");
			return new ReturnStatement(p,Expression());
		}else if(eat("function")){
			Position p = lastPosition;
			String name = token;
			if(!isVariable(token)) throw new Error(position,"Expected variable name, got "+token);
			next();
			return new VariableInitializer(p,name,Function(p));
		}else if(token.equals("const") && !tokens.get(pos+1).equals("(")){
			next();
			Position p = lastPosition;
			boolean function = false, immutable = eat("immutable");
			if(eat("function")){
				function = true;//throw new Error(position,"function statement not implemented yet");
			}else eat("var");
			
			String name = token;
			if(!isVariable(token)) throw new Error(position,"Expected variable name, got "+token);
			next();
			Expression init;
			if(function){
				init = Function(p);
			}else{
				if(eat("=")){
					init = Expression();
				}else init = new Literal(position,Constants.UNDEFINED);
			}
			eat(";");
			return new VariableInitializer(p,name,init,true,immutable);
		}else if(token.equals("immutable") && !tokens.get(pos+1).equals("(")){
			next();
			Position p = lastPosition;
			boolean function = false, constant = eat("const");
			if(eat("function")){
				function = true;//throw new Error(position,"function statement not implemented yet");
			}else eat("var");
			
			String name = token;
			if(!isVariable(token)) throw new Error(position,"Expected variable name, got "+token);
			next();
			Expression init;
			if(function){
				init = Function(p);
			}else{
				if(eat("=")){
					init = Expression();
				}else init = new Literal(position,Constants.UNDEFINED);
			}
			eat(";");
			return new VariableInitializer(p,name,init,constant,true);
		}else if(eat("var")){
			Position p = lastPosition;
			String name = token;
			if(!isVariable(token)) throw new Error(position,"Expected variable name, got "+token);
			next();
			Expression init;
			if(eat("=")){
				init = Expression();
			}else init = new Literal(position,Constants.UNDEFINED);
			eat(";");
			return new VariableInitializer(p,name,init);
		}else if(eat("delete")){
			Statement s = new DeleteStatement(lastPosition,Expression());
			eat(";");
			return s;
		}else if(eat("with")){
			Position p = lastPosition;
			assert_eat("(");
			Expression obj = Expression();
			assert_eat(")");
			assert_eat("{");
			List<Statement> statements = new ArrayList<>();
			while(!eat("}")){
				statements.add(Statement());
			}
			return new WithStatement(p,obj,statements);
		}else if(eat("class")){
			String name = token;
			Position p = lastPosition;
			if(!isVariable(name)) throw new Error(position,"Invalid class name "+name);
			next();
			List<Reference> parents = null;
			if(eat("extends")){
				parents = new ArrayList<>();
				do{
					Position p2 = position;
					Expression ex = Factor(false);
					if(!(ex instanceof Reference)) throw new Error(p2,"Invalid class parents for "+name);
					parents.add((Reference)ex);
				}while(eat(","));
			}
			List<VariableInitializer> contents = new ArrayList<>();
			
			if(eat("{")){
				while(!eat("}")){
					Statement s;
					if((token.equals("function") && tokens.get(pos+1).equals("new"))){
						Position p2 = position;
						next(); next();
						s = new VariableInitializer(p2,"new",Function(p2));
	
					}else s = Statement();
					if(!(s instanceof VariableInitializer)) throw new Error(position,"Invalid class body, statements of type "+s.getClass().getSimpleName()+" aren't allowed.");
					contents.add((VariableInitializer)s);
				}
			}
			return new ClassDefinition(p,name, contents, parents);
		}else{
			Statement s = new ExpressionStatement(position,Expression());
			eat(";");
			return s;
		}
	}
	private ForLoop ForLoop(Position p, boolean eat_do){
		boolean eat_parenthesis = eat_do? eat("(") : assert_eat("(");
		
		boolean foreach = (token.equals("var")? tokens.get(pos+2) : tokens.get(pos+1)).equals("in");
	
		ForLoop loop;
		boolean b = inLoop;
		if(foreach){
			boolean create = eat("var");
			if(!isVariable(token)) throw new Error(position,"Variable name expected, got "+token);
			
			String name = token;
			next();
			assert_eat("in");
			
			Expression list = Expression();
			if(eat_parenthesis) assert_eat(")");
			
			if(eat_do) assert_eat("do");
			
			inLoop = true;
			
			loop = new ForEachStatement(p,name,create,list,Statement());
			
		}else{
			
			int pdepth = 0, cdepth = 0, bdepth = 0;
			boolean count = true;
			for(int i = pos; i < tokens.size(); i++){
				String s = tokens.get(i);
				if(s.equals("(")) pdepth++;
				else if(s.equals(")")) pdepth--;
				else if(s.equals("{")) cdepth++;
				else if(s.equals("}")) cdepth--;
				else if(s.equals("[")) bdepth++;
				else if(s.equals("]")) bdepth--;

				if(pdepth < 0 || cdepth < 0 || bdepth < 0) break;
				if(s.equals(";") && pdepth == 0 && cdepth == 0 && bdepth == 0){
					count = false;
					break;
				}
			}
			
			if(count){
				Expression expr = Expression();
				if(eat_parenthesis) assert_eat(")");
				if(eat_do) assert_eat("do");
				inLoop = true;
				loop = new ForCountStatement(p,expr,Statement());
			}else{
				List<Statement> init = new ArrayList<>();
				
				if(eat("var")){
					init.add(Statement());
				}else if(!Scope.isReservedWord(token) && isVariable(token) && tokens.get(pos+1).equals("=")){
					init.add(Statement());
				}else if(!token.equals(";"))
					throw new Error(position,"Invalid initializer statement");
				if(eat(",")) do{
					if(!Scope.isReservedWord(token) && isVariable(token) && tokens.get(pos+1).equals("=")){
						init.add(Statement());
					}else throw new Error(position,"Invalid initializer statement");
				}while(eat(","));
				
				if(!eat(";") && !tokens.get(pos-1).equals(";"))
					throw new Error(position,"Expected ; after initializer in for statement");
				
				Expression cond;
				
				if(!token.equals(";")){
					cond = Expression();
					assert_eat(";");
				}else{
					cond = new Literal(position,BooleanValue.TRUE);
					next();
				}
				
				List<Expression> incr = new ArrayList<>();
				
				if(!token.equals(")")) do{
					incr.add(Expression());
				}while(eat(","));
				if(eat_parenthesis) assert_eat(")");
				if(eat_do) assert_eat("do");
				
				inLoop = true;
				
				loop = new ForStatement(p,init,cond,incr,Statement());
			}
		}
		
		inLoop = b;
		return loop;
	}
	private FunctionLiteral Function(Position position2){
		List<FunctionLiteral.Parameter> params = new ArrayList<>();
		Map<String,Expression> includedVariables = null;
		if(eat("[")){
			if(!token.equals("]")){
				includedVariables = new HashMap<>();
				do{
					Expression e = Expression();
					if(e.getClass() == VariableExpression.class){
						includedVariables.put(((Reference)e).getName(), e);
					}else if(e.getClass() == AssignExpression.class){
						AssignExpression expr = (AssignExpression)e;
						includedVariables.put(expr.getName(), expr.value);
					}else throw new Error(position,"Invalid static function parameter "+e.getClass().getName());
				}while(eat(","));
			}
			assert_eat("]");
		}
		FunctionLiteral.Parameter variadicName = null;
		if(eat("(")){
			if(!token.equals(")")) do{
				FunctionLiteral.Parameter p = Parameter();
				if(token.equals("...") && pos+1 < tokens.size() && tokens.get(pos+1).equals(")")){
					if(p.immutable) throw new Error(position,"Variadic parameters cannot be declared immutable");
					next();
					variadicName = p;
				}else
					params.add(p);
			}while(eat(","));
			assert_eat(")");
		}
		if(eat("=")) return new FunctionLiteral(position2,params,includedVariables,Expression(),variadicName);
		else{
			assert_eat("{");
			List<Statement> statements = new ArrayList<Statement>();
			boolean b = inFunction;
			inFunction = true;
			
			while(!eat("}"))
				statements.add(Statement());
			
			inFunction = b;
			return new FunctionLiteral(position2,params,includedVariables,statements,variadicName);
		}
	}
	private FunctionLiteral.Parameter Parameter(){
		boolean constant = false, immutable = false;
		if(eat("const")){
			constant = true;
		}else if(eat("immutable")){
			immutable = true;
		}
		if(!isVariable(token)) throw new Error(position,"Expected parameter name, got "+token);
		String name = token;
		next();
		Expression defaultValue;
		if(eat("=")){
			defaultValue = Expression();
		}else defaultValue = Literal.UNDEFINED;
		
		return new FunctionLiteral.Parameter(name, defaultValue, constant, immutable);
	}
	private Expression Expression(){
		Expression e = Assignment();
		return e;
	}
	private Expression Assignment(){
		Expression e;
		Position p = position;
		if(eat("++=")){
			e = Conditional();
			if(e instanceof MemberReference){
				MemberReference ref = (MemberReference)e;
				e = new AbsoluteValueMemberAssignExpression(p,ref.expr, ref.getName());
			}else if(e instanceof Reference){
				Reference ref = (Reference)e;
				e = new AbsoluteValueAssignExpression(p,ref.getName());
			}
		}else if(eat("--=")){
			e = Conditional();
			if(e instanceof MemberReference){
				MemberReference ref = (MemberReference)e;
				e = new NegateMemberAssignExpression(p,ref.expr, ref.getName());
			}else if(e instanceof Reference){
				Reference ref = (Reference)e;
				e = new NegateAssignExpression(p,ref.getName());
			}
		}else if(eat("!!=")){
			e = Conditional();
			if(e instanceof MemberReference){
				MemberReference ref = (MemberReference)e;
				e = new NotMemberAssignExpression(p,ref.expr, ref.getName());
			}else if(e instanceof Reference){
				Reference ref = (Reference)e;
				e = new NotAssignExpression(p,ref.getName());
			}
		}else e = Conditional();
		for(;;){
			if(eat("=")){
				e = assign(lastPosition,e,Conditional(),null);
			}else if(eat("+=")){
				e = assign(lastPosition,e,Conditional(),Operators.plus);
			}else if(eat("-=")){
				e = assign(lastPosition,e,Conditional(),Operators.minus);
			}else if(eat("*=")){
				e = assign(lastPosition,e,Conditional(),Operators.times);
			}else if(eat("/=")){
				e = assign(lastPosition,e,Conditional(),Operators.divide);
			}else if(eat("%=")){
				e = assign(lastPosition,e,Conditional(),Operators.modulus);
			}else if(eat("^=")){
				e = assign(lastPosition,e,Conditional(),Operators.pow);
			}else if(eat("<>=")){
				e = assign(lastPosition,e,Conditional(),Operators.compareTo);
			}else if(eat("^^=")){ //TODO add the bitwise & logical operators into the overloadable system
				e = assign(lastPosition,e,new BitwiseXorExpression(lastPosition,e,Conditional()),null);
			}else if(eat("&=")){
				e = assign(lastPosition,e,new BitwiseAndExpression(lastPosition,e,Conditional()),null);
			}else if(eat("|=")){
				e = assign(lastPosition,e,new BitwiseOrExpression(lastPosition,e,Conditional()),null);
			}else if(eat("<<=")){
				e = assign(lastPosition,e,new BitShiftLeft(lastPosition,e,Conditional()),null);
			}else if(eat(">>=")){
				e = assign(lastPosition,e,new BitShiftRight(lastPosition,e,Conditional()),null);
			}else if(eat(">>>=")){
				e = assign(lastPosition,e,new BitShiftRightUnsigned(lastPosition,e,Conditional()),null);
			}else if(eat("&&=")){
				e = assign(lastPosition,e,new AndExpression(lastPosition,e,Conditional()),null);
			}else if(eat("||=")){
				e = assign(lastPosition,e,new OrExpression(lastPosition,e,Conditional()),null);
			}else return e;
		}
	}
	private Expression InlineFor(Position p, Expression init){ // TODO
		ForLoop loop = ForLoop(p, true);
		return new InlineFor(p, init,loop);
	}
	private Expression Conditional(){ // a? b : c
		Expression e = LogicOR();
		
		for(;;){
			Position p = position;
			if(eat("for")){
				e = InlineFor(p,e);
			}else if(eat("if")){
				Expression condition = Expression();
				Expression other;
				if(eat("else")){
					other = Conditional();
				}else other = new Literal(position,Constants.UNDEFINED);
				e = new ConditionalExpression(p,condition,e,other);		
			}else if(eat("defined")){
				e = new DefinedExpression(lastPosition,e,false);
			}else if(eat("!defined")){
				e = new DefinedExpression(lastPosition,e,true);
			}else for(;;){
				p = position;
				if(eat("?")){
					Expression t = Expression();
					assert_eat(":");
					Expression f = Conditional();
					e = new ConditionalExpression(p,e,t,f);
				}else return e;
			}
		}
	}
	private Expression LogicOR(){
		Expression e = LogicAND();
		for(;;){
			Position p = position;
			if(eat("||") || eat("or"))
				e = new OrExpression(p,e,LogicAND());
			else return e;
		}
	}
	private Expression LogicAND(){
		Expression e = BitwiseOR();
		for(;;){
			Position p = position;
			if(eat("&&") || eat("and"))
				e = new AndExpression(p,e,BitwiseOR());
			else return e;
		}
	}
	private Expression BitwiseOR(){
		Expression e = BitwiseXOR();
		for(;;){
			Position p = position;
			if(eat("|"))
				e = new BitwiseOrExpression(p,e,BitwiseXOR());
			else return e;
		}
	}
	private Expression BitwiseXOR(){
		Expression e = BitwiseAND();
		for(;;){
			Position p = position;
			if(eat("xor") || eat("^^"))
				e = new BitwiseXorExpression(p,e,BitwiseAND());
			else return e;
		}
	}
	private Expression BitwiseAND(){
		Expression e = Equality();
		for(;;){
			Position p = position;
			if(eat("&"))
				e = new BitwiseAndExpression(p,e,Equality());
			else return e;
		}
	}
	private Expression Equality(){
		Expression e = Comparison();
		for(;;){
			Position p = position;
			if(eat("==")){
				e = new EqualsExpression(p,e,Comparison());
			}else if(eat("!="))
				e = new NotExpression(p,new EqualsExpression(p,e,Comparison()));
			else if(eat("==="))
				e = new SameInstanceExpression(p,e,Comparison(), false);
			else if(eat("!=="))
				e = new SameInstanceExpression(p,e,Comparison(), true);
			else return e;
		}
	}
	private Expression Comparison(){
		Expression e = Shift();
		for(;;){
			Position p = position;
			if(eat("<"))
				e = new LessThanExpression(p,e,Shift());
			else if(eat("<="))
				e = new LessThanEqualExpression(p,e,Shift());
			else if(eat(">"))
				e = new GreaterThanExpression(p,e,Shift());
			else if(eat(">="))
				e = new GreaterThanEqualExpression(p,e,Shift());
			else if(eat("<>"))
				e = new CompareToExpression(p,e,Shift());
			else if(eat("instanceof"))
				e = new InstanceofExpression(p,e,Shift(),false);
			else if(eat("!instanceof"))
				e = new InstanceofExpression(p,e,Shift(),true);
			else if(eat("in"))
				e = new InExpression(p,e,Shift(),false);
			else if(eat("!in"))
				e = new InExpression(p,e,Shift(),true);
			else return e;
		}
	}
	private Expression Shift(){
		Expression e = AddSubtract();
		for(;;){
			Position p = position;
			if(eat("<<"))
				e = new BitShiftLeft(p,e,AddSubtract());
			else if(eat(">>"))
				e = new BitShiftRight(p,e,AddSubtract());
			else if(eat(">>>"))
				e = new BitShiftRightUnsigned(p,e,AddSubtract());
			else return e;
		}
	}
	private Expression AddSubtract(){
		Expression e = Multiply();
		for(;;){
			Position p = position;
			if(eat("+"))
				e = new AddExpression(p,e,Multiply());
			else if(eat("-"))
				e = new SubtractExpression(p,e,Multiply());
			else return e;
		}
	}
	private Expression Multiply(){
		Expression e = Exponent();
		for(;;){
			Position p = position;
			if(eat("*"))
				e = new MultiplyExpression(p,e,Exponent());
			else if(eat("/"))
				e = new DivideExpression(p,e,Exponent());
			else if(eat("%"))
				e = new ModuloExpression(p,e,Exponent());
			else return e;
		}
	}
	private Expression Exponent(){
		Expression e = Factor(false);
		for(;;){
			Position p = position;
			if(eat("^")){
				e = new ExponentiationExpression(p,e,Factor(false));
			}else return e;
		}
	}
	/*private static Reference getReference(Expression e){
		if(!(e instanceof Reference)) throw new Error(position,"Expected reference");
		return (Reference)e;
	}*/
	static boolean isString(String token){
		return token.length() > 1 && (token.charAt(0) == '"' || token.charAt(0) == '\'') && token.charAt(0) == token.charAt(token.length()-1); 
	}
	
	private static final Pattern numberPattern = Pattern.compile("(\\d+(\\.\\d+)?)|(\\.\\d+)"),
			variablePattern = Pattern.compile("\\w[\\w\\d_]*");
	static boolean isNumber(String token){
		return numberPattern.matcher(token).matches();
	}
	static boolean isVariable(String token){
		return variablePattern.matcher(token).matches() && !Scope.isReservedWord(token);
	}
	private Expression Factor(boolean returnOnFunctionCall){
		if(eat("!")) return new NotExpression(lastPosition,Factor(false));
		else if(eat("~")) return new BitwiseNotExpression(lastPosition,Factor(false));
		else if(eat("+")) return new AbsoluteValueExpression(lastPosition,Factor(false));
		else if(eat("++")){
			Position p = lastPosition;
			Expression e = Factor(false);
			return assign(p,e,new Literal(p,new NumberValue(1)),Operators.plus);
		}else if(eat("--")){
			Position p = lastPosition;
			Expression e = Factor(false);
			return assign(p,e,new Literal(p,new NumberValue(1)),Operators.minus);
		}else if(eat("typeof")) return new TypeofExpression(lastPosition,Factor(false));
		else if(eat("-")) return new NegateExpression(lastPosition,Factor(false));
		
		Expression e;
		Position p = position;
		if(isNumber(token)){
			e = new Literal(p,new NumberValue(Double.valueOf(token)));
			next();
		}else if(isString(token)){
			e = new Literal(p,new StringValue(token.substring(1, token.length()-1)));
			next();
		}else if(eat("(")){
			e = Expression();
			assert_eat(")");
		}else if(eat("[")){
			throw new Error(p,"special syntax for [] is not implemented yet");
		}else if(eat("const")){
			assert_eat("(");
			e = Expression();
			assert_eat(")");
			if(e instanceof MemberReference){
				e = new MemberConstExpression(p,(MemberReference)e);
			}else if(e instanceof Reference){
				e = new ConstExpression(p,(Reference)e);
			}else throw new Error(p,"Cannot set "+e.getClass().getName()+" const");
		}else if(eat("seal")){
			assert_eat("(");
			e = Expression();
			assert_eat(")");
			if(e instanceof MemberReference){
				e = new MemberSealExpression(p,(MemberReference)e);
			}else if(e instanceof Reference){
				e = new SealExpression(p,(Reference)e);
			}else throw new Error(p,"Cannot seal "+e.getClass().getName());
		}else if(eat("finalize")){
			assert_eat("(");
			e = Expression();
			assert_eat(")");
			if(e instanceof MemberReference){
				e = new MemberFinalizeExpression(p,(MemberReference)e);
			}else if(e instanceof Reference){
				e = new FinalizeExpression(p,(Reference)e);
			}else throw new Error(position,"Cannot finalize "+e.getClass().getName());
		}else if(eat("undefined")){
			e = new Literal(p,Constants.UNDEFINED);
		}else if(eat("null")){
			e = new Literal(p,(Value)null);
		}else if(eat("true")){
			e = new Literal(p,BooleanValue.TRUE);
		}else if(eat("false")){
			e = new Literal(p,BooleanValue.FALSE);
		}else if(eat("function")){
			e = Function(p);
		}else if(eat("new")){
			Expression ex = Factor(true);
			Expression type;
			List<Expression> params;
			if(ex instanceof FunctionCall){
				FunctionCall func = (FunctionCall)ex;
				type = func.function;
				params = func.params;
			}else{
				type = ex;
				params = new ArrayList<>();
			}
			return new NewInstance(p,type,params);
		}else if(eat("for")){
			e = InlineFor(p,null);
		}else if(eat("{",":","}")){
			e = new ObjectLiteral(p,new HashMap<String,Expression>());
		}else if(eat("{")){ // {} -> empty array, {:} -> empty object, {;} -> empty inline body statement, returns Ans
			int type = 0; // 0 = array, 1 = object, 2 = body
			int pdepth = 0, cdepth = 0, bdepth = 0, conditional = 0;
			for(int i = pos; i < tokens.size(); i++){
				String s = tokens.get(i);
				if(s.equals("(")) pdepth++;
				else if(s.equals(")")) pdepth--;
				else if(s.equals("{")) cdepth++;
				else if(s.equals("}")){
					if(cdepth == 0) break;
					else cdepth--;
				}else if(s.equals("[")) bdepth++;
				else if(s.equals("]")) bdepth--;
				else if(s.equals("?")) conditional++;
				else if(s.equals(":")){
					if(conditional == 0){
						type = 1;
						break;
					}else conditional--;
				}else if(s.equals(";") && pdepth == 0 && cdepth == 0 && bdepth == 0){
					type = 2; break;
				}
					
			}
			if(type == 0){
				List<Expression> values = new ArrayList<>();
				if(!token.equals("}")) do{
					values.add(Expression());
				}while(eat(","));
				assert_eat("}");
				e = new ArrayLiteralExpression(p,values);
			}else if(type == 1){
				Map<String, Expression> map = new HashMap<>();
				if(!token.equals("}")) do{
					String name = token;
					next();
					assert_eat(":");
					map.put(name, Expression());
				}while(eat(","));
				assert_eat("}");
				e = new ObjectLiteral(p,map);
			}else/*if(type == 2)*/{
				List<Statement> statements = new ArrayList<>();
				while(!eat("}")){
					statements.add(Statement());
				}
				e = new InlineBodyStatementExpression(p,statements);
			}
		}else if(isVariable(token) || token.equals("this")){
			e = new VariableExpression(p,token);
			next();
		}else throw new Error(p,"Invalid start of expression: "+token);
		
		return Post(e, returnOnFunctionCall);
	}
	
	private Expression Post(Expression e, boolean returnOnFunctionCall){
		for(;;){
			Position p = position;
			if(eat(".")){
				e = member(e);
				next();
			}else if(eat("?.")){
				e = new MemberNullCatchExpression(p,e,token);
				next();
			}else if(eat(".=")){ // TODO fix this to just use Factor() again. The interfaces are implemented, use them.
				Expression f = new MemberExpression(p,e,token);
				next();
				f = Post(f,true);
				e = assign(p,e,f,null);
			}else if(eat("[")){
				Expression i = Expression();
				if(eat(",")){
					Expression i2 = Expression();
					e = new IndexExpression(p,e,i,i2);
				}else e = new IndexExpression(p,e,i);
				assert_eat("]");
			}else if(eat("(")){
				List<Expression> params = new ArrayList<>();
				if(!token.equals(")")) do{
					Expression expr = Expression();
					if(eat("...")) expr = new UnpackExpression(lastPosition,expr);
					params.add(expr);
				}while(eat(","));
				assert_eat(")");
				e = new FunctionCall(p,e,params);
				if(returnOnFunctionCall) return e;
			}else if(eat("++")){
				e = new PostFixIncrementExpression(p,e,false);
			}else if(eat("--")){
				e = new PostFixIncrementExpression(p,e,true);
			}else if(eat("of")){
				Expression expr = LogicOR();
				if(eat("...")) expr = new UnpackExpression(lastPosition,expr);
				e = new FunctionCall(p,e,Arrays.asList(expr));
			}else return e;
		}
	}
	private Expression assign(Position p, Expression ex, Expression value, String operator){
		if(ex instanceof IndexExpression){
			IndexExpression idx = (IndexExpression)ex;
			IndexAssignExpression result = new IndexAssignExpression(p,value,idx.thingToIndex,idx.index,idx.index2,operator);
			result.range = idx.range;
			return result;
		}else if(ex instanceof IndexAssignExpression){
			IndexAssignExpression idx = (IndexAssignExpression)ex;
			IndexAssignExpression result = new IndexAssignExpression(p,assign(idx.position(),idx.value,value,operator),idx.thingToIndex,idx.index,idx.index2,idx.operator);
			result.range = idx.range;
			return result;
		}else if(ex instanceof AssignExpression){
			AssignExpression asg = (AssignExpression)ex;
			return new AssignExpression(p,asg.name,assign(asg.position(),asg.value,value,operator),asg.operator);
		}else if(ex instanceof MemberAssignExpression){
			MemberAssignExpression asg = (MemberAssignExpression)ex;
			return new MemberAssignExpression(p,asg.name,asg.expr,assign(asg.position(),asg.value,value,operator),asg.operator);
		}else if(ex instanceof MemberReference){
			MemberReference ref = (MemberReference)ex;
			return new MemberAssignExpression(p,ref.getName(),ref.expr,value,operator);
		}else if(ex instanceof Reference){
			return new AssignExpression(p,((Reference)ex).getName(),value,operator);
		}else throw new Error(p,"Cannot calculate assign expression from lhs of type "+ex.getClass().getName());
	}
	private Expression member(Expression e){
		String name = token;
		if(name.equals("class")) name = "constructor";
		if(isVariable(name)){
			return new MemberExpression(lastPosition,e,name);
		}else throw new Error(position,"Invalid member name '"+token+"'");
	}
	public static Program compile(String program){
		return new Program(new Compiler(program).compile());
	}
	public static Program compile(File f) throws FileNotFoundException{
		return new Program(new Compiler(f).compile());
	}
	public static Program compile(List<String> lines){
		return new Program(new Compiler(lines).compile());
	}
}
