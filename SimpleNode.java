import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

  @Override
  public Object visit(SymbolTable data, int functionNum) {
    //System.out.println("\n\nid = " + this.id + ", symbol = " + this.symbol);

     /*
     for (int i = 0; i < this.jjtGetNumChildren(); i++) {
      System.out.println("cr id = " + this.jjtGetChild(i).getId() + ", symbol = " + this.jjtGetChild(i).getSymbol());
     }
     */

    if (id == NewJava.JJTVAR) {
      String name = (String) this.getSymbol();
      String type = data.checkIfExists(name, functionNum);

      System.out.println("TYPE = " + type);

      if (type.equals("error")) {
        if (this.jjtGetNumChildren() > 0)
        System.out.println("COISAS!!!! " + this.jjtGetChild(0).getSymbol());
        //return this.jjtGetChild(0).getSymbol();
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

    if (id == NewJava.JJTASSIGN) {
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

      Object expressionType = this.jjtGetChild(1).visit(data, functionNum);

      if (!identifierType.equals(expressionType)) {
        int lineNum = this.jjtGetChild(0).getLineNumber();
        System.out.println("\n" + NewJava.filePath + ":" + lineNum + ": error: incompatible types: " + expressionType
            + " cannot be converted to " + identifierType);

        try (Stream<String> lines = Files.lines(Paths.get(NewJava.filePath))) {
          System.out.println(lines.skip(lineNum - 1).findFirst().get());
          System.out.println("    ^");
        } catch (IOException e) {
          e.printStackTrace();
        }

        return SymbolType.Type.ERROR.toString();
      }
    }

    // Function type must match return type

    /*
     * // function type if (this.id == NewJava.JJTTYPE) { Object identifierType =
     * this.symbol;
     * 
     * System.out.println("function!!! = " + identifierType); }
     * 
     * if (this.id == NewJava.JJTRETURN) { System.out.println("return = " +
     * this.symbol + ", value = " + this.value); System.out.println("coisas = " +
     * data.checkReturnValue()); }
     */

    // ------------------------------------------------------------------------------
    // Function call -> must exist
    // -> must have right number of args of right types

    /**
     * to do
     */

    // ------------------------------------------------------------------------------
    // Function (if not void) must have return statement

    /**
     * to do
     */

    return "stuff";
  }
}
/*
 * JavaCC - OriginalChecksum=9eef391808ffa9c1f856b164b9ceaad4 (do not edit this
 * line)
 */
