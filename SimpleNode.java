import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

/* Generated By:JJTree: Do not edit this line. SimpleNode.java Version 6.0 */
/* JavaCCOptions:MULTI=false,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public class SimpleNode implements Node {

  protected Node parent;
  protected Node[] children;
  protected int id;
  protected Object value;
  protected NewJava parser;
  protected String symbol;
  protected int line;
  protected int column;

  public SimpleNode(int i) {
    id = i;
  }

  public SimpleNode(NewJava p, int i) {
    this(i);
    parser = p;
  }

  public void jjtOpen() {
  }

  public void jjtClose() {
  }

  public void jjtSetParent(Node n) {
    parent = n;
  }

  public Node jjtGetParent() {
    return parent;
  }

  public void jjtAddChild(Node n, int i) {
    if (children == null) {
      children = new Node[i + 1];
    } else if (i >= children.length) {
      Node c[] = new Node[i + 1];
      System.arraycopy(children, 0, c, 0, children.length);
      children = c;
    }
    children[i] = n;
  }

  public Node jjtGetChild(int i) {
    return children[i];
  }

  public int jjtGetNumChildren() {
    return (children == null) ? 0 : children.length;
  }

  public void jjtSetValue(Object value) {
    this.value = value;
  }

  public Object jjtGetValue() {
    return value;
  }

  /*
   * You can override these two methods in subclasses of SimpleNode to customize
   * the way the node appears when the tree is dumped. If your output uses more
   * than one line you should override toString(String), otherwise overriding
   * toString() is probably all you need to do.
   */

  public String toString() {
    return NewJavaTreeConstants.jjtNodeName[id];
  }

  public String toString(String prefix) {
    return prefix + toString();
  }

  /*
   * Override this method if you want to customize how the node dumps out its
   * children.
   */

  public void dump(String prefix) {
    System.out.print(toString(prefix));
    if (this.symbol != null)
      System.out.print(" " + this.symbol);
    System.out.println();
    if (children != null) {
      for (int i = 0; i < children.length; ++i) {
        SimpleNode n = (SimpleNode) children[i];
        if (n != null) {
          n.dump(prefix + " ");
        }
      }
    }
  }

  public int getId() {
    return id;
  }

  public String getSymbol() {
    return symbol;
  }

  public Object getValue() {
    return value;
  }

  public int getLineNumber() {
    return line;
  }

  public void setLineNumber(int lineNumber) {
    this.line = lineNumber;
  }

  public int getColumnNumber() {
    return column;
  }


  @Override
  public Object visit(SymbolTable data, int functionNum) {
    System.out.println("id = " + id + ", symbol = " + symbol);

    if (id == NewJava.JJTVAR) {
      String name = (String) this.getSymbol();
      String type = data.checkIfExists(name, functionNum);  //checkar locais

      if(type.equals("error")){
        type = data.searchParam(name, functionNum);         //checkar params
      }

      if(type.equals("error")){
        type = data.checkIfExistGlobals(name);             //checkar globais
      }

      if (type.equals("error")) {
        if (this.jjtGetNumChildren() > 0) {
          if (!Main.tables.containsKey(this.jjtGetChild(0).getSymbol())) {         //checkar se é outra classe
            int lineNum = this.jjtGetChild(0).getLineNumber();
            System.out.println("\n" + NewJava.filePath + ":" + lineNum + ": error: cannot find symbol");

            try (Stream<String> lines = Files.lines(Paths.get(NewJava.filePath))) {
              System.out.println(lines.skip(lineNum - 1).findFirst().get());
              System.out.println("    ^");
            } catch (IOException e) {
              e.printStackTrace();
            }

            System.out.println("    symbol:   class " + ((SimpleNode) this.jjtGetChild(0)).getSymbol());
            System.out.println("    location: file " + NewJava.filePath + "\n");
            return SymbolType.Type.ERROR.toString();
          } else {
            return this.jjtGetChild(0).getSymbol();
          }
        }
      }

      // variable does not have unique name
      if (type.contains("/")) {
        String[] responseArr = type.split("/");

        int lineNum = Integer.parseInt(responseArr[0]);

        // node with repeated variable name
        if (lineNum == this.line) {
          String args;
          args = "(";

          int index;
          while ((index = responseArr[2].indexOf("->")) != -1) {
            if (args.length() > 1) {
              args += ", ";
            }

            int finalIndex = responseArr[2].indexOf(",");
            if (finalIndex == -1) {
              finalIndex = responseArr[2].indexOf("]");
            }

            args += responseArr[2].substring(index + 3, finalIndex);
            responseArr[2] = responseArr[2].substring(finalIndex + 1);
          }
          args += ")";

          System.out.println("\n" + NewJava.filePath + ":" + lineNum + ": error: variable " + this.symbol
              + " is already defined in method " + responseArr[1] + args);

          try (Stream<String> lines = Files.lines(Paths.get(NewJava.filePath))) {
            System.out.println(lines.skip(lineNum - 1).findFirst().get());
            System.out.println("    ^");
          } catch (IOException e) {
            e.printStackTrace();
          }

          return SymbolType.Type.ERROR.toString();
        }
      }

      return type.toString();
    }

    if (id == NewJava.JJTTRUE || id == NewJava.JJTFALSE) {
      return "boolean";
    }

    if (id == NewJava.JJTVAL) {
      return "int";
    }

    if (id == NewJava.JJTNEW) {
      return symbol;
    }

    // type of arguments
    if (id == NewJava.JJTTEXT) {
      String type = data.checkIfExists(this.getSymbol(), functionNum);

      if(type.equals("error")){
        return data.searchParam(symbol, functionNum);
      }

      if(type.equals("error")){
        type = data.checkIfExistGlobals(symbol);
      }

      return type;
    }

    if (id == NewJava.JJTASSIGN) {

      //Lado esquerdo assign
      Object identifierType = this.jjtGetChild(0).visit(data, functionNum);

      if (identifierType.equals(SymbolType.Type.ERROR.toString())) {
        int lineNum = this.jjtGetChild(0).getLineNumber();
        System.out.println("\n" + NewJava.filePath + ":" + lineNum + ": error: cannot find symbol");

        try (Stream<String> lines = Files.lines(Paths.get(NewJava.filePath))) {
          System.out.println(lines.skip(lineNum - 1).findFirst().get());
          System.out.println("    ^");
        } catch (IOException e) {
          e.printStackTrace();
        }

        System.out.println("    symbol:   variable " + ((SimpleNode) this.jjtGetChild(0)).getSymbol());
        System.out.println("    location: file " + NewJava.filePath + "\n");
        return SymbolType.Type.ERROR.toString();
      }

      //Lado direito assign
      Object expressionType = new Object();
      boolean ok = true;

      if((this.jjtGetChild(1).getId() == NewJava.JJTOP2) || (this.jjtGetChild(1).getId() == NewJava.JJTOP3) || (this.jjtGetChild(1).getId() == NewJava.JJTOP4) || (this.jjtGetChild(1).getId() == NewJava.JJTOP5)){
        expressionType = "int";
        ok = checkOpType((SimpleNode)this.jjtGetChild(1), data, functionNum);

      } else{
        expressionType = this.jjtGetChild(1).visit(data, functionNum);
      }

      if (!ok){
        int lineNum = this.jjtGetChild(0).getLineNumber();
        System.out.println("\n" + NewJava.filePath + ":" + lineNum + ": error: bad operand types for binary operator '" + this.jjtGetChild(1).getSymbol() + "'");

        try (Stream<String> lines = Files.lines(Paths.get(NewJava.filePath))) {
          String errorLine = lines.skip(lineNum - 1).findFirst().get();
          System.out.println(errorLine);
          int index = this.jjtGetChild(1).getColumnNumber();
          while(index > 1) {
            System.out.print(" ");
            index--;
          }
          System.out.print("^\n");
        } catch (IOException e) {
          e.printStackTrace();
        }

        return SymbolType.Type.ERROR.toString();
      }

      if (!identifierType.equals(expressionType)) {
        int lineNum = this.jjtGetChild(0).getLineNumber();
        System.out.println("\n" + NewJava.filePath + ":" + lineNum + ": error: incompatible types: " + expressionType
            + " cannot be converted to " + identifierType);

        try (Stream<String> lines = Files.lines(Paths.get(NewJava.filePath))) {
          System.out.println(lines.skip(lineNum - 1).findFirst().get());
          int index = this.jjtGetChild(1).getColumnNumber();
          while(index > 1) {
            System.out.print(" ");
            index--;
          }
          System.out.print("^\n");
        } catch (IOException e) {
          e.printStackTrace();
        }

        return SymbolType.Type.ERROR.toString();
      }
    }

    // Function type must match return type

    if (this.id == NewJava.JJTRETURN) {
      for(int i = 0; i < this.jjtGetNumChildren(); i++){
        SimpleNode aux = (SimpleNode)this.jjtGetChild(0);

        String type = new String();

        if (aux.getId() == NewJava.JJTVAL) {
          type = "int";
        }

        if (aux.getId() == NewJava.JJTTRUE || aux.getId() == NewJava.JJTFALSE) {
          type = "boolean";
        }

        if(aux.getId() == NewJava.JJTTEXT){
          type = data.checkIfExists(aux.getSymbol(), functionNum);

          if(type.equals("error")){
            type = data.searchParam(aux.getSymbol(),functionNum);
          }

          if(type.equals("error")){
            type = data.checkIfExistGlobals(aux.getSymbol());
          }
        }

        String dataTypeBroken = data.getReturn(functionNum);

        int index = dataTypeBroken.indexOf("->");

        String dataType = dataTypeBroken.substring(index + 3, dataTypeBroken.length());

        if(!type.equals(dataType)){
          int lineNum = this.jjtGetChild(i).getLineNumber();
          System.out.println("\n" + NewJava.filePath + ":" + lineNum + ":  error: incompatible types: " + type + " cannot be converted to " + dataType);
          try (Stream<String> lines = Files.lines(Paths.get(NewJava.filePath))) {
            System.out.println();
            System.out.println(lines.skip(lineNum - 1).findFirst().get());
            System.out.println("           ^");
          } catch (IOException e) {
            e.printStackTrace();
          }
          return SymbolType.Type.ERROR.toString();
        }
      }
    }
     

    // ------------------------------------------------------------------------------
    // Function call -> must exist
    // -> must have right number of args of right types

    // existe .length
    if (id == NewJava.JJTFULLSTOP) {
      //for (int i = 0; i < this.jjtGetNumChildren(); i++)
      //System.out.println("symbol = " + this.jjtGetChild(i));
    }

    return "nothing";
  }
 
  private boolean checkOpType(SimpleNode startOP, SymbolTable data, int functionNum) {
    if(startOP.jjtGetNumChildren() != 2){
      return false;
    }
    
    
    //ramo esquerdo
    //se nao for op, verifica se e inteiro
    if(startOP.jjtGetChild(0).getId() != NewJava.JJTOP2 && startOP.jjtGetChild(0).getId() != NewJava.JJTOP3 && startOP.jjtGetChild(0).getId() != NewJava.JJTOP4 && startOP.jjtGetChild(0).getId() != NewJava.JJTOP5){
      String type = (String)startOP.jjtGetChild(0).visit(data, functionNum);
      if(!type.equals("int")){
        return false;
      }   
    //se for op, verifica se e vailda 
    } else if(!checkOpType((SimpleNode)startOP.jjtGetChild(0), data, functionNum))
      return false;

    //ramo direito
    if(startOP.jjtGetChild(1).getId() != NewJava.JJTOP2 && startOP.jjtGetChild(1).getId() != NewJava.JJTOP3 && startOP.jjtGetChild(1).getId() != NewJava.JJTOP4 && startOP.jjtGetChild(1).getId() != NewJava.JJTOP5){
      String type = (String)startOP.jjtGetChild(1).visit(data, functionNum);
      if(!type.equals("int")){
        return false;
      }    
    } else if(!checkOpType((SimpleNode)startOP.jjtGetChild(1), data, functionNum))
      return false;


    return true;
  }

}

/*
 * JavaCC - OriginalChecksum=9eef391808ffa9c1f856b164b9ceaad4 (do not edit this
 * line)
 */
