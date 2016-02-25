package cavd;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

/*
 * Trains classifier on audio data. Takes single .arff file as input. Extract MFCC and its derivatives from wav of individual video files.
 * Merge individual .arff files into a combined file which will be input. Saves classifier model.
 */

public class AudioClassifier {
	Instances data;
	RandomForest rForest;
	public void loadDataset(String filename) {
//		Load dataset here
		try {
			DataSource source = new DataSource(filename);
			data = source.getDataSet();
			if(data.classIndex() == -1)
				data.setClassIndex(data.numAttributes()-1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void evaluate() {
//		Creation of classifier
		try {
			rForest = new RandomForest();
			Evaluation eval = new Evaluation(data);
			eval.crossValidateModel(rForest, data, 10, new Random(1));
			System.out.println(eval.toSummaryString());
			System.out.println(eval.toMatrixString());
			System.out.println(eval.toClassDetailsString());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void learn() {
//		Classifier trained upon dataset
		try {
			rForest.buildClassifier(data);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void saveModel(String filename) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename));
			out.writeObject(rForest);
			out.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) throws Exception {
		AudioClassifier audClass = new AudioClassifier();
//		Load dataset here
		audClass.loadDataset("./src/training/audio/final.arff");
		audClass.evaluate();
		audClass.learn();
		audClass.saveModel("./src/audClassifier.model");
	}
}
