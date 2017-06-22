package com.ombda.mathx.expressions;

import java.util.ArrayList;

import com.ombda.mathx.Operators;
import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.Value;

public class NegateExpression extends UnaryExpression{

	public NegateExpression(Position p, Expression v){
		super(p, v);
	}

	@Override
	public Value eval(Scope scope){
		Value v = this.value.eval(scope);
		if(v.hasMember(Operators.negate))
			return v.getMember(Operators.negate).call(scope, new ArrayList<>());
		else if(v.type.hasMember(Operators.negate))
			return v.type.getMember(Operators.negate).bind(v).call(scope, new ArrayList<>());
		throw new RuntimeException("Invalid operand for operator "+Operators.getSymbol(Operators.negate)+": "+v.type);
	}
	
}
