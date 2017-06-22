package com.ombda.mathx.expressions;

import java.util.Arrays;

import com.ombda.mathx.Operators;
import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.BooleanValue;
import com.ombda.mathx.values.Type;
import com.ombda.mathx.values.Value;

public class InExpression extends BinaryExpression{
	public final boolean not;
	
	public InExpression(Position p, Expression l, Expression r, boolean not){
		super(p, l, r);
		this.not = not;
	}

	@Override
	public Value eval(Scope scope){
		Value v = right.eval(scope).getMember(Operators.in).call(scope, Arrays.asList(left.eval(scope)));
		if(!(v instanceof BooleanValue)){
			v = Type.BOOLEAN.getMember(Operators.cast).call(scope, Arrays.asList(v));
		}
		if(not)
			v = v == BooleanValue.TRUE? BooleanValue.FALSE : BooleanValue.TRUE;
		return v;
	}

}
