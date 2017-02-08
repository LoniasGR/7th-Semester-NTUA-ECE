parent(tom, bob).
parent(pam, bob).
parent(tom, liz).
parent(bob, ann).
parent(bob, pat).
parent(pat, jim).
male(tom).
male(bob).
male(jim).
female(liz).
female(pat).
female(ann).
female(pam).

offspring(Y,X) :-
    parent(X,Y).

mother(X,Y) :-
    parent(X,Y),
    female(X).

grandparent(X,Z) :-
    parent(X,Y),
    parent(Y,Z).

sister(X,Y) :-
    parent(Z,X),
    parent(Z,Y),
    female(X).

happy(X) :-
    parent(X,_).

hastwochildren(X) :-
    parent(X,Y),
    sister(Z,Y).

grandchild(X,Z) :-
    parent(Z,Y),
    parent(Y,X).

aunt(X,Y) :-
    sister(X,Z),
    parent(Z,Y).

predecessor(X, Z) :-
    parent(X, Z).

predecessor(X, Z) :-
    parent(Y, Z),
    predecessor(X, Y).

vertical(seg(point(X,Y), point(X,Y1))).
horizontal(seg(point(X,Y),point(X1,Y))).

regular(R) :-
 vertical(P1,P2),
 vertical(P3,P4),
 horizontal(P2,P3),
 horizontal(P3,P4).

 f(1, one).
 f(s(1), two).
 f(s(s(1)),three).
 f(s(s(s(X))), N) :-
     f(X, N).

 relatives(X,Y) :-
     predecessor(X,Y);
     predecessor(Y,X);
     predecessor(Z,Y), predecessor(Z,Y);
     predecessor(X,Z), predecessor(Y,Z).

translate(Number, Word) :-
    Number = 1, Word = one.
translate(Number, Word) :-
    Number = 2, Word = two.
translate(Number, Word) :-
    Number = 3, Word = three.

move(state(middle, onbox, middle, hasnot),
    grasp,
    state(middle,onbox,middle,has)).

move(state(P,onfloor,P,H),
    climb,
    state(P,onbox,P,H)).

move(state(P1,onfloor,P1,H),
    push(P1,P2),
    state(P2, onfloor,P2, H)).

move(state(P1,onfloor,B,H),
    walk(P1,P2),
    state(P2,onfloor,B,H)).

canget(state(_,_,_,has)).

canget(State1) :-
    move(State1, Move, State2),
    canget(State2).

canget1(state(_,_,_,has),[]).

canget1(State1, [Action|Rest]) :-
    move(State1, Action, State2),
    canget1(State2, Rest).

member(X, [X|Tail]). %X is a member of the list if it's the head

member(X, [Head|Tail]) :- %X is member if at somepoint becomes the head
    member(X, Tail).

conc([], L, L).  %Concatinate 2 lists

conc([X|L1], L2, [X|L3]) :-
    conc(L1, L2, L3).

deleteLast3(L, L1) :-
        conc(L1,[_,_,_], L).

deleteFirstAndLast3(L, L2) :-
        deleteLast3(L, L1),
        conc([_,_,_], L2, L1).

last1(Item, List) :- %find last item on a list
    conc(_, [Item], List).

last2(Item, [Item]).

last2(Item, [Head|Tail]) :-
    last2(Item, Tail).

evenlength([]). %return true if list is of even length

evenlength(L) :-
    conc([_,_], L1, L),
    evenlength(L1).

oddlength([_]). %return true if list is of odd length

oddlength(L) :-
    conc([_,_], L1, L),
    oddlength(L1).

add(X, L, [X|L]).

reverse([],[]). %reverse a list

reverse([Head|Tail], ReversedList) :-
    reverse(Tail, TailR),
    conc(TailR, [Head], ReversedList).

palindrome(List) :- %return true if list is a palindrome
    reverse(List, List).

shift([Head|Tail], L2) :- %shifts L1 by a character
    conc(Tail,[Head], L2).

means(0, zero).
means(1,one).
means(2,two).
means(3,three).
means(4,four).
means(5,five).

translate1([], []).

translate1([Head|Tail], List2) :-
        means(Head, L),
        translate1(Tail, TailR),
        conc([L],TailR, List2).

sublist(S,L) :-
    conc(L1,L2,L),
    conc(S,L3,L2).

subset([], []).

subset([First|Else], [First|Else2]) :-
    subset(Else, Else2).

subset([First|Else], Else2) :-
    subset(Else, Else2).

dividelist([],[],[]).

dividelist([Item],[Item],[]).

dividelist([H1,H2|T], List1, List2) :-
     dividelist(T, TList1, TList2),
     conc([H1], TList1, List1),
     conc([H2], TList2, List2).



flatten([], []).

flatten([H|T], L) :-
    flatten(H, HeadL),
    flatten(T, TailL),
    conc(HeadL,TailL,L).

flatten(Item, [Item]).


:- op(500,xfy,was).
:- op(100,fy, the).
:- op(200,xfy,of).

diana was the secretary of the department.

t(0+1, 1+0).
t(X+0+1, X+1+0).
t(X+1+1, Z) :-
    t(X+1, X1),
    t(X1+1, Z).
