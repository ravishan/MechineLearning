//$Id$
package Testing;

import NeuralNetworks.BackPropagationNeural_2H;

public class BackPropgationNeural_2H_Test {

    static float[] in1 = {0.1f, 0.1f, 0.9f};
//    static float[] in2 = {0.1f, 0.9f, 0.1f};
//    static float[] in3 = {0.9f, 0.1f, 0.1f};

    static float[] out1 = {1.0f, 2.0f, 6.0f};
//    static float[] out2 = {1.0f, 3.0f, 7.0f};
//    static float[] out3 = {1.0f, 5.0f, 8.0f};

    static float[] test1 = {0.1f, 0.1f, 0.9f};
//    static float[] test2 = {0.1f, 0.9f, 0.1f};
//    static float[] test3 = {0.9f, 0.1f, 0.1f};

    public static void main(String[] args) {
    	BackPropagationNeural_2H nn = new BackPropagationNeural_2H(3, 3, 3, 3);
        nn.addTrainingExample(in1, out1);
//        nn.addTrainingExample(in2, out2);
//        nn.addTrainingExample(in3, out3);
        training_loop:
        for (int i = 0; i < 25000; i++) {
            if (i == 5000 || i == 8000 || i == 10000 | i == 12000)  nn.LEARNING_RATE *= 0.75f;
            float error = nn.train();
            if (i > 0 && i % 500 == 0) {
                //
                // If the error is too large, slightly randomize weights:
                if (error > 0.75)  {
                    nn.randomizeWeights();
                    nn.LEARNING_RATE = 0.75f;
                } else if (error > 0.3)  {
                    nn.slightlyRandomizeWeights();
                }
                System.out.println("cycle " + i + " error is " + error);
                if (error < 0.1)  break training_loop;
            }
        } 
        test_recall(nn, test1);
//        test_recall(nn, test2);
//        test_recall(nn, test3);

//        nn.save("test.neural");
//        System.out.println("Reload a previously trained NN from disk and re-test:");
//        Neural_2H nn2 = Neural_2H.Factory("test.neural");
//        nn2.addTrainingExample(in1, out1);
//        nn2.addTrainingExample(in2, out2);
//        nn2.addTrainingExample(in3, out3);
//        test_recall(nn2, test1);
//        test_recall(nn2, test2);
//        test_recall(nn2, test3);
    }

    public static void test_recall(BackPropagationNeural_2H nn, float[] inputs) {
        float[] results = nn.recall(inputs);
        System.out.print("Test case: ");
        for (int i = 0; i < inputs.length; i++) System.out.print(pp(inputs[i]) + " ");
        System.out.print(" results: ");
        for (int i = 0; i < results.length; i++) System.out.print(pp((results[i])) + " ");
        System.out.println();
    }

    public static String pp(float x) {
        String s = new String("" + x + "00");
        int index = s.indexOf(".");
        if (index > -1) s = s.substring(0, index + 3);
        if (s.startsWith("-") == false) s = " " + s;
        return s;
    }
}