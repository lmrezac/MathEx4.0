package com.ombda.mathx.expressions;

import java.util.Arrays;

import com.ombda.mathx.Operators;
import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.BooleanValue;
import com.ombda.mathx.values.Type;
import com.ombda.mathx.values.Value;

public class NotExpression extends UnaryExpression{

	public NotExpression(Position p, Expression v){
		super(p, v);
	}

	@Override
	public Value eval(Scope scope){
		Value v = this.value.eval(scope);
		Value result = Type.BOOLEAN.getMember(Operators.cast).call(scope, Arrays.asList(v));
		if(result == BooleanValue.TRUE) return BooleanValue.FALSE;
		return BooleanValue.TRUE;
	}
	
}
