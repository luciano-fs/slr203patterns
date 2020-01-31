the convergecast pattern is also called merger or converger

![](img.png?raw=true)
```
title convergecast

participant a
participant b
participant c
participant merger
participant d

a->merger:join
b->merger:join
c->merger:join
a->merger:hi
b->merger:hi
c->merger:hi
merger->d:hi(a,b,c)
c->merger:unjoin
a->merger:hi2
b->merger:hi2
merger->d:hi2(a,b)
```

http://sequencediagram.org/index.html#initialData=C4S2BsFMAIGMHsB2A3SAnA5pWBDAzsAFCEAOOaosIZiw0Op5l1Ot0ARoxSFTXbF2Z9oAW3RY0gnizYATYjgC0APjGZ0ALgBW8EIkLsVaidt37YR8Zp17CS1VbQaAFiAOX1T14QsPPLt2N0FVkAgAocABp2SNgASh8PEwBXRBt9eyCvEAAmdz8TVzyskICciOi4oA
