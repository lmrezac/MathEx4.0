package com.ombda.mathx.expressions;

import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.Constants;
import com.ombda.mathx.values.Value;

public class Literal extends Expression{
	private Value value;
	public static final Literal UNDEFINED = new Literal(null,Constants.UNDEFINED){public Value eval(Scope s){return Constants.UNDEFINED;}};
	public Literal(Position p, Value v){
		super(p);
		value = v;
	}
	public Value eval(Scope s){
		return this.value;
	}
}
