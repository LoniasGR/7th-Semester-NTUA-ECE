/* Correct capacity of the taxi */
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


/*
 * Multiplier is the times we multiply our heuristic of one street depending on
 * the traffic there is on the street. Higher traffic , higher multiplier.
 */
multiplier(D, low) :-
    D = 1.

multiplier(D, medium) :-
    D = 1.5.

multiplier(D,high) :-
    D = 2.

/* Find the common Line between the nodes */
commonLine(Id1, Id2, Id) :-
    belongsIn(Id1, Id),
    belongsIn(Id2, Id).

/* Find the direction the street is going. A bit-error prone, as it only depends
 * on the X axis value. We know that when the nodes are inserted, they are
 * sorted by their X value, so we use it to determine the direction.
 */
findDirection(Id,X1,X2, 1) :-
    oneway(Id, false, false).

findDirection(Id, X1, X2, 1) :-
    X1 < X2,
    oneway(Id, true, false).

findDirection(Id, X1, X2, 1) :-
    X1 >= X2,
    oneway(Id, true, true).

/* Checks for the intensity of traffic depending on time */
findTraffic(T, Id, R) :-
        traffic(Id,S,E,R),
        T>= S,
        T=< E.

returnHeuristic(Id1,Id2, XN1,XN2,Y2, Xdst, Ydst, H, Id) :-
    commonLine(Id1, Id2, Id),
    highway(Id, true),
    findDirection(Id, XN1, XN2, D),
    distance(XN2, Y2, Xdst, Ydst,H).

returnTraffic(Id, T, M, R) :-
    findTraffic(T, Id, R),
    multiplier(M, R).
