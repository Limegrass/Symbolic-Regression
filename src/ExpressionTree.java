
import java.util.Random; //Generating random initial population

/**
 * Probably already done by Chris, to be imported
 * @author James
 *
 */
public class ExpressionTree 
{
	
	private ExpressionNode root;
	
	public ExpressionTree()
	{
		this.root = null;
	}
	
	private class ExpressionNode
	{
		//Assign operation value to correspond to a 
		//Different Operator
		private int operation;
		private int value;
		
		public ExpressionNode()
		{
			this.operation = 0;
			this.value = 0;
		}
		
	}
	
	private ExpressionTree[] crossover(ExpressionTree other)
	{
		
		return null;
	}
	
	private ExpressionTree mutation()
	{
		return null;
	}
	
}
