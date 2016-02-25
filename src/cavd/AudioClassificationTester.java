package cavd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.ObjectInputStream;

import org.apache.commons.io.FilenameUtils;

import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

/*
 * Evaluates audio classifier on test audio. Extract MFCC from .wav of audio file and save as arff file to be provided as input
 * Uses previously saved classifier model. Outputs the classified category
 */

public class AudioClassificationTester {
	Instances data;
	RandomForest rForest;
	static String filename;
	public void loadData(String filename) {
//		Load an arff file
		try {
			DataSource source = new DataSource(filename);
			data = source.getDataSet();
			if(data.classIndex() == -1)
				data.setClassIndex(data.numAttributes()-1);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void loadModel(String filename) {
//		Load a saved classifier model
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
			rForest = (RandomForest)in.readObject();
			in.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public double[] classify(int showProb) {
//		Display results of classification
		double[] pred = null;
		try {
			double predict = rForest.classifyInstance(data.instance(0));
			System.out.println("Audio predicted class: "+data.classAttribute().value((int)predict));
			pred = rForest.distributionForInstance(data.firstInstance());
			if(showProb == 1) {
				System.out.println("Probability from audio file:");
				for(int i=0;i<pred.length;i++)
					System.out.println(data.classAttribute().value(i)+":"+Double.toString(pred[i]));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return pred;
	}
	public static void main(String[] args) throws Exception {
		AudioClassificationTester tester = new AudioClassificationTester();
//		change source folder here
		File dir = new File("./src/testing/");
		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".arff");
			}
		});
//		change classifier if needed
		tester.loadModel("./src/audClassifier.model");
		for(File file:files) {
			filename = FilenameUtils.removeExtension(file.getName());
			tester.loadData(file.toString());
			tester.classify(0);
		}
	}
}
