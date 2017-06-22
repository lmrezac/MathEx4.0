package com.ombda.mathx.expressions;

import java.util.ArrayList;
import java.util.Arrays;

import com.ombda.mathx.Operators;
import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.BooleanValue;
import com.ombda.mathx.values.Type;
import com.ombda.mathx.values.Value;

public class NotMemberAssignExpression extends MemberReference{
	private String name;
	public NotMemberAssignExpression(Position p, Expression expr, String name){
		super(p, expr);
		this.name = name;
	}
	@Override
	public String getName(){
		return name;
	}

	@Override
	protected Value evalObject(Scope scope, Value obj){
		Value v = obj.getMember(name);
		Value result = Type.BOOLEAN.getMember(Operators.cast).call(scope, Arrays.asList(v)) == BooleanValue.TRUE? BooleanValue.FALSE : BooleanValue.TRUE;
		
		obj.setMember(name, result, false);
		
		return result;
	}
	@Override
	protected Value setObject(Value v){
		return object = v;
	}

}
