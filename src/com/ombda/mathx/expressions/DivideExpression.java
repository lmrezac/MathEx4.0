package com.ombda.mathx.expressions;

import java.util.Arrays;

import com.ombda.mathx.Operators;
import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.Value;

public class DivideExpression extends BinaryExpression{

	public DivideExpression(Position p, Expression l, Expression r){
		super(p, l, r);
	}
	@Override
	public Value eval(Scope scope){
		Value l = left.eval(scope);
		Value r = right.eval(scope);
		if(l.hasMember(Operators.divide))
			return l.getMember(Operators.plus).call(scope, Arrays.asList(r));
		else if(r.hasMember(Operators.rdivide))
			return r.getMember(Operators.rdivide).call(scope,Arrays.asList(l));
		else if(l.type.hasMember(Operators.divide))
			return l.type.getMember(Operators.divide).bind(l).call(scope,Arrays.asList(r));
		else if(r.type.hasMember(Operators.rdivide))
			return r.type.getMember(Operators.rdivide).bind(r).call(scope, Arrays.asList(l));
		else throw new RuntimeException("Invalid operands for operator /: "+l.type+" & "+r.type);
	}

}
