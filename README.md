Napisz aplikację, w której serwis, który będzie symulował pobieranie losowo 1 książki (np. z 5 przygotowanych tytułów).
Książkę może reprezentować mapa z tytułem i jej opisem/fragmentem.
Przykładowe książki:
https://www.gutenberg.org/browse/scores/top
Aplikacja powinna zawierać również UI w postaci tabeli z danymi na temat pobranych książek.

Kiedy serwis skończy symulację pobieranie, wyśle za pomocą Broadcastu nazwę książki, oraz statystyki tych książek: liczba słów, liczba liter, najczęściej występujące słowo.

Dodatkowo serwis po pobraniu wyśle powiadomienie o pobraniu książki.

Odbiorca powinien sprawdzić czy w tabeli już jest  taka książka, jeżeli tak to nie będzie wstawiał odebranych danych, w innym wypadku wstawi dane do listy. Tabela powinna zostać zaktualizowana.
