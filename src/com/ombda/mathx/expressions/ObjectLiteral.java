package com.ombda.mathx.expressions;

import java.util.Map;

import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.values.ObjectValue;
import com.ombda.mathx.values.Value;

public class ObjectLiteral extends Expression{
	private Map<String,Expression> values;
	public ObjectLiteral(Position p, Map<String,Expression> map){
		super(p);
		values = map;
	}
	@Override
	public Value eval(Scope scope){
		ObjectValue obj = new ObjectValue();
		for(Map.Entry<String, Expression> entry : values.entrySet()){
			obj.setMember(entry.getKey(), entry.getValue().eval(scope), false);
		}
		return obj;
	}
}
