# Analiza lexicala

## Exercițiul 1

1. $(10 | 01)^*0$
2. $01^+$
3. $1(01|0)^*$

Se dă $101010001001100011$. Ce reguli o vor acoperi?

Se ia cea mai lungă parsare. Apoi se ia următoarea de sus în jos.
$$\underbrace{101010001001}_{\text{regula 3}} \ \underbrace{10001}_{\text{regula 3}} \ \underbrace{1}_{\text{regula 3}}$$


## Exercițiul 2

Se dă $(a^*b) ^*c$ 
și 
$abc^*$. Dați exemplu de șir (preferabil scurt) care nu poate fi acceptat de acestă lexer.

Răspuns: $b$.

Se dă $cabccbbcaabbcab$. Impărțiți după regulile de mai sus.

$$\underbrace{c}_{\text{r. 1}} \ \underbrace{abcc}_{\text{r. 2}} \ \underbrace{bbc}_{\text{r. 1}} \ \underbrace{aabbc}_{\text{r. 1}} \ \underbrace{ab}_{\text{r. 2}}$$

Dacă alegi o secvență prea lungă care după nu mai iese se alege una mai scurtă cu alte reguli (nu prea se dă la examen).

# Analiza semantica

## Exercițiul 1

$$\begin{aligned}
S &\rightarrow Aa | b \\
A &\rightarrow Sc
\end{aligned}$$

Cum elimin recursivitatea la stânga?

Se factorizează:

$$\begin{aligned}
A &\rightarrow A \alpha | \beta \\
&\Downarrow \\
A &\rightarrow \beta A' \\
A' &\rightarrow \alpha A' | \epsilon
\end{aligned}$$

Rescriem gramatica inițială ca:

$$\begin{aligned}
S &\rightarrow Sca | b \\
&\Downarrow \\
S &\rightarrow bS' \\
S' &\rightarrow caS' | \epsilon \\
\end{aligned}$$

## Exercițiul 2

$$\begin{aligned}
E &\rightarrow E\underbrace{+T}_\alpha | \underbrace{T}_\beta \\
&\Downarrow \\
E &\rightarrow TE' \\
E' &\rightarrow +TE' | \epsilon \\
\end{aligned}$$

## Exercițiul 3

$$\begin{aligned}
S &\rightarrow XB \\
X &\rightarrow aXb | \epsilon \\
B &\rightarrow aB | BX | \epsilon \\
\end{aligned}$$


Trebuie scăpat de $\epsilon$.

$$\begin{aligned}
S' &\rightarrow XB | B | X | \epsilon \\
X &\rightarrow aXb | ab \\
B &\rightarrow a | aB | BX | X \\
\end{aligned}$$

Se aplică iar factorizare pt $B \rightarrow BX$.


## Exercițiul 4

$$E \rightarrow E*E | E+E | (E) | int$$

Câți arbori de parsare o să aibă următoarea expresie?

$$5 \underset{\substack{\downarrow \\2}}{*} 3 \underset{\substack{\downarrow \\1}}{+} (2*7)  \underset{\substack{\downarrow \\2}}{+} 5$$

Radacina poate sa fie $*$ (2 arb.), $+$ (1 arb.), sau ultimul $+$ (2 arb.). Toate combinatiile duc la $5$ arbori.

## Exercițiul 5

$$S \rightarrow aSb | Sb | b$$

E gramatica ambiguă?

Da. Exemplu: $abbb$.

$$S \rightarrow aSb \rightarrow aSbb \rightarrow abbb$$
$$S\rightarrow Sb \rightarrow aSbb \rightarrow abbb$$

## Exercițiul 6 - Recursive descent

$$S \rightarrow baSab | baS | b$$

Câți pași îi ia parserului să găsească o derivare pentru $babab$?

Facem backtracking, aplicând prima dată regula 1.

$$\begin{aligned}
S &\rightarrow \textcolor{green}{ba}Sab \text{ (potrivire pe $ba$)} \\
baSab &\rightarrow \textcolor{green}{baba}Sabab \\
babaSabab &\rightarrow \ldots\\
&\text{ (nicio potrivire, încă 3 derivări)}\\
\end{aligned}$$

