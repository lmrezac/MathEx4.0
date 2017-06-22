package com.ombda.mathx.expressions;

import java.util.ArrayList;

import com.ombda.mathx.Operators;
import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.Value;

public class AbsoluteValueMemberAssignExpression extends MemberReference{
	private String name;
	public AbsoluteValueMemberAssignExpression(Position p, Expression expr, String name){
		super(p,expr);
		this.name = name;
	}
	@Override
	public String getName(){
		return name;
	}

	@Override
	protected Value evalObject(Scope scope, Value obj){
		Value result;
		Value v = obj.getMember(name);
		if(v.hasMember(Operators.positate))
			result = v.getMember(Operators.positate).call(scope, new ArrayList<>());
		else if(v.type.hasMember(Operators.positate))
			result = v.type.getMember(Operators.positate).bind(v).call(scope, new ArrayList<>());
		else throw new RuntimeException("Invalid operand for operator "+Operators.getSymbol(Operators.positateAssign)+": "+v.type);
		
		obj.setMember(name, result, false);
		
		return result;
	}
	@Override
	protected Value setObject(Value v){
		return object = v;
	}

}
