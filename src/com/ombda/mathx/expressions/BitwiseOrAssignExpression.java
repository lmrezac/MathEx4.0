package com.ombda.mathx.expressions;

import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.Value;

public class BitwiseOrAssignExpression extends BitwiseOrExpression{
	
	public BitwiseOrAssignExpression(Position p, Expression l, Expression r){
		super(p, l, r);
	}
	
	@Override
	public Value eval(Scope s){
		return null;
	}

}
