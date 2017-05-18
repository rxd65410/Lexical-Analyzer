package compiler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SortingFocusTraversalPolicy;

public class LexicalAnalyzer {
	public static Boolean commentStarted = false;
	public static ArrayList<String> oper = new ArrayList<String>();
	public static ArrayList<String> id = new ArrayList<String>();
	public static ArrayList<String> alpha = new ArrayList<String>();
	public static ArrayList<String> num = new ArrayList<String>();
	public static ArrayList<String> fraction = new ArrayList<String>();
	public static ArrayList<String> error = new ArrayList<String>();
	public static String[] VALID_OPER = {"\"","==","+","-","(","<>",")","<","*",
									"{",">","/","}","<=","=","[",">=",
									"and","]",";","not",",","or",".","//",
									"put","if","void","then","main","else",
									"for","class","int","float","get","return"};
	
  public static enum TokenType {
    // Token types cannot have underscores
	  
	  //teja("([a-zA-Z][0-9a-zA-Z_]*)"),
	  WHITESPACE("[ \t\f\r\n]+"),
	  ERROR("([0-9]*\\.[0-9]*\\.[0-9]*\\.[0-9]*)+"),
	  FRACTION("((\\.[0-9]*[1-9])|(\\.0))"),
	  //OP("==|=|\\+|(|<>|)|<|\\*|\\{|>|/|\\}|<=|[|>=|and|]|;|int|not|,|or|\\.|put|if|void|then|main|else|for|class|int|float|get|return"),
	  //OP("\\(|\\)|\\{|\\}|\\==|\\=|;|%|int|not|,|or|\\.|put|if|\\void|then|main|else|for|class|int|float|get|return"),
	  OP("\\[|\\]|\\(|\\)|\\{|\\}|\\==|\\=|;|%|,|\\.|\\!|\\@|\\#|\\$|\\^|\\&|\\/|\\*|\\++|\\--|\\+|-|<>|<=|>=|<|>"),
	  ID("([a-zA-Z][0-9a-zA-Z_]*)"),
	 // FLOAT("([1-9][0-9]*|0)((\\.[0-9]*[1-9])|(\\.0))"),
	 // NUM("(([1-9][0-9]*|0)|([[1-9][0-9]*|0][\\.[0-9]*[1-9]|\\.0]))"),
	  //NUM("(([1-9][0-9]*|0) | ([1-9][0-9]*|0)((\\.[0-9]*[1-9])|(\\.0)))"),
	  //NUM("(([1-9][0-9]*|[0])|(([1-9][0-9]*|[0])(([.][0-9]*[1-9])|([.][0]))))"),
	  //NUM("(([1-9][0-9]*|[0])|(([1-9][0-9]*|[0])(([.][0-9]*[1-9])|([.][0]))))"),
	  NUM("([0-9]*[.])?[0-9]+"),
	  ALPHA("([a-zA-Z |_])");
	  
	  
	  //NONZERO("[1-9]"),
	  //LETTER("[a-zA-Z]"),
	  //FRACTION("((.[0-9]*[1-9])|(.0))"),
	  //INTEGER("([1-9][0-9]*|0)"),
	  //FLOAT("([1-9][0-9]*|0)((.[0-9]*[1-9])|(.0))"),
	  
	  //ravi("\\d+(\\.\\d+)?"),
	  //DIGIT("[0-9]");
	  
	  
	 // NUMBERRAVI("-?[0-9]+"), BINARYOP("[*|/|+|-]"), WHITESPACE("[ \t\f\r\n]+"),
    //id (letter alphanum*), alphanum ("letter | digit | _ "),num = integer | float,
    
    

    public final String pattern;

    private TokenType(String pattern) {
      this.pattern = pattern;
    }
  }

  public static class Token {
    public TokenType type;
    public String data;

    public Token(TokenType type, String data) {
      this.type = type;
      this.data = data;
    }

    @Override
    public String toString() {
      return String.format("(%s %s)", type.name(), data);
    }
  }

