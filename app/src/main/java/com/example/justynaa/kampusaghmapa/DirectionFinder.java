package com.example.justynaa.kampusaghmapa;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 * Created by Justynaa on 2017-01-13.
 */
public class DirectionFinder {
    private DirectionFinderListener listener;
    private Context context;
    private String start;
    private String finish;
    private int s, f, v, k;

    public DirectionFinder(Context context, DirectionFinderListener listener, String start, String finish) {
        this.start = start;
        this.finish = finish;
        this.listener = listener;
        this.context = context;

    }

    public DirectionFinder(Context context, DirectionFinderListener listener, int start, int finish) {
        this.s = start;
        this.f = finish;
        this.listener = listener;
        this.context = context;
    }

    public void execute() throws IOException {
        listener.onDirectionFinderStart();  //odkomentowac potem
        //Toast.makeText(context, "Odzcytano miejsca ",Toast.LENGTH_SHORT).show();
        DownloadData(start, finish);
        //DownloadData(s, f);
    }

    private void DownloadData(String start, String finish) throws IOException {
        InputStream dane = context.getResources().openRawResource(R.raw.dane);
        Scanner sc = new Scanner(dane);
        InputStream koordy = context.getResources().openRawResource(R.raw.koordy);
        Scanner scanner = new Scanner(koordy);
        int wierz = sc.nextInt(), kraw = sc.nextInt();   // MAMY  TO!
        int tablica[][] = new int[kraw][3];
        List<Route> routes = new ArrayList<Route>();

        // trzeba znalezc miejsce na jakim jest start i finish





        boolean QS[] = new boolean[wierz];
        int[] d = new int[wierz];
        int[] p = new int[wierz];
        int j, u, z=1;

        for (int i = 0; i < kraw; i++) {   // 9s
            tablica[i][0] = sc.nextInt();
            tablica[i][1] = sc.nextInt();
            tablica[i][2] = sc.nextInt();
        }
        for(int i=0; i<kraw; i++){
            System.out.println(tablica[i][0]+" "+tablica[i][1]+" "+tablica[i][2]);
        }

        //czytanie 2 pliku

        double x, y;
        int id, w = 50; // zmienic potem!!!
        String nazwa;
        String[] tab_nazw;
        double[][] tab_wsp;
        tab_wsp = new double[w][3];
        tab_nazw = new String[w];

        for (int i = 0; i < w; i++) {
            id = scanner.nextInt();
            nazwa = scanner.next();
            x = Double.parseDouble(scanner.next());
            y = Double.parseDouble(scanner.next());
            tab_nazw[i] = nazwa;
            tab_wsp[i][0] = id;
            tab_wsp[i][1] = x;
            tab_wsp[i][2] = y;
            if(Objects.equals(nazwa, start)) v=id-1;
            if(Objects.equals(nazwa, finish)) k=id-1;
        }
        //Toast.makeText(context, "",Toast.LENGTH_SHORT).show();

        //v = s;
       // k = f;


        for (int i = 0; i < wierz; i++) {  //9s
            QS[i] = false;
            d[i] = Integer.MAX_VALUE;  //?
            p[i] = -1;
        }
        d[k] = 0;

        for (int i = 0; i < wierz; i++) {  // 9s  ,wyszukiwanie wierzchołka o najmniejszym koszcie d
            for (j = 0; QS[j]; j++) ;
            for (u = j++; j < wierz; j++)
                if (!QS[j] && (d[j] < d[u])) u = j;

            QS[u] = true; // znaleziony wierzcholek

            for (int a = 0; a < kraw; a++) {  //9s
                if (tablica[a][0] == u) {
                    if(d[tablica[a][1]]> d[u]+tablica[a][2]){
                        d[tablica[a][1]]=d[u]+tablica[a][2];
                        p[tablica[a][1]]=u;
                    }
                }else if(tablica[a][1]==u){
                    if(d[tablica[a][0]] > d[u]+tablica[a][2]){
                        d[tablica[a][0]]=d[u]+tablica[a][2];
                        p[tablica[a][0]]=u;
                    }
                }
            }
        }

        String wynik = v + " ";
        int c = v;
        int dlugosc=1;
        while (p[c] > -1) {   //p[] jest tablica z punktami linii po ktorej sie idzie
            wynik = wynik + p[c] + " ";
            System.out.print(p[c]+" ");
            c = p[c];
            dlugosc++;
        }

       // Toast.makeText(context, ""+dlugosc,Toast.LENGTH_SHORT).show();

        double[][] tab_wynik;
        tab_wynik = new double[dlugosc][2];


        //Toast.makeText(context, " "+dlugosc, Toast.LENGTH_SHORT).show();
        //Toast.makeText(context, "Droga z wierzchołka " + v + " do wierzchołka " + k + " prowadzi przez:", Toast.LENGTH_SHORT).show();
        //Toast.makeText(context, wynik, Toast.LENGTH_SHORT).show();
        //Toast.makeText(context, "Długość trasy wynosi: " + d[v], Toast.LENGTH_SHORT).show();

        c=v;
        tab_wynik[0][0] = tab_wsp[v][1];
        tab_wynik[0][1] = tab_wsp[v][2];
        System.out.println(tab_wynik[0][0]+" "+tab_wynik[0][1]);
        while (p[c] > -1) {
            tab_wynik[z][0] = tab_wsp[p[c]][1];
            tab_wynik[z][1] = tab_wsp[p[c]][2];
            c = p[c];
            System.out.println(tab_wynik[z][0]+" "+tab_wynik[z][1]);
            z++;
        }
        // w wyniku dzialania algorytmu otrzymujemy tablice ze wspoolrzednymi x i y kolejnych punktów
        // przez ktore przejdzie linia


        for (int i = 0; i < tab_wynik.length-1; i++) {  // dla kazdego z tych punktów (z tab_wynik) tworzymy obiekt Route, który bedzie mial
            // swoją dlugosc, koordynaty staru i końca itp.
            Route route = new Route();

            route.dystans = d[v];
            route.czas = d[v];
            route.startNazwa = String.valueOf(v);
            route.startKordy = new LatLng(tab_wynik[i][0], tab_wynik[i][1]);
            route.koniecNazwa = String.valueOf(k);
            route.koniecKordy = new LatLng(tab_wynik[i+1][0], tab_wynik[i+1][1]);
            //route.punkty.add(i, new LatLng(2.3424, 4.4534));

            routes.add(route);

        }
       //Toast.makeText(context, ""+dlugosc,Toast.LENGTH_SHORT).show();
        //Toast.makeText(context, ""+routes.size(),Toast.LENGTH_SHORT).show();
        listener.onDirectionFinderSuccess(routes);

    }

