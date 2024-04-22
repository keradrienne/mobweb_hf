# Házi feladat specifikáció

Információk [itt](https://viauac00.github.io/laborok/hf)

## Mobil- és webes szoftverek
### [2023.10.15.]
### [Drink!]
### [Kerekes Adrienne]
### [ker.adrienne@gmail.com]

## Bemutatás

Folyadék bevitelt naplózó alkalmazást szeretnék megvalósítani. A napi folyadékbevitel rögzítése segít az egészségesebb életmód fenntartásában. 
Az alkalmazás olyan embereknek készül, akik szeretnék nyomon követni mikor, milyen és mennyi folyadékot vittek be a szereveztükbe a nap folyamán.

## Főbb funkciók

Az alkalmazásba rögzíteni lehet mikor mennyi és milyen folyadék bevitele történt. Az alkalmazás továbbá képes megjeleníteni ezen adatokat szövegesen, illetve grafikusan.
Előre beállított értesítések formájában képes emléketetni a felhasznélót, hogy ideje inni valamit.

Az alkalmazás 3 fragment-ből fog állni:
    - Az első és egyben kezdő fragmentben grafiukusan és szövegesen megjelenik az aznapi vízfogyasztás, illetve itt lehet új adatot rögzíteni aznapra.
    - A következőben szövegesen és grafikusan vissza lehet nézni a múltban történt adatrögzítéseket.
    - A harmadik fragmentben az alkalmazás beállításai szerepelnek pl. értesítések küldésének rendszeressége.

## Választott technológiák:

- UI
- Fragmentek
- RecyclerView
- Perzisztens adattárolás


# Házi feladat dokumentáció
A program ki lett egészítve néhány plusz funkcióval és technológiával a specifikációban rögzítettekhez képest. A második oldalon nemcsak az előző napi bejegyzéseket lehet megnézni, hanem a kiválasztott napon lehet a bejegyzéseket törölni, módosítani, illetve új bejegyzést hozzáadni.

Az felhasználói értesítések megvalósítása során felhasznált új technológiák:
- Broadcast Reciever
- Intent
- Shared Preferences
