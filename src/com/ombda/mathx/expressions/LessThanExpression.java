package com.ombda.mathx.expressions;

import java.util.Arrays;

import com.ombda.mathx.Operators;
import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.BooleanValue;
import com.ombda.mathx.values.NumberValue;
import com.ombda.mathx.values.Value;

public class LessThanExpression extends BinaryExpression{

	public LessThanExpression(Position p, Expression l, Expression r){
		super(p, l, r);
	}

	@Override
	public Value eval(Scope scope){
		Value v1 = this.left.eval(scope);
		Value v2 = this.right.eval(scope);
		return ((NumberValue)v1.getMember(Operators.compareTo).call(scope,Arrays.asList(v2))).value < 0? BooleanValue.TRUE : BooleanValue.FALSE;
	}

}
