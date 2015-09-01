//$Id$
package NeuralNetworks;

public abstract class ActivationFunction {
	public abstract double calculate(double value);
	public abstract double calculateDerivation(double value);
}
