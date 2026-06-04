import java.util.Scanner;
import java.util.Random;
 
public class Harc {
 
    // --- Karakter osztály ---
    static class Karakter {
        String nev;
        int hp, maxHp;
        int damage;
        int mana, maxMana;
        String jel;
        boolean vedekezes = false;
 
        Karakter(String nev, int hp, int damage, int mana, String jel) {
            this.nev    = nev;
            this.hp     = hp; this.maxHp  = hp;
            this.damage = damage;
            this.mana   = mana; this.maxMana = mana;
            this.jel    = jel;
        }
 
        boolean eletel()   { return hp > 0; }
        boolean vanMana()  { return mana >= 20; }
 
        // Normál támadás
        int tamadas(Karakter cel) {
            int alap   = damage;
            int szoras = new Random().nextInt(11) - 5; // -5..+5
            int sebzes = Math.max(1, alap + szoras);
            if (cel.vedekezes) {
                sebzes = sebzes / 2;
                System.out.println("  [Védekezés] A sebzés felére csökkent!");
            }
            cel.hp = Math.max(0, cel.hp - sebzes);
            return sebzes;
        }
 
        // Varázslat (mana × 1.5 sebzés, 20 manába kerül)
        int varazslat(Karakter cel) {
            if (!vanMana()) return -1;
            mana -= 20;
            int sebzes = (int)(damage * 1.5);
            if (cel.vedekezes) {
                sebzes = sebzes / 2;
                System.out.println("  [Védekezés] A sebzés felére csökkent!");
            }
            cel.hp = Math.max(0, cel.hp - sebzes);
            return sebzes;
        }
 
        // Védekezés beállítása (következő beérkező ütés feleakkora)
        void vedekez() {
            vedekezes = true;
        }
 
        // Körök elején töröljük a védekezés jelzőt
        void ujKor() {
            vedekezes = false;
        }
    }
 
    // --- Ellenfelek listája ---
    static Karakter[] ellenfelek = {
        new Karakter("Troll",    40, 20,  5, "+"),
        new Karakter("Csontváz", 10, 10, 10, "-"),
        new Karakter("Zombi",     5, 10,  5, "~")
    };
 
    // --- Játékos karakterek ---
    static Karakter[] karakterek = {
        new Karakter("Goblin",  40, 25, 45, "○"),
        new Karakter("Harcos", 100, 35, 50, "▢"),
        new Karakter("Mágus",   55, 30, 90, "△"),
        new Karakter("Elf",     25, 10, 35, "◇")
    };
 
    static Scanner sc     = new Scanner(System.in);
    static Random  rnd    = new Random();
 
    // ================================================================
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════╗");
        System.out.println("║     ⚔  HARCRENDSZER  ⚔       ║");
        System.out.println("╚══════════════════════════════╝");
        System.out.println();
 
        // 1. Karakter választás
        Karakter jatekos = karaktertValaszt();
        System.out.println("\nKiválasztottad: " + jatekos.jel + " " + jatekos.nev);
 
        // 2. Ellenfél kisorsol
        Karakter ellenfél = ellenfelKijelol();
        System.out.println("Ellenfeled: " + ellenfél.jel + " " + ellenfél.nev);
        System.out.println();
        nyomjEnter();
 
