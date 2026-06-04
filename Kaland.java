import java.util.Random;
import java.util.Scanner;

public class Kaland {

    static String[] szobaNevek = {
        "Barlang", "Konyvtar", "Fegyverkamra", "Tronterem", "Kripta"
    };

    static String[] szobaLeirasok = {
        "Sotet es nedves barlang. Csorog a viz a mennyezetrol.",
        "Regi konyvek ezrei sorakoznak a polcokon.",
        "Rozsas kardok es pajzsok lognak a falon.",
        "Hatalmas kotron all a terem kozepen.",
        "Kokoporsok sorakoznak a fal menten. Hideg van."
    };

    static int[][] szobak = {
        { 1, -1},
        { 2,  0},
        { 3,  1},
        { 4,  2},
        {-1,  3}
    };

    static int jelenlegiSzoba = 0;
    static boolean[] szobaBejart = new boolean[5];


    static class Karakter {
        String nev;
        int hp, maxHp;
        int damage;
        int mana, maxMana;
        String jel;
        boolean vedekezes = false;

        Karakter(String nev, int hp, int damage, int mana, String jel) {
            this.nev     = nev;
            this.hp      = hp;  this.maxHp   = hp;
            this.damage  = damage;
            this.mana    = mana; this.maxMana = mana;
            this.jel     = jel;
        }

        boolean eletel()  { return hp > 0; }
        boolean vanMana() { return mana >= 20; }

        int tamadas(Karakter cel) {
            int szoras = new Random().nextInt(11) - 5;
            int sebzes = Math.max(1, damage + szoras);
            if (cel.vedekezes) {
                sebzes = sebzes / 2;
                System.out.println("  [Vedekezes] A sebzes felere csоkkent!");
            }
            cel.hp = Math.max(0, cel.hp - sebzes);
            return sebzes;
        }

        int varazslat(Karakter cel) {
            if (!vanMana()) return -1;
            mana -= 20;
            int sebzes = (int)(damage * 1.5);
            if (cel.vedekezes) {
                sebzes = sebzes / 2;
                System.out.println("  [Vedekezes] A sebzes felere csоkkent!");
            }
            cel.hp = Math.max(0, cel.hp - sebzes);
            return sebzes;
        }

        void vedekez() { vedekezes = true; }
        void ujKor()   { vedekezes = false; }

        void gyogyul(int menny) {
            hp = Math.min(maxHp, hp + menny);
        }
    }

    static Karakter[] jatekosKarakterek = {
        new Karakter("Goblin",  40, 25, 45, "O"),
        new Karakter("Harcos", 100, 35, 50, "H"),
        new Karakter("Magus",   55, 30, 90, "M"),
        new Karakter("Elf",     25, 10, 35, "E")
    };

    static Karakter[][] szobaEllenfelek = {
        { new Karakter("Zombi",    20, 10,  5, "Z") },
        { new Karakter("Csontvaz", 30, 12, 10, "C") },
        { new Karakter("Csontvaz", 30, 12, 10, "C"), new Karakter("Zombi", 20, 10, 5, "Z") },
        { new Karakter("Troll",    50, 20,  5, "T") },
        { new Karakter("Troll",    70, 25, 10, "T"), new Karakter("Csontvaz", 30, 15, 10, "C") }
    };

    static Scanner sc  = new Scanner(System.in);
    static Random  rnd = new Random();


    public static void main(String[] args) {
        fejlec();
        System.out.println("Parancsok: elore, hatra, megnez, allapot, kilepes");
        System.out.println();

        Karakter jatekos = karaktertValaszt();
        System.out.println("\n>> Kalandod kezdodik, " + jatekos.jel + " " + jatekos.nev + "!");
        nyomjEnter();

        while (true) {
            szobatLeir();

            if (!szobaBejart[jelenlegiSzoba]) {
                szobaBejart[jelenlegiSzoba] = true;
                if (!harcokSzobaEllensegekkel(jatekos)) {
                    vereseg(jatekos);
                    break;
                }
                int gyogy = jatekos.maxHp / 5;
                jatekos.gyogyul(gyogy);
                System.out.println("  Pihenes utan +" + gyogy + " HP visszanyerve. (HP: " + jatekos.hp + ")");

                if (jelenlegiSzoba == szobaNevek.length - 1) {
                    gyozelem(jatekos);
                    break;
                }
                nyomjEnter();
            }

            System.out.print("> ");
            String parancs = sc.nextLine().trim().toLowerCase();

            if (parancs.equals("kilepes") || parancs.equals("kilépés")) {
                System.out.println("Viszlat, " + jatekos.nev + "!");
                break;
            } else if (parancs.equals("megnez") || parancs.equals("megnéz")) {
                szobatLeir();
            } else if (parancs.equals("allapot") || parancs.equals("állapot") || parancs.equals("stat")) {
                allapotKiir(jatekos);
            } else if (parancs.equals("elore") || parancs.equals("előre") || parancs.equals("jobb")) {
                mozog(0);
            } else if (parancs.equals("hatra") || parancs.equals("hátra") || parancs.equals("bal")) {
                mozog(1);
            } else {
                System.out.println("Ismeretlen parancs! (elore, hatra, megnez, allapot, kilepes)");
            }
        }

        sc.close();
    }


