//$Id$
package NeuralNetworks;

public class NeuralUtil {
	public static float clamp(float value,float min,float max){
		return Math.max(min, Math.min(value, max));
	}
}
