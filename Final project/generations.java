import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;


public class generations {
	int curGen=0;
	//int maxGen=500000;
	int maxGen=2000;
	int mazePop=120;
	int NUM_OF_TASKS=10;
	int search=0;
	int numProcessors=0;
	//These Queue stores all the mazes, sorted by order they were entered
	PriorityBlockingQueue<initial> queue = new PriorityBlockingQueue<initial>();

	PriorityBlockingQueue<initial> storage = new PriorityBlockingQueue<initial>();
/**This method reads genes from a file */
	@SuppressWarnings("unused")
	public void geneReader(String s) throws IOException
	{
		System.out.println("This will now print the entire generations mazes. ");
		System.out.println("The first image will contain no informaion about the maze.");
		System.out.println("The second image will contain the shortest path calculated.");
		int x=0,  y=0,  searchType=0,  maxFilled=0,  desiredLength=0;
		BufferedReader in = new BufferedReader(new FileReader(s)); 
		String text;
		StringTokenizer tokenizer;
		if(in.ready())
		{
			text = in.readLine();
			tokenizer=new StringTokenizer(text);
			x=Integer.parseInt(tokenizer.nextToken());
			y=Integer.parseInt(tokenizer.nextToken());
			searchType=Integer.parseInt(tokenizer.nextToken());
			maxFilled=Integer.parseInt(tokenizer.nextToken());
			desiredLength=Integer.parseInt(tokenizer.nextToken());

		}
		while (in.ready()) 
		{ 
			int[] gene=new int[80];
			text = in.readLine(); 
			tokenizer=new StringTokenizer(text);
			for(int i=0; i<80; i++)
			{
				//System.out.println(i);
				gene[i]=Integer.parseInt(tokenizer.nextToken());
				System.out.println(gene[i]);
			}
			initial temp= new initial(x,y,maxFilled, gene);
			temp.unpack();
			temp.printMaze();
			System.out.println();
			if(temp.checkExit(0))
			{
				if(searchType==0)
				{
					//System.out.println("it gets in here and you are crazy");
					node re=temp.shortSearch(0);
					temp.path(re);
					if(re==null)
					{ 
						//System.out.println("wat");
					}
					else
					{
						System.out.println("pathlength "+re.pathLength);
					}
				}
				if(searchType==1)
				{
					node resultsA= temp.checkPointSearch(temp.x-temp.x/3, temp.y/3);
					//System.out.println(resultsA.pathLength+" "+(x-x/3)+" "+(y/3));

					node resultsB= temp.checkPointSearch(temp.x/3, temp.y-temp.y/3);
					//System.out.println(resultsB.pathLength+" "+(x/3)+" "+(y-y/3));
					temp.path(resultsA);
					temp.path(resultsB);
					temp.mazeValue=resultsA.earlistNodeDist(resultsB);
				}
				temp.printMaze();
			}
			else
			{
				System.out.println("No solution to maze");
			}
			System.out.println("Hit enter for next maze");
			BufferedReader enter = new BufferedReader(new InputStreamReader(System.in));
			enter.readLine();
		}


		in.close();
	}
/**This method writes genes to a file */
	public void writeGenes(String s, int x, int y, int searchType, int maxFilled, int desiredLength)
	{
		FileWriter feed;
		BufferedWriter out;
		try {
			feed = new FileWriter(s);
			out= new BufferedWriter(feed);
			
			out.write(x+" "+y+" "+searchType+" "+maxFilled+" "+desiredLength); 
			out.newLine();
			while(!queue.isEmpty())
			{
				initial temp=queue.poll();
				out.write(""+temp.gene[0]);
				//System.out.println(temp.gene[0]);
				for(int i=1; i<79; i++)
				{
					out.write(" "+temp.gene[i]);
				}

				out.write(" "+temp.gene[79]);
				out.newLine();
				storage.add(temp);
			}
			queue.addAll(storage);
			storage.clear();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error writing to file: exiting program");
			System.exit(0);
		} 


	}
	
