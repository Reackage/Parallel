import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;


public class initial implements Comparable<initial> {
	int mazeNum=0;
	int mazeValue=0;
	int[][] maze;
	int[] gene = new int[80];
	int maxFill=0;
	int x=0;
	int y=0;
	int xEnd=0;
	int yEnd=0;
	double breedChance=0.05;
	double mutateChance=0.05;
	//sets up the class, initializes the string and the maze.
	public initial(int xLen, int yLen,int max, Random randnum)
	{
		
		x=xLen;
		y=yLen;
		//System.out.println(x+" "+y);
		maze = new int[x][y];	
		maxFill=max;
		// sets up the gene
		for(int i=0; i<80;i++)
		{
			gene[i]= randnum.nextInt(65535);
			//65535
		}
		// prepares the maze
		for(int a=1; a<x-1; a++)
		{
			for(int b=1; b<y-1; b++)
			{				
				maze[a][b]=0;
			}			
		}
		for(int a=0; a<x; a++)
		{
			maze[a][0]=1;
			maze[a][y-1]=1;
		}
		for(int a=0; a<x; a++)
		{
			maze[a][0]=1;
			maze[a][y-1]=1;
		}
		for(int a=0; a<y; a++)
		{
			//System.out.println(a);
			maze[0][a]=1;
			maze[x-1][a]=1;
		}
		// sets up the "rooms" for the start and exit
		for(int a=1; a<3; a++)
		{
			for(int b=1; b<3; b++)
			{	
				maze[a][b]=0;
			}
		}
		for(int a=x-2; a>x-4; a--)
		{
			for(int b=y-2; b>y-4; b--)
			{	
				maze[a][b]=0;
			}
		}	
		xEnd=x-2;
		yEnd=y-2;
	}
	
	//copies a origional gene into this maze
	public initial(int xOr, int yOr, int max, int origionalGene[])
	{
		x=xOr;
		y=yOr;
		xEnd=x-2;
		yEnd=y-2;
		for(int i=0; i<80; i++)
		{
				gene[i]=origionalGene[i];
			
		}
		maxFill=max;
		maze=new int[x][y];
		for(int a=1; a<x-1; a++)
		{
			for(int b=1; b<y-1; b++)
			{				
				maze[a][b]=0;
			}			
		}
		for(int a=0; a<x; a++)
		{
			maze[a][0]=1;
			maze[a][y-1]=1;
		}
		for(int a=0; a<x; a++)
		{
			maze[a][0]=1;
			maze[a][y-1]=1;
		}
		for(int a=0; a<y; a++)
		{
			//System.out.println(a);
			maze[0][a]=1;
			maze[x-1][a]=1;
		}
		// sets up the "rooms" for the start and exit
		for(int a=1; a<3; a++)
		{
			for(int b=1; b<3; b++)
			{	
				maze[a][b]=0;
			}
		}
		for(int a=x-2; a>x-4; a--)
		{
			for(int b=y-2; b>y-4; b--)
			{	
				maze[a][b]=0;
			}
		}	
	
		// TODO Auto-generated constructor stub
	}
	//copies a physical maze
	public initial(int xOr, int yOr, int max, int originalMaze[][]) 
	{
		
		x=xOr;
		y=yOr;
		xEnd=x-2;
		yEnd=y-2;
		for(int i=0; i<x; i++)
		{
			for(int j=0; j<y;j++)
			{
				maze[i][j]=originalMaze[i][j];
			}
		}
		maxFill=max;
		
		// TODO Auto-generated constructor stub
	}
	/**
	 * @param args
	 */
	//not used, dont use it, causes issues
	public initial clone()
	{
		initial clone= new initial(x,y,maxFill, maze);
		return clone;
	
		
	}