    static void mozog(int irany) {
        int cel = szobak[jelenlegiSzoba][irany];
        if (cel == -1) {
            System.out.println("Arra nem lehet menni!");
        } else {
            jelenlegiSzoba = cel;
        }
    }

    static void szobatLeir() {
        System.out.println();
        System.out.println("+--- " + szobaNevek[jelenlegiSzoba] + " ---+");
        System.out.println("  " + szobaLeirasok[jelenlegiSzoba]);
        boolean vanElore = szobak[jelenlegiSzoba][0] != -1;
        boolean vanHatra = szobak[jelenlegiSzoba][1] != -1;
        String kijarat = "  Kijáratok: ";
        if (vanElore) kijarat += "[elore -> " + szobaNevek[szobak[jelenlegiSzoba][0]] + "] ";
        if (vanHatra) kijarat += "[hatra <- " + szobaNevek[szobak[jelenlegiSzoba][1]] + "]";
        System.out.println(kijarat);
        if (szobaBejart[jelenlegiSzoba]) {
            System.out.println("  (Mar megtisztitottad ezt a szobat.)");
        } else {
            System.out.println("  !!! Ellenseg erezheto a levegоben...");
        }
        System.out.println();
    }


    static boolean harcokSzobaEllensegekkel(Karakter jatekos) {
        Karakter[] ellenfelek = szobaEllenfelek[jelenlegiSzoba];
        for (int i = 0; i < ellenfelek.length; i++) {
            Karakter ellenfél = ellenfelek[i];
            ellenfél.hp        = ellenfél.maxHp;
            ellenfél.mana      = ellenfél.maxMana;
            ellenfél.vedekezes = false;

            System.out.println("\n*** " + (i + 1) + ". ellenfél: "
                + ellenfél.jel + " " + ellenfél.nev
                + "  (HP:" + ellenfél.hp + " DMG:" + ellenfél.damage + ")");
            nyomjEnter();

            if (!harc(jatekos, ellenfél)) return false;

            if (i < ellenfelek.length - 1) {
                jatekos.mana = Math.min(jatekos.maxMana, jatekos.mana + 10);
                System.out.println("  Rovid piheno: +10 mana. (Mana: " + jatekos.mana + ")");
            }
        }
        return true;
    }

    static boolean harc(Karakter jatekos, Karakter ellenfél) {
        int kor = 1;

        while (jatekos.eletel() && ellenfél.eletel()) {
            System.out.println("========== " + kor + ". KOR ==========");
            hpKiir(jatekos, ellenfél);

            jatekos.ujKor();
            ellenfél.ujKor();


            System.out.println("\n[Kard] " + jatekos.nev + " kore:");
            boolean lepesMegteve = false;

            while (!lepesMegteve) {
                System.out.println("  [1] Normal tamadas");
                System.out.printf("  [2] Varazslat  (mana: %d/%d | -20 mana, x1.5 sebzes)%n",
                    jatekos.mana, jatekos.maxMana);
                System.out.println("  [3] Vedekezes  (kovetkezo sebzes felere csokken)");
                System.out.print("  > ");

                int lepes = bekerjSzam(1, 3);

                if (lepes == 1) {
                    int seb = jatekos.tamadas(ellenfél);
                    System.out.printf("  %s %s tamad -> %d sebzes! (Ellenfél HP: %d)%n",
                        jatekos.jel, jatekos.nev, seb, ellenfél.hp);
                    lepesMegteve = true;
                } else if (lepes == 2) {
                    if (!jatekos.vanMana()) {
                        System.out.println("  Nincs eleg manad! (kell: 20)");
                    } else {
                        int seb = jatekos.varazslat(ellenfél);
                        System.out.printf("  ** %s varazsol -> %d sebzes! (Ellenfél HP: %d | Mana: %d)%n",
                            jatekos.nev, seb, ellenfél.hp, jatekos.mana);
                        lepesMegteve = true;
                    }
                } else {
                    jatekos.vedekez();
                    System.out.println("  [Pajzs] " + jatekos.nev + " vedekszik!");
                    lepesMegteve = true;
                }
            }

            if (!ellenfél.eletel()) break;

            System.out.println("\n[Szornyek] " + ellenfél.nev + " kore:");
            ellenfelAI(ellenfél, jatekos);

            kor++;
            System.out.println();
        }

        if (jatekos.eletel()) {
            System.out.println("  [Gyozelem] " + ellenfél.nev + " legyozve!");
            return true;
        }
        return false;
    }

