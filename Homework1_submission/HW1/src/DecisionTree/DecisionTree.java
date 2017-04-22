package DecisionTree;

import java.io.*;
import java.util.Enumeration;
import java.util.Random;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.NoSupportForMissingValuesException;
import weka.core.Utils;
import Utility.Utility;

/*Class for constructing an unpruned decision tree based on
the ID3 algorithm. Can only deal with nominal attributes.
No missing values allowed. Empty leaves may result in unclassified instances.
 */
public class DecisionTree  {

    //The node's successors.
    private DecisionTree[] m_Successors;
    //Attribute used for splitting.
    private Attribute m_Attribute;
    //Class value if node is leaf.
    private double m_ClassValue;
    //Class distribution if node is leaf.
    private double[] m_Distribution;
    // Class attribute of data set.
    private Attribute m_ClassAttribute;
    
    public DecisionTree() {
    }
    
    public void decisionTree() throws Exception {
        BufferedReader file = Utility.readFile("./data/decision_tree/house.txt");
        Instances data = new Instances(file);
        int cIdx=data.numAttributes()-1;// The data has four attributes
        Random rand=new Random();
        int folds=5;
        Instances randData = new Instances(data);
        randData.randomize(rand); 
        
        double precision_sum=0;
        double test_count=0;
        for(int i=0;i<100;i++){
            for (int n = 0; n < folds; n++) {
         	   Instances train = randData.trainCV(folds, n);
         	   Instances test = randData.testCV(folds, n);
         	   train.setClassIndex(cIdx);
         	   test.setClassIndex(cIdx);
         	   buildClassifier(train, true);
         	   precision_sum+=printOutput(test,n,true);
         	   test_count++;
         	   // further processing, classification, etc.
            }
        }
        System.out.println("InfoGain "+precision_sum/test_count);

        precision_sum=0;
        test_count=0;
        for (int n = 0; n < folds; n++) {
     	   Instances train = randData.trainCV(folds, n);
     	   Instances test = randData.testCV(folds, n);
     	   train.setClassIndex(cIdx);
     	   test.setClassIndex(cIdx);
     	   buildClassifier(train, false);
     	   precision_sum+=printOutput(test,n,false);
     	   test_count++;
     	   // further processing, classification, etc.
        }
        System.out.println("InfoGainRatio "+precision_sum/test_count);
        //data.setClassIndex(cIdx);// set the attributes
        //buildClassifier(data);
        //printOutput(data);//predict 
    }   

    public void buildClassifier(Instances data, Boolean method_infogain) throws Exception {
        data = new Instances(data);
        this.makeTree(data, method_infogain);
    }   
    
    private void makeTree(Instances data, Boolean method_infogain) throws Exception {
    	if(data.numInstances() == 0) {
    		this.m_Attribute = null;
    		this.m_ClassValue = Instance.missingValue();
    		this.m_Distribution = new double[data.numClasses()];
    	} else {
    		double[] infoGains = new double[data.numAttributes()];// infoGains is the array to store infoGain of different attributes

    		Attribute splitDataAtt;
    		//This step compute the infoGain of different attribute
    		
    		if(method_infogain){
        		for(Enumeration attEnum = data.enumerateAttributes(); attEnum.hasMoreElements(); infoGains[splitDataAtt.index()] = this.computeInfoGain(data, splitDataAtt)) {
        			splitDataAtt = (Attribute)attEnum.nextElement();//Returns the next element of this enumeration if this enumeration object has at least one more element to provide.
        		}    			
    		}
    		else{
        		for(Enumeration attEnum = data.enumerateAttributes(); attEnum.hasMoreElements(); infoGains[splitDataAtt.index()] = this.computeGainRatio(data, splitDataAtt)) {
        			splitDataAtt = (Attribute)attEnum.nextElement();//Returns the next element of this enumeration if this enumeration object has at least one more element to provide.
        		}     			
    		}

    		this.m_Attribute = data.attribute(Utils.maxIndex(infoGains));// ? set the current attribute to the majority
    		if(Utils.eq(infoGains[this.m_Attribute.index()], 0.0D)) {// if the maximum information gain is zero
    			this.m_Attribute = null;
    			this.m_Distribution = new double[data.numClasses()];

    			Instance j;
    			for(Enumeration var6 = data.enumerateInstances(); var6.hasMoreElements(); ++this.m_Distribution[(int)j.classValue()]) {
    				j = (Instance)var6.nextElement();
    			}

    			Utils.normalize(this.m_Distribution);
    			this.m_ClassValue = (double)Utils.maxIndex(this.m_Distribution);
    			this.m_ClassAttribute = data.classAttribute();
    		} 
    		else {
    			Instances[] var7 = this.splitData(data, this.m_Attribute);
    			this.m_Successors = new DecisionTree[this.m_Attribute.numValues()];

    			for(int var8 = 0; var8 < this.m_Attribute.numValues(); ++var8) {
    				this.m_Successors[var8] = new DecisionTree();
    				this.m_Successors[var8].makeTree(var7[var8], method_infogain);
    			}
    		}
    	}
    }
    
    private double computeInfoGain(Instances data, Attribute att) throws Exception {
        double infoGain = this.computeEntropy(data);
        Instances[] splitData = this.splitData(data, att);// Split the data according the attribute

        /****************Please Fill Missing Lines Here*****************/
        for (int j = 0; j < att.numValues(); j++) {  
            if (splitData[j].numInstances() > 0) {  
              infoGain -= ((double) splitData[j].numInstances() /  (double) data.numInstances()) * computeEntropy(splitData[j]);  
            }  
        }        
        return infoGain;
    }    
    
