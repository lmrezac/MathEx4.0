package com.ombda.mathx.expressions;

import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.Constants;
import com.ombda.mathx.values.Value;

public class MemberNullCatchExpression extends MemberReference{
	public String name;
	
	public MemberNullCatchExpression(Position p, Expression e, String s){
		super(p, e);
		name = s;
	}
	
	@Override
	public Value evalObject(Scope scope, Value obj){
		return obj == null || obj == Constants.UNDEFINED? obj : obj.getMember(name);
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
