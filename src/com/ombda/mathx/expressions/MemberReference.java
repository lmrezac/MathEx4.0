package com.ombda.mathx.expressions;

import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.Value;

public abstract class MemberReference extends Reference{
	protected Value object;
	public Expression expr;
	
	public MemberReference(Position p, Expression expr){
		super(p);
		this.expr = expr;
	}
	
	protected abstract Value evalObject(Scope scope, Value object);
	public final Value eval(Scope scope){
		return evalObject(scope,setObject(expr.eval(scope)));
	}
	
	protected abstract Value setObject(Value v);
	
}
