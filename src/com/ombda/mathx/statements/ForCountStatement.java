package com.ombda.mathx.statements;

import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.errors.Break;
import com.ombda.mathx.errors.Continue;
import com.ombda.mathx.expressions.Expression;
import com.ombda.mathx.values.NumberValue;
import com.ombda.mathx.values.Value;

public class ForCountStatement extends ForLoop{
	private Expression count;
	private Statement body;
	public ForCountStatement(Position p, Expression e, Statement b){
		super(p);
		count = e;
		body = b;
	}
	
	@Override
	public void execute(Scope scope){
		Value v = count.eval(scope);
		
		if(!(v instanceof NumberValue)) throw new RuntimeException("Cannot count with "+v.type);
		
		double d = ((NumberValue)v).value;
		
		if(d != (int)d) throw new RuntimeException("Can only loop with an integer, not a float");
		
		int times = (int)d;
		
		if(times < 0) throw new RuntimeException("Can only loop with a POSITIVE integer");
		
		try{
			for(int count = 0; count < times; count++){
				try{
					body.execute(scope);
				}catch(Continue c){}
			}
		}catch(Break b){}
	}
	
	public String toString(){
		return "for("+count+")\n"+body;
	}

}
