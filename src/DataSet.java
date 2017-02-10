import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;


/**
 * Parse CSV Files into a Data Set type for use
 * as a expression fitness function evaluator 
 * when using genetic programming for symbolic regress
 * @author James Ni
 *
 */
public class DataSet 
{
	HashMap<Double, Double> data;
	
	/**
	 * Main function, as a tester
	 * @param args Arguments
	 */
	public static void main(String[] args)
	{
		String fileName = "C:/Eclipse Java Workspace/Symbolic Regression/src/dataset1.csv";
		DataSet dataSet = new DataSet(fileName);
	}

	
	/**
	 * Data set constructor using a CSV file
	 * Assumed two columns of data, an input column and
	 * an output column.
	 * @param fileName The name of the file to convert into a data set.
	 */
	public DataSet(String fileName)
	{
		data = new HashMap<Double, Double>();	//A map is a logical way to hold all the values
		//An alternative would be a hash map if I knew the range of the data.
		// For the data sets we seem to be working with, there is two decimals of precision, but
		// a map will be more robust.
		BufferedReader reader = null;	//Reader object 
		String line = "";				//String for line by line reading
		String separator = ",";			//Changeable parameter if the CSV is delimited by something else
		
		try{
			//Initialize the BufferedReader
			reader = new BufferedReader(new FileReader(fileName));
			//Read all lines and input them into the map
			while((line = reader.readLine())!= null)
			{
				String[] values = line.split(separator);
				try
				{
					double x = Double.parseDouble(values[0]);
					double y = Double.parseDouble(values[1]);
					data.put(x, y);
				}
				catch(NumberFormatException err)
				{
					//err.printStackTrace();
				}
			}

			//				for(int i=0; i<data.size(); i++){
			//					System.out.println(x.get(i) + " " + y.get(i));
			//				}
		} 
		catch(IOException err)
		{
			err.printStackTrace();
		}
		finally{
			if(reader != null)
			{
				try
				{
					reader.close();	
				}
				catch(IOException err)
				{
					err.printStackTrace();
				}
			}
		}
	}

	/**
	 * Fitness function to complete for checking the
	 * fitness of a linear regression
	 * @param expression Expression to evaluate how well it fits the data.
	 * @return The fitness of the inputed expression tree relative to the data set.
	 */
	public double fitness(ExpressionTree expression)
	{
		double fitness = 0;
		//Parse the expression to be a function
		Set<Double> keySet = data.keySet();
		int keysIterated = 0;
		int dataPoints = keySet.size();
		for(Double x: keySet){
			//Calculate parsed value
			//Subtract the different between that and the Map's value for x.
			//Square the difference
			//Add it to total fitness
			keysIterated++;
			if((double)keysIterated/dataPoints > .5)
			{
				break;
			}
		}
		return fitness;
	}



}