    private void DownloadData(int s, int f) throws IOException {
        InputStream dane = context.getResources().openRawResource(R.raw.dane);
        Scanner sc = new Scanner(dane);
        int wierz = sc.nextInt(), kraw = sc.nextInt();   // MAMY  TO!
        int tablica[][] = new int[kraw][3];
        List<Route> routes = new ArrayList<Route>();
        v = s;
        k = f;
        boolean QS[] = new boolean[wierz];
        int[] d = new int[wierz];
        int[] p = new int[wierz];
        int j, u, z=1;

        for (int i = 0; i < kraw; i++) {   // 9s
                tablica[i][0] = sc.nextInt();
                tablica[i][1] = sc.nextInt();
                tablica[i][2] = sc.nextInt();
        }
        for(int i=0; i<kraw; i++){
            System.out.println(tablica[i][0]+" "+tablica[i][1]+" "+tablica[i][2]);
        }

        //czytanie 2 pliku
        InputStream koordy = context.getResources().openRawResource(R.raw.koordy);
        Scanner scanner = new Scanner(koordy);

        double x, y;
        int id, w = 7; // zmienic potem!!!
        String nazwa;
        String[] tab_nazw;
        double[][] tab_wsp;
        tab_wsp = new double[w][3];
        tab_nazw = new String[w];

        for (int i = 0; i < w; i++) {
            id = scanner.nextInt();
            nazwa = scanner.next();
            x = Double.parseDouble(scanner.next());
            y = Double.parseDouble(scanner.next());
            tab_nazw[i] = nazwa;
            tab_wsp[i][0] = id;
            tab_wsp[i][1] = x;
            tab_wsp[i][2] = y;
            //System.out.println(id + " " + nazwa + " " + x + " " + y);
        }

        for (int i = 0; i < wierz; i++) {  //9s
            QS[i] = false;
            d[i] = Integer.MAX_VALUE;  //?
            p[i] = -1;
        }
        d[k] = 0;

        for (int i = 0; i < wierz; i++) {  // 9s  ,wyszukiwanie wierzchołka o najmniejszym koszcie d
            for (j = 0; QS[j]; j++) ;
            for (u = j++; j < wierz; j++)
                if (!QS[j] && (d[j] < d[u])) u = j;

            QS[u] = true; // znaleziony wierzcholek

            for (int a = 0; a < kraw; a++) {  //9s
                if (tablica[a][0] == u) {
                    if(d[tablica[a][1]]> d[u]+tablica[a][2]){
                        d[tablica[a][1]]=d[u]+tablica[a][2];
                        p[tablica[a][1]]=u;
                    }
                }else if(tablica[a][1]==u){
                    if(d[tablica[a][0]] > d[u]+tablica[a][2]){
                        d[tablica[a][0]]=d[u]+tablica[a][2];
                        p[tablica[a][0]]=u;
                    }
                }
            }
        }

        String wynik = v + " ";
        int c = v;
        int dlugosc=1;
        while (p[c] > -1) {   //p[] jest tablica z punktami linii po ktorej sie idzie
            wynik = wynik + p[c] + " ";
            System.out.print(p[c]+" ");
            c = p[c];
            dlugosc++;
        }

        double[][] tab_wynik;
        tab_wynik = new double[dlugosc][2];


        //Toast.makeText(context, " "+dlugosc, Toast.LENGTH_SHORT).show();
        //Toast.makeText(context, "Droga z wierzchołka " + v + " do wierzchołka " + k + " prowadzi przez:", Toast.LENGTH_SHORT).show();
        //Toast.makeText(context, wynik, Toast.LENGTH_SHORT).show();
        //Toast.makeText(context, "Długość trasy wynosi: " + d[v], Toast.LENGTH_SHORT).show();

        c=v;
        tab_wynik[0][0] = tab_wsp[v][1];
        tab_wynik[0][1] = tab_wsp[v][2];
        System.out.println(tab_wynik[0][0]+" "+tab_wynik[0][1]);
        while (p[c] > -1) {
            tab_wynik[z][0] = tab_wsp[p[c]][1];
            tab_wynik[z][1] = tab_wsp[p[c]][2];
            c = p[c];
            System.out.println(tab_wynik[z][0]+" "+tab_wynik[z][1]);
            z++;
        }
        // w wyniku dzialania algorytmu otrzymujemy tablice ze wspoolrzednymi x i y kolejnych punktów
        // przez ktore przejdzie linia


        for (int i = 0; i < tab_wynik.length-1; i++) {  // dla kazdego z tych punktów (z tab_wynik) tworzymy obiekt Route, który bedzie mial
                                                        // swoją dlugosc, koordynaty staru i końca itp.
            Route route = new Route();

            route.dystans = d[v];
            route.czas = d[v];
            route.startNazwa = String.valueOf(v);
            route.startKordy = new LatLng(tab_wynik[i][0], tab_wynik[i][1]);
            route.koniecNazwa = String.valueOf(k);
            route.koniecKordy = new LatLng(tab_wynik[i+1][0], tab_wynik[i+1][1]);
            //route.punkty.add(i, new LatLng(2.3424, 4.4534));

            routes.add(route);
        }
        listener.onDirectionFinderSuccess(routes);
    }

