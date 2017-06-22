package com.ombda.mathx;

import java.util.List;

import com.ombda.mathx.errors.Error;

public class ExpressionEater{
	private Token token;
	private int pos;
	private final List<Token> tokens;
	public ExpressionEater(List<Token> tokens){
		this.tokens = tokens;
	}
	private void next(){
		pos++;
		if(pos >= tokens.size()){
			pos = tokens.size();
		}else token = tokens.get(pos);
	}
	private boolean eat(String str){
		if(token.equals(str)){
			next();
			return true;
		}else return false;
	}
	private boolean assert_eat(String str){
		if(!eat(str)) throw new Error(token.position(),"Expected ), got "+token);
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
	public int parse(int start, int exprType){
		pos = start - 1;
		next();
		if(exprType == -1) Factor(false);
		else if(exprType == 0) Factor(true);
		else if(exprType == 1) Exponent();
		else if(exprType == 2) Multiply();
		else if(exprType == 3) AddSubtract();
		else if(exprType == 4) Shift();
		else if(exprType == 5) Comparison();
		else if(exprType == 6) Equality();
		else if(exprType == 7) BitwiseOR();
		else if(exprType == 8) LogicOR();
		else if(exprType == 9) Conditional();
		else Expression();
		return pos;
	}
	private void Expression(){
		Assignment();
	}
	private void Assignment(){
		if(eat("++=") || eat("--=") || eat("!!="));
		Conditional();
		
		while(eat("=") || eat("+=") || eat("-=") || eat("*=") || eat("/=") || eat("%=") || eat("^=") 
			|| eat("<>=") || eat("^^=") || eat("&=") || eat("|=") || eat("<<=") || eat(">>=")
			|| eat(">>>=") || eat("&&=") || eat("||="))
				Conditional();
		
	}
	private void InlineFor(){
		boolean parenthesis = eat("(");
		if(parenthesis){
			int depth = 1;
			while(depth > 0){
				if(token.equals("(")) depth++;
				else if(token.equals(")")) depth--;
				next();
			}
			assert_eat("do");
		}else{
			int pdepth = 0, cdepth = 0, bdepth = 0, fdepth = 0;
			while(!eat("do") || pdepth != 0 || cdepth != 0 || bdepth != 0 || fdepth != 0){
				if(token.equals("(")) pdepth++;
				else if(token.equals(")")) pdepth--;
				else if(token.equals("{")) cdepth++;
				else if(token.equals("}")) cdepth--;
				else if(token.equals("[")) bdepth++;
				else if(token.equals("]")) bdepth--;
				else if(token.equals("for")) fdepth++;
				else if(token.equals("do") && fdepth != 0) fdepth--; 
				next();
			}
		}
		if(eat("{")){
			Body();
		}else Expression();
	}
	private void Body(){
		int depth = 1;
		while(depth > 0){
			if(token.equals("{")) depth++;
			else if(token.equals("}")) depth--;
			next();
		}
	}
	private void Conditional(){ // a? b : c
		LogicOR();
		
		for(;;){
			if(eat("for")){
				InlineFor();
			}else if(eat("if")){
				Expression();
				if(eat("else"))
					Conditional();
			}else if(eat("defined") || eat("!defined"));
			else for(;;){
				if(eat("?")){
					Expression();
					assert_eat(":");
					Conditional();
				}else return;
			}
		}
	}
	private void LogicOR(){
		LogicAND();
		while(eat("||") || eat("or"))
			LogicAND();
	}
	private void LogicAND(){
		BitwiseOR();
		while(eat("&&") || eat("and"))
			BitwiseOR();
	}
	private void BitwiseOR(){
		BitwiseXOR();
		while(eat("|"))
			BitwiseXOR();
	}
	private void BitwiseXOR(){
		BitwiseAND();
		while(eat("xor") || eat("^^"))
			BitwiseAND();
	}
	private void BitwiseAND(){
		Equality();
		while(eat("&"))
			Equality();
	}
	private void Equality(){
		Comparison();
		while(eat("==") || eat("!=") || eat("===") || eat("!=="))
			Comparison();
	}
	private void Comparison(){
		Shift();
		while(eat("<") || eat("<=") || eat(">") || eat(">=") || eat("<>")
			|| eat("instanceof") || eat("!instanceof") || eat("in") || eat("!in"))
			Shift();
	}
	private void Shift(){
		AddSubtract();
		while(eat("<<") || eat(">>") || eat(">>>"))
			AddSubtract();
	}
	private void AddSubtract(){
		Multiply();
		while(eat("+") || eat("-"))
			Multiply();
	}
	private void Multiply(){
		Exponent();
		while(eat("*") || eat("/") || eat("%"))
			Exponent();
	}
	private void Exponent(){
		Factor(true);
		while(eat("^"))
			Factor(true);
	}
	private void Factor(boolean post){
		if(eat("!") || eat("~") || eat("+") || eat("-") || eat("++") || eat("--") || eat("typeof") || eat("new"));
		
		Position p = token.position();
		if(Compiler.isNumber(token.toString()) || Compiler.isString(token.toString()) || Compiler.isVariable(token.toString())){
			next();
		}else if(eat("(")){
			Expression();
			assert_eat(")");
		}else if(eat("[")){
			throw new Error(p,"special syntax for [] is not implemented yet");
		}else if(eat("const") || eat("seal") || eat("finalize")){
			assert_eat("(");
			Expression();
			assert_eat(")");
		}else if(eat("undefined") || eat("null") || eat("true") || eat("false") || eat("{",":","}") || eat("this"));
		else if(eat("function")){
			Function();
		}else if(eat("for")){
			InlineFor();
		}else if(eat("{")){ // {} -> empty array, {:} -> empty object, {;} -> empty inline body statement, returns Ans
			Body();
		}else return;//throw new Error(p,"Invalid start of expression: "+token);
		
		if(post)
			Post();
	}
	
	private void Post(){
		for(;;){
			if(eat(".") || eat("?.")){
				next();
			}else if(eat(".=")){
				next();
				Post();
			}else if(eat("[")){
				Expression();
				if(eat(","))
					Expression();
				assert_eat("]");
			}else if(eat("(")){
				if(!token.equals(")")) do{
					Expression();
					eat("...");
				}while(eat(","));
				assert_eat(")");
			}else if(eat("++") || eat("--"));
			else if(eat("of")){
				LogicOR();
				eat("...");
			}else return;
		}
	}
	private void Function(){
		if(eat("[")){
			if(!token.equals("]")){
				do Expression(); while(eat(","));
			}
			assert_eat("]");
		}
		if(eat("(")){
			if(!token.equals(")")) do{
				Parameter();
				if(token.equals("...") && pos+1 < tokens.size() && tokens.get(pos+1).equals(")"))
					next();
			}while(eat(","));
			assert_eat(")");
		}
		if(eat("=")){
			Expression();
			return;
		}
		else{
			assert_eat("{");
			
		}
	}
	private void Parameter(){
		boolean constant = false, immutable = false;
		if(eat("const") || eat("immutable"));
		if(!Compiler.isVariable(token.toString())) throw new Error(token.position(),"Expected parameter name, got "+token);
		next();
		if(eat("="))
			Expression();
	}
}
