package com.ombda.mathx.statements;

import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.errors.Break;

public class BreakStatement extends Statement{

	public BreakStatement(Position p){
		super(p);
	}

	@Override
	public void execute(Scope scope){
		throw new Break();
	}
	
	public String toString(){
		return "break";
	}

}
