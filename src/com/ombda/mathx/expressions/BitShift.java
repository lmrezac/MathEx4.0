package com.ombda.mathx.expressions;

import com.ombda.mathx.Position;
import com.ombda.mathx.values.NumberValue;
import com.ombda.mathx.values.Value;

public abstract class BitShift extends BinaryExpression{
	protected String operator;
	public BitShift(Position p, Expression l, Expression r){
		super(p, l, r);
	}
	
	protected int getInt(Value v){
		if(!(v instanceof NumberValue)) throw new RuntimeException("Invalid operand for operator "+operator+": "+v.type);
		double d = ((NumberValue)v).value;
		if(d != (int)d) throw new RuntimeException("Invalid operand for operator "+operator+": float");
		return (int)d;
	}

}
