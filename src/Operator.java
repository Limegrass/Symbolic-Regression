
public enum Operator {
	ADD, SUBTRACT, MULTIPLY, DIVIDE;
	
	@Override
	public String toString(){
		if(this == ADD) {
			return " + ";
		}

		if(this == SUBTRACT) {
			return " - ";
		}
		if(this == MULTIPLY) {
			return " * ";
		}

		if(this == DIVIDE) {
			return " / ";
		}
		return "unknown";
	}

}
