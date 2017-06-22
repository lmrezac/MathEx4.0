package com.ombda.mathx.expressions;

import java.util.Arrays;

import com.ombda.mathx.Operators;
import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.BooleanValue;
import com.ombda.mathx.values.Type;
import com.ombda.mathx.values.Value;

public class ConditionalExpression extends Expression{
	private Expression condition, ifTrue, ifFalse;
	public ConditionalExpression(Position p, Expression c, Expression t, Expression f){
		super(p);
		condition = c;
		ifTrue = t;
		ifFalse = f;
	}
	@Override
	public Value eval(Scope scope){
		return (Type.BOOLEAN.getMember(Operators.cast).call(scope, Arrays.asList(this.condition.eval(scope))) == BooleanValue.TRUE)? ifTrue.eval(scope) : ifFalse.eval(scope);
	}
}
