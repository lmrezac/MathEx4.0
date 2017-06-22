package com.ombda.mathx.errors;

import com.ombda.mathx.Position;
import com.ombda.mathx.values.Value;

public class Return extends Error{

	private static final long serialVersionUID = -5966103563957362359L;

	public Return(Position p, Value v){
		super(p, v);
	}

}
