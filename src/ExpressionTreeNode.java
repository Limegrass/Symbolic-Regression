import java.util.*;

public class ExpressionTreeNode {
	Type type;
	Object value;
	ExpressionTreeNode leftChild;
	ExpressionTreeNode rightChild;
	ExpressionTreeNode parent;

	public ExpressionTreeNode(Type type, Object value) throws IllegalArgumentException{
		this.type = type;
		if(type == Type.OPERATOR){
			if(!(value instanceof Operator)){
				throw new IllegalArgumentException("Value does not match type");
			}
			this.value = (Operator) value;
		}
		else if(type == Type.VARIABLE){
			if(!(value instanceof String)){
				throw new IllegalArgumentException("Value does not match type");
			}
			this.value = (String) value;
		}
		else if(type == Type.COEFFICIENT){
			if(!(value instanceof Double)){
				throw new IllegalArgumentException("Value does not match type");
			}
			this.value = (Double) value;
		}
		else{
			throw new IllegalArgumentException("Invalid type");
		}
		this.leftChild = null;
		this.rightChild = null;
		this.parent = null;
	}
}
