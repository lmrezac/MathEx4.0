package com.ombda.mathx.expressions;

import java.util.Arrays;

import com.ombda.mathx.Operators;
import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.Value;

public class MultiplyExpression extends BinaryExpression{

	public MultiplyExpression(Position p, Expression l, Expression r){
		super(p, l, r);
	}
	@Override
	public Value eval(Scope scope){
		Value l = left.eval(scope);
		Value r = right.eval(scope);
		if(l.hasMember(Operators.times))
			return l.getMember(Operators.times).call(scope, Arrays.asList(r));
		else if(r.hasMember(Operators.rtimes))
			return r.getMember(Operators.rtimes).call(scope,Arrays.asList(l));
		else if(l.type.hasMember(Operators.times))
			return l.type.getMember(Operators.times).bind(l).call(scope,Arrays.asList(r));
		else if(r.type.hasMember(Operators.rtimes))
			return r.type.getMember(Operators.rtimes).bind(r).call(scope, Arrays.asList(l));
		else throw new RuntimeException("Invalid operands for operator *: "+l.type+" & "+r.type);
	}

}
