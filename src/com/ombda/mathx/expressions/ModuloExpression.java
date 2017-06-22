package com.ombda.mathx.expressions;

import java.util.Arrays;

import com.ombda.mathx.Operators;
import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.Value;

public class ModuloExpression extends BinaryExpression{

	public ModuloExpression(Position p, Expression l, Expression r){
		super(p, l, r);
	}
	@Override
	public Value eval(Scope scope){
		Value l = left.eval(scope);
		Value r = right.eval(scope);
		if(l.hasMember(Operators.modulus))
			return l.getMember(Operators.modulus).call(scope, Arrays.asList(r));
		else if(r.hasMember(Operators.rmodulus))
			return r.getMember(Operators.rmodulus).call(scope,Arrays.asList(l));
		else if(l.type.hasMember(Operators.modulus))
			return l.type.getMember(Operators.modulus).bind(l).call(scope,Arrays.asList(r));
		else if(r.type.hasMember(Operators.rmodulus))
			return r.type.getMember(Operators.rmodulus).bind(r).call(scope, Arrays.asList(l));
		else throw new RuntimeException("Invalid operands for operator %: "+l.type+" & "+r.type);
	}

}
