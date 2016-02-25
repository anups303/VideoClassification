package cavd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.apache.commons.io.FilenameUtils;

import weka.classifiers.meta.FilteredClassifier;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.FastVector;
import weka.core.Instances;

/*
 * Evaluates text classifier on video captions with appended video info data. Change source folder and load model in main method
 */

public class TextClassifierTester {
	
	String text;
	static String filename;
	Instances instances;
	FilteredClassifier fc;
	
	public void load(String filename) {
//		Load caption and info file into string
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line;
			text = "";
			while((line = reader.readLine()) != null) {
				text = text + line;
			}
			reader.close();
		} catch (IOException e) {
			System.err.println(e.getCause()+" "+e.getMessage());
		}
	}
	
	public void loadModel(String filename) {
//		Load a saved classifier
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
			fc = (FilteredClassifier)in.readObject();
			in.close();
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	public Attribute makeInstance() {
//		Add a class attribute
		Attribute attribute1 = new Attribute("text", (FastVector)null);
		FastVector fvNominalVal = new FastVector(8);
		fvNominalVal.addElement("animation");
		fvNominalVal.addElement("ball");
		fvNominalVal.addElement("gaming");
		fvNominalVal.addElement("general");
		fvNominalVal.addElement("movies");
		fvNominalVal.addElement("music");
		fvNominalVal.addElement("racing");
		fvNominalVal.addElement("sports");
		Attribute attribute2 = new Attribute("class", fvNominalVal);
		FastVector fvWekaAttributes = new FastVector(2);
		fvWekaAttributes.addElement(attribute1);
		fvWekaAttributes.addElement(attribute2);
		instances = new Instances("Test relation", fvWekaAttributes, 1);
		instances.setClassIndex(1);
		Instance instance = new Instance(2);
		instance.setValue(attribute1, text);
		instances.add(instance);
		return instances.classAttribute();
	}
	
	public double[] classify(int showProb) {
//		Display results of classification
		double[] fDistribution = null;
		try {
			double pred = fc.classifyInstance(instances.instance(0));
			System.out.println("Text predicted class: "+instances.classAttribute().value((int)pred));
			fDistribution = fc.distributionForInstance(instances.firstInstance());
			if(showProb == 1) {
				System.out.println("Probability from video info and caption files");
				for(int i=0;i<fDistribution.length;i++)
					System.out.println(instances.classAttribute().value(i)+":"+Double.toString(fDistribution[i]));
			}
		} catch(Throwable t) {
			t.printStackTrace();
		}
		return fDistribution;
	}
	
	public static void main(String[] args) throws Exception {
		TextClassifierTester classifier = new TextClassifierTester();
//		Source folder for srt files
		File dir = new File("./src/testing/");
		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".srt");
			}
		});
		for(File file:files) {
			filename = FilenameUtils.removeExtension(file.getName());
			classifier.load(file.toString());
//			Change classifier here if needed
			classifier.loadModel("./src/Vote.model");
			classifier.makeInstance();
			classifier.classify(0);
		}
//		Debug: For single srt file
		/*classifier.load("./src/testing/NyelLxxJqFc.srt");
		classifier.loadModel("./src/Vote.model");
		classifier.makeInstance();
		classifier.classify(0);*/
	}

}
