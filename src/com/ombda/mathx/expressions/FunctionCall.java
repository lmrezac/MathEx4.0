package com.ombda.mathx.expressions;

import java.util.ArrayList;
import java.util.List;

import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.ArrayValue;
import com.ombda.mathx.values.Constants;
import com.ombda.mathx.values.Value;

public class FunctionCall extends Expression{
	public Expression function;
	public List<Expression> params;
	public FunctionCall(Position p, Expression f, List<Expression> pa){
		super(p);
		function = f;
		params = pa;
	}
	@Override
	public Value eval(Scope scope){
		Value v;
		
		v = function.eval(scope);
		if((function instanceof MemberNullCatchExpression || function instanceof MemberNullCatchAssignExpression) && (v == null || v == Constants.UNDEFINED)){
			return v;
		}
		List<Value> params = new ArrayList<>();
		
		for(int i = 0; i < this.params.size(); i++){
			Expression expr = this.params.get(i);
			Value v2 = expr.eval(scope);
			if(expr instanceof UnpackExpression){
				params.addAll(((ArrayValue)v2).values);
			}else
				params.add(v2);
		}
		return v.call(scope, params);
	}
}
