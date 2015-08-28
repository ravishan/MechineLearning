//$Id$
package NeuralNetworks;

import java.util.ArrayList;

/***
 *  delta Energy:
 *  energy[i,j] = -weight[i,j]*activation[i]*activation[j]
 *  
 * @author ravi-zt46
 *
 */


public class Hopfield {
	int numInputs;
	float[] inputCells;
	float[][] weight;
	float[] tempStorage;
	ArrayList<float[]> trainingDataSet = new ArrayList<float[]>();
	
	public Hopfield(int numInputs){
		this.numInputs = numInputs;
		weight = new float[numInputs][numInputs];
		inputCells = new float[numInputs];
		tempStorage = new float[numInputs];
	}
	
	private float deltaEnergy(int index){
		float temp =0.0f;
		for(int i=0;i<numInputs;i++){
			temp +=weight[index][i]*inputCells[i];
		}
		return 2.0f*temp-tempStorage[index];
	}
	
	public void train(){
		for(int j =1;j<numInputs;j++){
			for(int i=0;i<j;i++){
				for(int n=0;n<trainingDataSet.size();n++){
					float[] data = trainingDataSet.get(n);
					float temp1 = adjustInput(data[i])*adjustInput(data[j]);
					float temp = Math.round(temp1+weight[j][i]); //to do (Ravi): need to add truncate
					weight[i][j] = weight[j][i]=temp;
				}
			}
		}
		for(int i =0; i<numInputs;i++){
			tempStorage[i] = 0.0f;
			for(int j=0;j<i;j++){
				tempStorage[i] += weight[i][j];	
			}
		}
	}
	
	public void addTrainingData(float[] data){
		trainingDataSet.add(data);
	}
	
	private float adjustInput(float data){
		return NeuralUtil.clamp(data, 0.0f, 1.0f);
	}
	
	public float[] recall(float[] pattern,int numIteration){
		for(int i =0;i<numInputs;i++){
			inputCells[i] = pattern[i];
		}
		for(int ii =0;ii<numIteration;ii++){
			for(int i=0;i<numInputs;i++){
				if(deltaEnergy(i)>0.0f){
					inputCells[i] = 1.0f;
				}else{
					inputCells[i] = 0.0f;
				}
			}
		}
		return inputCells;
	}
}
