//$Id$
package NeuralNetworks;

public class SigmoidalFunction extends ActivationFunction{

	@Override
	public double calculate(double value) {
		return
			     (1.0f / (1.0f + Math.exp((double) (-value))));
	}

	@Override
	public double calculateDerivation(double value) {
		  double z = calculate(value);
		    return (double) (z * (1.0f - z));
	}

}
