package com.ombda.mathx.statements;

import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;

public class Stop extends Statement{
	private int exit_status;
	public Stop(Position p, int exit_status){
		super(p);
		this.exit_status = exit_status;
	}
	@Override
	public void execute(Scope scope){
		System.exit(exit_status);
	}
	
	public String toString(){
		return "stop "+exit_status;
	}
}
