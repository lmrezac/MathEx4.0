package com.ombda.mathx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;

import com.ombda.mathx.errors.Error;

public class PatternMacro extends Macro{
	private interface SingleToken{
		// returns -1 if did not match, else number of tokens that it matched
		public int matches(List<Token> tokens, int start);
	}
	private class TokenPattern{
		private List<SingleToken> tokens;
		//public final boolean reverse = false;

		public int matches(List<Token> tokens, int startpos){
			if(this.tokens.isEmpty()) return 0;
			int direction = 1;//this.reverse? -1 : 1;
			int i = startpos;
			for(int i2 = 0, count = 0; count < this.tokens.size(); count++, i++, i2 ++){
				SingleToken t = this.tokens.get(i2);

				//System.out.println("i = "+i+" i2 = "+i2+" tokens = "+tokens/*.subList(startpos, tokens.size())*/);
				int j = t.matches(tokens, i);
				//System.out.println("match of "+t+" against "+tokens.get(i)+" = "+j+"; count = "+count+"; tokens.size = "+this.tokens.size());
				if(j==-1) {
					//System.out.println("didn't match");
					return -1;
				}
				i += j-1;
			}
			//System.out.println("matches!");
			return i-startpos;
		}

		private class SimpleToken implements SingleToken{
			private String token;
			public SimpleToken(String s){
				token = s;
			}
			public int matches(List<Token> tokens, int start){
				//System.out.println("test match of "+token+" against "+tokens.get(start));
				if(tokens.get(start).equals(token))
					return 1;
				else
					return -1;
			}
			public String toString(){
				return token;
			}
		}

		private class VariableToken implements SingleToken{
			private String name;
			private boolean wordOnly;
			public VariableToken(String name, boolean word){
				this.name = name;
				wordOnly = word;
			}
			public int matches(List<Token> tokens, int start){
				Token token = tokens.get(start);
				if(variables.containsKey(name)){
					// System.out.println(token+" matched "+name);
					return token.equals(variables.get(name))? 1 : -1;
				}
				if(wordOnly){
					if(token.matches("[a-zA-Z_][a-zA-Z_0-9]*")||token.matches("[\\-+]?((\\d+(\\.\\d*)?)|(\\d*\\.\\d+))")){
						variables.put(name, Arrays.asList(token));
						// System.out.println(token+" matched "+name);
						return 1;
					}else{
						// System.out.println(token+" did not match "+name);
						return -1;
					}
				}else{
					// System.out.println(token+" matched "+name);
					variables.put(name, Arrays.asList(token));
					return 1;
				}
			}
			public String toString(){
				if(wordOnly)
					return "<"+name+">";
				else
					return "["+name+"]";
			}
		}
		private class VariableExpressionToken implements SingleToken{
			private int level;
			private String name;
			public VariableExpressionToken(String name, int exprType){
				this.level = exprType;
				this.name = name;
			//System.out.println("new variable expression "+name);
			}
			@Override
			public int matches(final List<Token> tokens,final int start){
				int end = new ExpressionEater(tokens).parse(start,level);
				//System.out.println("Ate expression "+tokens.subList(start, end));
				int size = end - start;
				if(size == 0) return -1;
				List<Token> result = new ArrayList<>();
				for(int i = 0; i < size; i++){
					result.add(tokens.get(start + i));
				}
				variables.put(name, result);
				return size;
			}
			public String toString(){
				switch(level){
				case -1:
					return "("+name+"!)";
				case 0:
					return "("+name+".)";
				case 1:
					return "("+name+"^)";
				case 2:
					return "("+name+"+)";
				case 3:
					return "("+name+"*)";
				case 4:
					return "("+name+">>)";
				case 5:
					return "("+name+"<>)";
				case 6:
					return "("+name+"==)";
				case 7:
					return "("+name+"&)";
				case 8:
					return "("+name+"&&)";
				case 9:
					return "("+name+"?:)";
				default:
					return "("+name+")";
				}
			}
		}
		private class MultiToken implements SingleToken{
			private List<SingleToken> tokens;
			private final int group;
			public MultiToken(int group, List<SingleToken> tokens){
				//System.out.println("new multitoken: "+tokens);
				this.tokens = tokens;
				this.group = group;
			}
			@Override
			public int matches(List<Token> tokens, int start){
				int i = start;
				List<Token> results = new ArrayList<>();
				for(int count = 0, i2 = 0; count < this.tokens.size(); i ++, count++, i2 ++){
					int match = this.tokens.get(i2).matches(tokens, i);
					//System.out.println("match of "+this.tokens.get(i2)+" against "+tokens.get(i)+" = "+match);
					if(match==-1){
						//System.out.println("didnt match");
						return -1;
					}
					int i3 = i;
					i += match-1;
					for(int j = i3; Math.abs(j)<Math.abs(i); j++){
						//if(direction==-1){
						//	results.add(0, tokens.get(i3-j));
						//}else{
							results.add(tokens.get(i3+j));
						//}
					}
				}
				while(group_results.size()<=group)
					group_results.add(null);
				group_results.set(group, results);
				return Math.abs(i-start);
			}
			public String toString(){
				String result = "(";
				for(SingleToken t : tokens)
					result += t.toString()+" ";
				result = result.substring(0, result.length()-1)+")";
				return result;
			}
		}
		private class OrToken implements SingleToken{
			private SingleToken left, right;

