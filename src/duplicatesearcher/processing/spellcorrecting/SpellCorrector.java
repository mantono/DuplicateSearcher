package duplicatesearcher.processing.spellcorrecting;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class SpellCorrector {
	static LevenshteinDistance lev = new LevenshteinDistance(2); // Kan sätta treshhold i SpellCorrectors kontruktor istället? Kankse är att föredra?
	static HashSet<String> words;
	
	public SpellCorrector(){
		Path path = Paths.get("dictionary/dict.txt");
		try {
			List<String> dictionary = Files.readAllLines(path);
			words = new HashSet<String>(dictionary);;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	public static List<String> correctWords(List<String> list){
		ArrayList<String> correctedWords = new ArrayList<>();
		
		for(String word : list){
			correctedWords.add(correctWord(word));
		}
		
		return correctedWords;
	}
	
	public static String correctWord(String textSubject){
		String tmp = textSubject;
		int newDistance;
		int closestDistance = Integer.MAX_VALUE;
		
		for(String word : words){
			newDistance = lev.apply(textSubject, word);
			
			if(newDistance == -1)
				continue;
			else if(newDistance == 0)
				return textSubject;
			else if(newDistance<closestDistance){
				tmp = word;
				closestDistance = newDistance;
			}
		}
		
		return tmp;
	}	
}
