//$Id$
package Testing;

import org.encog.neural.NeuralNetworkError;

import NeuralNetworks.Hopfield;
import NeuralNetworks.NeuralUtil;

public class HopfieldTest {
	static float [] data [] = {
		 {1, 1, 1,-1,-1,-1,-1,-1,-1,-1}, 
		{-1, -1, -1, 1, 1, 1, -1, -1, -1, -1}, 
		{-1, -1, -1, -1, -1, -1, -1, 1, 1, 1}
		};
	
	public static void main(String args[]){
		Hopfield test = new Hopfield(10);
		test.addTrainingData(data[0]);
		test.addTrainingData(data[1]);
		test.addTrainingData(data[2]);
		test.train();
		 helper(test, "pattern 0", data[0]);
		 helper(test, "pattern 1", data[1]);
		 helper(test, "pattern 2", data[2]);
		
	}
	 private static void helper(Hopfield test, String s,float [] test_data) {
		 float [] dd = new float[10];
		 for (int i=0; i<10; i++) {
			 dd[i] = test_data[i];
		 }
		 int index = (int)(9.0f * (float)Math.random());
		 int index2 = (int)(9.0f * (float)Math.random());
		 if (dd[index] < 0.0f) dd[index] =  1.0f;
		 else     	              
			 dd[index] = -1.0f;
		 
//		 if (dd[index2] < 0.0f) dd[index2] =  1.0f;
//		 else     	              
//			 dd[index2] = -1.0f;
		 
		 float [] rr = test.recall(dd, 5);
		 
		 System.out.print(s+"\nOriginal data:      ");
		 for (int i = 0; i < 10; i++)
			 System.out.print(Math.round(NeuralUtil.clamp(test_data[i],0.0f,1.0f)) + " ");
		 
		 System.out.print("\nRandomized data:    ");
		  for (int i = 0; i < 10; i++)
		  		System.out.print(Math.round(NeuralUtil.clamp(dd[i],0.0f,1.0f)) + " ");

		  System.out.print("\nRecognized pattern: ");
		  	for (int i = 0; i < 10; i++)
		  		System.out.print(Math.round(NeuralUtil.clamp(rr[i],0.0f,1.0f)) + " ");
		  	
		  System.out.println();
}
}
