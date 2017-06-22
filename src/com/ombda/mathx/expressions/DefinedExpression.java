package com.ombda.mathx.expressions;

import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.BooleanValue;
import com.ombda.mathx.values.Constants;
import com.ombda.mathx.values.Value;

public class DefinedExpression extends UnaryExpression{
	public final boolean not;
	public DefinedExpression(Position p, Expression v, boolean not){
		super(p, v);
		this.not = not;
	}

	@Override
	public Value eval(Scope scope){
		boolean result;
		if(value instanceof VariableExpression){
			result = scope.containsKey(((VariableExpression)value).name);
		}else result = value.eval(scope) == Constants.UNDEFINED;
		if(not) result = !result;
		return result? BooleanValue.TRUE : BooleanValue.FALSE;
	}

}
