package com.ombda.mathx.expressions;

import java.util.Arrays;

import com.ombda.mathx.Operators;
import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.Constants;
import com.ombda.mathx.values.Value;

public class IndexExpression extends Expression{
	public Expression thingToIndex, index, index2;
	public boolean range;
	public IndexExpression(Position p, Expression thingToIndex, Expression index){
		super(p);
		range = false;
		this.thingToIndex = thingToIndex;
		this.index = index;
	}
	public IndexExpression(Position p, Expression thingToIndex, Expression i, Expression i2){
		super(p);
		range = true;
		this.thingToIndex = thingToIndex;
		index = i;
		index2 = i2;
	}
	@Override
	public Value eval(Scope scope){
		Value v = thingToIndex.eval(scope);
		if((thingToIndex instanceof MemberNullCatchExpression || thingToIndex instanceof MemberNullCatchAssignExpression) && (v == null || v == Constants.UNDEFINED)){
			return v;
		}
		if(range){
			return v.getMember(Operators.index).call(scope, Arrays.asList(index.eval(scope),index2.eval(scope)));
		}else{
			return v.getMember(Operators.index).call(scope, Arrays.asList(index.eval(scope)));
		}
	}
}
