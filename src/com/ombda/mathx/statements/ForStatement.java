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

public class ForStatement extends ForLoop{
	private List<Statement> init;
	private List<Expression> increment;
	private Expression condition;
	private Statement body;
	
	public ForStatement(Position p, List<Statement> init, Expression cond, List<Expression> incr, Statement s){
		super(p);
		this.init = init;
		condition = cond;
		increment = incr;
		body = s;
		params.add(null);
	}
	private List<Value> params = new ArrayList<>();
	private List<Value> setValue(Value v){
		params.set(0, v);
		return params;
	}
	
	@Override
	public void execute(Scope scope){
		
		Value bool = Type.BOOLEAN.getMember(Operators.cast);
		
		scope.push();
		
		try{
			for(Statement statement : init)
				statement.execute(scope);
			
			while(bool.call(scope, setValue(condition.eval(scope))) == BooleanValue.TRUE){
				try{
					
					body.execute(scope);
					for(Expression e : increment){
						e.eval(scope);
					}
				}catch(Continue c){}
			}
			
		}catch(Break b){}
		
		scope.pop();
	}
	
	public String toString(){
		String result = "for(";
		for(Statement s : init){
			result += s+",";
		}
		if(result.endsWith(",")) result = result.substring(0, result.length()-1);
		result += ";"+condition+";";
		for(Expression expr : increment){
			result += expr + ",";
		}
		if(result.endsWith(",")) result = result.substring(0, result.length()-1);
		result += ")\n"+body;
		return result;
	}

}
