package cavd;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.commons.io.FilenameUtils;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSource;

/*
 * Adds a single class attribute to a .arff file. This is to be used on the .arff file generated from the MFCC extraction.
 * A nominal attribute is added which can hold any of the values from animation, ball, gaming, general, music, movies, racing or sports.
 * Creates new file with added class attribute.
 */

public class AddClassAttribute {
	
	Instances data;
	static String filename;
	
	public void loadDataset(String filename) {
//		Load an arff file
		try {
			DataSource source = new DataSource(filename);
			data = source.getDataSet();
		} catch(Exception e) {
			System.out.println("Error reading dataset");
		}
	}
	
	public void addAttribute() {
//		Add class attribute
		try {
			FastVector fvNominalVal = new FastVector(8);
			fvNominalVal.addElement("animation");
			fvNominalVal.addElement("ball");
			fvNominalVal.addElement("gaming");
			fvNominalVal.addElement("general");
			fvNominalVal.addElement("movies");
			fvNominalVal.addElement("music");
			fvNominalVal.addElement("racing");
			fvNominalVal.addElement("sports");
			Attribute attrib = new Attribute("class", fvNominalVal);
			data.insertAttributeAt(attrib, data.numAttributes());
			data.setClassIndex(data.numAttributes()-1);
			ArffSaver saver = new ArffSaver();
			saver.setInstances(data);
			saver.setFile(new File("./src/testing/"+ filename + ".arff"));
			saver.writeBatch();
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	public static void main(String[] args) throws Exception {
		AddClassAttribute aca = new AddClassAttribute();
//		change source folder here
		File dir = new File("./src/testing/arff/");
		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".arff");
			}
		});
		for(File file:files) {
			filename = FilenameUtils.removeExtension(file.getName());
			aca.loadDataset(file.toString());
			aca.addAttribute();
		}
	}

}
