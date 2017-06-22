package com.ombda.mathx.expressions;

import com.ombda.mathx.Position;

public abstract class BinaryExpression extends Expression{
	protected Expression left, right;
	public BinaryExpression(Position p, Expression l, Expression r){
		super(p);
		left = l;
		right = r;
	}
	
}
