package com.ombda.mathx.statements;

import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;

public abstract class ForLoop extends Statement{
	public ForLoop(Position p){
		super(p);
	}

	public abstract void execute(Scope scope);
}
