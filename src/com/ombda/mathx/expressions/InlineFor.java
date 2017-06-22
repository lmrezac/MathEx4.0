package com.ombda.mathx.expressions;

import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.statements.ForLoop;
import com.ombda.mathx.values.Value;

public class InlineFor extends Expression{
	private Expression init;
	private ForLoop loop;
	public InlineFor(Position p, ForLoop loop){
		this(p, null,loop);
	}
	public InlineFor(Position p, Expression init, ForLoop loop){
		super(p);
		this.init = init;
		this.loop = loop;
	}
	
	@Override
	public Value eval(Scope scope){
		scope.push();
		if(init != null){
			scope.set("Ans", init.eval(scope));
		}
		
		loop.execute(scope);
		
		scope.pop();
		
		return scope.get("Ans");
	}

}
