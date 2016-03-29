package duplicatesearcher.processing.spellcorrecting;

import java.util.ArrayList;
import java.util.List;


public class SpellCorrector {
	static LevenshteinDistance lev = new LevenshteinDistance();
	static ArrayList<String> words = new ArrayList<String>();
	static ArrayList<String> wordsToBeCorrected = new ArrayList<String>();
	
	public static List<String> correctWords(List<String> list){
		ArrayList<String> correctedWords = new ArrayList<>();
		
		for(String word : list){
			correctedWords.add(correctWord(word, words));
		}
		
		return correctedWords;
	}
	
	//Nu garanteras en returnerad sträng men det finns ingen "treshold", man kan använda sig av det i LevenShtein konstruktorn om det blir att föredra
	public static String correctWord(String textSubject, List<String> words){
		String tmp = "";
		int distance;
		int closestDistance = 100; //fulfix går att lösa på snyggare vis!?
		
		for(String word : words){
			distance = lev.apply(textSubject, word);
			
			if(distance == 0)
				return textSubject;
			else if(distance<closestDistance){
				tmp = word;
				closestDistance = distance;
			}
		}
		
		return tmp;
	}
	
	//Bara för denna simpla version del, ska ju använda ordboken egentligen samt JUnittester istället
	public static void fillWords(){
		words.add("hej");
		words.add("tjena");
		words.add("duplicate");
		words.add("sverige");
		words.add("kandidat");
		words.add("troll");
		words.add("boll");
		words.add("television");
		words.add("televisioner");
		
		wordsToBeCorrected.add("sverge");
		wordsToBeCorrected.add("trllo");
		wordsToBeCorrected.add("broll");
		wordsToBeCorrected.add("kandat");
		wordsToBeCorrected.add("duplicat");
		wordsToBeCorrected.add("tjean");
		wordsToBeCorrected.add("televisioneer");
		wordsToBeCorrected.add("televisionen");
		wordsToBeCorrected.add("tjen");
		wordsToBeCorrected.add("hejsan");
		
	}
	
	public static void main(String[] args) {
		fillWords();
		System.out.println(wordsToBeCorrected);
		
		List<String> correctedWords = correctWords(wordsToBeCorrected);
		System.out.println(correctedWords);
		
		
		
	}

}
