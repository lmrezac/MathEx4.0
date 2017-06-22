package com.ombda.mathx;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer{
	private StringHelper str;
	private int pos, ch;
	private Tokenizer(List<String> s){
		str = doLines(s);
	}
	private void next(){
		ch = ++pos >= str.length()? ((pos = str.length())-str.length()-1) : str.charAt(pos);
	}
	private void eatWhite(){
		while(pos < str.length() && Character.isWhitespace(ch)) next();
	}
	private boolean eat(int c){
		eatWhite();
		if(ch == c){
			next();
			return true;
		}
		return false;
	}
	private List<Token> tokenize(final int STARTLINE, final int STARTPOS){
		pos = -1;
		next();
		List<Token> tokens = new ArrayList<>();
		while(pos < str.length()){
			if(Character.isDigit(ch)){
				int startpos = pos;
				while(Character.isDigit(ch)) next();
				eatWhite();
				boolean dot = false;
				if(ch == '.' && pos < str.length() && Character.isDigit(str.charAt(pos+1)) && (dot = eat('.'))){
					eatWhite();
					while(Character.isDigit(ch)) next();
				}
				//if(!dot && !Character.isWhitespace(ch) && (eat('L') || eat('l') || eat('s') || eat('S') || eat('b') || eat('B')) || dot && (eat('f') || eat('F')));
				StringHelper sub = str.substring(startpos,pos);
				tokens.add(new Token(sub.toString().toLowerCase().trim(),sub.posAt(0).add(STARTLINE, STARTPOS)));
			}else if(Character.isLetter(ch) || ch == '_'){
				int startpos = pos;
				while(pos < str.length() && (Character.isLetterOrDigit(ch) || ch == '_')) next();
				StringHelper sub = str.substring(startpos,pos);
				tokens.add(new Token(sub.toString().trim(),sub.posAt(0).add(STARTLINE, STARTPOS)));
			}else if(ch == '@'){
				int startpos = pos;
				next();
				while(!eat('\u0001')) next();
				StringHelper sub = str.substring(startpos,pos-1);
				tokens.add(new Token(sub.toString().trim(),sub.posAt(0).add(STARTLINE, STARTPOS)));
			}else if(ch == '"' || ch == '\''){
				int end = ch;
				next();
				boolean tripleQuote = ch == end && pos+1 < str.length() && str.charAt(pos+1) == end;
				int last = 0;
				int startpos = pos-1;
				if(tripleQuote){
					next();
					next();
				}
				while(pos < str.length() && (ch != end || last == '\\')){
					last = ch;
					next();
					if(ch == '\n' && !tripleQuote) throw new RuntimeException("End of line encountered before end of string literal starting at "+str.posAt(startpos));
				}
				next();
				int add = 0;
				if(tripleQuote){
					if(!eat(end) || !eat(end)) throw new RuntimeException("Expected "+end+" at "+str.posAt(pos));
					add = 2;
				}
				
				StringHelper sub = str.substring(startpos+add,pos-add);
				tokens.add(new Token(evalString(sub.toString().trim()),sub.posAt(0).add(STARTLINE, STARTPOS)));
			}else{
				int startpos = pos;
				int c = ch;
				next();
				if(pos < str.length()){
					//y'know, i could probably compress this entire body into 1 expression.
					if(c == '=' || c == '!'){
						eat('=');
						eat('=');
					}else if(c == '<' || c == '>' || c == '+' || c == '-' || c == '*' || c == '/' || c == '%'){
						if(c == '-' && eat('>'));
						else if(!eat('=')){
							if(eat(c));
							else if(c == '<' && (eat('<') || eat('>')));
							else if(c == '>' && eat('>') && eat('>'));
							
							eat('=');
						}
					}else if(c == '&' || c == '|' || c == '^'){
						eat(c);
						eat('=');
					}else if(c == '?'){
						eat('.');
					}else if(c == '.'){
						if(ch == '.' && pos+1 < str.length() && str.charAt(pos+1) == '.'){
							eat('.');
							eat('.');
						}
					}else if(c == '#'){
						eat('#');
					}
				}
				StringHelper sub = str.substring(startpos,pos);
				tokens.add(new Token(sub.toString().trim(),sub.posAt(0).add(STARTLINE, STARTPOS)));
			}
			eatWhite();
		}
		for(int i = tokens.size()-1; i >= 0; i--){
			Token token = tokens.get(i);
			if((token.equals("defined") || token.equals("instanceof")) && i > 0 && tokens.get(i-1).equals("!")){
				tokens.remove(i);
				tokens.set(i-1, tokens.get(i-1).append(token.toString()));
			}else if((token.equals("=") || token.equals("==") || token.equals("!=")) && i > 0 && tokens.get(i-1).equals(".")){
				tokens.remove(i);
				tokens.set(i-1, tokens.get(i-1).append(token.toString()));
				if(i < tokens.size()){
					if(token.equals("==") && tokens.get(i).equals("&"))
						tokens.set(i-1, tokens.get(i-1).append(tokens.remove(i).toString()));
					else if(token.equals("!=") && tokens.get(i).equals("|"))
						tokens.set(i-1, tokens.get(i-1).append(tokens.remove(i).toString()));
				}
			}
		}
		return tokens;
	}
	private String evalString(String str){
		str = str.replaceAll("\\\\n", "\n").replaceAll("\\\\r", "\r").replaceAll("\\\\b", "\b").replaceAll("\\\\t", "\t").replaceAll("\\\\\"", "\"").replaceAll("\\\\'", "'");
		int i = str.indexOf("\\u");
		while(i != -1){
			char c = (char)Integer.parseInt(str.substring(i+2, i+6));
			str = str.substring(0, i) + c + str.substring(i+6);
			i = str.indexOf("\\u");
		}
		return str;
	}
	private StringHelper removeComments(StringHelper str){
		Pattern p1 = Pattern.compile("/([+\\*]).*\\1/");
		Matcher m = p1.matcher(str);
		while(m.find()){
			str = str.substring(0,m.start()).append(str.substring(m.end()));
			m = p1.matcher(str);
		}
		Pattern p2 = Pattern.compile("//.*\n");
		m = p2.matcher(str);
		while(m.find()){
			str = str.substring(0,m.start()).append(str.substring(m.end()));
			m = p2.matcher(str);
		}
		return str;
	}
	private StringHelper doLines(List<String> lines){
		/*{
			for(int line = 0; line < lines.size(); line++){
				char[] chars = lines.get(line).toCharArray();
				for(int pos = 0; pos < chars.length; pos++){
					positions.add(new Position(line,pos));
				}
			}
		}
		
		String result = "";
		
		for(int i = 0; i < lines.size(); i++){
			String line = lines.get(i);
			int idx = line.indexOf("\"\"\"");
			while(idx != -1){
				int idx2 = line.indexOf("\"\"\"",idx+1);
				if(idx2 == -1){
					int j = i+1;
					if(j >= lines.size()) throw new RuntimeException("Syntax error: unclosed triple quote around line "+i);
					String newline = line+"\n";
					for(; j < lines.size() && lines.get(j).indexOf("\"\"\"") == -1; j++){
						newline += lines.remove(j)+"\n";
					}
					newline += lines.get(j);
					idx = newline.indexOf("\"\"\"");
					idx2 = newline.indexOf("\"\"\"",idx+1);
					line = line.substring(0, idx) + '"' + line.substring(idx+3,idx2) + '"' + line.substring(idx2+3);
					
				}else{
					line = line.substring(0, idx) + '"' + line.substring(idx+3,idx2) + '"' + line.substring(idx2+3);
				//	lines.set(i, line);
				}
				
				idx = line.indexOf("\"\"\"");
			}
			result += removeComments(lines.get(i));
		}*/
		String str = "";
		for(int i = 0; i < lines.size(); i++){
			str += lines.get(i);
			if(i < lines.size()-1)
				str += "\n";
		}
		return removeComments(new StringHelper(str));
	}
	static List<Token> tokenize(List<String> str, int line, int startpos){
		return new Tokenizer(str).tokenize(line, startpos);
	}
	public static List<Token> tokenize(List<String> str){
		return new Tokenizer(str).tokenize(0,0);
	}
}
