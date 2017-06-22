package com.ombda.mathx.expressions;

import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.ArrayValue;
import com.ombda.mathx.values.BooleanValue;
import com.ombda.mathx.values.Constants;
import com.ombda.mathx.values.Function;
import com.ombda.mathx.values.NumberValue;
import com.ombda.mathx.values.StringValue;
import com.ombda.mathx.values.Value;

public class TypeofExpression extends UnaryExpression{

	public TypeofExpression(Position p, Expression v){
		super(p, v);
	}

	@Override
	public Value eval(Scope scope){
		Value v = this.value.eval(scope);
		return new StringValue(
			v == null || v == Constants.UNDEFINED? "undefined" : 
			v instanceof StringValue? "string" :
			v instanceof NumberValue? "number" : 
			v instanceof BooleanValue? "boolean" :
			v instanceof Function? "function" :
			v instanceof ArrayValue? "array" :
			"object"
		);
	}

}
