package cavd;

import weka.core.Attribute;

/*
 * Combines text and audio classifier to perform classification for a given .srt file with captions and video title info
 * and a .arff file with MFCC data from audio content. Outputs classified category from the saved classifier models.
 * Changing variable showProb to 1 will also give percentage probabilities of each category.
 */

public class AudioTextClassifier {
	
	public static void main(String[] args) throws Exception {
		Attribute att;
		TextClassifierTester text = new TextClassifierTester();
		AudioClassificationTester audio = new AudioClassificationTester();
		double[] audProb;
		double[] textProb;
		double[] combClass = new double[8];
		//set next value to 1 to output probabilities
		int showProb = 0;
//		Set next line to individual srt file
		text.load("./src/testing/yVgcSyFfyzg.srt");
		text.loadModel("./src/Vote.model");
		att = text.makeInstance();
		textProb = text.classify(showProb);
//		Set next line to individual arff file created from audio
		audio.loadData("./src/testing/40.arff");
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
