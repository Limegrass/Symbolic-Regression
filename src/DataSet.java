import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
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
	public final static double PERCENT_TEST = .5; 
	HashMap<HashMap<String, Double>, Double > data;
	HashMap<HashMap<String, Double>, Double > test;


	/**
	 * Data set constructor using a CSV file
	 * Assumed two columns of data, an input column and
	 * an output column.
	 * @param fileName The name of the file to convert into a data set.
	 */
	public DataSet(String fileName)
	{
		data = new HashMap<HashMap<String, Double>, Double >();
		test = new HashMap<HashMap<String, Double>, Double >();
		//A map is a logical way to hold all the values
		// For the data sets we seem to be working with, there is two decimals of precision, but
		// a map will be more robust.
		BufferedReader reader = null;	//Reader object 
		String line = "";				//String for line by line reading
		String separator = ",";			//Changeable parameter if the CSV is delimited by something else
		Random rand = new Random();
		try{
			//Initialize the BufferedReader
			reader = new BufferedReader(new FileReader(fileName));
			//Read all lines and input them into the map
			while((line = reader.readLine())!= null)
			{
				String[] values = line.split(separator);
				try
				{
					double y = Double.parseDouble(values[values.length-1]);
					HashMap<String, Double> xVals = new HashMap<String, Double>();
					for(int i = 0; i < values.length-1; i++){
						xVals.put( "x" + (i+1) , Double.parseDouble(values[i]));
					}
					if(rand.nextDouble() < PERCENT_TEST){
						data.put(xVals, y);
					}
					else{
						test.put(xVals, y);
					}
				}
				catch(NumberFormatException err)
				{
					//err.printStackTrace();
				}
			}
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
	 * Returns the y value for a given map of x values
	 * @param xValues the map of x values
	 * @return the y value
	 */
	public double fx(HashMap<String, Double> xValues){
		return data.get(xValues);
	}

	/**
	 * @return the set variable name to value pairs
	 */
	public Set<HashMap<String, Double>> xValues(){
		return data.keySet();
	}

	/**
	 * Fitness function to complete for checking the
	 * fitness of a linear regression
	 * @param expression Expression to evaluate how well it fits the data.
	 * @return The fitness of the inputed expression tree relative to the data set.
	 */
	public double fitness(ExpressionTree expression, boolean testAll){
		double fitness = 0;
		for(HashMap<String,Double> map : data.keySet()){
			double error = data.get(map) - expression.evaluate(map);
//			fitness += (error * error) / data.size();
			fitness+=error*error;
		}
		if(testAll){
			for(HashMap<String,Double> test : data.keySet()){
				double error = data.get(test) - expression.evaluate(test);
//				fitness += (error * error) / data.size();
				fitness+=error*error;
			}
		}
//		fitness+=expression.getSize();
		return fitness;
//		return Math.sqrt(fitness);
	}


}
