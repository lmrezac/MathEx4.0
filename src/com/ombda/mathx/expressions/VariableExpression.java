package com.ombda.mathx.expressions;

import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.Value;

public class VariableExpression extends Reference{
	String name;
	public VariableExpression(Position p, String name){
		super(p);
		this.name = name;
	}
	@Override
	public String getName(){
		return name;
	}
	@Override
	public Value eval(Scope scope){
		return scope.get(name);
	}

}
