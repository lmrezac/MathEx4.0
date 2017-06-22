package com.ombda.mathx.statements;

import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.errors.Return;
import com.ombda.mathx.expressions.Expression;
import com.ombda.mathx.values.Value;

public class ReturnStatement extends Statement{
	private Expression expr;
	public ReturnStatement(Position p, Expression expr){
		super(p);
		this.expr = expr;
	}
	@Override
	public void execute(Scope scope){
		Value v = expr.eval(scope);
		throw new Return(position(),v);
	}

}
