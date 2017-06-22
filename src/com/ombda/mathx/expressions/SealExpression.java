package com.ombda.mathx.expressions;

import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.Value;

public class SealExpression extends Reference{
	private Reference ref;
	
	public SealExpression(Position p, Reference ref){
		super(p);
		this.ref = ref;
	}
	
	
	@Override
	public Value eval(Scope scope){
		Value v = ref.eval(scope);
		v.seal();
		return v;
	}

	@Override
	public String getName(){
		return ref.getName();
	}

}