	/**this method computes all the generations of the mazes */
	public void gen(int x, int y, Random randnum, int searchType, int maxFilled, int desiredLength, int maxGens, int printGen, double mutationChance, double breedChance, int numPro)
	{
		numProcessors=numPro;
		maxGen=maxGens;
		int x1=0;
		search=searchType;
		/* Initializes all the mazes*/
		for(x1=0; x1<mazePop; x1++)
		{
			//System.out.println(x1);
			initial maze =  new initial(x,y, maxFilled, randnum);
			maze.firstGo(randnum);
			maze.mazeNum=x1;
			maze.mutateChance=mutationChance;
			maze.breedChance=breedChance;
			queue.add(maze);
		}
		// Starts the generations
		for(x1=0; x1<maxGen; x1++)
		{
			//checks if its time to write to a file
			if(x1%200==0)
			{
				//String nameFile=new String("generation"+Integer.toString(x1)+"x"+Integer.toString(x)+"y"+Integer.toString(y)+".txt");
			

				//this.writeGenes(nameFile,x, y, searchType, maxFilled, desiredLength);
				System.out.println("Generation #:"+x1);
			}
			if(printGen!=0)
			{
				if(x1%printGen==0)
				{
					String nameFile=new String("generation"+Integer.toString(x1)+"x"+Integer.toString(x)+"y"+Integer.toString(y)+".txt");
					this.writeGenes(nameFile,x, y, searchType, maxFilled, desiredLength);
				}
			}
			curGen=x1;
			
			//This parallelizes the program, broken down into unpacking each maze and running a fitness on them
			new CompletionServiceTest().run();
			
			queue.clear();
				
			//this checks over the fitness and breeds/mutates a maze
			if(storage.size()==0)
			{
				System.exit(0);//TODO fix this
			}
			else
			{
				//this is an offset randomized to give "equal" chance to breed/ compare mazes
				int[] offset= new int[4];
				offset[0]=randnum.nextInt(29);
				offset[1]=randnum.nextInt(29);
				offset[2]=randnum.nextInt(29);
				offset[3]=randnum.nextInt(29);
				ArrayList<initial> index=new ArrayList<initial>();
				for(int i=0; i<120; i++)
				{
					index.add(storage.poll());
				}
				for(int i=0; i<120; i+=4)
				{
					//this grabs the off set mazes to compare
					initial[] breeding= new initial[4];
					for(int j=0; j<4;j++)
					{
						int location=offset[j]*4+j+i;
						if(location>=120)
						{
							location-=120;
						}
						
						breeding[j]=index.get(location);
					}
					int max1=0;
					for(int j=1; j<4;j++)
					{
						int valueA= desiredLength - breeding[max1].mazeValue;
						if(valueA<0)
						{
							valueA*=-1;
						}
						int valueB=desiredLength - breeding[j].mazeValue;
						if(valueB<0)
						{
							valueB*=-1;
						}
						if(valueB <valueA)//if it has a closer result to desired:
						{
							max1=j;
						}
					}
					int max2=1;
					for(int j=2; j<4; j++)
					{
						int valueA= desiredLength - breeding[max2].mazeValue;
						if(valueA<0)
						{
							valueA*=-1;
						}
						int valueB=desiredLength - breeding[j].mazeValue;
						if(valueB<0)
						{
							valueB*=-1;
						}
						if(valueB <valueA)//if it has a closer result to desired:
						{
							max2=j;
						}
					}
					int maxFlag=0;
					int breedA=0; int breedB=0;
					for(int j=0; j<4; j++)
					{
						if(j==max1|| j==max2)
						{
							continue;
						}
						if(maxFlag==0)
						{
							breedA=j;
						}
						else
						{
							breedB=j;
							break;
						}
						maxFlag++;
					}
					
					//this breeds and mutates genes
					breeding[breedA].mazeValue=0;
					breeding[breedB].mazeValue=0;
					breeding[breedA].breedParents(breeding[max1], breeding[max2],randnum);
					breeding[breedB].breedParents(breeding[max2], breeding[max1],randnum);
					breeding[breedA].mutation(randnum);
					for(int k=0; k<4;k++)
					{
						queue.add(breeding[k]);
						//System.out.println("queue size: "+queue.size());
					}
				}
				index.clear();
				storage.clear();
			}
			//System.out.println("end of generation: "+x1);
		}
		if(printGen!=0)
		{
			String nameFile=new String("generation"+Integer.toString(maxGen)+"x"+Integer.toString(x)+"y"+Integer.toString(y)+".txt");
			this.writeGenes(nameFile,x, y, searchType, maxFilled, desiredLength);
		}
		initial print=null;
		initial print2=null;
		print = queue.poll();

		//print.printMaze();
		//this finds the Winner/most qualified maze
		while(!queue.isEmpty())
		{
			print2=queue.poll();
			//print2.printMaze();
			int valueA= desiredLength - print.mazeValue;
			if(valueA<0)
			{
				valueA*=-1;
			}
			int valueB=desiredLength - print2.mazeValue;
			if(valueB<0)
			{
				valueB*=-1;
			}
			if(valueB <valueA)//if it has a closer result to desired:
			{
				print=print2;
			}
		}
		System.out.println("This is the best maze in this generation:");
		print.printMaze();
	}
	public class CompletionServiceTest {

