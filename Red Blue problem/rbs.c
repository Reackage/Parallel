/*
Assignment 2
By:Ricky Break
ID:
Class:CIS*3090
*/



#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include <semphore.h>



int pN=0;
int bN=0;
int tN=0;
int cN=0;
int mN=0;
int sN=-1;
int i=0;
int endFlag=0;
int interations=0;
int **redCount;
int **blueCount;
int **board;

pthread_mutex_t update_lock;
pthread_mutex_t barrier_lock;
pthread_cond_t all_here;
int threadCount=0;

int** boardInt();
void boardView(int **board);
void moveRed(int **board, int row);
void moveBlue(int **board, int col);
void tileSum(int **board, int row);
void interactiveMode(int **board);
void moveOnlyRed(int **board);
void moveOnlyBlue(int **board);
void completeTurns(int **board, int currentTurn, int max);
void argPrint();
void barrier();
/*creates the board*/
int** boardInt()
{
	redCount=malloc(sizeof(int*)*(bN/tN));
	blueCount=malloc(sizeof(int*)*(bN/tN));
	int **board=malloc(sizeof(int*)*bN);
	if(board==NULL)
	{
		printf("board was never initalized. exiting");exit(0);
	}
	int x, y;
	for(x=0; x<(bN/tN);x++)
	{
		redCount[x]=malloc(sizeof(int)*(bN/tN));
		blueCount[x]=malloc(sizeof(int)*(bN/tN));
		for(y=0; y<(bN/tN);y++)
		{
			redCount[x][y]=0;
			blueCount[x][y]=0;
		}
	}
	for(x=0; x<bN; x++)
	{
		board[x]=malloc(sizeof(int)*bN);
		for(y=0; y<bN; y++)
		{
			board[x][y]=rand()%3;
		}
	}
	return board;

}
/*prints board to screen*/
void boardView(int **board)
{
	int x, y;
	if(board==NULL)
	{
		printf("board never initalied. exiting");exit(0);
	}
	for(x=0; x< bN;x++)
	{//remove
		//printf("%d:",x);
		for(y=0; y<bN; y++)
		{
		
			if(board[x][y]==0)
			{
				printf(" ");fflush(stdout);
			}
			else if(board[x][y]==1)
			{
				printf(">");fflush(stdout);
			}
			else if(board[x][y]==2)
			{
				printf("v");fflush(stdout);
			}
			else
			{
				printf("Lulwat\n ");fflush(stdout);
			}
		}
		printf("\n");fflush(stdout);
	}
}
void boardPrint(int **board)
{
	int x, y;
	if(board==NULL)
	{
		printf("board never initalied. exiting");exit(0);
	}
	FILE *fp= fopen("redblue.txt", "w");
	for(x=0; x< bN;x++)
	{//remove
		//printf("%d:",x);
		for(y=0; y<bN; y++)
		{
		
			if(board[x][y]==0)
			{
				fprintf(fp,"%s"," ");
			}
			else if(board[x][y]==1)
			{
				fprintf(fp,"%s",">");
			}
			else if(board[x][y]==2)
			{
				fprintf(fp,"%s","v");
			}
			else
			{
				//printf("Lulwat\n ");
			}
		}
		fprintf(fp,"%s","\n");
	}
	int max=0;
	for(x=0; x<(bN/tN);x++)
	{
		for(y=0; y<(bN/tN);y++)
		{
			if(max<redCount[x][y])
			{
				max=redCount[x][y];
			}
			if(max<blueCount[x][y])
			{
				max=blueCount[x][y];
			}
		}
	}
	printf("p%d b%d t%d c%d m%d ",pN,bN,tN, cN, mN);
	fprintf(fp,"p%d b%d t%d c%d m%d ",pN,bN,tN, cN,mN);
	if(sN!=-1)
	{
		printf("s%d ",sN);
		fprintf(fp,"s%d ",sN);
	}
	if(i==1)
	{
		printf("i ");
		fprintf(fp,"i ");
	}
	//TODO: add time!
	printf("%dmax\n",max);
	printf("%d %d %.2f\n", interations, (max*100)/(tN*tN), 0.0000);
	fprintf(fp,"%d %d %.2f\n", interations, (max*100)/(tN*tN), 0.0000);
	fclose(fp);
}

