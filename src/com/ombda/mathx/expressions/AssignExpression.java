package com.ombda.mathx.expressions;

import java.util.Arrays;

import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.Value;

public class AssignExpression extends Reference{
	public final String name, operator;
	public final Expression value;
	public final boolean operate;
	public AssignExpression(Position p, String name, Expression value, String operator){
		super(p);
		if(name.equals("Ans")) throw new RuntimeException("Cannot assign to Ans");
		if(Scope.isReservedWord(name)) throw new RuntimeException("Cannot use "+name+", it is a reserved word");
		this.name = name;
		this.value = value;
		this.operate = operator != null;
		this.operator = operator;
	}
	@Override
	public Value eval(Scope scope){
		Value val = value.eval(scope);
		Value result;
		if(operate){
			Value variable = scope.get(name);
			result = variable.getMember(operator).call(scope,Arrays.asList(val));
		}else{
			result = val;
		}

		scope.set(name, result);
		return result;
	}
	@Override
	public String getName(){
		return name;
	}
}
