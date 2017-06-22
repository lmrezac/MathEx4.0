package com.ombda.mathx.expressions;

import java.util.Arrays;

import com.ombda.mathx.Operators;
import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.BooleanValue;
import com.ombda.mathx.values.Type;
import com.ombda.mathx.values.Value;

public class NotAssignExpression extends Reference{
	private String name;
	public NotAssignExpression(Position p, String name){
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
		Value result = Type.BOOLEAN.getMember(Operators.cast).call(scope, Arrays.asList(v)) == BooleanValue.TRUE? BooleanValue.FALSE : BooleanValue.TRUE;
		
		scope.set(name, result);
		return result;
	}

}
