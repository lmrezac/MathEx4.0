package com.ombda.mathx.expressions;

import java.util.Arrays;

import com.ombda.mathx.Operators;
import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.Value;

public class AddExpression extends BinaryExpression{

	public AddExpression(Position p, Expression l, Expression r){
		super(p, l, r);
	}
	@Override
	public Value eval(Scope scope){
		Value l = left.eval(scope);
		Value r = right.eval(scope);
		if(l.hasMember(Operators.plus))
			return l.getMember(Operators.plus).call(scope, Arrays.asList(r));
		else if(r.hasMember(Operators.rplus))
			return r.getMember(Operators.rplus).call(scope,Arrays.asList(l));
		else if(l.type.hasMember(Operators.plus))
			return l.type.getMember(Operators.plus).bind(l).call(scope,Arrays.asList(r));
		else if(r.type.hasMember(Operators.rplus))
			return r.type.getMember(Operators.rplus).bind(r).call(scope, Arrays.asList(l));
		else throw new RuntimeException("Invalid operands for operator +: "+l.type+" & "+r.type);
	}

}
