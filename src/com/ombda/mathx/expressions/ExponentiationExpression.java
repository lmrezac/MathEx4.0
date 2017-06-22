package com.ombda.mathx.expressions;

import java.util.Arrays;

import com.ombda.mathx.Operators;
import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.Value;

public class ExponentiationExpression extends BinaryExpression{

	public ExponentiationExpression(Position p, Expression l, Expression r){
		super(p, l, r);
	}
	@Override
	public Value eval(Scope scope){
		Value l = left.eval(scope);
		Value r = right.eval(scope);
		if(l.hasMember(Operators.pow))
			return l.getMember(Operators.pow).call(scope, Arrays.asList(r));
		else if(r.hasMember(Operators.rpow))
			return r.getMember(Operators.rpow).call(scope,Arrays.asList(l));
		else if(l.type.hasMember(Operators.pow))
			return l.type.getMember(Operators.pow).bind(l).call(scope,Arrays.asList(r));
		else if(r.type.hasMember(Operators.rpow))
			return r.type.getMember(Operators.rpow).bind(r).call(scope, Arrays.asList(l));
		else throw new RuntimeException("Invalid operands for operator ^: "+l.type+" & "+r.type);
	}

}
