package com.ombda.mathx.expressions;

import java.util.ArrayList;
import java.util.List;

import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.Type;
import com.ombda.mathx.values.Value;

public class NewInstance extends Expression{
	private Expression type;
	private List<Expression> params;
	public NewInstance(Position p, Expression type, List<Expression> params){
		super(p);
		this.type = type;
		this.params = params;
	}
	
	@Override
	public Value eval(Scope scope){
		Value v = type.eval(scope);
		if(!(v instanceof Type)) throw new RuntimeException(v.toString()+" is not a type.");
		List<Value> args = new ArrayList<>();
		Type t = (Type)v;
		for(Expression e : params)
			args.add(e.eval(scope));
		
		return t.newInstance(scope, args);
	}

}
