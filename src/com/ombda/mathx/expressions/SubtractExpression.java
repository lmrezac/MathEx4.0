package com.ombda.mathx.expressions;

import java.util.Arrays;

import com.ombda.mathx.Operators;
import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.Value;

public class SubtractExpression extends BinaryExpression{

	public SubtractExpression(Position p, Expression l, Expression r){
		super(p, l, r);
	}
	@Override
	public Value eval(Scope scope){
		Value l = left.eval(scope);
		Value r = right.eval(scope);
		if(l.hasMember(Operators.minus))
			return l.getMember(Operators.minus).call(scope, Arrays.asList(r));
		else if(r.hasMember(Operators.rminus))
			return r.getMember(Operators.rminus).call(scope,Arrays.asList(l));
		else if(l.type.hasMember(Operators.minus))
			return l.type.getMember(Operators.minus).bind(l).call(scope,Arrays.asList(r));
		else if(r.type.hasMember(Operators.rminus))
			return r.type.getMember(Operators.rminus).bind(r).call(scope, Arrays.asList(l));
		else throw new RuntimeException("Invalid operands for operator -: "+l.type+" & "+r.type);
	}

}
