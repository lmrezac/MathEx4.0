package com.ombda.mathx.statements;

import java.util.ArrayList;
import java.util.List;

import com.ombda.mathx.Operators;
import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.errors.Break;
import com.ombda.mathx.errors.Continue;
import com.ombda.mathx.expressions.Expression;
import com.ombda.mathx.values.BooleanValue;
import com.ombda.mathx.values.Type;
import com.ombda.mathx.values.Value;

public class WhileStatement extends Statement{
	private Expression condition;
	private Statement statement;
	private boolean until;
	public WhileStatement(Position p, Expression condition, Statement statement, boolean until){
		super(p);
		this.condition = condition;
		this.statement = statement;
		params.add(null);
		this.until = until;
	}
	
	private List<Value> params = new ArrayList<>();
	
	private List<Value> setParams(Value v){
		params.set(0,v);
		return params;
	}
	@Override
	public void execute(Scope scope){
		Value cast = Type.BOOLEAN.getMember(Operators.cast);
		BooleanValue bool = until? BooleanValue.FALSE : BooleanValue.TRUE;
		try{
			while(cast.call(scope, setParams(condition.eval(scope))) == bool){
				try{
					statement.execute(scope);
				}catch(Continue c){}
			}
		}catch(Break b){}
	}

	public String toString(){
		String result = (until? "until" : "while") + "(" + condition + ")\n"+statement;
		return result;
	}
}
