package cavd;

import java.io.File;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.unsupervised.attribute.Add;

public class AddClassAttribute {
	
	Instances data;
	
	public void loadDataset(String filename) {
		try {
			DataSource source = new DataSource(filename);
			data = source.getDataSet();
		} catch(Exception e) {
			System.out.println("Error reading dataset");
		}
	}
	
	public void addAttribute() {
		try {
	//		Add filter = new Add();
	//		filter.setAttributeIndex("last");
	//		filter.setNominalLabels("animation,ball,gaming,general,movies,music,racing,sports");
	//		filter.setAttributeName("class");
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
			/*for(int n=0;n<data.numInstances();n++) {
				Instance inst = data.instance(n);
				inst.setClassValue("sports");
			}*/
			ArffSaver saver = new ArffSaver();
			saver.setInstances(data);
			saver.setFile(new File("./src/testing/audio.arff"));
			saver.writeBatch();
		} catch(Exception e) {
//			System.err.println(e.getMessage());
		}
	}
	
	public static void main(String[] args) throws Exception {
		AddClassAttribute aca = new AddClassAttribute();
		aca.loadDataset("./src/testing.arff");
		aca.addAttribute();
	}

}
