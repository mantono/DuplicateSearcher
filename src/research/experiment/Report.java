package research.experiment;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.egit.github.core.RepositoryId;

import research.experiment.datacollectiontools.ExperimentSetGenerator;
import duplicatesearcher.ProcessingFlags;
import duplicatesearcher.Token;
import duplicatesearcher.analysis.Duplicate;
import duplicatesearcher.analysis.IssueComponent;
import duplicatesearcher.analysis.frequency.TermFrequencyCounter;

public class Report{
	EnumSet<ProcessingFlags> flagSet;
	EnumMap<IssueComponent, Double> weights;
	RepositoryId repoId;
	Duration processing;
	Duration analysis;
	ExperimentSetGenerator exSetGenerator;
	Set<Duplicate> foundDuplicates;
	ExperimentEvaluator exEval;
	List<String> reportList;
	
	public Report(EnumSet<ProcessingFlags> flagSet, EnumMap<IssueComponent, Double> weights, RepositoryId repoId,
			Duration processing, Duration analysis, ExperimentSetGenerator exSetGenerator, Set<Duplicate> foundDuplicates){
		this.flagSet = flagSet;
		this.weights = weights;
		this.repoId = repoId;
		this.processing = processing;
		this.analysis = analysis;
		this.exSetGenerator = exSetGenerator;
		this.foundDuplicates = foundDuplicates;
		this.exEval = new ExperimentEvaluator(foundDuplicates, exSetGenerator.getCorpusDuplicates());
 	}
	
	public List<String> buildHTML(){
	    reportList = new ArrayList<String>();
	    final String header = "<html><head><title>"+repoId+"</title></head><body>";
	    final String flags = "<p>Flags: "+ flagSet +"</p>";
	    StringBuilder weighting = new StringBuilder("<p>Weightings: ");
	    
	    for(final Entry<IssueComponent, Double> entry : weights.entrySet()){ 
	    	  weighting.append("\n" + entry.getKey().toString());
	    	  weighting.append(":" + entry.getValue().toString());
	    }
	    weighting.append("</p>");
	    
	    final String F1score = "<p>F1-score: "+ exEval.calculateF1Score()+"</p>";
	    final String precision = "<p>Precision: "+ exEval.calculatePrecision()+"</p>";
	    final String recall = "<p>F1-score: "+ exEval.calculateRecall()+"</p>";
	    final String time = "<p>Processingtime: "+ processing.toString() + "Analysistime: " + analysis.toString() + "</p>";
	    final String truePositiveHeader = ("<h> True Positives: </h>"); 
	    StringBuilder truePositives = new StringBuilder();
	    Set<Duplicate> truePositivesSet = exEval.getTruePositives();
	    
	    for(Duplicate duplicate : truePositivesSet){
	    	        truePositives.append("<table><tr><td>" + duplicate.getMaster().getNumber()
	    	        		+ "</td><td>" + duplicate.getDuplicate().getNumber());
	        for(IssueComponent ic : IssueComponent.values()){
	        	TermFrequencyCounter terms = duplicate.getMaster().getComponent(ic);	        		
	        	truePositives.append(ic +":"+ terms.toString());
	        }
	        		 
	        truePositives.append("</td><td>" + duplicate.getDuplicate().getNumber());
	        for(IssueComponent ic : IssueComponent.values()){
	        	TermFrequencyCounter terms = duplicate.getDuplicate().getComponent(ic);	        		
	        	truePositives.append(ic +":"+ terms.toString());
	        }
	        truePositives.append("</td></tr></table>");
	    }
	   
	    final String falsePositiveHeader = ("<h> False Positives: </h>"); 
	    StringBuilder falsePositives = new StringBuilder();
	    Set<Duplicate> falsePositivesSet = exEval.getFalsePositives();
	    
	    for(Duplicate duplicate : falsePositivesSet){
	        falsePositives.append("<table><tr><td colspan = \"3\">" + duplicate.getMaster().getNumber()); 
	        for(IssueComponent ic : IssueComponent.values()){
	        	TermFrequencyCounter terms = duplicate.getMaster().getComponent(ic);	        		
	        	falsePositives.append(ic +":"+ terms.toString());
	        }
	        		 
	        falsePositives.append("</td><td>" + duplicate.getDuplicate().getNumber());
	        for(IssueComponent ic : IssueComponent.values()){
	        	TermFrequencyCounter terms = duplicate.getDuplicate().getComponent(ic);	        		
	        	falsePositives.append(ic +":"+ terms.toString());
	        }
	        falsePositives.append("</td></tr></table>");
	    }
	    
	    final String falseNegativeHeader = ("<h> False Negatives: </h>"); 
	    StringBuilder falseNegatives = new StringBuilder();
	    Set<Duplicate> falseNegativesSet = exEval.getFalseNegatives();
	    
	    for(Duplicate duplicate : falseNegativesSet){
	        falseNegatives.append("<table><tr><td>" + duplicate.getMaster().getNumber()
	        		+ "</td><td>" + duplicate.getDuplicate().getNumber());
	      
	        for(IssueComponent ic : IssueComponent.values()){
	        	falseNegatives.append("<tr><td>" + ic + "</td>");
	        	TermFrequencyCounter terms = duplicate.getDuplicate().getComponent(ic);	        		
	        	falseNegatives.append("<td>"+ terms.toString() + "</td>");
	        	
	        	terms = duplicate.getMaster().getComponent(ic);	        		
	        	falseNegatives.append("<td>"+ terms.toString() + "</td>");
	        	falseNegatives.append("</tr>");
	        }
	        		 
	        falseNegatives.append("</td><td>" + duplicate.getDuplicate().getNumber());
	        for(IssueComponent ic : IssueComponent.values()){
	        	TermFrequencyCounter terms = duplicate.getDuplicate().getComponent(ic);	        		
	        	falseNegatives.append(ic +":"+ terms.toString());
	        }
	        falseNegatives.append("</td></tr></table>");
	    }
	    
	    reportList.add(header);
	    reportList.add(flags);
	    reportList.add(weighting.toString());
	    reportList.add(F1score);
	    reportList.add(precision);
	    reportList.add(recall);
	    reportList.add(time);
	    reportList.add(truePositiveHeader);
	    reportList.add(truePositives.toString());
	    reportList.add(falsePositiveHeader);
	    reportList.add(falsePositives.toString());
	    reportList.add(falseNegativeHeader);
	    reportList.add(falseNegatives.toString());
	    
	    
	    reportList.add("</body></html>");
	    
	    return reportList;
	}
	
	public void buildFile(){
		List<String> reportList = buildHTML(); 
		
		Path file = Paths.get("experimentreport.html");
		try {
			Files.write(file, reportList, Charset.forName("UTF-8"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	
	
}
