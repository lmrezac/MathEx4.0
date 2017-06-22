package com.ombda.mathx.expressions;

import java.util.Arrays;

import com.ombda.mathx.Operators;
import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.BooleanValue;
import com.ombda.mathx.values.Type;
import com.ombda.mathx.values.Value;

public class AndExpression extends BinaryExpression{

	public AndExpression(Position p, Expression l, Expression r){
		super(p, l, r);
	}

	@Override
	public Value eval(Scope scope){
		Value l = left.eval(scope);
		if(Type.BOOLEAN.getMember(Operators.cast).call(scope, Arrays.asList(l)) == BooleanValue.FALSE) return l;
		return right.eval(scope);
	}

}
