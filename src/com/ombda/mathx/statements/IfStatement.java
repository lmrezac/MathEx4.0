package com.ombda.mathx.statements;

import java.util.Arrays;

import com.ombda.mathx.Operators;
import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.expressions.Expression;
import com.ombda.mathx.values.BooleanValue;
import com.ombda.mathx.values.Type;
import com.ombda.mathx.values.Value;

public class IfStatement extends Statement{
	private Expression condition;
	private Statement ifTrue, ifFalse;
	
	public IfStatement(Position p, Expression cond, Statement t, Statement f){
		super(p);
		condition = cond;
		ifTrue = t;
		ifFalse = f;
	}
	
	@Override
	public void execute(Scope scope){
		Value b = condition.eval(scope);
		if(Type.BOOLEAN.getMember(Operators.cast).call(scope, Arrays.asList(b)) == BooleanValue.TRUE){
			ifTrue.execute(scope);
		}else if(ifFalse != null){
			ifFalse.execute(scope);
		}
	}

}
