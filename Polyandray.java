import java.util.Scanner;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.IOException;
import java.io.File;
public class Polyandray{
	
	//TODO: Update the usage notification and add a '--h' help command

	static Scanner input;
	static ArrayList<LinkedList<Person>> hash;
	static int mod;

	/*
	 * The main method will take in an input args[0] and will read in the file, parse through the names and number of partners, 
	 * 	instantiate a Person with each name, fill an array of all of those Persons and operate in that array to find optimal parings
	 * The main method will also take optional arguments that will determine whether females ask instead of males, and if the user wants to print out the matrix. 
	 * 	It is called triex because a Matrix is nxn array, but this aray has nxn with every index being another array, making it have 3 arrays; 3 == trio, trio + matrix == triex
	 * 	n will be the biggest amount of parings, it's not determined on soley male or female, but whoever has the most parings
	 */
	
	//Optional commands from command line is --triex and --f
	//--f will have the females ask rather than the males asking, which is the default
	//--triex will print out an actual array representation of the pairings
	public static void main(String[] args) throws IOException {
		boolean males = true, triex = false;
		try{
			input = new Scanner(new File(args[0]));
			for(int i = args.length-1; i > 0; i--){
				if(args[i].equals("--f")
				else if(args[i].equals("--triex"))
					triex = true;
				else if(args[i].equals("--fem"))
					males = false;
			}
		} catch(Exception e){
			System.err.printf("Error:\nUse Case =>java Polyandray <file>\n");

			System.exit(0);
		} 
		//list will hold all lines read in, and each string in the list will be read to determine ranks.
		//The number of lines corrospond with the number of people, hence list.size() == number of people.
		LinkedList<String> list = new LinkedList<String>(), people = new LinkedList<String>();
		//Miai will hold Person objects, with each element being a unique person (different names);
		//	Miai derives from a Japanese word meaning an arranged marriage matchmaking. I'm not a weeb, I just needed a concise word. I swear...
		Person miai[];
		int x, y;

		//Read all lines of the passed text file
		while(input.hasNext()){
			list.add(input.nextLine());
			if(list.get(list.size()-1).length() == 0) //If the string has nothing in it then remove it from the list
				list.remove(list.size()-1);	
		}
		
		/* TESTING; PRINT OUT ALL LINES	
		for(int i = 0; i < list.size(); i++)
			System.out.println(list.get(i));
		//*/
		
		String setup = list.get(0), name, hold;
		int n = 0, p = list.size();
		input = new Scanner(setup);
		//miai will hold every unique person
		miai = new Person[p];
		input.next(); //Skip the name
		input.next(); //Skip the num of partners
		while(input.hasNext()){ //Find how many men there are, incrementing n
			input.next();
			n += 1;
		}
		y = n;   //y (the amount of men) is then assigned to n
		x = p-y; //the women is the remainding number of people after subtracting the number of men
			 //	from the total amount of people (which is the size of the list)
		
		//mod is the number of people/2 (if it's evan) and number of people + 1/2 if it's odd,
		//	this is for the hahsing algorithm
		mod = p%2==0 ? (p/2) : (p+1)/2; //hashing modulus
		hash = new ArrayList<LinkedList<Person>>(mod); 
		for(int i = 0; i < mod; i++)
			hash.add(new LinkedList<Person>());
		
		//This will hash each unique name (the first string in every line), and hash them to it's hashed index
		//The hash table will hold 'Persons,'
		//A Person object is defined and expanded on in the Person.java file
		for(int i = 0; i < p; i++){
			n = -1;
			input = new Scanner(list.get(i));
			name = input.next();
			hold = input.next();
			for(int j = hold.length()-1; j != -1; j--){
				if(Character.isDigit(hold.charAt(j))){
					n = Integer.valueOf(hold.substring(0, j+1));
					break;
				}
			}
			if(n == -1)
				name = name.substring(0, name.length()-1);
			people.add(name);
			hash.get(name.length()%mod).add(new Person(name, i, n)); //n in this instance is the argument for the Person object's prs
			miai[i] = find(name);
			miai[i].ary = new int[n == -1 ? x : y];
			if(n == -1)
				miai[i].prs = 1;
		}
		
		//This segment will read through every line with every string and will populate their
		//	ranking array, hashing each name and inserting their miai location as the element
		Person p1;
		for(int i = 0, k = 0; list.size()!=0; i++, k = 0){
			input = new Scanner(list.remove());
			name = input.next();
			if(i < x)
				input.next();
			else
				name = name.substring(0,name.length()-1);
			p1 = find(name);
			for(int j = 0; input.hasNext(); j++)
				p1.ary[j] = find(input.next()).n;
		}
	
		//Sati will determine if all of the people who are asking are satisfied
		//	i.e. a woman who wants 3 parters has 3 partners -> sati++;
		//		a woman who wants 2 partners has 2 partners -> sati++
		//		etc. etc...
		//	if everyone who asks is satisfied then exit.
		//Whether sati's limit is == to the number of men/women depending on who's the one asking
		int sati = 0;
		long start, stop, overhead;
		start = System.nanoTime();
		stop = System.nanoTime();
		overhead = stop - start;
		start = System.nanoTime();
		/*
		 * The for loop will iterate through miai; The initialization, condition and procedure in the for loop argumen(?) are managed by ternary operators
		 * The idea:
		 * if the people asking are women then the starting index will be 0, where women's starting index
		 * 	will be at in miai and will iterate up to x-1 logically, and resets to 0 if all women
		 * 	are iterated and aren't satisfied
		 * else if men are asking then it will start at x (because that's the index after women) and will
		 * 	iterate up to p-1 and resets to x if they've been iterated and aren't satitsfied.
		 */
		for(int i = males?x:0,j=-1;sati!=(males?y:x);i=i<(males?p-1:x-1)?(i+1):(males?x:0)){
			if(miai[i].prs == miai[i].matches.size())//if person at miai[i] is satisfied then continue
				continue;
			p1 = miai[miai[i].ary[miai[i].r]]; //p1 is the partner miai[i] is considering
			if(males ? p1.matches.size() == p1.prs : p1.taken) { //if male, see if woman is satisfied, else see if the man is currently taken
				//This is in the case where one has multiple partners and will find the lowest ranking one, this used to be used only when males asked,
				//	but when the male condition was removed, it actually improved the female ask time, so I just removed it
				if(males){
					for(int k = 0; k < p1.matches.size(); k++)
						j=p1.ranking(p1.matches.get(k).n)>j?p1.ranking(p1.matches.get(k).n):j;
					p1.locate = j; 
					j = -1;
				}
				//change partners if miai[i] is p1's higher ranked than their current/current lowest ranked partner,
				//	if so then remove p1's lowest ranked/current parnter and that partner's mathces
				if(p1.locate > p1.ranking(i)){
					//p1's previous partner will soon lose one partner, leaving them unsatisfied,
					//	hence decrementing sati ONLY IF THEY WERE ALREADY SATISFIED BEFORE
					if(miai[p1.ary[p1.locate]].matches.size() == miai[p1.ary[p1.locate]].prs)
						sati -= 1;
					//miai[i]'s new match will have to remove it's current partner
					miai[p1.ary[p1.locate]].remove(p1);
					//previously iff males asked then p1 will also have to remove their previous parter, now for consistancy, both will do so
					p1.matches.add(miai[i]);
					p1.remove(miai[p1.ary[p1.locate]]);
					//add it's new match and reassign it's new partner's location in it's ranking array
					miai[i].matches.add(p1);
					p1.locate = p1.ranking(i);
				}
			} else {
				//else if they're not taken then just add
				miai[i].matches.add(p1);
				if(males)
					p1.matches.add(miai[i]);
				p1.taken = true;
				p1.locate = p1.ranking(i);
			}
			//increment r (ranking) which will be the index in miai[i]'s ranking array
			miai[i].r += 1;
			//if miai[i] is satisfied then sati++
			if(miai[i].matches.size() == miai[i].prs)
				sati += 1;
			else 
				i -= 1;
		}
		stop = System.nanoTime();
		start = stop - start - overhead;
		//if triex command was invoked then print the matching matrix w/ array of rankings
		if(triex)
			triex(new int[y][x][2], miai, x);
		//Print out all women and their matches
		for(p = 0; p < x; p++){
			System.out.printf("\n%-8s:", miai[p].name);
			for(int t = 0; t < miai[p].matches.size(); t++)
				System.out.printf(" %s,", miai[p].matches.get(t).name);
			System.out.print("\b ");
		}
		//Print time
		System.out.printf("\nTime(ns): %d\n", start);

	}
	

	//return the Person object from the hash table
	static Person find(String name){
		int n = name.length()%mod;
		for(int i = 0; i < hash.get(n).size(); i++)
			if(name.equals(hash.get(n).get(i).name))
				return hash.get(n).get(i);
		return null;
	}

	//Format the 3 dimensional array with the names in their appropriate names and columns
	//and fill it with their rankings of each other. [male's evaluation of the woman, woman's evaluation of the male]
	static void triex(int[][][] triex, Person[] ary, int x){
		for(int i = 0; i < triex.length; i++)
			for(int j = 0; j < triex[i].length; j++){
				triex[i][j][0] = ary[i+x].ranking(j) + 1;
				triex[i][j][1] = ary[j].ranking(i+x) + 1;
			}
		System.out.printf("%-8s","");
		int i = 0;
		for(; i < x; i++)
			System.out.printf("%-8s", ary[i].name);
		System.out.println();
		for(; i < ary.length; i++){
			System.out.printf("%-8s", ary[i].name);
			for(int[] p : (triex[i-x]))
				System.out.printf("%-8s", Arrays.toString(p));
			System.out.println();
		}
	}
}
