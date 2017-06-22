package com.ombda.mathx;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class StringHelper implements CharSequence{
	public static class Builder{
		private List<Position> positions;
		private String value;
		public Builder(){
			positions = new ArrayList<>();
			value = "";
		}
		public void append(char c, Position p){
			value += c;
			positions.add(p);
		}
		public StringHelper build(){
			return new StringHelper(value, positions);
		}
	}
	private final String value;
	private final List<Position> positions;
	public StringHelper(String s){
		this(s,new ArrayList<>());
		int line = 0, pos = 0;
		for(char c : s.toCharArray()){
			positions.add(Position.get(line, pos));
			if(c == '\n'){
				line++;
				pos = 0;
			}else pos++;
		}
	}
	private StringHelper(String s, List<Position> p){
		value = s;
		positions = p;
	}
	public StringHelper substring(int start){
		return substring(start,length());
	}
	public StringHelper substring(int start, int end){
		List<Position> newpositions = new ArrayList<>();
		for(int i = start; i < end; i++){
			newpositions.add(positions.get(i));
		}
		return new StringHelper(this.value.substring(start, end), newpositions);
	}
	public int length(){
		return value.length();
	}
	public char charAt(int pos){
		return value.charAt(pos);
	}
	public Position posAt(int pos){
		return positions.get(pos);
	}
	public StringHelper append(String s){
		List<Position> newpositions = new ArrayList<>(positions);
		Position p = positions.get(positions.size()-1);
		for(int i = 0; i < s.length(); i++)
			newpositions.add(p);
		return new StringHelper(this.value + s, newpositions);
	}
	public StringHelper appendBefore(String s){
		List<Position> newpositions = new ArrayList<>(positions);
		Position p = positions.get(0);
		for(int i = 0; i < s.length(); i++)
			newpositions.add(0,p);
		return new StringHelper(s + this.value, newpositions);
	}
	public StringHelper append(StringHelper s){
		List<Position> newpositions = new ArrayList<>(positions);
		newpositions.addAll(s.positions);
		return new StringHelper(this.value + s.value, newpositions);
	}
	public StringHelper appendBefore(StringHelper s){
		List<Position> newpositions = new ArrayList<>(s.positions);
		newpositions.addAll(positions);
		return new StringHelper(s.value + this.value, newpositions);
	}
	@Override
	public String toString(){
		return value;
	}
	@Override
	public boolean equals(Object obj){
		return obj.toString().equals(value);
	}
	@Override
	public CharSequence subSequence(int start, int end){
		return substring(start, end);
	}
	@Override
	public IntStream chars(){
		return value.chars();
	}
	@Override
	public IntStream codePoints(){
		return value.codePoints();
	}
	
}
