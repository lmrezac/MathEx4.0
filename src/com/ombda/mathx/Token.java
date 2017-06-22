package com.ombda.mathx;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Token extends Position implements CharSequence{
	public final String str;
	public Token(String s, Position p){
		this(s, p.line, p.pos);
	}
	public Token(String s, int l, int p){
		super(l, p);
		str = s;
	}
	public String toString(){
		return str;
	}
	public boolean equals(Object obj){
		return /*(!(obj instanceof Token) || ((Token)obj).line == this.line && ((Token)obj).pos == this.pos) && (!(obj instanceof StringHelper) || ((StringHelper)obj).posAt(0).equals(this)) &&*/ obj.toString().equals(this.str);
	}
	public Token append(String s){
		return new Token(str + s, line, pos);
	}
	public Token appendBefore(String s){
		return new Token(s + str, line, pos);
	}
	public boolean startsWith(CharSequence s){
		return str.startsWith(s.toString());
	}
	public boolean endsWith(CharSequence s){
		return str.endsWith(s.toString());
	}
	public int length(){
		return str.length();
	}
	@Override
	public char charAt(int index){
		return str.charAt(index);
	}
	@Override
	public CharSequence subSequence(int start, int end){
		return new Token(str.substring(start, end), line, pos + start);
	}
	public CharSequence subSequence(int start){
		return subSequence(start,length());
	}
	public String substring(int start){
		return substring(start,length());
	}
	public String substring(int start, int end){
		return str.substring(start, end);
	}
	public boolean matches(String pattern){
		return str.matches(pattern);
	}
	public Token replaceAll(String regex, String replacement){
		Pattern p1 = Pattern.compile(regex);
		Token result = this;
		Matcher m = p1.matcher(result);
		while(m.find()){
			result = ((Token)result.subSequence(0,m.start())).append(replacement).append(result.substring(m.end()));
			m = p1.matcher(result);
		}
		return result;
	}
	public Position position(){
		return Position.get(line, pos);
	}
}
