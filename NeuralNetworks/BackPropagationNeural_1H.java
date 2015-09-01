//$Id$
package NeuralNetworks;
import java.util.Random;





public class BackPropagationNeural_1H {
	private static final int NEURON_LAYER = 3;
	int input_size;
	int hidden_size;
	int output;
	public double[] learningRate = new double[] { 0.1 };
	protected double momentum = 0.8;
	protected int[] numNeurons;
	protected int outputLayer;
	public double[][] neuronValue;
	protected double[][] threshold;
	protected double[][][] weight;
	protected double[][][] lastWeightChange;
	protected double[][] error;
	protected Random rand = new Random();
	protected ActivationFunction fun = null;
	
	public BackPropagationNeural_1H(int input_size,int hidden_size,int output){
		fun = new TanhFunction();
	//	fun = new SigmoidalFunction();
		this.input_size= input_size;
		this.hidden_size = hidden_size;
		this.output = output;
		outputLayer =2;
		numNeurons = new int[3];
		numNeurons[0] = input_size;
		numNeurons[1] = hidden_size;
		numNeurons[2] = output;
		neuronValue = new double[NEURON_LAYER][];
		threshold = new double[NEURON_LAYER][];
		weight = new double[NEURON_LAYER-1][][];
		lastWeightChange = new double[NEURON_LAYER-1][][];
		error = new double [NEURON_LAYER][];
		
		//1st layer
		neuronValue[0] = new double[input_size];
		threshold[0] = new double[input_size];
		error[0] = new double[input_size];
		
		//2nd layer 
		neuronValue[1] = new double[hidden_size];
		threshold[1] = new double[hidden_size];
		error[1] = new double[hidden_size];

		weight[0] = new double[input_size][];
		lastWeightChange[0] = new double[input_size][];
		for (int prevNeuron = 0; prevNeuron < weight[0].length; prevNeuron++) {
			weight[0][prevNeuron] = new double[hidden_size];
			lastWeightChange[0][prevNeuron] = new double[hidden_size];
		}
		
		//3rd layer 
		neuronValue[2] = new double[output];
		threshold[2] = new double[output];
		error[2] = new double[output];

		weight[1] = new double[hidden_size][];
		lastWeightChange[1] = new double[hidden_size][];
		for (int prevNeuron = 0; prevNeuron < weight[1].length; prevNeuron++) {
			weight[1][prevNeuron] = new double[output];
			lastWeightChange[1][prevNeuron] = new double[output];
		}
		randomizeWeight();
	}
	
	public void randomizeWeight(){
		//2nd layer
		for(int neuron=0;neuron<hidden_size;neuron++){
			threshold[1][neuron] = (rand.nextDouble()-0.5)/2;
			for(int prev=0;prev<input_size;prev++){
				weight[0][prev][neuron] = (rand.nextDouble()-0.5)/2;
				lastWeightChange[0][prev][neuron] =0.0;
			}
		}
		
		//3rd layer
		for(int neuron=0;neuron<output;neuron++){
			threshold[2][neuron] = (rand.nextDouble()-0.5)/2;
			for(int prev=0;prev<hidden_size;prev++){
				weight[1][prev][neuron] = (rand.nextDouble()-0.5)/2;
				lastWeightChange[1][prev][neuron] =0.0;
			}
		}
	}
	
	public void forwardPass(){
		//2nd layer
		for(int neuron=0;neuron<hidden_size;neuron++){
			double result = 0.0;
			for(int prev=0;prev<input_size;prev++){
				result +=
						neuronValue[0][prev] *
						weight[0][prev][neuron];
			}
			neuronValue[1][neuron] = fun.calculate(result-threshold[1][neuron]);
		}
		
		//3rd layer
		for(int neuron=0;neuron<output;neuron++){
			double result = 0.0;			
			for(int prev=0;prev<hidden_size;prev++){
				result +=
						neuronValue[1][prev] *
						weight[1][prev][neuron];
			}
			neuronValue[2][neuron] = fun.calculate(result-threshold[2][neuron]);
		}
	}
	
	public void backPropagate(double[] expectedOutputs) {
		for (int layer = NEURON_LAYER-1, prevLayer = NEURON_LAYER-2, nextLayer = NEURON_LAYER;
			 layer > 0;
			 layer--, prevLayer--, nextLayer--) {
			double prevLearningRate = learningRate[0];
			for (int neuron = 0; neuron < numNeurons[layer]; neuron++) {
				if (layer == outputLayer) {
					error[layer][neuron] =
						fun.calculateDerivation(neuronValue[layer][neuron]) *
						(expectedOutputs[neuron]-neuronValue[layer][neuron]);
				} else {
					double sum = 0.0;
					for (int nextNeuron = 0; nextNeuron < numNeurons[nextLayer]; nextNeuron++) {
						sum +=
							error[nextLayer][nextNeuron] *
							weight[layer][neuron][nextNeuron];
					}
					error[layer][neuron] =
						fun.calculateDerivation(neuronValue[layer][neuron]) * sum;
				}
				for (int prevNeuron = 0; prevNeuron < numNeurons[prevLayer]; prevNeuron ++) {
					double weightChange =
						prevLearningRate *
						neuronValue[prevLayer][prevNeuron] *
						error[layer][neuron];
					weight[prevLayer][prevNeuron][neuron] +=
						weightChange + (lastWeightChange[prevLayer][prevNeuron][neuron] * momentum);
					lastWeightChange[prevLayer][prevNeuron][neuron] = weightChange;
				}
				threshold[layer][neuron] -= prevLearningRate * error[layer][neuron];
			}
		}
	}
}
