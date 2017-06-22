package com.ombda.mathx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.ombda.mathx.errors.Error;
public class MacroFunction extends Macro{
	private List<Token> definition;
	private Map<String, List<Token>> variables = new HashMap<>();
	private String[] paramNames;
	public MacroFunction(String name, String[] paramNames, List<Token> definition){
		super(name);
		this.definition = hashtags(definition);
		this.paramNames = paramNames;
	}

	@Override
	public List<Token> replacement(){
		List<Token> tokens = new ArrayList<>(this.definition);
		
		for(int i = tokens.size()-1; i >= 0; i--){
			Token t = tokens.get(i);
			List<Token> testResult = variableTest(t,variables);
			if(testResult != null){
				tokens.remove(i);
				tokens.addAll(i, testResult);
			}
		}
		return tokens;
	}

	@Override
	public int matches(List<Token> tokens, int position){
		variables.clear();
		int startpos = position;
		if(tokens.get(position++).equals(getName())){
			if(!tokens.get(position++).equals("(")) return -1;//throw new Error(tokens.get(position-1).position(),"Invalid macro call: Expected ( here");
			//List<List<Token>> params = new ArrayList<>();
			//int size = 0;
			int i;
			for(i = 0; i < this.paramNames.length; i++){
				List<Token> param = eatParam(tokens,position);
				variables.put(paramNames[i], param);
				position += param.size();
				if(i < this.paramNames.length-1 && !tokens.get(position).equals(","))
					return -1;//throw new Error(tokens.get(position).position(),"Invalid macro call: Expected , here");
				position++;
			}
			if(!tokens.get(position-1).equals(")"))
				return -1;//throw new Error(tokens.get(position-1).position(),"Invalid macro call: Expected ) here, got "+tokens.get(position-1));
			//position++;
			int size = position - startpos;
			return size;
		}else return -1;
	}
	
	private List<Token> eatParam(List<Token> tokens, int position){
		List<Token> result = new ArrayList<>();
		int pdepth = 0, cdepth = 0, bdepth = 0;
		while(position < tokens.size() && (!tokens.get(position).equals(",") || pdepth != 0 || cdepth != 0 || bdepth != 0)){
			Token t = tokens.get(position);
			if(t.equals("(")) pdepth++;
			else if(t.equals(")")) pdepth--;
			else if(t.equals("[")) bdepth++;
			else if(t.equals("]")) bdepth--;
			else if(t.equals("{")) cdepth++;
			else if(t.equals("}")) cdepth--;
			
			if(pdepth < 0) return result;
			
			position++;
			
			result.add(t);
		}
		return result;
	}
	public String toString(){
		return getName()+Arrays.toString(this.paramNames).replace('[','(').replace(']',')')+" -> "+tokenString(definition);
	}
}
