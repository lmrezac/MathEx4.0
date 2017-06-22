package com.ombda.mathx.expressions;

import java.util.ArrayList;

import com.ombda.mathx.Operators;
import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.Value;

public class AbsoluteValueExpression extends UnaryExpression{

	public AbsoluteValueExpression(Position p, Expression v){
		super(p, v);
	}

	@Override
	public Value eval(Scope scope){
		Value v = this.value.eval(scope);
		if(v.hasMember(Operators.positate))
			return v.getMember(Operators.positate).call(scope, new ArrayList<>());
		else if(v.type.hasMember(Operators.positate))
			return v.type.getMember(Operators.positate).bind(v).call(scope, new ArrayList<>());
		throw new RuntimeException("Invalid operand for operator +: "+v.type);
	}
	
}
