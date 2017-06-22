package com.ombda.mathx.statements;

import com.ombda.mathx.Scope;

public class EmptyStatement extends Statement{
	public static final Statement INSTANCE = new EmptyStatement();
	
	private EmptyStatement(){
		super(null);
	}
	
	@Override
	public void execute(Scope scope){}

}
