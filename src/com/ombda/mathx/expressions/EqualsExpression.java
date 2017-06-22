package com.ombda.mathx.expressions;

import java.util.Arrays;

import com.ombda.mathx.Operators;
import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.Value;

public class EqualsExpression extends BinaryExpression{

	public EqualsExpression(Position p, Expression l, Expression r){
		super(p, l, r);
	}

	@Override
	public Value eval(Scope scope){
		return left.eval(scope).getMember(Operators.equals).call(scope, Arrays.asList(right.eval(scope)));
	}

}
