package com.ombda.mathx.expressions;

import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.NumberValue;
import com.ombda.mathx.values.Value;

public class BitwiseNotExpression extends UnaryExpression{

	public BitwiseNotExpression(Position p, Expression v){
		super(p, v);
	}

	@Override
	public Value eval(Scope scope){
		Value v = this.value.eval(scope);
		if(!(v instanceof NumberValue))
			throw new RuntimeException("Invalid operands for operator ~: "+v.type);
		double d = ((NumberValue)v).value;
		if(d != (int)d)
			throw new RuntimeException("Invalid operands for operator ~: float");
		return new NumberValue(~(int)d);
	}

}
