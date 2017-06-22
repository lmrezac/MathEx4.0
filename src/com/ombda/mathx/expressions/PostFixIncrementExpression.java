package com.ombda.mathx.expressions;

import java.util.Arrays;

import com.ombda.mathx.Operators;
import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.NumberValue;
import com.ombda.mathx.values.Value;

public class PostFixIncrementExpression extends Expression{
	private Expression value;
	private boolean decrement;
	public PostFixIncrementExpression(Position p, Expression e, boolean decrement){
		super(p);
		this.value = e;
		this.decrement = decrement;
	}
	
	@Override
	public Value eval(Scope scope){
		if(value instanceof Reference){
			String name = ((Reference)value).getName();
			Value v = scope.get(name);
			if(!(v instanceof NumberValue)) throw new RuntimeException("Invalid operand for operator "+(decrement? "--" : "++")+": "+v.type);
			scope.set(name, v.getMember(decrement? Operators.minus : Operators.plus).call(scope, Arrays.asList(new NumberValue(1))));
			return v;
		}else if(value instanceof MemberExpression){
			MemberExpression ex = (MemberExpression)value;
			Value obj = ex.expr.eval(scope);
			Value v = obj.getMember(ex.name);
			if(!(v instanceof NumberValue)) throw new RuntimeException("Invalid operand for operator "+(decrement? "--" : "++")+": "+v.type);
			obj.setMember(ex.name, new NumberValue(((NumberValue)v).value-1), false);
			return v;
		}else if(value.getClass() == IndexExpression.class){
			throw new RuntimeException("I just can't do it");
		}else throw new RuntimeException("Invalid operand for operator "+(decrement? "--" : "++")+": "+value.eval(scope).type);
	}

}