    /*private List<LatLng> linia(double[][] tab) {
        List<LatLng> l = new ArrayList<LatLng>();
        int len = tab.length-1;



        return  l;
    }*/
}
        /*List<LatLng> listaPunktow = new ArrayList<LatLng>();
        int tablica2[][] = new int[wierz][4];
        String nazwa;
        int id;
        String x,y;
        double wspX,wspY;*/


        /*for (int wiersze = 0; wiersze < 2; wiersze++) {
            for (int kolumny = 0; kolumny < 4; kolumny++) {

                id = scanner.nextInt();
                nazwa = scanner.next();
                x = scanner.next();
                y = scanner.next();
                wspX = Double.valueOf(x);
                wspY = Double.valueOf(y);
                System.out.println("Found :" + id);
                System.out.println("Found :" + nazwa);
                System.out.println("Found :" + wspX);
                System.out.println("Found :" + wspY);

            }*/
            // for(int i=0; i<1; i++){
            //for(j=0; j<4; j++){
                /*tablica2[0][0] = scanner.nextInt();
                tablica2[0][2] = scanner.nextInt();  // tu wyrzuca błąd bo 2 kolumna w koordach to string
                Toast.makeText(context,""+tablica2[0][0],Toast.LENGTH_SHORT).show();*/

            //while (scanner.hasNext()) {}
            /*if (scanner.hasNextInt()) {// if the next is a int, print found and the int
                System.out.println("Found :" + scanner.nextInt());
            }else if(scanner.hasNextDouble())
                System.out.println("Found :" + scanner.nextDouble());
            // if no int is found, print "Not Found:" and the token
            else  System.out.println("Not Found :" + scanner.next());*/


            // close the scanner


        //scanner.close();





    /*private List<LatLng> doListy (int punkty[]){

        InputStream koordy = context.getResources().openRawResource(R.raw.koordy);
        Scanner sc = new Scanner(koordy);
        List<LatLng> listaPunktow = new ArrayList<LatLng>();
        //LatLng abcd = new LatLng(50.078763, 19.916058);

        int l = punkty.length, tmp;
        for(int i=0;i<l;i++){
           //listaPunktow.add();
        }

        return listaPunktow;
    }*/


















