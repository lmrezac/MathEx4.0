package com.ombda.mathx.statements;

import java.util.List;

import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;

public class BodyStatement extends Statement{
	protected List<Statement> statements;
	public BodyStatement(Position p, List<Statement> statements){
		super(p);
		this.statements = statements;
	}
	
	@Override
	public void execute(Scope scope){
		scope.push();
		for(Statement statement : statements){
			statement.execute(scope);
		}
		scope.pop();
	}

}