	//this checks if there is a solution to the problem, as it may look close to short search, it has a copy of the maze[][] and alters it for a faster search area
	public boolean checkExit(int searchType)
	{
		node current;
		//this Queue contains all the points it wishes to explore
		PriorityBlockingQueue<node> queue = new PriorityBlockingQueue<node>();
		
		current=new node(1,1,null);
		current.pathLength=0;
		queue.add(current);
		int[][] copy = new int[x][y];//clone was not preforming correctly
			for(int i=0; i<x;i++)
			{
				for(int j=0; j<y;j++)
				{
					copy[i][j]=maze[i][j];
				}
				
			}
		
		current=new node(1,1,null);
		//current.setSearch(searchType);
		current.pathLength=0;
		queue.add(current);
		@SuppressWarnings("unused")
		int count=0;
		//while there are nodes to pop off
		while(!queue.isEmpty())
		{
			count++;
			try {
				
				current= queue.take();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//if the current node is the end:
			if(current.xCor==xEnd&&current.yCor==yEnd)
			{
			
				return true;
			}
			node newNode=null;
			if(copy[current.xCor][current.yCor]==1)
			{
				//System.out.println("killing visited node");
				continue;
			}
			//sets the current node to a wall
			
			copy[current.xCor][current.yCor]=1;
			
			//checks all 4 moves, see if they can be pushed into the queue
			if(copy[current.xCor-1][current.yCor]!=1)
			{
				if(current.checkexists(current.xCor-1, current.yCor))
				{
					newNode= new node(current.xCor-1,current.yCor,current);					
					newNode.pathLength=current.pathLength+1;
					newNode.pathCost=newNode.pathLength+((xEnd-newNode.xCor)+(yEnd-newNode.yCor));
					queue.add(newNode);
				}
			}
			//System.out.println(maze[current.xCor+1][current.yCor-1]);
			if(copy[current.xCor+1][current.yCor]!=1)
			{
				//System.out.println(":it can go right"+current.xCor +"," +current.yCor);
				if(current.checkexists(current.xCor+1, current.yCor))
				{
					newNode= new node(current.xCor+1,current.yCor,current);
					//newNode.setSearch(current.searchFlag);
					newNode.pathLength=current.pathLength+1;
					newNode.pathCost=newNode.pathLength+((xEnd-newNode.xCor)+(yEnd-newNode.yCor));
					queue.add(newNode);
				}
			}
			//System.out.println(maze[current.xCor-1][current.yCor+1]);
			if(copy[current.xCor][current.yCor-1]!=1)
			{
				//System.out.println(":it can go down"+current.xCor +"," +current.yCor);
				if(current.checkexists(current.xCor, current.yCor-1))
				{
					newNode= new node(current.xCor,current.yCor-1,current);
					//newNode.setSearch(current.searchFlag);
					newNode.pathLength=current.pathLength+1;
					newNode.pathCost=newNode.pathLength+((xEnd-newNode.xCor)+(yEnd-newNode.yCor));
					queue.add(newNode);
				}
			}	
			//System.out.println(maze[current.xCor-+1][current.yCor+1]);
			
			if(copy[current.xCor][current.yCor+1]!=1)
			{
				//System.out.println(":it can go up"+current.xCor +"," +current.yCor);
				if(current.checkexists(current.xCor, current.yCor+1))
				{
					newNode= new node(current.xCor,current.yCor+1,current);
					//newNode.setSearch(current.searchFlag);
					newNode.pathLength=current.pathLength+1;
					newNode.pathCost=newNode.pathLength+((xEnd-newNode.xCor)+(yEnd-newNode.yCor));
					queue.add(newNode);
				}
			}			
		}
		return false;		
	}
	//never used, ment to initialize a maze with another representation, was dropped due to time constraints
	public void creation()
	{
		for(int i=0; i<80;i+=2)
		{
			int type=(gene[i])>>13;
			int remainder=gene[i];
			if(remainder>=32768)
			{
				remainder-=32768;
			}
			if(remainder>=16384)
			{
				remainder-=16384;
			}
			if(type==0)//means skip
			{
				continue;
			}
			int xPos=gene[i+1]%x;
			int yPos=(gene[i+1]/x)%y;
			int corridorLen=0;
			int roomHight=0;
			int roomWidth=0;
			if(type==1)
			{
				corridorLen=remainder%x;
				for(int a=xPos; a<corridorLen&&a<x; a++)
				{
					maze[a][yPos]=0;
				}
			}
			else if(type==2)
			{
				corridorLen=remainder%y;
				for(int a=xPos; a<corridorLen&&a<y; a++)
				{
					maze[a][yPos]=0;
				}
			}
			else
			{
				roomWidth=(remainder%4)+2;
				roomHight=((remainder/4)%4)+2;
				for(int a=xPos; a<roomWidth&&a<x; a++)
				{
					for(int b=xPos; b<roomHight&&b<y; b++)
					{
						maze[a][b]=0;
					}
				}
			}
			
		}
	}
	
	// this unpacks the gene, meaning it will generate the maze based on the gene array.
	//this presets the walls to be between the length 1 to 3, called sparse initiation
	public void firstGo(Random randnum)
	{
		int max=Math.max(x,y);
		for(int i=0; i<80;i+=2)
		{
			int flag=0;
			while(flag==0)
			{
				int len=(gene[i]);
				// this gives us the remainder, used for wall length
				if(len>=32768)
				{
					len-=32768;					
				}
				if(len>=16384)
				{
					len-=16384;				
				}
				if(len>=8192)
				{
					len-=8192;				
				}
				if((len%max)<=3)
				{
					flag=1;
					//System.out.println(len);
				}
				else
				{
					gene[i]=randnum.nextInt(65535);
				}
			}
			
		}
	}
	//unpacks the maze from a gene. this means turn the 80 integer array into a maze
	public void unpack()
	{			
		int fillCount=0;
		for(int a=1; a<x-1; a++)
		{
			for(int b=1; b<y-1; b++)
			{				
				maze[a][b]=0;
			}			
		}
		for(int i=0; i<80&&fillCount<maxFill;i+=2)
		{
			// if the wall is penetrating 
			int penFlag = (gene[i])>>15;
			//System.out.println(one);
			// direction of the wall
			int direction = (gene[i])>>13;
			if(direction>=4)
			{
				direction-=4;
			}
			int len=(gene[i]);
			// this gives us the remainder, used for wall length
			if(len>=32768)
			{
				len-=32768;					
			}
			if(len>=16384)
			{
				len-=16384;			
				
			}
			if(len>=8192)
			{
				len-=8192;				
				
			}
			// gives us the x, y cord of where the wall starts!
			int temp=len;
			if(x>y)
			{
				temp=len%x;
			}
			else
			{
				temp=len%y;
			}
			
			int xcor=0, ycor=0;
			xcor=gene[i+1]%x;
			ycor= (gene[i+1]/x)%y;
			
			//TOREM: remove this
			//System.out.println(gene[i] + "|"+len+" "+temp+" x,y= "+xcor+","+ycor);
			
			//this alters the maze.
			int curCount=0;
			while(xcor>=1 &&xcor<x-1 && ycor>=1 && ycor<y-1&&fillCount<maxFill&&temp>curCount)
			{
				if(maze[xcor][ycor]==0&&penFlag==1)
				{
					break;
				}
				maze[xcor][ycor]=1;
				if(direction==0)
				{
					xcor--;
				}
				if(direction==1)
				{
					ycor--;
				}
				if(direction==2)
				{
					xcor++;
				}
				if(direction==3)
				{
					ycor++;
				}
				fillCount++;
				curCount++;
			}
			
			
		}	
		for(int a=1; a<3; a++)
		{
			for(int b=1; b<3; b++)
			{	
				maze[a][b]=0;
			}
		}
		for(int a=x-2; a>x-4; a--)
		{
			for(int b=y-2; b>y-4; b--)
			{	
				maze[a][b]=0;
			}
		}	
		
	}
	//this search uses an A* algorithm. the heuristic function is the Manhattan Distance. This also records the previous nodes in a hashmap.
	public node shortSearch(int searchType)
	{
		node current;
		PriorityBlockingQueue<node> queue = new PriorityBlockingQueue<node>();
		
		ConcurrentHashMap<Integer,node> map = new ConcurrentHashMap<Integer,node>();
		
		current=new node(1,1,null);
		//current.setSearch(searchType);
		int cords=1+1*x;
	
		map.put(new Integer(cords),current);
		current.pathLength=0;
		queue.add(current);
		//while there are nodes to look at
		while(!queue.isEmpty())
		{
			try { 
				
				current= queue.take();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(current.xCor==xEnd&&current.yCor==yEnd)
			{
				//System.out.println("this hates you");
				return current;
			}
			node newNode=null;
			//System.out.println("testing point "+current.xCor+" "+current.yCor);
			//System.out.println(current.xCor +"," +current.yCor);
			//System.out.println(maze[current.xCor-1][current.yCor-1]);
			if(maze[current.xCor-1][current.yCor]!=1)
			{
				if(current.checkexists(current.xCor-1, current.yCor))
				{
					cords=current.xCor-1+ current.yCor*x;
					//System.out.println(x)
					if(!map.containsKey(new Integer(cords)))
					{
						
						newNode= new node(current.xCor-1,current.yCor,current);
						//newNode.setSearch(current.searchFlag);
						newNode.pathLength=current.pathLength+1;
						newNode.pathCost=newNode.pathLength+((xEnd-newNode.xCor)+(yEnd-newNode.yCor));
						queue.add(newNode);
						map.put(cords, newNode);
					}
					
				}
			}
			//checks if it can move into another point
			//System.out.println(maze[current.xCor+1][current.yCor-1]);
			if(maze[current.xCor+1][current.yCor]!=1)
			{
				if(current.checkexists(current.xCor+1, current.yCor))
				{
					
					cords=current.xCor+1+ current.yCor*x;
					if(!map.containsKey(new Integer(cords)))
					{
						newNode= new node(current.xCor+1,current.yCor,current);
						//newNode.setSearch(current.searchFlag);
						newNode.pathLength=current.pathLength+1;
						newNode.pathCost=newNode.pathLength+((xEnd-newNode.xCor)+(yEnd-newNode.yCor));
						queue.add(newNode);
						map.put(cords, newNode);
					}
				}
			}
			//System.out.println(maze[current.xCor-1][current.yCor+1]);
			if(maze[current.xCor][current.yCor-1]!=1)
			{
				if(current.checkexists(current.xCor, current.yCor-1))
				{
					cords=current.xCor+ (current.yCor-1)*x;
					if(!map.containsKey(new Integer(cords)))
					{
						newNode= new node(current.xCor,current.yCor-1,current);
						//newNode.setSearch(current.searchFlag);
						newNode.pathLength=current.pathLength+1;
						newNode.pathCost=newNode.pathLength+((xEnd-newNode.xCor)+(yEnd-newNode.yCor));
						queue.add(newNode);
						map.put(cords, newNode);
					}
				}	
			}	
			//System.out.println(maze[current.xCor-+1][current.yCor+1]);
			if(maze[current.xCor][current.yCor+1]!=1)
			{
				if(current.checkexists(current.xCor, current.yCor+1))
				{
					cords=current.xCor+ (current.yCor+1)*x;
					if(!map.containsKey(new Integer(cords)))
					{
				
						newNode= new node(current.xCor,current.yCor+1,current);
						//newNode.setSearch(current.searchFlag);
						newNode.pathLength=current.pathLength+1;
						newNode.pathCost=newNode.pathLength+((xEnd-newNode.xCor)+(yEnd-newNode.yCor));
						queue.add(newNode);
						map.put(cords, newNode);
					}
				}
			}			
		}		
		return current;
	}
	//this calculates the path from a node to the enterance
	public node checkPointSearch(int xGoal, int yGoal)
	{
		node current;
		PriorityBlockingQueue<node> queue = new PriorityBlockingQueue<node>();
		
		ConcurrentHashMap<Integer,node> map = new ConcurrentHashMap<Integer,node>();
		
		current=new node(1,1,null);
		//current.setSearch(searchType);
		int cords=1+1*x;
	
		map.put(new Integer(cords),current);
		current.pathLength=0;
		queue.add(current);
		
		while(!queue.isEmpty())
		{
			try { 
				
				current= queue.take();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(current.xCor==xGoal&&current.yCor==yGoal)
			{
				//System.out.println("this hates you");
				return current;
			}
			node newNode=null;
			//System.out.println("testing point "+current.xCor+" "+current.yCor);
			//System.out.println(current.xCor +"," +current.yCor);
			//System.out.println(maze[current.xCor-1][current.yCor-1]);
			if(maze[current.xCor-1][current.yCor]!=1)
			{
				if(current.checkexists(current.xCor-1, current.yCor))
				{
					cords=current.xCor-1+ current.yCor*x;
					//System.out.println(x)
					if(!map.containsKey(new Integer(cords)))
					{
						
						newNode= new node(current.xCor-1,current.yCor,current);
						//newNode.setSearch(current.searchFlag);
						newNode.pathLength=current.pathLength+1;
						int xPathCost=xGoal-newNode.xCor;
						int yPathCost=yGoal-newNode.yCor;
						if(xPathCost<0)
						{
							xPathCost*=-1;
						}
						if(yPathCost<0)
						{
							yPathCost*=-1;
						}
						newNode.pathCost=newNode.pathLength+xPathCost+yPathCost;
						queue.add(newNode);
						map.put(cords, newNode);
					}
					
				}
			}
			//System.out.println(maze[current.xCor+1][current.yCor-1]);
			if(maze[current.xCor+1][current.yCor]!=1)
			{
				if(current.checkexists(current.xCor+1, current.yCor))
				{
					
					cords=current.xCor+1+ current.yCor*x;
					if(!map.containsKey(new Integer(cords)))
					{
						newNode= new node(current.xCor+1,current.yCor,current);
						//newNode.setSearch(current.searchFlag);
						newNode.pathLength=current.pathLength+1;
						int xPathCost=xGoal-newNode.xCor;
						int yPathCost=yGoal-newNode.yCor;
						if(xPathCost<0)
						{
							xPathCost*=-1;
						}
						if(yPathCost<0)
						{
							yPathCost*=-1;
						}
						newNode.pathCost=newNode.pathLength+xPathCost+yPathCost;
						queue.add(newNode);
						map.put(cords, newNode);
					}
				}
			}
			//System.out.println(maze[current.xCor-1][current.yCor+1]);
			if(maze[current.xCor][current.yCor-1]!=1)
			{
				if(current.checkexists(current.xCor, current.yCor-1))
				{
					cords=current.xCor+ (current.yCor-1)*x;
					if(!map.containsKey(new Integer(cords)))
					{
						newNode= new node(current.xCor,current.yCor-1,current);
						//newNode.setSearch(current.searchFlag);
						newNode.pathLength=current.pathLength+1;
						int xPathCost=xGoal-newNode.xCor;
						int yPathCost=yGoal-newNode.yCor;
						if(xPathCost<0)
						{
							xPathCost*=-1;
						}
						if(yPathCost<0)
						{
							yPathCost*=-1;
						}
						newNode.pathCost=newNode.pathLength+xPathCost+yPathCost;
						queue.add(newNode);
						map.put(cords, newNode);
					}
				}	
			}	
			//System.out.println(maze[current.xCor-+1][current.yCor+1]);
			if(maze[current.xCor][current.yCor+1]!=1)
			{
				if(current.checkexists(current.xCor, current.yCor+1))
				{
					cords=current.xCor+ (current.yCor+1)*x;
					if(!map.containsKey(new Integer(cords)))
					{
				
						newNode= new node(current.xCor,current.yCor+1,current);
						//newNode.setSearch(current.searchFlag);
						newNode.pathLength=current.pathLength+1;
						int xPathCost=xGoal-newNode.xCor;
						int yPathCost=yGoal-newNode.yCor;
						if(xPathCost<0)
						{
							xPathCost*=-1;
						}
						if(yPathCost<0)
						{
							yPathCost*=-1;
						}
						newNode.pathCost=newNode.pathLength+xPathCost+yPathCost;
						queue.add(newNode);
						map.put(cords, newNode);
					}
				}
			}			
		}		
		return current;
	}
	//printss the maze
	public void printMaze()
	{
		for(int a=0; a<x; a++)
		{
			for(int b=0; b<y; b++)
			{				
				System.out.print(maze[a][b]);
			}			
			System.out.println();
		}
		
	}
	//this is for reading in mazes, sets the path equal to 4 for visual appeal
	public void path(node current)
	{
		while(current!=null)
		{
			maze[current.xCor][current.yCor]=4;
			current=current.parent;
		}
	}
	
	public int compareTo(initial temp) {
		
		return mazeNum-temp.mazeNum;
	}
	//swaps genes between parents
	public void breedParents(initial a, initial b, Random randnum)
	{
		double chance=breedChance;
		for(int i=0; i<80; i++)
		{
			if(randnum.nextDouble()<chance)
			{
				gene[i]=b.gene[i];
			}
			else
			{
				gene[i]=a.gene[i];
			}
		}
		
	}
	//mutates genes
	public void mutation(Random randnum)
	{
		double chance=mutateChance;
		for(int i=0; i<80;i++)
		{
			if(randnum.nextDouble()<chance)
			{
				gene[i]=randnum.nextInt(65535);
			}
		}
		
	}
	public static void main(String[] args) {
		long t0 = System.currentTimeMillis();
		Random randnum = new Random();
		
	
		int x=1;
		int y=1;
		double lim=0;
		int maxFilled=0;
		@SuppressWarnings("unused")
		int i=0;		
		int seed=0;		
		int searchType=0;
		int maxGen=50000;
		int printGen=2000;
		double cP=0.05;
		double mP=0.05;
		int desiredValue=0;
		int numPro=1;
		for(String s: args)
		{
			if(s.contains("file:"))
			{
				String fileName= new String(s.substring(5));
				generations gene=new generations();
				try {
					gene.geneReader(fileName);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(s.contains("x"))
			{
				x= Integer.parseInt(s.substring(1));
			}
			if(s.contains("y"))
			{
				y= Integer.parseInt(s.substring(1));
			}
			if(s.contains("g"))
			{
				maxGen= Integer.parseInt(s.substring(1));
			}
			if(s.contains("o"))
			{
				printGen= Integer.parseInt(s.substring(1));
			}
			if(s.contains("s"))
			{
				searchType= Integer.parseInt(s.substring(1));
			}
			if(s.contains("p"))
			{
				numPro= Integer.parseInt(s.substring(1));
			}
			if(s.contains("l"))
			{
				lim= Double.parseDouble(s.substring(1));
			}
			if(s.contains("m"))
			{
				mP=Double.parseDouble(s.substring(1));
			}
			if(s.contains("b"))
			{
				cP= Double.parseDouble(s.substring(1));
			}
			if(s.contains("r"))
			{
				seed= Integer.parseInt(s.substring(1));
			}
			if(s.contains("d"))
			{
				desiredValue= Integer.parseInt(s.substring(1));
			}
		}
		if(x==0|| y==0||searchType>=2||searchType<0||maxGen<1||numPro<1)
		{
			System.out.println("Error: Value(s) are incorrect, please try again");
		}
		if(seed!=0)
		{
			randnum.setSeed(seed);
		}
		
		maxFilled=(int) (x*y*lim);		        
		
		generations gen = new generations();
		gen.gen(x, y, randnum, searchType, maxFilled, desiredValue, maxGen, printGen, mP, cP, numPro);
		long t1 = System.currentTimeMillis();
        
		double elapsedTimeSeconds = (t1 - t0)/1000.0;
		System.out.println("Time " + elapsedTimeSeconds + " s");
		
	}
	

}
