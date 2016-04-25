package research.experiment;

import java.util.Set;
import duplicatesearcher.analysis.*;

public class ExperimentEvaluator {
	
	Set<Duplicate> foundDuplicates, oracleDuplicates, truePositives, falsePositives, falseNegatives;

	
	public ExperimentEvaluator(Set<Duplicate> foundDuplicates, Set<Duplicate> oracleDuplicates)
	{
		this.foundDuplicates = foundDuplicates;
		this.oracleDuplicates = oracleDuplicates;
		
		//true positive = intersect(oracleDupes, foundDupes)
		this.truePositives.addAll(oracleDuplicates);
		this.truePositives.retainAll(foundDuplicates);
		
		//falsePositive = alla element i foundDupes minus alla element i oracleDupes
		this.falsePositives.addAll(foundDuplicates);
		this.falsePositives.removeAll(oracleDuplicates);
		
		//falseNegatives = alla element i oracleDupes minus alla element i foundDupes
		this.falseNegatives.addAll(oracleDuplicates);
		this.falseNegatives.removeAll(foundDuplicates);
	}
	
	public double calculatePrecision()
	{
		double truePositiveSize = truePositives.size();
		double foundDuplicatesSize = foundDuplicates.size();
		
		return truePositiveSize/foundDuplicatesSize;
	}
	
	public double calculateRecall()
	{
		double truePositiveSize = truePositives.size();
		double oracleSize = oracleDuplicates.size();
		
		return truePositiveSize/oracleSize;
	}
	
	public double calculateF1Score()
	{
		double precision = calculatePrecision();
		double recall = calculateRecall();
		
		return (2)*(precision*recall)/(precision+recall);
	}
	
	public Set<Duplicate> getTruePositives()
	{
		return truePositives;
	}
	
	public Set<Duplicate> getFalsePositives()
	{
		return falsePositives;
	}
	
	public Set<Duplicate> getFalseNegatives()
	{
		return falseNegatives;
	}
	
	public Set<Duplicate> getOracleDuplicates()
	{
		return oracleDuplicates;
	}
	
	public Set<Duplicate> getFoundDuplicates()
	{
		return foundDuplicates;
	}

}
