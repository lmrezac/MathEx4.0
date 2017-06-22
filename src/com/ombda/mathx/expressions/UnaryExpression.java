package com.ombda.mathx.expressions;

import com.ombda.mathx.Position;

public abstract class UnaryExpression extends Expression{
	protected Expression value;
	public UnaryExpression(Position p, Expression v){
		super(p);
		value = v;
	}
}
