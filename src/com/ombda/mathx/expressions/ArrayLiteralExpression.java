package com.ombda.mathx.expressions;

import java.util.ArrayList;
import java.util.List;

import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.ArrayValue;
import com.ombda.mathx.values.Value;

public class ArrayLiteralExpression extends Expression{
	private List<Expression> exprs;
	public ArrayLiteralExpression(Position p, List<Expression> l){
		super(p);
		exprs = l;
	}
	@Override
	public Value eval(Scope scope){
		List<Value> values = new ArrayList<>();
		for(Expression ex : exprs){
			values.add(ex.eval(scope));
		}
		return new ArrayValue(values);
	}
}