		public CompletionServiceTest() {
		}

		@SuppressWarnings({ "unchecked", "unused" })
		public void run() {
			int nrOfProcessors = Runtime.getRuntime().availableProcessors();
			nrOfProcessors = numProcessors;
			ExecutorService eservice = Executors.newFixedThreadPool(nrOfProcessors);
			CompletionService< Object> cservice = new ExecutorCompletionService< Object>(eservice);
			int index = 0;
			for (index = 0; index < NUM_OF_TASKS; index++){
				cservice.submit(new Task(index));
			}

			Object taskResult;
			for (int i = 0; i < index; i++) {
				try {
					taskResult = cservice.take().get();
					//System.out.println("result " + taskResult);
				} catch (InterruptedException e) {
				} catch (ExecutionException e) {
				}
			}
			eservice.shutdown();
		}
	}

	@SuppressWarnings("rawtypes")
	public class Task implements Callable {

		private int seq;

		public Task() {
		}

		public Task(int i) {
			seq = i;
		}

		public Object call() {
			//System.out.println("start - Task " + seq);
			while(!queue.isEmpty()){
				//for (int i = 0; i < 1; i++) {
				/* if (queue.isEmpty()) {
break;
}*/
				initial current = null;
				current = queue.poll();
				if (current == null) {
				//	System.out.println("Error: an exception occured, exiting."+storage.size());
					continue;
				}
				current.unpack();

				//current.printMaze();
				//System.out.println("testing.current.exitcheck");
				boolean testing = current.checkExit(0);
				//System.out.println("testing.current.exitcheck complete");
				//System.out.println("finished checking e)
				if (testing == false) {
					//System.out.println("no solution");
					//TODO we want to add in the bad ones to compare, fix the compare function for expected results!
					current.mazeValue = 0;
					storage.add(current);
				} else {
					if(search==0)
					{
						//System.out.println("it starts shortSearch");
						node results=current.shortSearch(search);
						//System.out.println("end shortSearch");
						current.mazeValue=results.pathLength;
					}
					else if(search==1)
					{
						//TODO do a safe checkpoint search
						//current.printMaze();
						if(current.maze[current.x-current.x/3][current.y/3]==1)
						{
							current.mazeValue=0;
						}
						else if(current.maze[current.x/3][current.y-current.y/3]==1)
						{
							current.mazeValue=0;
						}
						else
						{
							node resultsA= current.checkPointSearch(current.x-current.x/3, current.y/3);
							//System.out.println(resultsA.pathLength+" "+(x-x/3)+" "+(y/3));

							node resultsB= current.checkPointSearch(current.x/3, current.y-current.y/3);
							//System.out.println(resultsB.pathLength+" "+(x/3)+" "+(y-y/3));
							current.mazeValue=resultsA.earlistNodeDist(resultsB);
						}

					}
					storage.add(current);
				}
			}
			return seq;
		}
	}
}
