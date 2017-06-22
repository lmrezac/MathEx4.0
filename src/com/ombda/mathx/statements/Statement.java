package com.ombda.mathx.statements;

import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;

public abstract class Statement{
	private final Position position;
	public Statement(Position p){
		position = p;
	}
	public final Position position(){
		return position;
	}
	public abstract void execute(Scope scope);
}
