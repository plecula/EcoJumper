

# EcoJumper 

## Opis projektu
EcoJumper to prosta gra typu "Serious Game" napisana w języku Java z wykorzystaniem biblioteki Swing.  
Celem gry jest unikanie przeszkód, zbieranie odpadów oraz ich poprawne sortowanie w drugiej fazie rozgrywki.

---

## Mechanika gry
Gra składa się z trzech głównych etapów:

1. **Menu**
   - rozpoczęcie nowej gry
   - wyświetlenie najlepszego wyniku (level, score)

2. **Rozgrywka**
   - sterowanie postacią (skok, ruch)
   - unikanie przeszkód (np. chmury, plamy ropy)
   - zbieranie odpadów (papier, plastik, szkło)
   - system punktów i żyć

3. **Sortowanie odpadów**
   - przeciąganie zebranych śmieci do odpowiednich koszy
   - etap edukacyjny sprawdzający poprawność segregacji

---


## Struktura projektu

```
EcoJumper/
├── src/                        # kod źródłowy aplikacji Java
│   ├── EcoJumperGame.java
│   ├── GamePanel.java
│   ├── MenuPanel.java
│   ├── SortingPanel.java
│   ├── ...
│
├── assets/                     # zasoby graficzne
│   ├── background.png
│   ├── bin_glass.png
│   ├── paper_green.png
│   └── ...
│
└── README.md

```

---

## Technologie
- **Java**
- **Swing (JFrame, JPanel, Timer)**

---

## Jak uruchomić projekt
### Wymagania:
- Java JDK 8 lub nowsza
- Dowolne IDE 

### Uruchomienie w IntelliJ IDEA:
1. Otworz katalog projektu ```EcoJumper```
2. Upewnij się, że katalog assets/ znajduje się w projekcie
3. Otwórz plik ```EcoJumperGame.java```
4. Uruchom aplikację

## Autor

- Urszula Plec 
- Współczesne języki programowania
- [@plecula](https://www.github.com/plecula)

