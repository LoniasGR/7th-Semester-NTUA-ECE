correctCap(X, Y, Z) :-
    X>= Y,
    X=< Z.

distance(X1,Y1,X2,Y2, D) :-
    D is sqrt((X2-X1)^2 + (Y2-Y1)^2).

longdistance(D, true) .

longdistance(D, false) :-
    D =< 30.0.


checkLD(Xax,Yax,Cxax,Cyax,Cdxax,Cdyax, LD) :-
    distance(Xax,Yax,Cxax,Cyax, A),
    distance(Cxax,Cyax,Cdxax,Cdyax, B),
    longdistance((A+B), LD).



compatible(taxi(Xax,Yax,Id,true,MinCap,MaxCap,Rat,LD,T),
client(Cxax,Cyax,Cdxax,Cdyax,Time,P,Lang,Lug)) :-
    language(Id, L),
    correctCap(P, MinCap, MaxCap).
    checkLD(Xax,Yax,Cxax,Cyax,Cyax,Cdxax,Cdyax,LD).

multiplier(D, low) :-
    D is 1.

multiplier(D, medium) :-
    D is 1.5.

multiplier(D,high) :-
    D is 2.

commonLine(Id1, Id2, Id) :-
    belongsIn(Id1, Id),
    belongsIn(Id2, Id).

findDirection(Id,X1,X2, 1) :-
    oneway(Id, false, false).

findDirection(Id, X1, X2, 1) :-
    X1 < X2,
    oneway(Id, true, false).

findDirection(Id, X1, X2, 1) :-
    X1 >= X2,
    oneway(Id, true, true).

returnHeuristic(Id1,Id2, XN1,XN2,Y2, Xdst, Ydst, A, D) :-
    commonLine(Id1, Id2, Id),
    findDirection(Id, XN1, XN2, D),
    highway(Id, true),
    distance(XN2, Y2, Xdst, Ydst,A).
