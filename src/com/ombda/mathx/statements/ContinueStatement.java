package com.ombda.mathx.statements;

import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.errors.Continue;

public class ContinueStatement extends Statement{

	public ContinueStatement(Position p){
		super(p);
	}

	@Override
	public void execute(Scope scope){
		throw new Continue();
	}
	
	public String toString(){
		return "continue";
	}

}
