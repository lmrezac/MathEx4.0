package com.ombda.mathx.expressions;

import java.util.Arrays;

import com.ombda.mathx.Operators;
import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.BooleanValue;
import com.ombda.mathx.values.NumberValue;
import com.ombda.mathx.values.Value;

public class SameInstanceExpression extends BinaryExpression{
	public final boolean not;
	public SameInstanceExpression(Position p, Expression l, Expression r, boolean not){
		super(p, l, r);
		this.not = not;
	}

	@Override
	public Value eval(Scope scope){
		Value v1 = this.left.eval(scope);
		Value v2 = this.right.eval(scope);
		boolean b;
		if(v1 instanceof NumberValue && v2 instanceof NumberValue)
			b = ((NumberValue)v1).value == ((NumberValue)v2).value;
		else b = v1 == v2;
		if(not) b = !b;
		return b? BooleanValue.TRUE : BooleanValue.FALSE;
	}

}
