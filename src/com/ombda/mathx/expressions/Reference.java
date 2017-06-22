package com.ombda.mathx.expressions;

import com.ombda.mathx.Position;

public abstract class Reference extends Expression{
	public Reference(Position p){
		super(p);
	}

	public abstract String getName();
}
