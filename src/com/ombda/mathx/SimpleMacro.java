package com.ombda.mathx;

import java.util.List;

public class SimpleMacro extends Macro{
	private List<Token> definition;
	public SimpleMacro(String name, List<Token> def){
		super(name);
		definition = def;
	}
	@Override
	public List<Token> replacement(){
		return definition;
	}
	@Override
	public int matches(List<Token> tokens, int position){
		return tokens.get(position).equals(getName())? definition.size() : -1;
	}
	public String toString(){
		return getName()+" -> "+tokenString(definition);
	}
}
