package com.ombda.mathx.statements;

import java.util.List;

import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.expressions.Expression;
import com.ombda.mathx.values.ObjectValue;
import com.ombda.mathx.values.Value;

public class WithStatement extends Statement{
	private Expression object;
	private List<Statement> statements;
	
	public WithStatement(Position p, Expression obj, List<Statement> statements){
		super(p);
		this.object = obj;
		this.statements = statements;
	}
	
	@Override
	public void execute(Scope scope){
		Value v = object.eval(scope);
		if(!(v instanceof ObjectValue)) throw new RuntimeException("Cannot use with statement with type "+v.type);
		scope.with(((ObjectValue)v).variables);
		for(Statement statement : statements)
			statement.execute(scope);
		scope.pop();
	}

}
