all: rbs 

rbs: rbs.o
	gcc -Wall -g -lm -pthread rbs.o -o rbs
rbs.o: rbs.c
	gcc -c rbs.c
