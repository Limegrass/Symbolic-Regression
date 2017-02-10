import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

public class DataSet {
	HashMap<Double, Double> data;
	
	public static void main(String[] args){
		String fileName = "C:/Eclipse Java Workspace/Symbolic Regression/src/dataset1.csv";
		DataSet dataSet = new DataSet(fileName);
	}

	public DataSet(String fileName){
		data = new HashMap<Double, Double>();
		BufferedReader reader = null;
		String line = "";
		String separator = ",";
		try{
			reader = new BufferedReader(new FileReader(fileName));
			while((line = reader.readLine())!= null){
				String[] values = line.split(separator);
				try{
					double x = Double.parseDouble(values[0]);
					double y = Double.parseDouble(values[0]);
					data.put(x, y);

				}
				catch(NumberFormatException err){
					//err.printStackTrace();
				}
			}

			//				for(int i=0; i<data.size(); i++){
			//					System.out.println(x.get(i) + " " + y.get(i));
			//				}
		} 
		catch(IOException err){
			err.printStackTrace();
		}
		finally{
			if(reader != null){
				try{
					reader.close();	
				}
				catch(IOException err){
					err.printStackTrace();
				}
			}
		}
	}

	public double fitness(String expression){
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
			if((double)keysIterated/dataPoints > .5){
				break;
			}
		}
		return fitness;
	}



}
