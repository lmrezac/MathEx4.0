package com.ombda.mathx.expressions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ombda.mathx.Position;
import com.ombda.mathx.Scope;
import com.ombda.mathx.statements.ReturnStatement;
import com.ombda.mathx.statements.Statement;
import com.ombda.mathx.values.Function;
import com.ombda.mathx.values.Value;

public class FunctionLiteral extends Expression{
	private List<Statement> statements;
	private Map<String,Expression> statics;
	private List<Parameter> params;
	private Parameter variadicName;
	public FunctionLiteral(Position p, List<Parameter> params, Map<String,Expression> statics, List<Statement> statements, Parameter variadicName){
		super(p);
		this.statements = statements;
		this.params = params;
		this.statics = statics;
		this.variadicName = variadicName;
	}
	public FunctionLiteral(Position p, List<Parameter> params, Map<String,Expression> statics, Expression expr, Parameter variadicName){
		this(p, params, statics, Arrays.asList(new ReturnStatement(expr.position(),expr)),variadicName);
	}
	@Override
	public Value eval(Scope scope){
		List<Function.Param> params = new ArrayList<>();
		for(Parameter param : this.params){
			params.add(new Function.Param(param.name, param.value.eval(scope), param.constant, param.immutable));
		}
		Map<String,Value> statics = null;
		if(this.statics != null){
			statics = new HashMap<>();
			for(Map.Entry<String, Expression> entry : this.statics.entrySet()){
				statics.put(entry.getKey(), entry.getValue().eval(scope));
			}
		}
		return new Function(this.statements,params,statics,variadicName == null? null : new Function.Param(variadicName.name, variadicName.value.eval(scope), variadicName.constant, variadicName.immutable));
	}
	
	public static class Parameter{
		public final String name;
		public final Expression value;
		public final boolean constant, immutable;
		public Parameter(String name, Expression value, boolean constant, boolean immutable){
			this.name = name;
			this.value = value;
			this.constant = constant || immutable;
			this.immutable = immutable;
		}
	}

}