			public OrToken(SingleToken l, SingleToken r){
				left = l;
				right = r;
			}
			@Override
			public int matches(List<Token> tokens, int start){
				int i1 = left.matches(tokens, start);
				if(i1==-1)
					return right.matches(tokens, start);
				else
					return i1;
			}
			public String toString(){
				return left.toString()+"|"+right.toString();
			}
		}
		private class OptionalToken implements SingleToken{
			private SingleToken token;
			public OptionalToken(SingleToken t){
				token = t;
			}
			@Override
			public int matches(List<Token> tokens, int start){
				int i = token.matches(tokens, start);
				if(i==-1)
					return 0;
				else
					return i;
			}
			public String toString(){
				return token.toString()+"?";
			}
		}
		private class MatchCountToken implements SingleToken{
			private int count;
			private SingleToken token;

			public MatchCountToken(SingleToken t, int i){
				count = i;
				token = t;
			}

			@Override
			public int matches(List<Token> tokens, int start){
				int size = 0;
				if(count==-1){ // zero or more times
					int startpos = start;
					int i = token.matches(tokens, startpos);
					while(i!=-1){
						size += i;
						startpos += i;
						i = token.matches(tokens, startpos);
					}
				}else if(count==-2){ // 1 or more times
					int startpos = start;
					int i = token.matches(tokens, startpos);
					if(i==-1) return -1;
					while(i!=-1){
						size += i;
						startpos += i;
						i = token.matches(tokens, startpos);
					}
				}else{
					int count = 0;
					int startpos = start;
					int i = token.matches(tokens, startpos);
					while(i!=-1){
						size += i;
						startpos += i;
						count++;
						i = token.matches(tokens, startpos);
					}
					if(count != this.count)
						return -1;
					else
						return size;
				}
				return size;
			}

		}

