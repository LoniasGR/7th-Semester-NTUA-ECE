all: Zing Zing2

Zing: main.o zing.o
	gcc main.o zing.o -o Zing

Zing2: main.o zing2.o
	gcc main.o zing2.o -o Zing2

main.o:  zing.h
zing2.o: zing2.c

.PHONY: clean
clean:
	-rm  Zing Zing2 main.o zing2.o