void moveRed(int **board, int row)		
{
	int x;
	for(x=0; x<bN; x++)
	{
		if(board[row][x]==1)
		{
			if(x+1==bN) 
			{
				if(board[row][0]==0)
				{
					board[row][x]=0;
					board[row][0]=1;
				}
			}
			else 
			{
				if(board[row][x+1]==0)
				{
					board[row][x]=0;
					board[row][x+1]=1;
					x++;
				}
			}			
		}
	}
}

void moveBlue(int **board, int col)
{
	int x;
	for(x=0; x<bN; x++)
	{
		if(board[x][col]==2)
		{
			if(x+1==bN) 
			{
				if(board[0][col]==0)
				{
					board[x][col]=0;
					board[0][col]=2;
				}
			}
			else 
			{
				if(board[x+1][col]==0)
				{
					board[x][col]=0;
					board[x+1][col]=2;
					x++;
				}
			}			
		}
	}
}

void tileSum(int **board, int row)
{
	int x=0, y=0;
	int bCount=0, rCount=0;
	for(x=0; x<bN; x=x+tN)
	{
		for(y=x; y<((x/tN+1)*tN); y++)
		{
			if(board[row][y]==1)
			{
				//printf("%d,%d,>\n",row, y);
				rCount++;
			}
			if(board[row][y]==2)
			{
				bCount++;
			}
		}
		//needs a mutex here
		//printf("rcount[%d][%d]+%d\n",row/tN,x/tN,rCount);
		if(rCount>0)
		{
			redCount[row/tN][x/tN]+=rCount;
			
		}
		//printf("bcount[%d][%d]+%d\n",row/tN,x/tN,bCount);
		if(bCount>0)
		{
			blueCount[row/tN][x/tN]+=bCount;
	
		}
		rCount=0;
		bCount=0;
	}	
}

void interactiveMode(int **board)
{
	char str[100];
	int x, flag=0;
	int halfturn=0;
	while(1)
	{
		flag=0;
		printf("Please enter a command:\n");
		fgets(str,100, stdin);
		//printf("|%s|",str);
		
		if(strcmp(str,"\n")==0)
		{
			printf("one turn");
			if(halfturn)
			{
				halfturn=!halfturn;
				moveOnlyBlue(board);
			}
			else
			{
				completeTurns(board,interations, interations+1);
			}
			interations++;
			boardView(board);
		}
		else if(strcmp(str,"h\n")==0)
		{
			if(halfturn)
			{
				interations++;
				moveOnlyBlue(board);
				//todo needs to check board
			}
			else
			{
				moveOnlyRed(board);
			}
			halfturn=!halfturn;
			boardView(board);
		}
		else if(strcmp(str,"c\n")==0)
		{
			if(halfturn)
			{
				//TODO blue moves
				halfturn=!halfturn;
			}
			//todo remainder
			//boardView(board);
		}
		else if(strcmp(str,"x\n")==0)
		{
			boardPrint(board); 
			exit(0);
		}	
		else
		{
			str[strlen(str)-1]='\0';
			for(x=0; x< strlen(str); x++)
			{
				if(!(str[x]>='0'&& str[x]<='9'))
				{
					flag=1;
					break;
				}
			}
			if(flag==1)
			{
				printf("Error:Try again\n");
			}
			else
			{
				int turns=atoi(str);
				if(halfturn)
				{
					moveOnlyBlue(board);
					//TODO blue moves
					halfturn=!halfturn;
					turns--;
					interations++;
				}
				interations+=turns;
				boardView(board);
			}
		}
		printf("turnCompleted=%d\n",interations);
	}

}

//todo: make pthread
void moveOnlyRed(int **board)
{
	int x;
	for(x=0;x<bN; x++)
	{
		moveRed(board,x);
	}
}
void moveOnlyBlue(int **board)
{
	int x;
	for(x=0;x<bN; x++)
	{
		moveBlue(board,x);
	}
}
void completeTurns(int **board, int currentTurn, int max)
{
	int x,y, turn;
	
	for(x=0; x<(bN/tN);x++)
	{
		for(y=0; y<(bN/tN);y++)
		{	
			redCount[x][y]=0;
			blueCount[x][y]=0;
		}
	}
	for(turn=currentTurn; turn<currentTurn+max&&turn<mN; turn++)
	{
		for(x=0;x<bN; x++)
		{
			moveRed(board,x);
		}
		for(x=0;x<bN; x++)
		{
			moveBlue(board,x);
		}
		for(x=0;x<bN; x++)
		{
			tileSum(board, x);
		}
		for(x=0; x<(bN/tN);x++)
		{
			for(y=0; y<(bN/tN);y++)
			{
				if(tN*tN*cN/100<redCount[x][y]|| tN*tN*cN/100<blueCount[x][y])
				{
					printf("ending!\n");
					boardPrint(board);
					exit(0);
				}
			}
		}
	}
}
void *moves(void *i)
{
	int index=(int) i;
	//todo get board access?
	boardView(board);
	int x;
	while(endFlag==0)
	{
		for(x=index; x<bN; x+=pN)
		{
			moveRed(board,index);
			
		}
		barrier();
		for(x=index; x<bN; x+=pN)
		{
			moveBlue(board,index);
		}
		barrier();
		for(x=index; x<bN; x+=pN)
		{
			tileSum(board, index);
		}
		barrierCheck();
	}
	
	return NULL;
}

