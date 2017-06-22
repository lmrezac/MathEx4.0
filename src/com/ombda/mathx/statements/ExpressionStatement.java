package com.ombda.mathx.statements;

import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.expressions.Expression;

public class ExpressionStatement extends Statement{
	private Expression expr;
	public ExpressionStatement(Position p, Expression expr){
		super(p);
		this.expr = expr;
	}
	@Override
	public void execute(Scope scope){
		scope.set("Ans", expr.eval(scope));
	}
	
}
