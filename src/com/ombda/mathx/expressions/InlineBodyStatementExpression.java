package com.ombda.mathx.expressions;

import java.util.List;

import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.statements.BodyStatement;
import com.ombda.mathx.statements.Statement;
import com.ombda.mathx.values.Value;

public class InlineBodyStatementExpression extends Expression{
	protected List<Statement> statements;
	public InlineBodyStatementExpression(Position p, List<Statement> statements){
		super(p);
		this.statements = statements;
	}

	@Override
	public Value eval(Scope scope){
		scope.push();
		for(Statement s : statements){
			s.execute(scope);
		}
		Value result = scope.get("Ans");
		scope.pop();
		return result;
	}

}
