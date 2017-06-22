package com.ombda.mathx.values;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ombda.mathx.Operators;
import com.ombda.mathx.Scope;

public class Type extends ObjectValue{
	public static final Function toStringBasic, equalsBasic;
	public static final Type TYPE, BOOLEAN, NUMBER, STRING, FUNCTION, OBJECT, ARRAY;
	static{
		
		TYPE = new Type("Type"){
			public Value newInstance(Scope s, List<Value> params){
				throw new RuntimeException("Cannot instantiate type Type");
			}
		};
		TYPE.setMember("toString", new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				return new StringValue(((Type)this.boundObject).name);
			}
		}, true);
		FUNCTION = new Type("function"){
			public Value newInstance(Scope s, List<Value> params){
				throw new RuntimeException("Cannot instantiate type Function");
			}
		};
		FUNCTION.setMember("toString", new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				return new StringValue("function");
			}
		}, true);
		
		OBJECT = new Type("Object");
		OBJECT.setMember(Operators.in, new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				Value v = params.get(0);
				if(!(v instanceof StringValue)) throw new RuntimeException("Invalid operands for operator in: "+v.type+" & "+this.boundObject.type);
				ObjectValue obj = (ObjectValue)this.boundObject;
				//List<Value> param = Arrays.asList(v);
				String key = v.toString();
				for(String str : obj.variables.keySet()){
					if(key.equals(str)) return BooleanValue.TRUE;
				}
				return BooleanValue.FALSE;
			}
		}, true);
		OBJECT.setMember("toString", new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				String result = "{";
				Set<java.util.Map.Entry<String,Value>> members = ((ObjectValue)this.boundObject).variables.entrySet();
				for(java.util.Map.Entry<String, Value> entry : members){
					result += entry.getKey()+":"+entry.getValue()+",";
				}
				if(result.length() != 1)
					result = result.substring(0, result.length()-1)+"}";
				else result = "{:}";
				return new StringValue(result);
			}
		}, true);
		OBJECT.setMember(Operators.index, new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				Value v = params.get(0);
				if(v instanceof StringValue)
					return this.boundObject.getMember(v.toString());
				else if(v instanceof NumberValue){
					return this.boundObject.getMember(String.valueOf((int)((NumberValue)v).value));
				}else throw new RuntimeException("Cannot index object with "+v.type);
			}
		}, true);
		OBJECT.setMember(Operators.indexAssign, new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				if(params.size() <= 1) throw new RuntimeException("Invalid number of params, expected 2");
				Value v = params.get(0);
				Value i = params.get(1);
				if(i instanceof StringValue)
					this.boundObject.setMember(i.toString(),v,false);
				else if(i instanceof NumberValue){
					this.boundObject.setMember(String.valueOf((int)((NumberValue)i).value),v,false);
				}else throw new RuntimeException("Cannot index object with "+i.type);
				return v;
			}
		}, true);
		
		BOOLEAN = new Type("Boolean"){
			public Value newInstance(Scope s, List<Value> params){
				return BOOLEAN.getMember(Operators.cast).call(s, params);
			}
		};
		BOOLEAN.setMember("toString",  new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				return new StringValue(((BooleanValue)this.boundObject).toString());
			}
		}, true);
		BOOLEAN.setMember(Operators.cast, new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) return BooleanValue.FALSE;
				Value v = params.get(0);
				if(v == null || v == Constants.UNDEFINED) return BooleanValue.FALSE;
				if(v instanceof BooleanValue) return v;
				if(v instanceof NumberValue) return ((NumberValue)v).value == 0? BooleanValue.FALSE : BooleanValue.TRUE;
				if(v instanceof Type) return BooleanValue.TRUE;
				if(v instanceof ObjectValue){
					if(v.hasMember("toBoolean"))
						return v.getMember("toBoolean").call(s, new ArrayList<Value>());
				}
				return BooleanValue.TRUE;
			}
		}, true);
		BOOLEAN.setMember(Operators.newinstance, new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				this.boundObject = BOOLEAN.getMember(Operators.cast).call(s, params);
				return null;
			}
		}, true);
		BOOLEAN.setMember(Operators.compareTo, new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				Value v = params.get(0);
				if(!(v instanceof BooleanValue)) throw new RuntimeException("Invalid operands for operator <>: boolean & "+v.type);
				return new NumberValue(this.boundObject == v? 0 : this.boundObject == BooleanValue.TRUE? 1 : -1);
			}
		}, true);
		
		
		NUMBER = new Type("Number");
		NUMBER.setMember(Operators.newinstance, new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				this.boundObject = NUMBER.getMember(Operators.cast).call(s, params);
				return null;
			}
		}, true);
		NUMBER.setMember(Operators.cast, new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) return new NumberValue(0);
				Value v = params.get(0);
				if(v instanceof NumberValue) return v;
				if(v instanceof BooleanValue) return new NumberValue(v == BooleanValue.TRUE? 1.0 : 0.0);
				if(v instanceof StringValue) return new NumberValue(Double.valueOf(v.toString()));
				if(v.getClass() == ObjectValue.class){
					if(v.hasMember("toNumber")) return v.getMember("toNumber").call(s, new ArrayList<Value>());
				}
				throw new RuntimeException("Cannot convert "+v.type.name+" to number");
			}
		}, true);
		NUMBER.setMember("toString", new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				return new StringValue(String.valueOf(((NumberValue)this.boundObject).value).replace(".0", ""));
			}
		}, true);
		NUMBER.setMember(Operators.plus, new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				Value v = params.get(0);
				if(v instanceof StringValue)
					return new StringValue(String.valueOf(((NumberValue)this.boundObject).value)+v.toString());
				if(!(v instanceof NumberValue)) throw new RuntimeException("Invalid operands for operator +: number & "+v.type+" "+v.getClass());
				return new NumberValue(((NumberValue)this.boundObject).value + ((NumberValue)v).value);
			}
		}, true);
		NUMBER.setMember(Operators.rplus, NUMBER.getMember(Operators.plus), true);
		NUMBER.setMember(Operators.minus, new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				Value v = params.get(0);
				if(!(v instanceof NumberValue)) throw new RuntimeException("Invalid operands for operator -: number & "+v.type);
				return new NumberValue(((NumberValue)this.boundObject).value - ((NumberValue)v).value);
			}
		}, true);
		NUMBER.setMember(Operators.rminus, new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				Value v = params.get(0);
				if(!(v instanceof NumberValue)) throw new RuntimeException("Invalid operands for operator -: number & "+v.type);
				return new NumberValue(((NumberValue)v).value - ((NumberValue)this.boundObject).value);
			}
		}, true);
		NUMBER.setMember(Operators.times, new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				Value v = params.get(0);
				if(v instanceof StringValue){
					return STRING.getMember(Operators.times).bind(v).call(s, Arrays.asList(this.boundObject));
				}
				if(!(v instanceof NumberValue)) throw new RuntimeException("Invalid operands for operator +: number & "+v.type);
				return new NumberValue(((NumberValue)this.boundObject).value * ((NumberValue)v).value);
			}
		}, true);
		NUMBER.setMember(Operators.rtimes, NUMBER.getMember(Operators.times), true);
		NUMBER.setMember(Operators.divide, new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				Value v = params.get(0);
				if(!(v instanceof NumberValue)) throw new RuntimeException("Invalid operands for operator -: number & "+v.type);
				double d1 = ((NumberValue)this.boundObject).value,
						d2 = ((NumberValue)v).value;
				if(Math.abs(d1) == Double.POSITIVE_INFINITY && d2 == d1)
					return new NumberValue(1);
				else if((d1 == Double.NEGATIVE_INFINITY || d2 == Double.NEGATIVE_INFINITY) && d1 == -d2)
					return new NumberValue(-1);
				else if(Math.abs(d1) == 1 && Math.abs(d2) == Double.POSITIVE_INFINITY)
					return new NumberValue(1);
				return new NumberValue(d1 / d2);
			}
		}, true);
		NUMBER.setMember(Operators.rdivide, new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				Value v = params.get(0);
				if(!(v instanceof NumberValue)) throw new RuntimeException("Invalid operands for operator -: number & "+v.type);
				return v.getMember(Operators.divide).call(s, Arrays.asList(this.boundObject));
			}
		}, true);
		NUMBER.setMember(Operators.modulus, new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				Value v = params.get(0);
				if(!(v instanceof NumberValue)) throw new RuntimeException("Invalid operands for operator -: number & "+v.type);
				return new NumberValue(((NumberValue)this.boundObject).value % ((NumberValue)v).value);
			}
		}, true);
		NUMBER.setMember(Operators.rmodulus, new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				Value v = params.get(0);
				if(!(v instanceof NumberValue)) throw new RuntimeException("Invalid operands for operator -: number & "+v.type);
				return new NumberValue(((NumberValue)v).value % ((NumberValue)this.boundObject).value);
			}
		}, true);
		NUMBER.setMember(Operators.negate, new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				return new NumberValue( -((NumberValue)this.boundObject).value);
			}
		}, true);
		NUMBER.setMember(Operators.positate, new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				return new NumberValue(Math.abs(((NumberValue)this.boundObject).value));
			}
		}, true);
		NUMBER.setMember(Operators.xor, new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				Value v1 = this.boundObject, v2 = params.get(0);
				if(!(v1 instanceof NumberValue) || !(v2 instanceof NumberValue))
					throw new RuntimeException("Invalid operands for operator "+Operators.getSymbol(Operators.xor)+": "+v1.type+" & "+v2.type);
				double d1 = ((NumberValue)v1).value;
				double d2 = ((NumberValue)v2).value;
				if(d1 != (int)d1)
					throw new RuntimeException("Invalid operands for operator "+Operators.getSymbol(Operators.xor)+": float & "+v2.type);
				if(d2 != (int)d2)
					throw new RuntimeException("Invalid operands for operator "+Operators.getSymbol(Operators.xor)+": "+v1.type+" & "+v2.type);
				return new NumberValue((int)d1 ^ (int)d2);
			}
		}, true);
		NUMBER.setMember(Operators.pow, new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				Value v = params.get(0);
				if(!(v instanceof NumberValue)){
					if(v.getClass() == ObjectValue.class){
						if(v.hasMember(Operators.rpow))
							return v.getMember(Operators.rpow).call(s, Arrays.asList(this));
					}
					throw new RuntimeException("Invalid operands for operator ^: number & "+v.type);
				}
				double d1 = ((NumberValue)this.boundObject).value,
						d2 = ((NumberValue)v).value;
				if(d2 == -1 && Math.abs(d1) == Double.POSITIVE_INFINITY)
					return new NumberValue(0);
				if(d2 == 0) return new NumberValue(1);
				return new NumberValue(Math.pow(d1,d2));
			}
		}, true);
		NUMBER.setMember(Operators.rpow, new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				Value v = params.get(0);
				return v.getMember(Operators.pow).call(s, Arrays.asList(this.boundObject));
			}
		}, true);
		NUMBER.setMember(Operators.equals, new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				Value v = params.get(0);
				return v instanceof NumberValue && ((NumberValue)v).value == ((NumberValue)this.boundObject).value? BooleanValue.TRUE : BooleanValue.FALSE;
			}
		}, true);
		NUMBER.setMember(Operators.compareTo, new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				Value v = params.get(0);
				if(!(v instanceof NumberValue)) throw new RuntimeException("Invalid operands for operator <>: "+this.boundObject.type+" & "+v.type);
				return new NumberValue(Double.compare(((NumberValue)this.boundObject).value,((NumberValue)v).value));
			}
		}, true);
		
		
		STRING = new Type("String"){;
			public Value newInstance(Scope s, List<Value> params){
				return STRING.getMember(Operators.cast).call(s, params);
			}
		};
		STRING.setMember("toString", new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				return this.boundObject;
			}
		}, true);
		STRING.setMember(Operators.in, new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				Value v = params.get(0);
				if(!(v instanceof StringValue)) throw new RuntimeException("Invalid operands for operator in: string & "+v.type);
				return this.boundObject.toString().contains(v.toString())? BooleanValue.TRUE : BooleanValue.FALSE;
			}
		}, true);
		STRING.setMember(Operators.plus, new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				return new StringValue(((StringValue)this.boundObject).toString()+params.get(0).toString());
			}
		}, true);
		STRING.setMember(Operators.times, new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				Value v = params.get(0);
				if(v instanceof NumberValue){
					NumberValue n = (NumberValue)v;
					String str = ((StringValue)this.boundObject).toString();
					String result = "";
					double d = n.value;
					if(d == 0) return new StringValue("");
					if(d < 0){
						String temp = "";
						for(int i = str.length()-1; i >= 0; i--)
							temp += str.charAt(i);
						str = temp;
						d = Math.abs(d);
					}
					for(int i = 0; i < (int)d; i++){
						result += str;
					}
					if((int)d != d){
						d -= (int)d;
						result += str.substring(0, (int)(str.length()*d));
					}
					return new StringValue(result);
				}
				else throw new RuntimeException("Invalid operands for operator *: string & "+v.type);
			}
		}, true);
		STRING.setMember(Operators.index, new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				Value v1 = params.get(0);
				String str = this.boundObject.toString();
				int i1, i2;
				{
					if(!(v1 instanceof NumberValue)){
						throw new RuntimeException("Cannot index string with "+v1.type);
					}
					double d = ((NumberValue)v1).value;
					if(d != (int)d) throw new RuntimeException("Cannot index string with float");
					i1 = (int)d;
					if(i1 < 0) i1 += str.length()-1;
				}
				if(params.size() >= 2){
					Value v2 = params.get(1);
					if(!(v2 instanceof NumberValue)){
						throw new RuntimeException("Cannot index string with "+v2.type);
					}
					double d = ((NumberValue)v2).value;
					if(d != (int)d) throw new RuntimeException("Cannot index string with float");
					i2 = (int)d;
					if(i2 < 0) i2 += str.length()-1;
				}else{
					i2 = i1 + 1;
				}
				return new StringValue(str.substring(i1, i2));
			}
		}, true);
		STRING.setMember(Operators.equals, new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				Value v = params.get(0);
				return v instanceof StringValue && v.toString().equals(((StringValue)this.boundObject).toString())? BooleanValue.TRUE : BooleanValue.FALSE;
			}
		}, true);
		STRING.setMember(Operators.compareTo, new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				Value v = params.get(0);
				if(!(v instanceof StringValue)) throw new RuntimeException("Invalid operands for operator <>: "+this.boundObject.type+" & "+v.type);
				return new NumberValue(this.boundObject.toString().compareTo(v.toString()));
			}
		}, true);
		STRING.setMember(Operators.cast, new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) return new StringValue("");
				Value v = params.get(0);
				if(v instanceof StringValue) return v;
				return new StringValue(v.getMember("toString").call(s,new ArrayList<>()).toString());
			}
		}, true);
		STRING.setMember(Operators.newinstance, new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) return new StringValue("");
				this.boundObject = new StringValue(params.get(0).toString());
				return null;
			}
		}, true);
		
		ARRAY = new Type("Array"){
			public Value newInstance(Scope s, List<Value> params){
				return new ArrayValue(params);
			}
		};
		ARRAY.setMember("toString", new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				String result = "{";
				for(Value v : ((ArrayValue)this.boundObject).values){
					result += v.toString()+",";
				}
				if(result.length() > 1) result = result.substring(0,result.length()-1);
				result += "}";
				return new StringValue(result);
			}
		}, true);
		/** index(<number> index) */
		ARRAY.setMember(Operators.index, new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				Value i1 = params.get(0);
				if(!(i1 instanceof NumberValue)) throw new RuntimeException("Cannot index array with "+i1.type);
				double d = ((NumberValue)i1).value;
				if(d != (int)d) throw new RuntimeException("Cannot index array with float");
				int i = (int)d;
				List<Value> values = ((ArrayValue)this.boundObject).values;
				if(i < 0) i += values.size()-1;
				return values.get(i);
			}
		}, true);
		/** slice(<number> start, <number> end) */
		ARRAY.setMember(Operators.slice, new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				if(params.size() <= 1) throw new RuntimeException("Invalid number of params, expected 2");
				Value i1 = params.get(0);
				if(!(i1 instanceof NumberValue)) throw new RuntimeException("Cannot index array with "+i1.type);
				double d = ((NumberValue)i1).value;
				if(d != (int)d) throw new RuntimeException("Cannot index array with float");
				int i = (int)d;
				List<Value> values = ((ArrayValue)this.boundObject).values;
				if(i < 0) i += values.size()-1;
				
				Value i2 = params.get(1);
				if(!(i2 instanceof NumberValue)) throw new RuntimeException("Cannot index array with "+i2.type);
				double d2 = ((NumberValue)i2).value;
				if(d2 != (int)d2) throw new RuntimeException("Cannot index array with float");
				int ii = (int)d2;
				if(ii < 0) ii += values.size()-1;
				List<Value> newlist = new ArrayList<Value>();
				if(i < ii){
					for(int idx = i; idx < ii; idx++){
						newlist.add(values.get(idx));
					}
				}else if(i > ii){
					for(int idx = i; idx > ii; idx--){
						newlist.add(values.get(idx));
					}
				}
				return new ArrayValue(values);
			}
		}, true);
		/** indexAssign(<any> value, <number> index) */
		ARRAY.setMember(Operators.indexAssign, new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				if(params.size() == 1) throw new RuntimeException("Invalid number of params, expected 1");
				Value v = params.get(0);
				if(((ArrayValue)this.boundObject).sealed)
					throw new RuntimeException("Cannot assign values to a sealed array");
				Value i1 = params.get(1);
				if(!(i1 instanceof NumberValue)) throw new RuntimeException("Cannot index array with "+i1.type);
				double d = ((NumberValue)i1).value;
				if(d != (int)d) throw new RuntimeException("Cannot index array with float");
				int i = (int)d;
				List<Value> values = ((ArrayValue)this.boundObject).values;
				if(i < 0) i += values.size()-1;
				
				values.set(i, v);
				
				return v;
			}
		}, true);
		/** sliceAssign(<any> value, <number> start, <number> end) */
		ARRAY.setMember(Operators.sliceAssign, new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				if(params.size() <= 2) throw new RuntimeException("Invalid number of params, expected 3");
				Value v = params.get(0);
				if(((ArrayValue)this.boundObject).sealed)
					throw new RuntimeException("Cannot assign values to a sealed array");
				Value i1 = params.get(1);
				if(!(i1 instanceof NumberValue)) throw new RuntimeException("Cannot index array with "+i1.type);
				double d = ((NumberValue)i1).value;
				if(d != (int)d) throw new RuntimeException("Cannot index array with float");
				int i = (int)d;
				List<Value> values = ((ArrayValue)this.boundObject).values;
				if(i < 0) i += values.size()-1;
				
				Value i2 = params.get(2);
				if(!(i2 instanceof NumberValue)) throw new RuntimeException("Cannot index array with "+i2.type);
				double d2 = ((NumberValue)i2).value;
				if(d2 != (int)d2) throw new RuntimeException("Cannot index array with float");
				int ii = (int)d2;
				if(ii < 0) ii += values.size()-1;
				
				if(v instanceof ArrayValue){
					List<Value> newvalues = ((ArrayValue)v).values;
					if(newvalues.size() == Math.max(i, ii) - Math.min(i, ii)){
						if(i < ii){
							for(int idx = i, ix = 0; idx < ii; idx++, ix++){
								values.set(idx, newvalues.get(ix));
							}
						}else if(i > ii){
							for(int idx = i, ix = 0; idx > ii; idx--, ix++){
								values.set(idx, newvalues.get(ix));
							}
						}
					}else{
						for(int count = 0; count < Math.max(i, ii) - Math.min(i, ii); count++){
							values.remove(Math.min(i, ii));
						}
						if(i < ii){
							for(int idx = 0; idx < newvalues.size(); idx++)
								values.add(Math.min(i, ii),newvalues.get(idx));
						}else if(i > ii){
							for(int idx = newvalues.size()-1; idx >= 0; idx--)
								values.add(Math.min(i, ii),newvalues.get(idx));
						}
					}
					//((ArrayValue)this.boundObject).values = newvalues;
				}else{
					if(i < ii){
						for(int idx = i; idx < ii; idx++){
							values.set(idx, v);
						}
					}else if(i > ii){
						for(int idx = i; idx > ii; idx--){
							values.set(idx, v);
						}
					}
				}
				
				return v;
			}
		}, true);
		ARRAY.setMember(Operators.in, new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				Value v = params.get(0);
				List<Value> values = ((ArrayValue)this.boundObject).values;
				for(int i = 0; i < values.size(); i++){
					if(BOOLEAN.getMember(Operators.cast).call(s, Arrays.asList(values.get(i).getMember(Operators.equals).call(s, Arrays.asList(v)))) == BooleanValue.TRUE){
						return BooleanValue.TRUE;
					}
				}
				return BooleanValue.FALSE;
			}
		}, true);
		ARRAY.setMember("indexOf", new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				Value v = params.get(0);
				List<Value> values = ((ArrayValue)this.boundObject).values;
				for(int i = 0; i < values.size(); i++){
					if(BOOLEAN.getMember(Operators.cast).call(s, Arrays.asList(values.get(i).getMember(Operators.equals).call(s, Arrays.asList(v)))) == BooleanValue.TRUE){
						return new NumberValue(i);
					}
				}
				return Constants.UNDEFINED;
			}
		}, true);
		ARRAY.setMember(Operators.equals, new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				Value v = params.get(0);
				if(!(v instanceof ArrayValue)) return BooleanValue.FALSE;
				List<Value> values1 = ((ArrayValue)this.boundObject).values;
				List<Value> values2 = ((ArrayValue)v).values;
				if(values1.size() != values2.size()) return BooleanValue.FALSE;
				for(int i = 0; i < values1.size(); i++){
					Value v1 = values1.get(i);
					Value v2 = values2.get(i);
					if(BOOLEAN.getMember(Operators.cast).call(s, Arrays.asList(v1.getMember(Operators.equals).call(s, Arrays.asList(v2)))) != BooleanValue.TRUE){
						return BooleanValue.FALSE;
					}
				}
				return BooleanValue.TRUE;
			}
		}, true);
		ARRAY.setMember("add", new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				Value v = params.get(0);
				if(((ArrayValue)this.boundObject).finalized)
					throw new RuntimeException("Cannot add values to finalized array");
				List<Value> values1 = ((ArrayValue)this.boundObject).values;
				if(params.size() >= 2){
					Value v2 = params.get(1);
					if(!(v instanceof NumberValue)) throw new RuntimeException("Cannot index array with "+v.type);
					double d = ((NumberValue)v).value;
					if(d != (int)d) throw new RuntimeException("Cannot index array with float");
					int i = (int)d;
					values1.add(i,v2);
				}else{
					values1.add(v);
				}
				return Constants.UNDEFINED;
			}
		}, true);
		ARRAY.setMember("remove", new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				Value v = params.get(0);
				if(((ArrayValue)this.boundObject).finalized)
					throw new RuntimeException("Cannot remove values from a finalized array");
				List<Value> values1 = ((ArrayValue)this.boundObject).values;
				if(!(v instanceof NumberValue)) throw new RuntimeException("Cannot index array with "+v.type);
				double d = ((NumberValue)v).value;
				if(d != (int)d) throw new RuntimeException("Cannot index array with float");
				int i = (int)d;
				return values1.remove(i);
			}
		}, true);
		
		toStringBasic = new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				return new StringValue(String.valueOf(this.boundObject));
			}
		};
		equalsBasic = new Function(){
			@Override
			public Value call(Scope s, List<Value> params){
				if(params.size() == 0) throw new RuntimeException("Invalid number of params, expected 1");
				Value v = params.get(0);
				return this.boundObject == v || this.boundObject.equals(v)? BooleanValue.TRUE : BooleanValue.FALSE;
			}
		};
		
		TYPE.setMember(Operators.equals, equalsBasic, true);
		BOOLEAN.setMember(Operators.equals, equalsBasic, true);
		FUNCTION.setMember(Operators.equals, equalsBasic, true);
		OBJECT.setMember(Operators.equals, equalsBasic, true);
		
		TYPE.type = OBJECT.type = TYPE;
		
		TYPE.parent = OBJECT.parent = Arrays.asList(OBJECT);
		BOOLEAN.parent = NUMBER.parent = STRING.parent = FUNCTION.parent = ARRAY.parent = null;
		
	}
	public final String name;
	public List<Type> parent;
	public Type(String name){
		this(name,OBJECT);
	}
	public Type(String name, Type parent){
		this(name,Arrays.asList(parent == null? OBJECT : parent));
		
	}
	public Type(String name, List<Type> parent){
		this.type = this;
		this.name = name;
		this.parent = parent == null || parent.isEmpty() || parent.contains((Type)null)? Arrays.asList(OBJECT) : parent;
		this.variables.put("name", new StringValue(name));
	}
	@Override
	public String toString(){
		return this.name;
	}
	@Override
	public Value call(Scope s, List<Value> params){
		Value v = this.getMember(Operators.cast);
		if(v == null || v == Constants.UNDEFINED) return super.call(s, params);
		return v.call(s, params);
	}
	public Value newInstance(Scope s, List<Value> params){
		Value v = getMember(Operators.newinstance);
		ObjectValue obj = new ObjectValue(this);
		if(v == null || v == Constants.UNDEFINED || !(v instanceof Function)) return obj;
		v.bind(obj).call(s, params);
		return ((Function)v).boundObject;
	}
	private boolean parentHasMember(String name){
		if(this == TYPE || this == OBJECT) return OBJECT == null? false : OBJECT.hasMember(name);
		if(parent == null || parent.isEmpty()) return false;
		for(Type t : parent){
			if(t != null && t.hasMember(name)) return true;
		}
		return false;
	}
	@Override
	public boolean hasMember(String name){
		if(this != OBJECT){
			return this.variables.containsKey(name) || parentHasMember(name) || this != TYPE && TYPE.variables.containsKey(name);
		}else return this.variables.containsKey(name) || TYPE.variables.containsKey(name);
	}
	private void parentSetMember(String name, Value val, boolean constant){
		for(Type t : parent){
			if(t.hasMember(name)){
				t.setMember(name, val, constant);
				return;
			}
		}
	}
	/*@Override
	public void setMember(String name, Value val, boolean constant){
		if(super.hasMember(name)){
			if(this.variables.containsKey(name)){
				this.variables.put(name, val);
				if(constant)
					this.variables.setConstant(name);
			}else if(this != OBJECT && parentHasMember(name)){
				parentSetMember(name, val, constant);
			}else{
				super.setMember(name, val, constant);
			}
		}else super.setMember(name, val, constant);
	}*/
	private Value parentGetMember(String name){
		for(Type t : parent){
			if(t.hasMember(name))
				return t.getMember(name);
		}
		return Constants.UNDEFINED;
	}
	private boolean parentExtends(Type t){
		if(this == TYPE) return t == this || t == OBJECT;
		else if(this == OBJECT) return t == this;
		for(Type parent : this.parent){
			if(parent.Extends(t)) return true;
		}
		return false;
	}
	@Override
	public Value getMember(String name){
		if(this.variables.containsKey(name)) return this.variables.get(name);
		else if(this.parentHasMember(name)) return this.parentGetMember(name);
		else return super.getMember(name);
	}
	public boolean Extends(Type t){
		return t == this || parent != null && parentExtends(t);
	}
}
