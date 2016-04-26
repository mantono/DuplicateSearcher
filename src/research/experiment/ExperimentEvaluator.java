package research.experiment;

import java.util.HashSet;
import java.util.Set;
import duplicatesearcher.analysis.*;

public class ExperimentEvaluator {
	
	private final Set<Duplicate> foundDuplicates, oracleDuplicates, truePositives, falsePositives, falseNegatives;

	
	public ExperimentEvaluator(Set<Duplicate> foundDuplicates, Set<Duplicate> oracleDuplicates)
	{
		this.foundDuplicates = foundDuplicates;
		this.oracleDuplicates = oracleDuplicates;
		
		this.truePositives = new HashSet<Duplicate>(oracleDuplicates.size());
		//true positive = intersect(oracleDupes, foundDupes)
		this.truePositives.addAll(oracleDuplicates);
		this.truePositives.retainAll(foundDuplicates);
		
		this.falsePositives = new HashSet<Duplicate>(oracleDuplicates.size());
		//falsePositive = alla element i foundDupes minus alla element i oracleDupes
		this.falsePositives.addAll(foundDuplicates);
		this.falsePositives.removeAll(oracleDuplicates);
		
		this.falseNegatives = new HashSet<Duplicate>(oracleDuplicates.size());
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
		if(precision == 0 && recall == 0)
			throw new IllegalStateException("You suck!");
		
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