        // 3. Harc
        harc(jatekos, ellenfél);
    }
 
    // ================================================================
    static Karakter karaktertValaszt() {
        System.out.println("Válassz karaktert:");
        for (int i = 0; i < karakterek.length; i++) {
            Karakter k = karakterek[i];
            System.out.printf("  [%d] %s %s  HP:%-4d DMG:%-4d MANA:%-4d%n",
                i + 1, k.jel, k.nev, k.hp, k.damage, k.mana);
        }
        System.out.print("> ");
        int valasz = bekerjSzam(1, karakterek.length);
        return karakterek[valasz - 1];
    }
 
    static Karakter ellenfelKijelol() {
        // Véletlenszerű ellenfél
        return ellenfelek[rnd.nextInt(ellenfelek.length)];
    }
 
    // ================================================================
    static void harc(Karakter jatekos, Karakter ellenfél) {
        int kor = 1;
 
        while (jatekos.eletel() && ellenfél.eletel()) {
            System.out.println("══════════════ " + kor + ". KÖR ══════════════");
            allapotKiir(jatekos, ellenfél);
 
            // Játékos köre
            jatekos.ujKor();
            ellenfél.ujKor();
 
            System.out.println("\n⚔  " + jatekos.nev + " köre:");
            boolean ervenyesLepes = false;
 
            while (!ervenyesLepes) {
                System.out.println("  [1] Normál támadás");
                System.out.printf("  [2] Varázslat  (mana: %d/%d, -20 mana, x1.5 sebzés)%n",
                    jatekos.mana, jatekos.maxMana);
                System.out.println("  [3] Védekezés  (következő sebzés feleakkora)");
                System.out.print("  > ");
 
                int lepes = bekerjSzam(1, 3);
 
                switch (lepes) {
                    case 1 -> {
                        int seb = jatekos.tamadas(ellenfél);
                        System.out.printf("  %s %s támad → %d sebzés! (Ellenfél HP: %d)%n",
                            jatekos.jel, jatekos.nev, seb, ellenfél.hp);
                        ervenyesLepes = true;
                    }
                    case 2 -> {
                        if (!jatekos.vanMana()) {
                            System.out.println("  Nincs elég manád! (kell: 20)");
                        } else {
                            int seb = jatekos.varazslat(ellenfél);
                            System.out.printf("  ✨ %s varázsol → %d sebzés! (Ellenfél HP: %d | Maradék mana: %d)%n",
                                jatekos.nev, seb, ellenfél.hp, jatekos.mana);
                            ervenyesLepes = true;
                        }
                    }
                    case 3 -> {
                        jatekos.vedekez();
                        System.out.println("  🛡 " + jatekos.nev + " védekezik! A következő beérkező sebzés felére csökken.");
                        ervenyesLepes = true;
                    }
                }
            }
 
            if (!ellenfél.eletel()) break;
 
            // Ellenfél köre (AI)
            System.out.println("\n👹 " + ellenfél.nev + " köre:");
            ellenfelAI(ellenfél, jatekos);
 
            kor++;
            System.out.println();
            nyomjEnter();
        }
 
        // Eredmény
        System.out.println("\n╔══════════════════════════════╗");
        if (jatekos.eletel()) {
            System.out.println("║   🏆  GYŐZELEM!  🏆           ║");
            System.out.printf( "║  %s legyőzte %s-t!%n", jatekos.nev, ellenfél.nev);
            System.out.printf( "║  Maradt HP: %d / %d%n", jatekos.hp, jatekos.maxHp);
        } else {
            System.out.println("║   💀  VERESÉG!  💀            ║");
            System.out.printf( "║  %s elesett!%n", jatekos.nev);
        }
        System.out.println("╚══════════════════════════════╝");
    }
 
    // --- Ellenfél AI ---
    static void ellenfelAI(Karakter ellenfél, Karakter cel) {
        // Ha van mana és 30% eséllyel varázsol
        if (ellenfél.vanMana() && rnd.nextInt(10) < 3) {
            int seb = ellenfél.varazslat(cel);
            System.out.printf("  ✨ %s varázsol → %d sebzés! (Te HP: %d)%n",
                ellenfél.nev, seb, cel.hp);
        } else if (rnd.nextInt(10) < 2) {
            // 20% eséllyel védekezik
            ellenfél.vedekez();
            System.out.println("  🛡 " + ellenfél.nev + " védekezik!");
        } else {
            int seb = ellenfél.tamadas(cel);
            System.out.printf("  %s %s támad → %d sebzés! (Te HP: %d)%n",
                ellenfél.jel, ellenfél.nev, seb, cel.hp);
        }
    }
 
    // --- Állapot kiírása ---
    static void allapotKiir(Karakter jatekos, Karakter ellenfél) {
        System.out.printf("  %s %-8s  HP: %s (%d/%d)   Mana: %d/%d%n",
            jatekos.jel, jatekos.nev,
            hpSav(jatekos.hp, jatekos.maxHp),
            jatekos.hp, jatekos.maxHp,
            jatekos.mana, jatekos.maxMana);
        System.out.printf("  %s %-8s  HP: %s (%d/%d)   Mana: %d/%d%n",
            ellenfél.jel, ellenfél.nev,
            hpSav(ellenfél.hp, ellenfél.maxHp),
            ellenfél.hp, ellenfél.maxHp,
            ellenfél.mana, ellenfél.maxMana);
    }
 
    static String hpSav(int hp, int maxHp) {
        int teli  = (int)((double)hp / maxHp * 10);
        int ures  = 10 - teli;
        return "[" + "█".repeat(teli) + "░".repeat(ures) + "]";
    }
 
    // --- Segédmetódusok ---
    static int bekerjSzam(int min, int max) {
        while (true) {
            try {
                int n = Integer.parseInt(sc.nextLine().trim());
                if (n >= min && n <= max) return n;
                System.out.printf("  Kérlek %d és %d közötti számot adj meg: ", min, max);
            } catch (NumberFormatException e) {
                System.out.print("  Érvénytelen bemenet, próbáld újra: ");
            }
        }
    }
 
    static void nyomjEnter() {
        System.out.println("(Nyomj Entert a folytatáshoz...)");
        sc.nextLine();
    }
}