package com.ombda.mathx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.ombda.mathx.errors.Error;
public abstract class Macro{
	private final String name;
	
	public Macro(String name){
		this.name = name;
	}
	
	public abstract List<Token> replacement();

	public final String getName(){
		return name;
	}
	//returns {a,b} where a is number of tokens to remove from end of result and b is number of tokens to skip 
	public abstract int matches(List<Token> tokens, int position);
	
	protected final String tokenString(List<Token> tokens){
		String result = "";
		for(int i = 0; i < tokens.size(); i++){
			char last = result.isEmpty()? '\u0000' : Character.isWhitespace(result.charAt(result.length()-1))? result.charAt(result.length()-2) : result.charAt(result.length()-1), next = i == tokens.size()-1? '\u0000' : tokens.get(i+1).toString().charAt(0);
			String current = tokens.get(i).toString();
			if(Character.isDigit(last) && !Character.isLetterOrDigit(current.charAt(0)) && Character.isLetter(last) != Character.isLetter(current.charAt(0)))
				result = result.trim();
			result += tokens.get(i);
			//System.out.println("Current = "+current.charAt(current.length()-1)+" Next = "+next+" addspace = "+(i != tokens.size()-1)+" && "+Character.isLetter(current.charAt(current.length()-1))+" && "+Character.isLetterOrDigit(next));
			if(i != tokens.size()-1 && Character.isLetter(current.charAt(current.length()-1)) && Character.isLetterOrDigit(next)) result += " ";
			//System.out.println("Rolling result = '"+result+"'");
		}
		//System.out.println("Result = "+result);
		return result;
	}
	protected final List<Token> variableTest(Token token, Map<String, List<Token>> variables){
		for(Map.Entry<String, List<Token>> entry : variables.entrySet()){
			if(token.toString().equals("#"+entry.getKey())){
				//Position p = entry.getValue().isEmpty()? token.position() : entry.getValue().get(0).position();
				return Arrays.asList(new Token('"'+tokenString(entry.getValue())+'"', token.position()));
			}else if(token.toString().equals(entry.getKey())){
				return entry.getValue();
			}else if(token.toString().matches("\\Q"+entry.getKey()+"\\E##[a-zA-Z_0-9]+")){
				String str = token.toString();
				int i = str.indexOf("##");
				String last = str.substring(i+2);
				if(entry.getValue().size() > 1 || entry.getValue().size() == 1 &&  !entry.getValue().get(0).toString().matches("[a-zA-Z_][a-zA-Z_0-9]*")) throw new Error(token.position(),"Combining "+tokenString(entry.getValue())+" with "+last+" does not produce a valid token!");
				if(variables.containsKey(last)){
					return Arrays.asList(new Token(tokenString(entry.getValue())+tokenString(variables.get(last)),token.position()));
				}else return Arrays.asList(new Token(tokenString(entry.getValue())+last,token.position()));
			}else if(token.toString().matches("[a-zA-Z_][a-zA-Z_0-9]*##\\Q"+entry.getKey()+"\\E")){
				String str = token.toString();
				int i = str.indexOf("##");
				String first = str.substring(0, i);
				if(entry.getValue().size() > 1 || entry.getValue().size() == 1 && !entry.getValue().get(0).toString().matches("[a-zA-Z_0-9]+")) throw new Error(token.position(),"Combining "+first+" with "+tokenString(entry.getValue())+" does not produce a valid token!");
				if(variables.containsKey(first))
					return Arrays.asList(new Token(tokenString(variables.get(first))+tokenString(entry.getValue()),token.position()));
				else return Arrays.asList(new Token(first+tokenString(entry.getValue()),token.position()));
			}
		}
		return null;
	}
	protected final <T extends CharSequence> List<T> hashtags(List<T> oldtokens){
		List<T> tokens = new ArrayList<>(oldtokens);
		String last = "";
		for(int i = tokens.size()-1; i >= 0; i--){
			T t = tokens.get(i);
			if(t.equals("#") && last.matches("[a-zA-Z_][a-zA-Z_0-9]*")){
				tokens.remove(i+1);
				//System.out.println("#"+last);
				T t2;
				if(t instanceof Token)
					t2 = (T)new Token("#" + last.trim(), ((Token)t).position());
				else{
					t2 = (T)("#" + last.trim());
				}
				//System.out.println("new token "+t2);
				tokens.set(i, t2);
			}else if(i != 0 && t.equals("##") && last.matches("[a-zA-Z_0-9]+") && tokens.get(i-1).toString().matches("[a-zA-Z_][a-zA-Z_0-9]*")){
				String end = tokens.remove(i+1).toString();
				tokens.remove(i);
				i--;
				T t2 = tokens.get(i);
				if(t2 instanceof Token)
					t2 = (T)new Token(t2.toString()+"##"+end.trim(),((Token)t2).position());
				else
					t2 = (T)(t2 + "##"+end.trim());
				//System.out.println("new token "+t2);
				tokens.set(i, t2);
			}
			last = t.toString();
		}
		return tokens;
	}
}
