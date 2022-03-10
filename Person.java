import java.util.LinkedList;
import java.util.Arrays;
public class Person{
	/*
	 * A Person will hold the location the person (unique name) will be located in the miai array
	 * prs is how many partners they can have
	 * ary is the array that will hold the indexes of the opposite gender from miai, inxeded by their
	 * 	rankings; e.g. rank 1 = index 0, rank 3 = index 2 etc. etc. 
	 * 	To reiterate: the actual data in this array are the indexes in miai
	 * name is obviously their name
	 * The matches list holds the Person object of the person/people they're currently matched with
	 * The size of the mathces list and prs will be compared to check if the person is satisfied for the
	 * 	sati variable denoted in the Polyandray.java file
	 * r is the current index iteration in their ary array (rankings), starting from index 0 as they will aim for
	 * 	their ranked 1 person. This is a field variable because the Person might have their match taken
	 * 	away and when looking they will need to start from where their ranking considerations left off
	 * n is the Person object's index in miai
	 * locate will hold the index of their current partner/lowest ranked partner's index in it's ranking array, used for comparing ranks
	 */
	public int n = 0, prs = 0, r = 0, locate = -1;
	public boolean taken = false;
	public String name;
	public LinkedList<Person> matches = new LinkedList<Person>();
	public int[] ary;
	public Person(String name, int n, int prs){
		this.name = name;
		this.n = n;
		this.prs = prs;
	}

	//this ranking method will take the index of the Person in miai and find it's index in it's ranking 
	//	array by comparisons and returning it's index in THIS Person's ranking array (not miai)
	public int ranking(int loc){
		for(int i = 0; i < ary.length; i++)
			if(ary[i] == loc)
				return i;
		return -1;
	}

	//This method will find the index of a Person (derived from miai) in the list and remove them,
	//	found by matching pointer addresses
	public void remove(Person p){
		for(int i = 0; i < matches.size(); i++)
			if(matches.get(i) == p){
				matches.remove(i);
				return;
			}
	}
	
	//Used for testing; print out relevant traits for debugging
	public String toString(){
		return String.format("[Name: %s\nIndex: %d\nPairs: %d\nRankings: %s\nTaken: %b]\n",name,n,prs, Arrays.toString(ary), taken);
	}
}
