package com.ombda.mathx.expressions;

import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.NumberValue;
import com.ombda.mathx.values.Value;

public class BitShiftRight extends BitShift{

	public BitShiftRight(Position p, Expression l, Expression r){
		super(p, l, r);
		operator = ">>";
	}

	@Override
	public Value eval(Scope scope){
		return new NumberValue(getInt(left.eval(scope)) >> getInt(right.eval(scope)));
	}
	
}