Ne întoarcem din backtracking și aplicăm regula 2:

$$\begin{aligned}
baSab &\rightarrow \textcolor{green}{baba}Sab \\
babaSab &\rightarrow \ldots\\
&\text{ (nicio potrivire, încă 3 derivări)}\\
\end{aligned}$$

Dar nu există nicio potrivire ulterioară. Se încearcă și regula 3.

$$baSab \rightarrow \textcolor{green}{babab}$$

Ajunge la 7 secvențe finale și face 10 derivări.

O aranjare eficientă ar fi fost dacă regula 3 era prima!

## Exercițiul 7

$$S \rightarrow A | B | 0S$$
$$A \rightarrow 0A | 0$$
$$B \rightarrow 1$$

Pentru $0^n1$ cate încercări face? Care e complexitatea? Variante:

- [ ] $\Theta(1)$
- [x] $\Theta(n^2)$
- [ ] $\Theta(n^3)$
- [ ] $\Theta(2^n)$

Încearcă prima regulă:

$$S \rightarrow A \rightarrow 0A \rightarrow 00A \rightarrow \ldots$$

Se intoarce și încearcă $B \rightarrow 1$.

Apoi încearcă a treia regulă:

$$\begin{aligned}
S   &\rightarrow 0S \rightarrow 0A \ldots\\
    &\rightarrow 00S \rightarrow 00A \ldots \\
    &\ldots \\
    &\rightarrow 0^nS \rightarrow 0^nA \ldots \\
    &\rightarrow 0^nS \rightarrow 0^nB \rightarrow \textbf{$0^n 1$}
\end{aligned}$$

$$\Rightarrow \Theta(n^2)$$
Pentru $\Theta(n)$ ar trebui pus regula $S \rightarrow 0S$ prima.

# Multimi First și Follow

## Exercițiul 1

$$\begin{aligned}
S &\rightarrow A(S)B | \epsilon \\
A &\rightarrow S | SB | x | \epsilon \\
B &\rightarrow SB | y \\
\end{aligned}$$

Observație: $N \rightarrow^*N \alpha \Rightarrow \epsilon \in First(N)$.

|Neterminal|$First(\cdot)$|
|-|-|
|$S$   |$\epsilon,x,(,y$|
|$A$   |$\epsilon,x,(,y$|
|$B$   |$y,x,($|

Pentru $S$:

$$First(A) \setminus \epsilon \subseteq First(S)$$
$$\epsilon \in First(A) \Rightarrow (\ \in First(S)$$

Pentru $A$:

$$First(S) \setminus \epsilon \subseteq First(A)$$
$$\Rightarrow First(A) = First(S)$$
$$First(B) \setminus \epsilon \subseteq First(A)$$

Pentru $B$:

$$First(S) \setminus \epsilon \subseteq First(B)$$

Observație: $\epsilon \notin Follow(\cdot)$.

|Neterminal|$Follow(\cdot)$|
|-|-|
|$S$   |$\$,),x,(,y$|
|$A$   |$($|
|$B$   |$\$,),x,(,y$|

$$\begin{aligned}
\$ &\in Follow(S) \text{ (terminator de fisier)} \\
) &\in Follow(S) \\
First(B) \setminus \epsilon &\subseteq Follow(S) \\
Follow(S) &\subseteq Follow(B) \\
Follow(A) &\subseteq Follow(B) \\
\end{aligned}$$

## Exercițiul 2

$$S \rightarrow ABc | d$$
$$A \rightarrow a | \epsilon$$
$$B \rightarrow b | \epsilon$$

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
First(Bc) \setminus \epsilon &\subseteq Follow(A) \\
\end{aligned}$$


## Exercițiul 3 (de rezolvat)

$$S \rightarrow aTUb | \epsilon$$
$$T \rightarrow cUc | bUb | aUa$$
$$U \rightarrow Sb | cc$$
