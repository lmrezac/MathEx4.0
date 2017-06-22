package com.ombda.mathx.statements;

import java.util.List;

import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.errors.Break;
import com.ombda.mathx.errors.Continue;
import com.ombda.mathx.expressions.Expression;
import com.ombda.mathx.values.ArrayValue;
import com.ombda.mathx.values.Constants;
import com.ombda.mathx.values.StringValue;
import com.ombda.mathx.values.Value;

public class ForEachStatement extends ForLoop{
	private String name;
	private boolean create;
	private Expression list;
	private Statement body;
	
	public ForEachStatement(Position p, String name, boolean create, Expression list, Statement b){
		super(p);
		this.name = name;
		this.create = create;
		this.list = list;
		body = b;
	}
	
	@Override
	public void execute(Scope scope){
		if(create){
			scope.push();
			scope.create(name, Constants.UNDEFINED);
		}
		
		Value v = list.eval(scope);
		try{
			if(v instanceof StringValue){
				String str = v.toString();
				
				for(int i = 0; i < str.length(); i++){
					scope.set(name, new StringValue(Character.toString(str.charAt(i))));
					try{
						body.execute(scope);
					}catch(Continue c){}
				}
			}else{
				if(!(v instanceof ArrayValue)) throw new RuntimeException("Cannot iterate over "+v.type);
				
				List<Value> values = ((ArrayValue)v).values;
				
				for(Value var : values){
					scope.set(name, var);
					try{
						body.execute(scope);
					}catch(Continue c){}
				}
			}
		}catch(Break b){}
		
		if(create)
			scope.pop();
	}

}
