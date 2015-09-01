//$Id$
package NeuralNetworks;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;



/**
 * Back propagation Neural with two hidden layer
 *
 * Activation function :
 * Activation function used here is sigmoid due to following reason
 *    (i) real value and differentiable
 *    (ii) have exactly one inflation point
 *    (iii) have a pair of horizontal asymptotes
 *    (iv) Normalized betwen i and 0
 *
 *    sigmoid : f(x)= i/(i+e^(-x))
 *
 *    derivative   = (f(x) * (1.0f - f(x))
 *
 * For activition :
 * O1= sigmoid(I1*w[1,1]+I2*w[2,1])
 *
 * @author ravi-zt46
 */
public class BackPropagationNeural_2H implements Serializable{
    
    /**
     *
     */
    private static final long serialVersionUID = 3770396648711695545L;
    protected int size_in;
    protected int size_hidden1;
    protected int size_hidden2;
    protected int size_output;
    
    public float[] inputs;
    protected float[] hidden1;
    protected float[] hidden2;
    public float[] output;
    
    protected float[][] weight1;
    protected float[][] weight2;
    protected float[][] weight3;
    
    protected float[] output_error;
    protected float[] hidden1_error;
    protected float[] hidden2_error;
    
    // last delta weights for momentum term:
    protected float W1_last_delta[][];
    protected float W2_last_delta[][];
    protected float W3_last_delta[][];
    
    
    transient public ArrayList inputTraining = new ArrayList();
    transient public ArrayList outputTraining = new ArrayList();
    
    private int trainingExample;
    
    public float LEARNING_RATE = 0.5f;
    
    private float alpha = 0.8f;  // momentum scaling term that is applied to last delta weight
    
    public BackPropagationNeural_2H(int size_in,int size_hidden1,int size_hidden2,int size_output){
        this.size_in = size_in;
        this.size_hidden1 = size_hidden1;
        this.size_hidden2 = size_hidden2;
        this.size_output = size_output;
        
        inputs = new float[size_in];
        hidden1 = new float[size_hidden1];
        hidden2 = new float[size_hidden2];
        output = new float[size_output];
        
        weight1 = new float[size_in][size_hidden1];
        weight2 = new float[size_hidden1][size_hidden2];
        weight3 = new float[size_hidden2][size_output];
        W1_last_delta = new float[size_in][size_hidden1];
        W2_last_delta = new float[size_hidden1][size_hidden2];
        W3_last_delta = new float[size_hidden2][size_output];
        
        randomizeWeights();
        output_error = new float[size_output];
        hidden1_error = new float[size_hidden1];
        hidden2_error = new float[size_hidden2];
    }
    
    
    public float[] recall(float[] data){
        for(int i=0;i<size_in;i++){
            inputs[i] = data[i];
        };
        forwardPropagation();
        float[] ret = new float[size_output];
        for(int j=0;j<size_output;j++){
            ret[j] = output[j];
        }
        return ret;
    }
    
    private void forwardPropagation(){
        for(int i=0;i<size_hidden1;i++){
            hidden1[i] = 0.0f;
        }
        for(int i=0;i<size_hidden2;i++){
            hidden2[i] = 0.0f;
        }
        //Propagating through   (Input neuron layer and hidden1 neuron layer)
        for(int i=0;i<size_in;i++){
            for(int j=0;j<size_hidden1;j++){
                hidden1[j] += inputs[i] *weight1[i][j];
            }
            
        }
        //propagating through (hidden1 neuron layer and hidden2 neuron layer)
        for(int i=0;i<size_hidden1;i++){
            for(int j=0;j<size_hidden2;j++){
                hidden2[j] += hidden1[i] * weight2[i][j];
            }
        }
        
        for (int o = 0; o < size_output ; o++){
            output[o] = 0.0f;
        }
        //propagating through (hidden2 neuron layer to output layer)
        for(int i=0;i<size_hidden2;i++){
            for(int j=0;j<size_output;j++){
                //	System.out.println("the answer ------>"+sigmoid(hidden2[i]));
                output[j] += sigmoid(hidden2[i])*weight3[i][j];
                //	System.out.println("propagation "+i+" "+j+" "+output[j]);
            }
        }
        
    }
    
