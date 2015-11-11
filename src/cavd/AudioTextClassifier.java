package cavd;

import weka.core.Attribute;

public class AudioTextClassifier {
	
	public static void main(String[] args) throws Exception {
		Attribute att;
		TestClassifier text = new TestClassifier();
		AudioClassificationTester audio = new AudioClassificationTester();
		double[] audProb;
		double[] textProb;
		double[] combClass = new double[8];
		//set next value to 1 to output probabilities
		int showProb = 1;
//		CollateInfo.main(null);
		text.load("./src/testing/gDvNw2S4_nA.srt");
		text.loadModel("./src/SMO.model");
		att = text.makeInstance();
		textProb = text.classify(showProb);
//		AddClassAttribute.main(null);
		audio.loadData("./src/testing/audio.arff");
		audio.loadModel("./src/audClassifier.model");
		audProb = audio.classify(showProb);
		int max=0;
		System.out.println("Combined probabilities");
		for(int i=0;i<combClass.length;i++) {
			combClass[i] = (audProb[i]+textProb[i])/2;
			if(showProb == 1)
				System.out.println(att.value(i)+":"+Double.toString(combClass[i]));
			if(combClass[i]>combClass[max])
				max = i;
		}
		System.out.println("Predicted class: "+att.value(max));
	}

}
