package gr.auth.csd.mlkd.utils;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.kohsuke.args4j.*;

public class LLDACmdOption extends LDACmdOption implements Serializable {
  
    

    
    @Option(name = "-parallel", usage = "Estimate/Infer parameters in parallel")
    public boolean parallel = false;
    @Option(name = "-possibleLabels", usage = "possibleLabels")
    public String possibleLabels="alpha";
    @Option(name = "-trFolds", usage = "training folds file")
    public String trainFoldsFile;
    @Option(name = "-tstFolds", usage = "test folds file")
    public String testFoldsFile;
    public double gamma = 0.01;

    public LLDACmdOption(String[] args) {
        super();
        CmdLineParser parser = new CmdLineParser(this);
        if (args.length == 0) {
            parser.printUsage(System.out);
            return;
        }
        try {
            parser.parseArgument(args);
        } catch (CmdLineException ex) {
            Logger.getLogger(CmdOption.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
