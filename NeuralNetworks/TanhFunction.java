//$Id$
package NeuralNetworks;

public class TanhFunction extends ActivationFunction{

	@Override
	public double calculate(double value) {
		double a = Math.exp(value);
		double b = Math.exp(-value);
		return (a-b)/(a+b);
	}

	@Override
	public double calculateDerivation(double value) {
		double tmp = calculate(value);
		return 1.0 - (tmp * tmp);
	}

}
