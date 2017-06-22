package com.ombda.mathx.expressions;

import java.util.Arrays;

import com.ombda.mathx.Operators;
import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.Value;

public class CompareToExpression extends BinaryExpression{

	public CompareToExpression(Position p, Expression l, Expression r){
		super(p, l, r);
	}

	@Override
	public Value eval(Scope scope){
		return left.eval(scope).getMember(Operators.compareTo).call(scope, Arrays.asList(right.eval(scope)));
	}

}