void automated(int **board, int start)
{
	int i;
	pthread_t tid[pN];
	pthread_mutex_init(&update_lock,NULL);
	
	pthread_mutex_init(&update_lock,NULL);
	
	pthread_cond_init(&all_here,NULL);
	
	for(i=0; i<pN;i++)
	{
		pthread_create(&tid[i],NULL,moves, (void *) i);
	}
	for(i=0; i<pN;i++)
	{
		pthread_join(tid[i],NULL);
	}
	
	printf("it ends.. NOW");exit(0);
}


void barrier()
{
	pthread_mutex_lock(&barrier_lock);
	threadCount++;
	if(threadCount==pN)
	{
		threadCount=0;
		pthread_cond_brodcast(&all_here);
	}
	else
	{
		pthread_cond_wait(&all_here, &barrier_lock);
	}
	pthread_mutex_unlock(&barrier_lock);
}


int main(int argc, char *argv[])
{
	
		
	int x=0;
	char argTemp[100]="";
	if(argc<6 || argc>8)
	{
		printf("Error: Incorrect number of args\n");
		return 1;
	}
	else if(argc>=6 && argc<=8)
	{
		for(x=1; x<argc; x++)
		{
			/*printf("%s\n", argv[x]);
			printf("%c\n",argv[x][0]);*/
			if(argv[x][0]=='p')
			{
				strncpy(argTemp, &argv[x][1], strlen(argv[x]));
				pN=atoi(argTemp);
			}
			else if(argv[x][0]=='b')
			{
				strncpy(argTemp, &argv[x][1], strlen(argv[x]));
				bN=atoi(argTemp);
			}
			else if(argv[x][0]=='t')
			{
				strncpy(argTemp, &argv[x][1], strlen(argv[x]));
				tN=atoi(argTemp);
			}
			else if(argv[x][0]=='c')
			{
				strncpy(argTemp, &argv[x][1], strlen(argv[x]));
				cN=atoi(argTemp);
			}
			else if(argv[x][0]=='m')
			{
				strncpy(argTemp, &argv[x][1], strlen(argv[x]));
				mN=atoi(argTemp);
			}
			else if(argv[x][0]=='s')
			{
				strncpy(argTemp, &argv[x][1], strlen(argv[x]));
				sN=atoi(argTemp);
			}
			else if(argv[x][0]=='i')
			{
				i=1;
			}
			else
			{
				printf("Error: One or more arguments is not in correct formatting, exiting..\n");
				return 1;
			}			
		}	
	}
	else
	{
		printf("Magic, this should never occur.. ever..\n");
		return 1;
	
	}
	/*if the seed was specified, use that seed, otherwise use wall time*/
	if(sN!=-1)
	{
		srand(sN);
	}
	else
	{
		srand(time(NULL));
	}
	if(pN<1 || bN<2 || bN%tN!=0 || cN<1 ||cN>100)
	{
		printf("Error: One or more arguments are incorrect values, exiting.\n");
	}
	/*Finished Error checking the input*/ 
	
	
	
	board=boardInt();
	automated(board, 0);
	interactiveMode(board);
	//boardView(board);

	if(tN==1)
	{
		/*terminate NOW*/ 
	}
	if(tN==bN)/*special case, either terminates (because the % of colours doesnt change throughout the whole board) OR complets # of steps*/
	{
	
	}
	if(cN==100)/* special case,  effectively deactivates the colour stoping condition, so you only need to compute the # of steps*/ 
	{
	
	}
	for(x=0; x<bN;x++)
	{
		tileSum(board,x);
	}	
	return 0;
}
