		private List<SingleToken> compileTokens(List<String> pattern){
			group_count = 0;
			List<SingleToken> tokens = new ArrayList<>();
			for(String str : pattern){
				tokens.add(compileToken(str));
			}
			return tokens;
		}
		private int group_count = 0;
		private SingleToken compileToken(String str){
			int pdepth = 0;
			for(int i = 0; i<str.length(); i++){
				char c = str.charAt(i);
				if(c=='(')
					pdepth++;
				else if(c==')')
					pdepth--;
				else if(c=='|'&&pdepth==0){
					return new OrToken(compileToken(str.substring(0, i)), compileToken(str.substring(i+1)));
				}
			}
			if(str.matches("\\([a-zA-Z][a-zA-Z0-9]*\\)")){
				return new VariableExpressionToken(str.substring(1, str.length()-1),10);
			}else if(str.matches("\\([a-zA-Z][a-zA-Z0-9]*\\?:\\)")){
				return new VariableExpressionToken(str.substring(1, str.length()-3),9);
			}else if(str.matches("\\([a-zA-Z][a-zA-Z0-9]*\\|\\||\\&\\&\\)")){
				return new VariableExpressionToken(str.substring(1, str.length()-3),8);
			}else if(str.matches("\\([a-zA-Z][a-zA-Z0-9]*[\\|\\&]\\)")){
				return new VariableExpressionToken(str.substring(1, str.length()-2),7);
			}else if(str.matches("\\([a-zA-Z][a-zA-Z0-9]*[!=]=\\)")){
				return new VariableExpressionToken(str.substring(1, str.length()-3),6);
			}else if(str.matches("\\([a-zA-Z][a-zA-Z0-9]*([<>]=?|<>)\\)")){
				int i = Math.min(str.indexOf('<'), str.indexOf('>'));
				return new VariableExpressionToken(str.substring(1, i),5);
			}else if(str.matches("\\([a-zA-Z][a-zA-Z0-9]*(<<|>>>?)\\)")){
				return new VariableExpressionToken(str.substring(1, str.length()-(str.endsWith(">>>)")? 4 : 3)),4);
			}else if(str.matches("\\([a-zA-Z][a-zA-Z0-9]*[\\+\\-]\\)")){
				return new VariableExpressionToken(str.substring(1, str.length()-2),3);
			}else if(str.matches("\\([a-zA-Z][a-zA-Z0-9]*[/*%]\\)")){
				return new VariableExpressionToken(str.substring(1, str.length()-2),2);
			}else if(str.matches("\\([a-zA-Z][a-zA-Z0-9]*\\^\\)")){
				return new VariableExpressionToken(str.substring(1, str.length()-2),1);
			}else if(str.matches("\\([a-zA-Z][a-zA-Z0-9]*\\.\\)")){
				return new VariableExpressionToken(str.substring(1, str.length()-2),0);
			}else if(str.matches("\\([a-zA-Z][a-zA-Z0-9]*\\!\\)")){
				return new VariableExpressionToken(str.substring(1, str.length()-2),-1);
			}else if(str.startsWith("(")&&str.endsWith(")")){
				str = str.substring(1, str.length()-1);
				return new MultiToken(++group_count, compileTokens(Compiler.compilePatternString(str)));
			}else if(str.matches("<[a-zA-Z][a-zA-Z0-9]*>")){
				return new VariableToken(str.substring(1, str.length()-1), true);
			}else if(str.matches("\\[[^\\s]+\\]")){
				return new VariableToken(str.substring(1, str.length()-1), false);
			}else if(str.endsWith("?")&&str.length()>1){
				return new OptionalToken(compileToken(str.substring(0, str.length()-1)));
			}else if(str.endsWith("*")&&str.length()>1){
				return new MatchCountToken(compileToken(str.substring(0, str.length()-1)), -1);
			}else if(str.endsWith("+")&&str.length()>1&&!str.equals("++")){
				return new MatchCountToken(compileToken(str.substring(0, str.length()-1)), -2);
			}else if(str.length()>3&&str.charAt(str.length()-1)=='}'&&str.contains("{")){
				return new MatchCountToken(compileToken(str.substring(0, str.lastIndexOf("{"))), Integer.parseInt(str.substring(str.lastIndexOf("{")+1, str.length()-1)));
			}else
				return new SimpleToken(str);
		}

		public String toString(){
			String result = "";
			for(SingleToken t : tokens){
				result += t.toString()+" ";
			}
			result = result.trim();
			return result;
		}
	}

	private interface PatternDefinitionResult{
		public Token eval();
	}
	private class PDReturn implements PatternDefinitionResult{
		private final Token result;
		public PDReturn(Token result){
			this.result = result;
		}
		public Token eval(){
			return result;
		}
		public String toString(){
			return "->\""+result.toString()+"\"";
		}
	}
	private class PDIf implements PatternDefinitionResult{
		private final boolean not;
		private final int group;
		private final PatternDefinitionResult t, f;
		public PDIf(int g, boolean b, PatternDefinitionResult t, @Nullable PatternDefinitionResult f){
			group = g;
			not = b;
			this.t = t;
			this.f = f;
		}
		public Token eval(){
			boolean b = !group_results.get(group).isEmpty();
			if(not) b = !b;
			if(b)
				return t.eval();
			else
				return f.eval();
		}
		public String toString(){
			String result = "if ";
			if(not) result += "!";
			result += group+" ("+t.toString()+") else ("+f.toString()+")";
			return result;
		}
	}

