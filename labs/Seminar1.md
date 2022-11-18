# Exercitii examen

## Analiza lexicala

### Ex 1

1. $(10 | 01)^*0$
2. $01^+$
3. $1(01|0)^*$

Se da $101010001001100011$. Ce reguli o vor acoperi?
Se ia cea mai lunga parsare. Apoi se ia urmatoarea de sus in jos

|Sir|Regula|
|-|-|
|$101010001001$ | 3 |
|$10001$ | 3 |
|$1$ | 3 |

### Ex 2

Se da $(a^*b) ^*c$ 
si 
$abc^*$. Dati exemplu de sir care nu poate fi acceptat de acest lexer ( raspuns:
 $b$ | sa fie mereu scurte)

Se da $cabccbbcaabbcab$. Impartiti dupa regulile de mai sus

|Sir|Regula|
|-|-|
|$c$ | 1 |
|$abcc$ | 2 |
|$bbc$ | 1 |
|$aabc$ | 1  |
|$ab$ | 2 |

Daca alegi o secventa prea lunga care dupa nu mai iese se alege una mai scurta cu alte reguli (nu prea se da la examen)


## Analiza semantica

### Ex 1

$$S \rightarrow Aa | b$$
$$A \rightarrow Sc$$

Cum elimin recursivitatea la stanga? (ex direct : $S\rightarrow Sb $, $S\rightarrow Aa A \rightarrow Sc S\rightarrow Sca$)

Se factorizeaza:

$$A \rightarrow A \alpha | \beta$$

$$A \rightarrow \beta_A'$$
$$A' \rightarrow \alpha_A' | \epsilon$$


Rescriem:

$$S \rightarrow Aa | b$$
$$A \rightarrow Sc$$

ca 

$$S \rightarrow Sca | b$$

$$\Rightarrow S \rightarrow bS'$$
$$S' \rightarrow caS' | \epsilon$$


### Ex 2

$$E \rightarrow E+T | T$$
$$+T = \alpha$$
$$T = \beta$$

$$E \rightarrow TE'$$
$$E' \rightarrow +TE' | \epsilon$$

### Ex 3

$$S \rightarrow XB$$
$$X \rightarrow aXb | \epsilon$$
$$B \rightarrow aB | BX | \epsilon$$

Trebuie scapat de $\epsilon$.

$$B \rightarrow aB | BX | \epsilon => S' \rightarrow XB | B | X | \epsilon$$

$$X \rightarrow aXb | ab$$
$$B \rightarrow a | aB | BX | X$$

se aplica iar factorizare pt $B \rightarrow BX$ ( se iau toate combinatiile de $\beta$ )


### Ex 4

$$E \rightarrow E*E | E+E | (E) | int$$

Cati arbori de parsare o sa aiba expresia $$5*3+(2*7)+5$$

Radacina poate sa fie $+$ , $*$ sau ultimul plus. Toate combinatiile duc la 5 arbori.

### Ex 5

$$S \rightarrow aSb | Sb | b$$

E gramatica ambigua?

Da. Exp: $abbb$.

$$S \rightarrow aSb \rightarrow aSbb \rightarrow abbb$$
$$S\rightarrow Sb \rightarrow aSbb \rightarrow abbb$$

Deci doua diferita rezulta ambigua

### Ex 6 - Recursive descent

$$S \rightarrow baSab | baS | b$$

Cati pasi isi ia parserului sa ne gaseasca/genereze $babab$ ?

Face bkt si le incearca pe fiecare

$$S \rightarrow baSab$$

Face match pe $ba$

$$baSab \rightarrow babaSabab$$
Face match pe $baba$

Ia regula 1, 2, 3 si nu merge niciuna

Ne intoarcem din bkt

$$baSab \rightarrow babaSab \text{ (regula 2)}$$

Face match pe $baba$

Se incearca toate 3 nu merge niciuna

$$baSab \rightarrow babab (regula 3)$$
Face match pe babab tot sirul
END

Ajunge la 7 finale si face 10 incercari. (merge orice raspuns)

O aranjare mai buna e regula 3 sa fie prima!

### Ex 7

$$S \rightarrow A | B | 0S$$
$$A \rightarrow 0A | 0$$
$$B \rightarrow 1$$

Pentru $0^n1$ cate incercari face? Care e complexitatea? Raspunsuri : 1, n^2, n^3, 2^n

$$S \rightarrow A \rightarrow 0A \rightarrow 00A \rightarrow \ldots$$

se intoarce incearca $B \rightarrow 1$

$$\begin{aligned}
S   &\rightarrow A \ldots\\
    &\rightarrow 0S \rightarrow 0A \ldots\\
    &\rightarrow 00S \rightarrow 00A \ldots \\
    &\ldots \\
    &\rightarrow 0^nS \rightarrow 0^nA \ldots \\
    &\rightarrow 0^nS \rightarrow 0^nB \rightarrow \textbf{$0^n 1$}
\end{aligned}$$

$$\Rightarrow \Theta(n^2)$$
Pt n ar trebui pusa regula $0S$ prima.

## Multimi First si Follow

### Ex 1

$$S \rightarrow A(S)B | \epsilon$$
$$A \rightarrow S | SB | x | \epsilon$$
$$B \rightarrow SB | y$$

|Neterminal|$First(\cdot)$|
|-|-|
|$S$   |$\epsilon,x,(,y$|
|$A$   |$\epsilon,x,(,y$|
|$B$   |$x,(,y$|

$$First(B) = First(S) \setminus \epsilon$$
$$Frist(A) = (First(S) \setminus \epsilon) \cup \epsilon \text{ (il are A)}$$

ce urmeaza dupa ce se termina regula respectiva
Epsilon nu e niciodata in Follow


|Neterminal|$Follow(\cdot)$|
|-|-|
|$S$   |$),x,(,y,\$$|
|$A$   |$($|
|$B$   |$ \$ $|

$$\begin{aligned}
\$ &\in Follow(S) \text{ (terminator de fisier)} \\
) &\in Follow(S) \\
First(B) &\subseteq Follow(S) \\
Follow(S) &\subseteq Follow(B) \\
Follow(A) &\subseteq Follow(B) \\
Follow(B) &\subseteq Follow(B)
\end{aligned}$$

## Ex 2

$$S \rightarrow ABc | d$$
$$A \rightarrow a | ε$$
$$B \rightarrow b | ε$$

$$First(A) \subseteq First(S) \setminus \epsilon$$
$$First(B) \subseteq First(S) \setminus \epsilon$$


|Neterminal|$First(\cdot)$|
|-|-|
|$S$   |$d,a,b,c$|
|$A$   |$a,\epsilon$|
|$B$   |$b,\epsilon$|

|Neterminal|$Follow(\cdot)$|
|-|-|
|$S$   |$\$$|
|$A$   |$b,c$|
|$B$   |$c$|

$$\begin{aligned}
\$ &\in Follow(S) \\
c &\in Follow(B) \\
First(B) &\subseteq Follow(A) \setminus \epsilon \\
Follow(B) &\subseteq Follow(A)
\end{aligned}$$



### Ex 3 (de rezolvat)

$$S \rightarrow aTUb | \epsilon$$
$$T \rightarrow cUc | bUb | aUa$$
$$U \rightarrow Sb | cc$$
