package cavd;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;

import weka.classifiers.meta.FilteredClassifier;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.FastVector;
import weka.core.Instances;

public class TestClassifier {
	
	String text;
	Instances instances;
	FilteredClassifier fc;
	
	public void load(String filename) {
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
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
			fc = (FilteredClassifier)in.readObject();
			in.close();
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	public void makeInstance() {
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
//		System.out.println(instances);
	}
	
	public void classify() {
		try {
			double pred = fc.classifyInstance(instances.instance(0));
			System.out.println("Predicted class: "+instances.classAttribute().value((int)pred));
//			double[] fDistribution = fc.distributionForInstance(instances.instance(1));
//			System.out.println(fDistribution);
		} catch(Throwable t) {
			t.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
		TestClassifier classifier = new TestClassifier();
		classifier.load("./src/testing/ljpBwguIqos.srt");
		classifier.loadModel("./src/fClassifier.model");
		classifier.makeInstance();
		classifier.classify();
	}

}
