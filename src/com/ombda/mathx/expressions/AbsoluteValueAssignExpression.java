package com.ombda.mathx.expressions;

import java.util.ArrayList;

import com.ombda.mathx.Operators;
import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.Value;

public class AbsoluteValueAssignExpression extends Reference{
	private String name;
	public AbsoluteValueAssignExpression(Position p, String name){
		super(p);
		this.name = name;
	}
	@Override
	public String getName(){
		return name;
	}
	
	@Override
	public Value eval(Scope scope){
		Value v = scope.get(name);
		Value result;
		if(v.hasMember(Operators.positate))
			result = v.getMember(Operators.positate).call(scope, new ArrayList<>());
		else if(v.type.hasMember(Operators.positate))
			result = v.type.getMember(Operators.positate).bind(v).call(scope, new ArrayList<>());
		else throw new RuntimeException("Invalid operand for operator "+Operators.getSymbol(Operators.positateAssign)+": "+v.type);
		scope.set(name, result);
		return result;
	}

}
