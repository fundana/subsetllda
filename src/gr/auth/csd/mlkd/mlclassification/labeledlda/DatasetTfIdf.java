package gr.auth.csd.mlkd.mlclassification.labeledlda;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.map.hash.TIntDoubleHashMap;
import gnu.trove.set.hash.TIntHashSet;
import gr.auth.csd.mlkd.utils.Utils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatasetTfIdf extends Dataset {

    protected String svmFile;
    protected TIntDoubleHashMap labels;

    public TIntDoubleHashMap getLabels() {
        return labels;
    }

    //constructor for LDA
    public DatasetTfIdf(String svmFile, boolean inference,
            int numFeatures, TIntDoubleHashMap[] fi) {
        super(inference);
        this.svmFile = svmFile;
        if (!inference) {
            readLabels();
            V = numberOfFeatures(svmFile);
            //this.K = K;
        } else {
            this.K = fi.length;
            this.V = numFeatures;
            labels = (TIntDoubleHashMap) Utils.readObject("lda.labels");
        }
    }

    protected void readLabels() {
        try (BufferedReader br = new BufferedReader(new FileReader(new File(svmFile)))) {
            String line;
            labels = new TIntDoubleHashMap();
            line = br.readLine();
            K = -1;
            while ((line = br.readLine()) != null) {
                String[] splits = line.split(",");
                HashSet<Integer> tags = new HashSet<>();
                for (int i = 0; i < splits.length - 1; i++) {
                    tags.add(Integer.parseInt(splits[i]));
                }
                String[] splits2 = splits[splits.length - 1].split(" ");
                try {
                    tags.add(Integer.parseInt(splits2[0]));
                } catch (NumberFormatException ex) {
                    System.out.println(line);
                }
                for (Integer tag : tags) {
                    if (tag > K) {
                        K = tag;
                    }
                    labels.adjustOrPutValue(tag, 1, 1);
                }
            }
            K++;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DatasetTfIdf.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DatasetTfIdf.class.getName()).log(Level.SEVERE, null, ex);
        }
        Utils.writeObject(labels, "lda.labels");
    }

    @Override
    public void create(boolean ignoreFirstLine) {
        docs = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(new File(svmFile)))) {
            String line;
            int id = 0;
            if (ignoreFirstLine) {
                line = br.readLine();
            }
            while ((line = br.readLine()) != null) {
                TIntDoubleHashMap doc = new TIntDoubleHashMap();
                String[] splits = line.split(",");
                TIntHashSet tags = new TIntHashSet();
                for (int i = 0; i < splits.length - 1; i++) {
                    tags.add(Integer.parseInt(splits[i]));
                }
                String[] splits2 = splits[splits.length - 1].split(" ");
                if (!splits2[0].isEmpty()) {
                    tags.add(Integer.parseInt(splits2[0]));
                }
                //if(id==0) System.out.println(tags);
                for (int i = 1; i < splits2.length; i++) {
                    String[] featNValue = splits2[i].split(":");
                    int feature = Integer.parseInt(featNValue[0]);
                    double value = Double.parseDouble(featNValue[1]);
                    doc.put(feature, value);
                }
                int[] ls = new int[tags.size()];
                TIntIterator it = tags.iterator();
                int i = 0;
                while (it.hasNext()) {
                    ls[i] = it.next();
                    i++;
                }
                if (ls.length != 0) {
                    setDoc(new Document(doc, ls));
                    id++;
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DatasetTfIdf.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DatasetTfIdf.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public String getWord(Integer index) {
        return index.toString();
    }

    @Override
    public String getLabel(int id) {
        return id + "";
    }

    private int numberOfFeatures(String svmFile) {
        int maxFeature = -1;
        try (BufferedReader br = new BufferedReader(new FileReader(new File(svmFile)))) {
            String line;
            line = br.readLine();
            while ((line = br.readLine()) != null) {

                String[] splits = line.split(",");
                String[] splits2 = splits[splits.length - 1].split(" ");
                for (int i = 1; i < splits2.length; i++) {
                    String[] featNValue = splits2[i].split(":");
                    int feature = Integer.parseInt(featNValue[0]);
                    if (maxFeature < feature) {
                        maxFeature = feature;
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DatasetTfIdf.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DatasetTfIdf.class.getName()).log(Level.SEVERE, null, ex);
        }
        return maxFeature + 1;
    }
}
