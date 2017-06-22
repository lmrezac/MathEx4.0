package com.ombda.mathx.expressions;

import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.NumberValue;
import com.ombda.mathx.values.Value;

public class BitwiseOrExpression extends BinaryExpression{

	public BitwiseOrExpression(Position p, Expression l, Expression r){
		super(p, l, r);
	}

	@Override
	public Value eval(Scope scope){
		Value v1 = this.left.eval(scope);
		Value v2 = this.right.eval(scope);
		if(!(v1 instanceof NumberValue) || !(v2 instanceof NumberValue))
			throw new RuntimeException("Invalid operands for operator |: "+v1.type+" & "+v2.type);
		double d1 = ((NumberValue)v1).value;
		double d2 = ((NumberValue)v2).value;
		if(d1 != (int)d1)
			throw new RuntimeException("Invalid operands for operator |: float & "+v2.type);
		if(d2 != (int)d2)
			throw new RuntimeException("Invalid operands for operator |: "+v1.type+" & "+v2.type);
		return new NumberValue((int)d1 | (int)d2);
	}

}
