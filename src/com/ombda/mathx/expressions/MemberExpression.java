package com.ombda.mathx.expressions;

import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.Value;

public class MemberExpression extends MemberReference{
	public String name;
	
	public MemberExpression(Position p, Expression e, String s){
		super(p, e);
		name = s;
	}
	
	@Override
	public Value evalObject(Scope scope, Value obj){
		return obj.getMember(name);
	}

	@Override
	public String getName(){
		return name;
	}

	@Override
	protected Value setObject(Value v){
		return object = v;
	}

}
