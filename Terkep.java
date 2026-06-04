import java.util.Scanner;

public class Terkep {
    static String[] nevek = {
            "Barlang", "Könyvtár", "Fegyverkamra", "Trónterem", "Kripta"
    };

    static String[] leirasok = {
            "Sötét és nedves barlang. Csöpög a víz a mennyezetről.",
            "Régi könyvek ezrei sorakoznak a polcokon.",
            "Rozsdás kardok és pajzsok lógnak a falon.",
            "Hatalmas kőtrón áll a terem közepén.",
            "Kőkoporsók sorakoznak a fal mentén. Hideg van."
    };


    static int[][] Szobak = {
            { 1, -1},  //Barlang
            { 2,  0},  //Könyvtár
            { 3,  1},  //Fegyverkamra
            { 4,  2},  //Trónterem
            {-1,  3}   //Kripta
    };  // {irányok}

    static int jelenlegiSzoba = 0;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("----- Mágikus Térkép -----");
        System.out.println("Parancsok: előre, hátra, megnéz, kilépés");
        System.out.println();

        szobatLeir();

        while (true) {
            System.out.print("> ");
            String parancs = sc.nextLine().trim().toLowerCase();

            if (parancs.equals("kilépés") || parancs.equals("kilepes")) {
                System.out.println("Viszlát!");
                break;

            } else if (parancs.equals("megnéz") || parancs.equals("megnéz")) {
                szobatLeir();

            } else if (parancs.equals("előre") || parancs.equals("elore") || parancs.equals("jobb")) {
                int cel = Szobak[jelenlegiSzoba][0];
                if (cel == -1) {
                    System.out.println("Arra nem lehet menni!");
                } else {
                    jelenlegiSzoba = cel;
                    szobatLeir();
                }

            } else if (parancs.equals("hátra") || parancs.equals("hatra") || parancs.equals("bal")) {
                int cel = Szobak[jelenlegiSzoba][1];
                if (cel == -1) {
                    System.out.println("Arra nem lehet menni!");
                } else {
                    jelenlegiSzoba = cel;
                    szobatLeir();
                }

            } else {
                System.out.println("Ismeretlen parancs! (előre, hátra, jobb, bal, megnéz, kilépés)");
            }
        }

        sc.close();
    }

    static void szobatLeir() {
        System.out.println("--- " + nevek[jelenlegiSzoba] + " ---");
        System.out.println(leirasok[jelenlegiSzoba]);
        System.out.println("Lehetséges irányok: előre, hátra, jobb, bal, kilépés, megnéz");
        System.out.println();
    }
}