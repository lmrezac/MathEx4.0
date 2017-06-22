package com.ombda.mathx.expressions;

import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.ArrayValue;
import com.ombda.mathx.values.Value;

public class UnpackExpression extends Expression{
	private Expression expr;
	public UnpackExpression(Position p, Expression expr){
		super(p);
		this.expr = expr;
	}
	@Override
	public Value eval(Scope scope){
		Value v = expr.eval(scope);
		if(!(v instanceof ArrayValue)) throw new RuntimeException("Cannot unpack "+v.type);
		
		return v;
	}
	
}