    private double computeEntropy(Instances data) throws Exception {
        double[] classCounts = new double[data.numClasses()];// this is class! not attribute
        Instance instance;
        //obtain classCounts of different class
        for(Enumeration instEnum = data.enumerateInstances(); instEnum.hasMoreElements(); ++classCounts[(int)instance.classValue()]) {
            instance = (Instance)instEnum.nextElement();
        }

        double totalEntropy = 0.0D;
        int classNum = data.numClasses();
        double [] classProbVec = new double[classNum];// classProbVec has the percentage pi of each class
        
        for(int j = 0; j < classNum; ++j) {
            if(classCounts[j] > 0.0D) {
                classProbVec[j]= classCounts[j]/data.numInstances();
            }
            else
            	classProbVec[j]=0;
        }
        /****************Please Fill Missing Lines Here*****************/
        
        for(int j=0; j<classNum;j++){
        	if(classProbVec[j]>0){
        		totalEntropy -=classProbVec[j]*Utils.log2(classProbVec[j]);
        	}
        }
        
        return totalEntropy;

    }

    private double computeGainRatio(Instances data, Attribute att) throws Exception {
    	double splitinfo=0.0;

        double infoGain = this.computeEntropy(data);
        Instances[] splitData = this.splitData(data, att);// Split the data according the attribute

        /****************Please Fill Missing Lines Here*****************/
        for (int j = 0; j < att.numValues(); j++) {  
            if (splitData[j].numInstances() > 0) {  
              infoGain -= ((double) splitData[j].numInstances() /  (double) data.numInstances()) * computeEntropy(splitData[j]);
              splitinfo -= ((double) splitData[j].numInstances() /  (double) data.numInstances()) * Utils.log2( ((double) splitData[j].numInstances() /  (double) data.numInstances()) );
            }  
        }    	
    	if(splitinfo==0.0){
    		return 0;
    	}
    	else{
    		return infoGain/splitinfo;
    	}
        /****************Please Fill Missing Lines Here*****************/
    }
    
    private Instances[] splitData(Instances data, Attribute att) {
        Instances[] splitData = new Instances[att.numValues()];// The first one return the number of attribute values (like )

        for(int instEnum = 0; instEnum < att.numValues(); ++instEnum) {// It created multiple arrays for different split, 
        	// each split will have the same spot number
            splitData[instEnum] = new Instances(data, data.numInstances());
        }

        Enumeration var6 = data.enumerateInstances();

        while(var6.hasMoreElements()) {
            Instance i = (Instance)var6.nextElement();
            splitData[(int)i.value(att)].add(i);
        }

        for(int var7 = 0; var7 < splitData.length; ++var7) {
            splitData[var7].compactify();
        }
        return splitData;
    }
    
    //Classifies a given test instance using the decision tree.
    public double classifyInstance(Instance instance) throws NoSupportForMissingValuesException {
        if(instance.hasMissingValue()) {
            throw new NoSupportForMissingValuesException("DecisionTree: no missing values, please.");
        } else {
            return this.m_Attribute == null?this.m_ClassValue:this.m_Successors[(int)instance.value(this.m_Attribute)].classifyInstance(instance);// DFS to find the 
        }
    }
    
    private double printOutput(Instances data, int times, Boolean method_infogain) throws IOException, NoSupportForMissingValuesException {
    	String mark=new String("");
    	if(method_infogain){
    		mark="info_gain";
    	}
    	else{
    		mark="info_ratio";
    	}
    	FileWriter fStream = new FileWriter("./data/decision_tree/output"+mark+times+".txt");     // Output File
        BufferedWriter out = new BufferedWriter(fStream);
        double sample_count=0;
        double right_count=0;
        for(int index =0; index<data.numInstances();index++) {
        	sample_count++;
            Instance testRowInstance = data.instance(index);
            double prediction =classifyInstance(testRowInstance);
            double true_label=testRowInstance.classValue();
            if(prediction==true_label){
            	right_count++;
            }
            //System.out.println("yay");
            //System.out.println(data.classAttribute().value((int) true_label));
            //System.out.println(data.classAttribute().value((int) prediction));
            out.write(data.classAttribute().value((int) prediction));
            out.newLine();
        }
        out.close();
        return right_count/sample_count;
    }    
    
    public String toString() {
        return this.m_Distribution == null && this.m_Successors == null?"DecisionTree: No model built yet.":"DecisionTree\n\n" + this.toString(0);
    }
    //Computes information gain for an attribute.
 
    private String toString(int level) {
        StringBuffer text = new StringBuffer();
        if(this.m_Attribute == null) {
            if(Instance.isMissingValue(this.m_ClassValue)) {
                text.append(": null");
            } else {
                text.append(": " + this.m_ClassAttribute.value((int)this.m_ClassValue));
            }
        } else {
            for(int j = 0; j < this.m_Attribute.numValues(); ++j) {
                text.append("\n");

                for(int i = 0; i < level; ++i) {
                    text.append("|  ");
                }

                text.append(this.m_Attribute.name() + " = " + this.m_Attribute.value(j));
                text.append(this.m_Successors[j].toString(level + 1));
            }
        }
        return text.toString();
    }
}
