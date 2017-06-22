package com.ombda.mathx.expressions;

import java.util.Arrays;

import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.Constants;
import com.ombda.mathx.values.Value;

public class MemberAssignExpression extends MemberReference{
	public final Expression value;
	public final String name, operator;
	public final boolean operate;
	public MemberAssignExpression(Position p, String name, Expression ex, Expression val, String operator){
		super(p, ex);
		this.name = name;
		if(Scope.isReservedWord(name)) throw new RuntimeException("Cannot use "+name+", it is a reserved word");
		value = val;
		operate = operator != null;
		this.operator = operator;
	}
	@Override
	public Value evalObject(Scope scope, Value obj){
		Value val = value.eval(scope);
		Value result;

		if(operate){
			Value op = obj.getMember(name).getMember(operator);
			result = op.call(scope, Arrays.asList(val));
		}else{
			result = val;
		}
		
		obj.setMember(name, result, false);
		return result;
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