  public static ArrayList<Token> lex(String input) {	  
    // The tokens to return
    ArrayList<Token> tokens = new ArrayList<Token>();

    // Lexer logic begins here
    StringBuffer tokenPatternsBuffer = new StringBuffer();
    for (TokenType tokenType : TokenType.values())
      tokenPatternsBuffer.append(String.format("|(?<%s>%s)", tokenType.name(), tokenType.pattern));
    
    Pattern tokenPatterns = Pattern.compile(new String(tokenPatternsBuffer.substring(1)));

    // Begin matching tokens
    Matcher matcher = tokenPatterns.matcher(input);
    
    while (matcher.find()) {
      if (matcher.group(TokenType.WHITESPACE.name()) != null){
      	  continue;
      }else if(matcher.group(TokenType.ERROR.name())!=null){
    	  error.add(matcher.group(TokenType.ERROR.name().toString()));
    	  continue;
      }else if(matcher.group(TokenType.FRACTION.name())!=null){
    	  fraction.add(matcher.group(TokenType.FRACTION.name().toString()));
    	  continue;
      }else if (matcher.group(TokenType.OP.name()) != null) {
          //tokens.add(new Token(TokenType.OP, matcher.group(TokenType.OP.name())));
    	  String idTemp = matcher.group(TokenType.OP.name().toString());
    	  if(Arrays.asList(VALID_OPER).contains(idTemp)){
          	oper.add(idTemp);
          }else{
        	  error.add(idTemp);  
          }
          
          continue;
      }else if (matcher.group(TokenType.ID.name()) != null) {
    	  String idTemp = matcher.group(TokenType.ID.name().toString()); 
        tokens.add(new Token(TokenType.ID, matcher.group(TokenType.ID.name())));
       if(Arrays.asList(VALID_OPER).contains(idTemp)){
        	oper.add(idTemp);
        }else{
        id.add(idTemp);
        }
        continue;
      }else if (matcher.group(TokenType.NUM.name()) != null ) {
    	  String tok = matcher.group(TokenType.NUM.name());
    	 if(tok.length()==1){
    		 tokens.add(new Token(TokenType.ALPHA, matcher.group(TokenType.NUM.name())));
    		 alpha.add(matcher.group(TokenType.NUM.name().toString()));
    	 }else if((!tok.startsWith("0.") && tok.startsWith("0")) || (!tok.endsWith(".0") &&
    			 tok.contains(".")&&
    			 tok.endsWith("0"))){
    		 error.add(tok);
    	 }else{
             tokens.add(new Token(TokenType.NUM, matcher.group(TokenType.NUM.name())));
             num.add(matcher.group(TokenType.NUM.name().toString()));
    	 }
          continue;
     } else if (matcher.group(TokenType.ALPHA.name()) != null) {
          tokens.add(new Token(TokenType.ALPHA, matcher.group(TokenType.ALPHA.name())));
          alpha.add(matcher.group(TokenType.ALPHA.name().toString()));
          continue;
     }else{
    	 System.out.print("here error..."+ matcher.group());
    	 if(matcher.group().equals("0")){
    		 alpha.add(matcher.group());
    	 }else if(!matcher.group().equals("")){
    	 System.out.print("here error..."+ matcher.group());
    	 error.add(matcher.group());
    	 }
    	 continue;
     }
    }

    return tokens;
  }
  
  public static void read(File file) throws IOException{
	    Scanner scanner = new Scanner(file);
	    String del = ",|;";
	    while(scanner.hasNext()){
	    	String line = scanner.nextLine();
	    	//lex(line);
	    	System.out.println("input is : " + line);
	    	
	    	String[] input = line.split(" ");
	    	
	    	for(String str : input){
	    		if(str.contains("/*")){
	    			commentStarted=true;
	    		}else if(str.contains("*/")){
	    			commentStarted=false;
	    		}
	    		//System.out.println("input is : " + str);
	    		if(!commentStarted && !str.contains("*/") && !str.equals("")){
	    			System.out.println("Passing to Lex func is : " + str);
	    			lex(str);
	    		}
		        
	    	}
	    }
	    scanner.close();
	}

  public static void main(String[] args) {
    //String input = "int a=15.0, b_=4, c=5.69;";
    String file = "/Users/raviteja/cd.txt";
    
    try {
		read(new File(file));
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    System.out.println("\n\nOperaters are : ");
    for(int i=0;i<oper.size();i++){
    	System.out.print(oper.get(i)+" ");
    	if(i!=0 && i%15==0)
    		System.out.println("");
    }
    System.out.println("\n\nIDENTIFIERS : ");
    for(int i=0;i<id.size();i++){
    	System.out.print(id.get(i)+" ");
    	if(i!=0 && i%5==0)
    		System.out.println("");
    }
    System.out.println("\n\nALPHANUMBER: ");
    for(int i=0;i<alpha.size();i++){
    	System.out.print(alpha.get(i)+" ");
    	if(i!=0 && i%5==0)
    		System.out.println("");
    }
    System.out.println("\n\nNUMBERS : ");
    for(int i=0;i<num.size();i++){
    	System.out.print(num.get(i)+" ");
    	if(i!=0 && i%5==0)
    		System.out.println("");
    }
    System.out.println("\n\nFraction : ");
    for(int i=0;i<fraction.size();i++){
    	System.out.print(fraction.get(i)+" ");
    	if(i!=0 && i%5==0)
    		System.out.println("");
    }
    /* er werjhwjr sdf  sdg ert */
    System.out.println("\n\nERROR : ");
    for(int i=0;i<error.size();i++){
    	System.out.print(error.get(i)+" ");
    	if(i!=0 && i%5==0)
    		System.out.println("");
    }
    
   
  }
}