	private PatternDefinitionResult definition;
	private TokenPattern pattern;
	@SuppressWarnings("unused")
	private int definitionLine, definitionPos;
	private HashMap<String, List<Token>> variables = new HashMap<>();
	private List<List<Token>> group_results = new ArrayList<>();
	public PatternMacro(String name, List<String> pattern, List<Token> definition){
		super(name);
		Position p = definition.get(0).position();
		definitionLine = p.line;
		definitionPos = p.pos;
		this.pattern = compile(pattern);
		//System.out.println("Pattern = "+this.pattern);
		//System.out.println("definition before = "+definition);
		//definition = hashtags(definition);
		//System.out.println("definition after = "+definition);
		compileDefinition(definition);
	}
	private TokenPattern compile(List<String> pattern){
		TokenPattern x = new TokenPattern();
		x.tokens = x.compileTokens(pattern);
		return x;
	}
	private void compileDefinition(final List<Token> tokens){
		this.definition = new Object(){
			private Token token;
			private int pos;
			private void next(){
				pos++;
				if(pos>=tokens.size()){
					pos = tokens.size();
					token = null;
				}else
					token = tokens.get(pos);
			}
			private boolean eat(String str){
				if(token.equals(str)){
					next();
					return true;
				}else
					return false;
			}
			public PatternDefinitionResult compile(){
				pos = -1;
				next();
				if(!eat("{")) throw new Error(token.position(), "Expected { here, got "+token);
				PatternDefinitionResult result = statement();
				if(!eat("}")) throw new Error(token.position(), "Expected } here, got "+token);
				return result;
			}
			private PatternDefinitionResult statement(){
				if(eat("->")){
					if(!Compiler.isString(token.toString())) throw new RuntimeException("Expected string for return statement, got "+token.toString());
					PatternDefinitionResult result = new PDReturn(token);
					next();
					return result;
				}else if(eat("if")){
					boolean not = eat("!");
					if(!token.matches("[1-9]\\d*")) throw new Error(token.position(), "Expected a number here, got "+token.toString());
					int group = Integer.parseInt(token.toString());
					next();
					if(!eat("(")) throw new Error(token.position(), "Expected ( here");
					PatternDefinitionResult t = statement();
					if(!eat(")")) throw new Error(token.position(), "Expected ) here");
					if(!eat("else")) throw new Error(token.position(), "Expected else here");
					if(!eat("(")) throw new Error(token.position(), "Expected ( here");
					PatternDefinitionResult f = statement();
					if(!eat(")")) throw new Error(token.position(), "Expected ) here");
					return new PDIf(group, not, t, f);
				}else
					throw new Error(token.position(), "Expected if or -> here, got "+token);
			}
		}.compile();
	}

	/*private static String findName(List<String> tokens){
		for(String s : tokens){
			if(s.startsWith("#")){
				if(s.length()==1) throw new RuntimeException("Invalid define directive: Invalid pattern token '#', use %# to get a #");
				return s.substring(1);
			}
		}
		throw new RuntimeException("Invalid define directive: A name was not found in the pattern");
	}*/

	/*@Override
	public List<Token> replacement(){
		List<Token> result = new ArrayList<>();
		for(int i = 0; i < definition.size(); i++){
			Token str = definition.get(i);
			if(str.equals("$") && i+1 < definition.size() && definition.get(i+1).equals("{")){
				int depth = 1;
				int j;
				String s = "";
				for(j = i+2; depth > 0; j++){
					Token str2 = definition.get(j);
					if(str2.equals("{")) depth++;
					else if(str2.equals("}")) depth--;
					if(depth != 0)
						s += str2;
				}
				i = j-1;
				result.addAll(variables.get(s));
			}else result.add(str);
		}
		return result;
	}*/
	@Override
	public List<Token> replacement(){
		Token str = this.definition.eval();
		List<Token> tokens = hashtags(Tokenizer.tokenize(Arrays.asList(str.substring(1,str.length()-1)),str.line,str.pos));
		
		for(int i = tokens.size()-1; i >= 0; i--){
			Token t = tokens.get(i);
			//System.out.println("Variable test "+t);
			List<Token> testResult = variableTest(t,variables);
			if(testResult != null){
				tokens.remove(i);
				tokens.addAll(i, testResult);
			}
		}
		return tokens;
	}
	@Override
	public int matches(List<Token> tokens, int position){
		variables.clear();
		return pattern.matches(tokens, position);
	}

	public String toString(){
		return "pattern("+pattern.toString()+"){"+definition.toString()+"}";
	}

}