    static void ellenfelAI(Karakter ellenfél, Karakter cel) {
        if (ellenfél.vanMana() && rnd.nextInt(10) < 3) {
            int seb = ellenfél.varazslat(cel);
            System.out.printf("  ** %s varazsol -> %d sebzes! (Te HP: %d)%n",
                ellenfél.nev, seb, cel.hp);
        } else if (rnd.nextInt(10) < 2) {
            ellenfél.vedekez();
            System.out.println("  [Pajzs] " + ellenfél.nev + " vedekszik!");
        } else {
            int seb = ellenfél.tamadas(cel);
            System.out.printf("  %s %s tamad -> %d sebzes! (Te HP: %d)%n",
                ellenfél.jel, ellenfél.nev, seb, cel.hp);
        }
    }



    static Karakter karaktertValaszt() {
        System.out.println("Valassz karaktert:");
        for (int i = 0; i < jatekosKarakterek.length; i++) {
            Karakter k = jatekosKarakterek[i];
            System.out.printf("  [%d] %s %-8s  HP:%-4d  DMG:%-4d  MANA:%-4d%n",
                i + 1, k.jel, k.nev, k.hp, k.damage, k.mana);
        }
        System.out.print("> ");
        return jatekosKarakterek[bekerjSzam(1, jatekosKarakterek.length) - 1];
    }

    static void hpKiir(Karakter jatekos, Karakter ellenfél) {
        System.out.printf("  %s %-8s  HP: %s (%d/%d)  Mana: %d/%d%n",
            jatekos.jel, jatekos.nev,
            hpSav(jatekos.hp, jatekos.maxHp),
            jatekos.hp, jatekos.maxHp,
            jatekos.mana, jatekos.maxMana);
        System.out.printf("  %s %-8s  HP: %s (%d/%d)  Mana: %d/%d%n",
            ellenfél.jel, ellenfél.nev,
            hpSav(ellenfél.hp, ellenfél.maxHp),
            ellenfél.hp, ellenfél.maxHp,
            ellenfél.mana, ellenfél.maxMana);
    }

    static void allapotKiir(Karakter jatekos) {
        System.out.println("-- Karakter allapot --");
        System.out.printf("  %s %s%n", jatekos.jel, jatekos.nev);
        System.out.printf("  HP:     %s (%d/%d)%n", hpSav(jatekos.hp, jatekos.maxHp), jatekos.hp, jatekos.maxHp);
        System.out.printf("  Mana:   %d/%d%n", jatekos.mana, jatekos.maxMana);
        System.out.printf("  Sebzes: %d%n", jatekos.damage);
        System.out.printf("  Szoba:  %s (%d/%d)%n",
            szobaNevek[jelenlegiSzoba], jelenlegiSzoba + 1, szobaNevek.length);
    }

    static String ismetel(String s, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append(s);
        return sb.toString();
    }

    static String hpSav(int hp, int maxHp) {
        int teli = (int)((double)hp / maxHp * 10);
        teli = Math.max(0, Math.min(10, teli));
        int ures = 10 - teli;
        return "[" + ismetel("#", teli) + ismetel(".", ures) + "]";
    }

    static int bekerjSzam(int min, int max) {
        while (true) {
            try {
                int n = Integer.parseInt(sc.nextLine().trim());
                if (n >= min && n <= max) return n;
                System.out.printf("  Kerlek %d es %d kozotti szamot adj meg: ", min, max);
            } catch (NumberFormatException e) {
                System.out.print("  Ervenytelen bemenet, probald ujra: ");
            }
        }
    }

    static void nyomjEnter() {
        System.out.println("(Nyomj Entert a folytatáshoz...)");
        sc.nextLine();
    }

    static void fejlec() {
        System.out.println("+==================================+");
        System.out.println("|   KALAND A KASTELYBAN            |");
        System.out.println("|  Jard be az 5 szobat, gyozd le  |");
        System.out.println("|     az osszes szornyet!          |");
        System.out.println("+==================================+");
        System.out.println();
    }

    static void gyozelem(Karakter jatekos) {
        System.out.println();
        System.out.println("+==================================+");
        System.out.println("|   *** GRATULALOK! ***            |");
        System.out.println("|  Bejartad az egesz kastelytt!   |");
        System.out.println("|  Legyoztel minden ellenseget!   |");
        System.out.printf( "|  %s - maradt HP: %d/%d%n", jatekos.nev, jatekos.hp, jatekos.maxHp);
        System.out.println("+==================================+");
    }

    static void vereseg(Karakter jatekos) {
        System.out.println();
        System.out.println("+==================================+");
        System.out.println("|      *** JATEK VEGE ***          |");
        System.out.printf( "|  %s elesett a csataban!%n |", jatekos.nev);
        System.out.println("|  A kastely homalya elnyelt...   |");
        System.out.println("+==================================+");
    }
}