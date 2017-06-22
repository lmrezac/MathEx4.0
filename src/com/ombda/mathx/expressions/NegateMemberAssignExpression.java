package com.ombda.mathx.expressions;

import java.util.ArrayList;

import com.ombda.mathx.Operators;
import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.Value;

public class NegateMemberAssignExpression extends MemberReference{
	private String name;
	public NegateMemberAssignExpression(Position p, Expression expr, String name){
		super(p, expr);
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
		if(v.hasMember(Operators.negate))
			result = v.getMember(Operators.negate).call(scope, new ArrayList<>());
		else if(v.type.hasMember(Operators.negate))
			result = v.type.getMember(Operators.negate).bind(v).call(scope, new ArrayList<>());
		else throw new RuntimeException("Invalid operand for operator "+Operators.getSymbol(Operators.negateAssign)+": "+v.type);
		
		obj.setMember(name, result, false);
		
		return result;
	}
	@Override
	protected Value setObject(Value v){
		return object = v;
	}

}
