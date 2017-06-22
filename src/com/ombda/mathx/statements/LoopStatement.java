package com.ombda.mathx.statements;

import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.errors.Break;
import com.ombda.mathx.errors.Continue;

public class LoopStatement extends Statement{
	private Statement body;
	public LoopStatement(Position p, Statement s){
		super(p);
		body = s;
	}
	@Override
	public void execute(Scope scope){
		try{
			for(;;){
				try{
					body.execute(scope);
				}catch(Continue c){}
			}
		}catch(Break b){}
	}

}
