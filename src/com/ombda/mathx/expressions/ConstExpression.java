package com.ombda.mathx.expressions;

import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.Value;

public class ConstExpression extends Reference{
	private Reference ref;
	
	public ConstExpression(Position p, Reference ref){
		super(p);
		this.ref = ref;
	}
	
	
	@Override
	public Value eval(Scope scope){
		scope.setConstant(ref.getName());
		return ref.eval(scope);
	}

	@Override
	public String getName(){
		return ref.getName();
	}

}
