package com.ombda.mathx.expressions;

import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.Value;

public abstract class Expression{
	private final Position position;
	public Expression(Position p){
		position = p;
	}
	public final Position position(){
		return position;
	}
	public abstract Value eval(Scope scope);
}
