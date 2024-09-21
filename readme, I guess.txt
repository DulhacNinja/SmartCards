Nu folositi alt constructor pentru comenzi APDU decat ala fara parametri! Constructorul care primeste parametri e bugged.
Mostly orice chestie care tine de cod de lucrat cu APDU doar uitati-va cum e implementat in proiectu asta, nu poti face altfel.
It should mostly work. Really sorry if something is bugged out.

Cand vrei sa testezi un card, mai intai pornesti simulatorul, dupa rulezi scriptu cap-Health pentru a copia applet-ul pe card.
Dupa ce copiezi applet-ul poti sa rulezi scirptul pentru teste.

Stiu ca eclipse e ciudat, insa nu va chinuiti sa folositi alte IDE-uri. Incearca sa reproduci cat mai fidel tutorialele lui Birjoveanu, they work.

Cand lucrati cu procesul de simulator, sa nu uitati sa il inchideti. Daca aplicatia de terminal este inchisa in mod neasteptat, simulatorul trebuie inchis manual. Procesu se cheama "cref" smth smth.
