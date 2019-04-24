
public class SymbolType {

  enum Type {
    INT {
      @Override
      public String toString() {
        return "int";
      }

    },
    INT_ARR {
      @Override
      public String toString() {
        return "int[]";
      }
    },
    BOOLEAN {
      @Override
      public String toString() {
        return "boolean";
      }
    },
    VOID {
      @Override
      public String toString() {
        return "void";
      }
    },
    ERROR {
      @Override
      public String toString() {
        return "error";
      }
    };
  }

  public static Type getType(String varType) {
    switch (varType) {
    case "void":
      return Type.VOID;
    case "int":
      return Type.INT;
    case "boolean":
      return Type.BOOLEAN;
    case "int[]":
      return Type.INT_ARR;
    default:
      return Type.ERROR;
    }
  }

  String symbol;
  Type type;

  @Override
  public String toString() {
    if (this.symbol == null) {
      return "  -> " + type.toString();
    } else {
      return symbol + " -> " + type.toString();
    }
  }

  SymbolType(String symbol, String varType) {
    this.symbol = symbol;
    this.type = getType(varType);
  }

  SymbolType(String varType) {
    this.type = getType(varType);
  }
}
