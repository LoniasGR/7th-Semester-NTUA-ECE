correctCap(X, Y, Z) :-
    X>= Y,
    X=< Z.

distance(X1,Y1,X2,Y2, D) :-
    D is sqrt((X2-X1)^2 + (Y2-Y1)^2).

longdistance(D, true) :-
    D >= 30.0.

longdistance(D, true)  :-
    D =< 30.0.

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
