correctCap(X, Y, Z) :-
    X>= Y,
    X <= Z.

distance(X1,Y1,X2,Y2, D) :-
    D is sqrt((X2-X1)^2 + (Y2-Y1)^2).


compatible(taxi(Xax,Yax,Id,true,MinCap,MaxCap,Rat,LD,T),
client(Cxax,Cyax,Cdxax,Cdyax,Time,P,Lang,Lug)) :-
    language(Id, L),
    correctCap(P, MinCap, MaxCap).
