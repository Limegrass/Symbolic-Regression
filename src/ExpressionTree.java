import java.util.*;

public class ExpressionTree {
	ExpressionTreeNode root;

	public ExpressionTree(List<Operator> operators, List<String> variables, List<Double> coefficients){
		Queue<ExpressionTreeNode> q1 = new LinkedList<ExpressionTreeNode>();
		Queue<ExpressionTreeNode> q2 = new LinkedList<ExpressionTreeNode>();
		for(Operator op : operators){
			q1.add(new ExpressionTreeNode(Type.OPERATOR, op));
		}
		for(String var : variables){
			q2.add(new ExpressionTreeNode(Type.VARIABLE, var));
		}
		for(Double coef: coefficients){
			q2.add(new ExpressionTreeNode(Type.COEFFICIENT, coef));
		}
		ExpressionTreeNode current = null;
		//While there are still operators remaining
		while(!q1.isEmpty()){
			//Poll an operator from q1
			current = q1.poll();
			//Set its left and right children to trees from q2
			current.leftChild = q2.poll();
			current.leftChild.parent = current;
			current.rightChild = q2.poll();
			current.rightChild.parent = current;
			//Add the new larger tree back to q2
			q2.add(current);
		}
		this.root = current;
	}

	public ExpressionTree(ExpressionTreeNode root){
		this.root = root;
	}

	public static void print(ExpressionTreeNode node){
		if(node == null){
			return;
		}
		if(node.leftChild != null){
			System.out.print("(");
			print(node.leftChild);
		}
		System.out.print(node.value.toString() + "");
		if(node.rightChild != null){
			print(node.rightChild);
			System.out.print(")");
		}

	}

	public static double evaluate(ExpressionTreeNode node, HashMap<String, Double> variables) throws IllegalArgumentException{
		if(node == null){
			return 0.0;
		}
		if(node.type == Type.COEFFICIENT){
			return (double) node.value;
		}
		else if(node.type == Type.VARIABLE){
			if(!variables.containsKey(node.value)){
				throw new IllegalArgumentException("Variable not defined");
			}
			return variables.get(node.value);
		}
		else{
			double left = evaluate(node.leftChild, variables);
			double right = evaluate(node.rightChild, variables);
			if(node.value == Operator.ADD){
				return left + right;
			}
			else if(node.value == Operator.SUBTRACT){
				return left - right;
			}
			else if(node.value == Operator.MULTIPLY){
				return left * right;
			}
			else if(node.value == Operator.DIVIDE){
				if(right == 0.0){
					//Dividing by 0 return 1 right now we need to figure out what we want to do with this.
					return 1.0;
				}
				return left / right;
			}
			else{
				return 0.0;
			}
		}
	}

	public ExpressionTree clone(){
		if(this.root == null){
			return null;
		}
		//Deep copies root node and creates new tree rooted at copy
		return new ExpressionTree(copy(this.root));
	}

	private static ExpressionTreeNode copy(ExpressionTreeNode node){
		if(node == null){
			return null;
		}
		//Recursively creates deep copy of node
		ExpressionTreeNode copy = new ExpressionTreeNode(node.type, node.value);
		copy.leftChild = copy(node.leftChild);
		if(copy.leftChild != null){
			copy.leftChild.parent = copy;
		}
		copy.rightChild = copy(node.rightChild);
		if(copy.rightChild != null){
			copy.rightChild.parent = copy;
		}
		return copy;
	}

	public static int getSize(ExpressionTreeNode node){
		if(node == null){
			return 0;
		}
		return 1 + getSize(node.leftChild) + getSize(node.rightChild);
	}

	public ExpressionTreeNode getRandomNode(){
		//Get the size of the tree generate a random position less than size return node
		//in that position of in order traversal
		int size = getSize(this.root);
		Random r = new Random();
		int position = r.nextInt(size-1);
		return getKthNode(this.root, position);

	}

	private ExpressionTreeNode getKthNode(ExpressionTreeNode node, int k){
		int leftSize = getSize(node.leftChild);
		//If size of left subtree equals k node is kth element 
		if(leftSize == k){
			return node;
		}
		//If left subtree is greater than k kth element is in left subtree
		else if(leftSize > k){
			return getKthNode(node.leftChild, k);
		}
		//If left subtree is less than k kth element is k - leftSize - 1st element in right subtree
		else{
			return getKthNode(node.rightChild, k - leftSize - 1);
		}
	}

	public List<ExpressionTree> crossover(ExpressionTree other){
		List<ExpressionTree> output = new ArrayList<ExpressionTree>();
		//Clone the two expression trees to be crossed over
		ExpressionTree offspringOne = this.clone();
		ExpressionTree offspringTwo = other.clone();
		//Select random crossover points that are not the root of the trees
		ExpressionTreeNode crossoverPointOne = offspringOne.getRandomNode();
		while(crossoverPointOne.parent == null){
			crossoverPointOne = offspringOne.getRandomNode();
		}
		ExpressionTreeNode crossoverPointTwo = offspringTwo.getRandomNode();
		while(crossoverPointTwo.parent == null){
			crossoverPointTwo = offspringTwo.getRandomNode();
		}
		
		//Swap crossover point one and crossover point two
		if(crossoverPointOne.parent.leftChild == crossoverPointOne){
			crossoverPointOne.parent.leftChild = crossoverPointTwo;
		}
		else{
			crossoverPointOne.parent.rightChild = crossoverPointTwo;
		}

		if(crossoverPointTwo.parent.leftChild == crossoverPointTwo){
			crossoverPointTwo.parent.leftChild = crossoverPointOne;
		}
		else{
			crossoverPointTwo.parent.rightChild = crossoverPointOne;
		}
		
		//Swap parent pointers for crossover point one and crossover point two
		ExpressionTreeNode temp = crossoverPointOne.parent;
		crossoverPointOne.parent = crossoverPointTwo.parent;
		crossoverPointTwo.parent = temp;
		
		output.add(offspringOne);
		output.add(offspringTwo);
		return output;
	}

	public static void mutate(ExpressionTreeNode node, double mutationRate, int minMutation, int maxMutation, Random r){
		//Only mutate coefficients
		if(node.type == Type.COEFFICIENT){
			if(r.nextDouble() < mutationRate){
				node.value = (double) node.value + (double) (r.nextInt(maxMutation - minMutation + 1) + minMutation);
			}
		}
		if(node.leftChild != null){
			mutate(node.leftChild, mutationRate, minMutation, maxMutation, r);
		}
		if(node.rightChild != null){
			mutate(node.rightChild, mutationRate, minMutation, maxMutation, r);
		}
	}
}