    public float train(ArrayList in,ArrayList out){
        float error = 0.0f;
        int num_cases = in.size();
        //re- initialize the error arrays
        for(int i=0;i<size_hidden1;i++){
            hidden1_error[i] = 0.0f;
        }
        for(int i=0;i<size_hidden2;i++){
            hidden2_error[i] = 0.0f;
        }
        for(int i=0;i<size_output;i++){
            output_error[i] = 0.0f;
        }
        
        for(int i=0;i<size_in;i++){
            inputs[i] = ((float[])in.get(trainingExample))[i];
        };
        float[] desired_out = ((float[])out.get(trainingExample));
        
        forwardPropagation();
        
        for (int i = 0; i < size_output; i++) {
            output_error[i] =
            (desired_out[i] -
             output[i])
            * sigmoidP(output[i]);
        }
        for (int i = 0; i < size_hidden2; i++) {
            hidden2_error[i] = 0.0f;
            for (int j = 0; j < size_output; j++) {
                hidden2_error[i] +=
                output_error[j] * weight3[i][j];
            }
        }
        for (int i = 0; i < size_hidden1; i++) {
            hidden1_error[i] = 0.0f;
            for (int j = 0; j < size_hidden2; j++) {
                hidden1_error[j] +=
                hidden2_error[j] * weight2[i][j];
            }
        }
        
        for (int i = 0; i < size_hidden2; i++) {
            hidden2_error[i] =
            hidden2_error[i] * sigmoidP(hidden2[i]);
        }
        for (int i = 0; i < size_hidden1; i++) {
            hidden1_error[i] =
            hidden1_error[i] * sigmoidP(hidden1[i]);
        }
        // update the hidden2 to output weights:
		      for (int i = 0; i < size_output; i++) {
                  for (int j = 0; j < size_hidden2; j++) {
                      weight3[j][i] +=
                      LEARNING_RATE * output_error[i] * hidden2[j]+alpha*W3_last_delta[j][i];
                      weight3[j][i] = clampWeight(weight3[j][i]);
                      W3_last_delta[j][i] = LEARNING_RATE*output_error[i]*hidden2[j];
                  }
              }
		      // update the hidden1 to hidden2 weights:
		      for (int i = 0; i < size_hidden2; i++) {
                  for (int j = 0; j < size_hidden1; j++) {
                      weight2[j][i] +=
                      LEARNING_RATE * hidden2_error[i] * hidden1[j]+alpha*W2_last_delta[j][i];
                      weight2[j][i] = clampWeight(weight2[j][i]);
                      W2_last_delta[j][i] = LEARNING_RATE*hidden2_error[i]*hidden1[j];
                  }
              }
		      // update the input to hidden1 weights:
		      for (int i = 0; i < size_hidden1; i++) {
                  for (int j = 0; j < size_in; j++) {
                      weight1[j][i] +=
                      LEARNING_RATE * hidden1_error[i] * inputs[j]+alpha*W1_last_delta[j][i];
                      weight1[j][i] = clampWeight(weight1[j][i]);
                      W1_last_delta[j][i] = LEARNING_RATE*hidden1_error[i]*inputs[j];
                  }
              }
		      for (int i = 0; i < size_output; i++) {
                  error += Math.abs(desired_out[i] - output[i]);
              }
		      trainingExample++;
		      if (trainingExample >= num_cases) trainingExample = 0;
		      return error;
        
    }
    
    protected float sigmoid(float x) {
        return
        (float) (1.0f / (1.0f + Math.exp((double) (-x))));
		  }
    
    protected float sigmoidP(float x) {
        float z = sigmoid(x);
        return (float) (z * (1.0f - z));
    }
    
    /**Maintain the weight in certain range**/
    protected float clampWeight(float weigth) {
        float ret = weigth;
        if (ret < -10) ret = -10;
        if (ret > 10)  ret =  10;
        return ret;
		  }
    
    public void addTrainingExample(float[] inputs, float[] outputs) {
        if (inputs.length != size_in || outputs.length != size_output) {
            System.out.println("addTrainingExample(): array size is wrong");
            return;
        }
        inputTraining.add(inputs);
        outputTraining.add(outputs);
		  }
    
    public float train() {
        return train(inputTraining, outputTraining);
    }
    
    
    public void randomizeWeights() {
        // Randomize weights here:
        for (int ii = 0; ii < size_in; ii++)
            for (int hh = 0; hh < size_hidden1; hh++)
                weight1[ii][hh] =
                2f * (float) Math.random() - 1f;
        for (int ii = 0; ii < size_hidden1; ii++)
            for (int hh = 0; hh < size_hidden2; hh++)
                weight2[ii][hh] =
                2f * (float) Math.random() - 1f;
        for (int hh = 0; hh < size_hidden2; hh++)
            for (int oo = 0; oo < size_output; oo++)
                weight3[hh][oo] =
                2f * (float) Math.random() - 1f;
		  }
    
		  public void slightlyRandomizeWeights() {
              // Randomize weights here:
              for (int ii = 0; ii < size_in; ii++)
                  for (int hh = 0; hh < size_hidden1; hh++)
                      weight1[ii][hh] +=
                      0.2f * (float) Math.random() - 0.1f;
              for (int ii = 0; ii < size_hidden1; ii++)
                  for (int hh = 0; hh < size_hidden2; hh++)
                      weight2[ii][hh] +=
                      0.2f * (float) Math.random() - 0.1f;
              for (int hh = 0; hh < size_hidden2; hh++)
                  for (int oo = 0; oo < size_output; oo++)
                      weight3[hh][oo] +=
                      0.2f * (float) Math.random() - 0.1f;
          }
    
    public static BackPropagationNeural_2H Factory(String serialized_file_name) {
        BackPropagationNeural_2H nn = null;
        try {
            InputStream ins = ClassLoader.getSystemResourceAsStream(serialized_file_name);
            if (ins == null) {
                System.out.println("CachedExamples(): failed to open 'cache.dat' in JAR file");
                System.exit(1);
            } else {
                ObjectInputStream p = new ObjectInputStream(ins);
                nn = (BackPropagationNeural_2H) p.readObject();
                nn.inputTraining = new ArrayList();
                nn.outputTraining = new ArrayList();
                ins.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return nn;
    }
    
    public void save(String file_name) {
        try {
            FileOutputStream ostream = new FileOutputStream(file_name);
            ObjectOutputStream p = new ObjectOutputStream(ostream);
            p.writeObject(this);
            p.flush();
            ostream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
