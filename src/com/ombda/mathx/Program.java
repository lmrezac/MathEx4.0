package com.ombda.mathx;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import com.ombda.mathx.statements.Statement;
import com.ombda.mathx.values.BooleanValue;
import com.ombda.mathx.values.Constants;
import com.ombda.mathx.values.Function;
import com.ombda.mathx.values.NumberValue;
import com.ombda.mathx.values.ObjectValue;
import com.ombda.mathx.values.StringValue;
import com.ombda.mathx.values.Type;
import com.ombda.mathx.values.Value;

public class Program{
	private List<Statement> statements;
	private Scope scope;
	private Value redirected = null;
	public Program(List<Statement> statements){
		this.statements = statements;
		scope = new Scope();
		Value print, write;
		scope.put("print", print = new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				if(redirected != null)
					return redirected.getMember("print").call(s, params);
				if(params.isEmpty()){
					System.out.println();
					return null;
				}
				for(Value v : params){
					//Value result = v.getMember()
					System.out.println(v);
				}
				return null;
			}
		}); scope.setConstant("print");
		print.setMember("redirect", new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) return redirected = null;
				redirected = params.get(0);
				return null;
			}
		}, true);
		scope.put("write", write = new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				if(redirected != null)
					return redirected.getMember("write").call(s, params);
				if(params.isEmpty()){
					return null;
				}
				for(Value v : params)
					System.out.print(v);
				return null;
			}
		}); scope.setConstant("write");
		write.setMember("redirect", print.getMember("redirect"), true);
		scope.put("Number", Type.NUMBER); scope.setConstant("Number");
		scope.put("Boolean", Type.BOOLEAN); scope.setConstant("Boolean");
		scope.put("String", Type.STRING); scope.setConstant("String");
		scope.put("Type", Type.TYPE); scope.setConstant("Type");
		scope.put("Function", Type.FUNCTION); scope.setConstant("Function");
		scope.put("Object", Type.OBJECT); scope.setConstant("Object");
		scope.put("Array", Type.ARRAY); scope.setConstant("Array");
		scope.put("Ans", Constants.UNDEFINED);
		ObjectValue math = new ObjectValue();
		math.setMember("abs",new Function(){
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				return params.get(0).getMember(Operators.positate).call(s, new ArrayList<>());
			}
		}, true);
		math.setMember("min", new Function(){
			public Value call(Scope s, List<Value> params){
				if(params.size() <= 1) throw new RuntimeException("Invalid number of params, expected 2");
				return new NumberValue(Math.min(((NumberValue)params.get(0)).value,((NumberValue)params.get(1)).value));
			}
		}, true);
		math.setMember("max", new Function(){
			public Value call(Scope s, List<Value> params){
				if(params.size() <= 1) throw new RuntimeException("Invalid number of params, expected 2");
				return new NumberValue(Math.max(((NumberValue)params.get(0)).value,((NumberValue)params.get(1)).value));
			}
		}, true);
		math.setMember("sqrt",new Function(){
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				return new NumberValue(Math.sqrt(((NumberValue)params.get(0)).value));
			}
		}, true);
		math.setMember("sin",new Function(){
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				return new NumberValue(Math.sin(((NumberValue)params.get(0)).value));
			}
		}, true);
		math.setMember("cos",new Function(){
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				return new NumberValue(Math.cos(((NumberValue)params.get(0)).value));
			}
		}, true);
		math.setMember("tan",new Function(){
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				return new NumberValue(Math.tan(((NumberValue)params.get(0)).value));
			}
		}, true);
		math.setMember("csc",new Function(){
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				return new NumberValue(1.0/Math.sin(((NumberValue)params.get(0)).value));
			}
		}, true);
		math.setMember("sec",new Function(){
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				return new NumberValue(1.0/Math.cos(((NumberValue)params.get(0)).value));
			}
		}, true);
		math.setMember("cot",new Function(){
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				return new NumberValue(1.0/Math.tan(((NumberValue)params.get(0)).value));
			}
		}, true);
		math.setMember("asin",new Function(){
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				return new NumberValue(Math.asin(((NumberValue)params.get(0)).value));
			}
		}, true);
		math.setMember("acos",new Function(){
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				return new NumberValue(Math.acos(((NumberValue)params.get(0)).value));
			}
		}, true);
		math.setMember("atan",new Function(){
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				if(params.size() >= 2) return new NumberValue(Math.atan2(((NumberValue)params.get(0)).value,((NumberValue)params.get(1)).value));
				return new NumberValue(Math.atan(((NumberValue)params.get(0)).value));
			}
		}, true);
		math.setMember("acsc",new Function(){
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				return new NumberValue(Math.asin(1.0/((NumberValue)params.get(0)).value));
			}
		}, true);
		math.setMember("asec",new Function(){
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				return new NumberValue(Math.acos(1.0/((NumberValue)params.get(0)).value));
			}
		}, true);
		math.setMember("acot",new Function(){
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				if(params.size() >= 2) return new NumberValue(Math.atan2(((NumberValue)params.get(1)).value,((NumberValue)params.get(0)).value));
				return new NumberValue(Math.atan(1.0/((NumberValue)params.get(0)).value));
			}
		}, true);
		math.setMember("rint",new Function(){
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				return new NumberValue(Math.rint(((NumberValue)params.get(0)).value));
			}
		}, true);
		math.setMember("ceil",new Function(){
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				return new NumberValue(Math.ceil(((NumberValue)params.get(0)).value));
			}
		}, true);
		math.setMember("floor",new Function(){
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				return new NumberValue(Math.floor(((NumberValue)params.get(0)).value));
			}
		}, true);
		math.setMember("round",new Function(){
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				double d = ((NumberValue)params.get(0)).value;
				if(params.size() >= 2){
					int precision = (int)Math.pow(10, (int)((NumberValue)params.get(1)).value);
					return new NumberValue(Math.round(d*precision)/precision);
				}
				return new NumberValue(Math.round(((NumberValue)params.get(0)).value));
			}
		}, true);
		math.setMember("ulp",new Function(){
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				return new NumberValue(Math.ulp(((NumberValue)params.get(0)).value));
			}
		}, true);
		math.setMember("signum",new Function(){
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				return new NumberValue(Math.signum(((NumberValue)params.get(0)).value));
			}
		}, true);
		math.setMember("sinh",new Function(){
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				return new NumberValue(Math.sinh(((NumberValue)params.get(0)).value));
			}
		}, true);
		math.setMember("cosh",new Function(){
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				return new NumberValue(Math.cosh(((NumberValue)params.get(0)).value));
			}
		}, true);
		math.setMember("tanh",new Function(){
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				return new NumberValue(Math.tanh(((NumberValue)params.get(0)).value));
			}
		}, true);
		math.setMember("ln",new Function(){
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				return new NumberValue(Math.log(((NumberValue)params.get(0)).value));
			}
		}, true);
		math.setMember("log",new Function(){
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				double d = ((NumberValue)params.get(0)).value;
				if(params.size() == 2){
					double d2 = ((NumberValue)params.get(1)).value;
					return new NumberValue(Math.log(d2)/Math.log(d));
				}
				return new NumberValue(Math.log10(d));
			}
		}, true);
		math.setMember("cbrt",new Function(){
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				return new NumberValue(Math.cbrt(((NumberValue)params.get(0)).value));
			}
		}, true);
		math.setMember("E", new NumberValue(Math.E), true);
		math.setMember("PI", new NumberValue(Math.PI), true);
		math.setMember("e", math.getMember("E"), true);
		math.setMember("pi", math.getMember("PI"), true);
		math.setMember("Infinity", new NumberValue(Double.POSITIVE_INFINITY), true);
		math.setMember("NaN", new NumberValue(Double.NaN), true);
		math.setMember("isNaN",new Function(){
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				return Double.isNaN(((NumberValue)params.get(0)).value)? BooleanValue.TRUE : BooleanValue.FALSE;
			}
		}, true);
		scope.put("Math", math);
		scope.setConstant("Math");
		scope.set("isImmutable", new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				return params.get(0).isImmutable()? BooleanValue.TRUE : BooleanValue.FALSE;
			}
		});
		scope.set("input", new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				if(params.size() != 0){
					write.call(s, params);
				}
				Scanner scan = new Scanner(System.in);
				String input;
				try{
					input = scan.nextLine();
				}catch(NoSuchElementException e){
					input = scan.next();
				}
				scan.close();
				return new StringValue(input);
			}
		}); scope.setConstant("input");
	}
	public Scope getScope(){
		return scope;
	}
	public void run(){
		for(Statement statement : statements){
			statement.execute(scope);
		}
	}
}
