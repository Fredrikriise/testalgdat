/*
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class DobbeltLenketListe<T> implements Liste<T> {
    private static final class Node<T> {  // en indre nodeklasse
        // instansvariabler
        private T verdi;
        private Node<T> forrige, neste;

        private Node(T verdi, Node<T> forrige, Node<T> neste)  // konstruktør
        {
            this.verdi = verdi;
            this.forrige = forrige;
            this.neste = neste;
        }

        protected Node(T t, T verdi)  // konstruktør
        {
            this(verdi, null, null);
        }
    } // Node

    // instansvariabler
    private Node<T> hode;          // peker til den første i listen
    private Node<T> hale;          // peker til den siste i listen
    private int antall;            // antall noder i listen
    private int endringer;   // antall endringer i listen

    // hjelpemetode
    private Node<T> finnNode(int indeks)
    {
         if(indeks < (antall/2)) {
             Node<T> p = hode;
             for(int i = 0; i < indeks; i++) p = p.neste;
                 return p;
         } else {
             Node<T> p = hale;
             for (int i = antall-1; i > indeks; i--) p = p.forrige;
             return p;
         }
    }

    // konstruktør
    public DobbeltLenketListe()
    {
        hode = hale = null;
        antall = 0;
        endringer = 0;
    }

    // konstruktør

    public DobbeltLenketListe(T[]a)
        {
            this();

            Objects.requireNonNull(a, "Tabellen a er null!");

            if (a.length == 0) return;
            Node<T> temp = hode;
            for (int i = 0; i < a.length; i++) {
                if (a[i] == null)
                    continue;
                Node<T> newNode = new Node<>(a[i], null, null);
                if (antall == 0) {
                    hode = hale = newNode;
                } else {
                    newNode.forrige = temp;
                    hale.neste = newNode;
                    hale = newNode;
                }
                temp = newNode;
                antall++;
            }
        }
    // fratilKontroll fra kompendiet
    private static void fratilKontroll(int antall, int fra, int til) {
        if (fra < 0)
            throw new IndexOutOfBoundsException
                    ("fra(" + fra + ") er negativ !");
        if (til > antall)
            throw new IndexOutOfBoundsException
                    ("til(" + til + ") > antall(" + antall + ")");
        if (fra > til)
            throw new IllegalArgumentException
                    ("fra(" + fra + ") > til(" + til + ") - ulovlig intervall!");
    }

    // subliste
    public Liste<T> subliste(int fra, int til)
    {
        fratilKontroll(antall, fra, til);
        Liste<T> tmpListe = new DobbeltLenketListe<>();
        for(int i = fra; i < til; i++) {
            tmpListe.leggInn(finnNode(i).verdi);
        }
        return tmpListe;
    }

    @Override
    public int antall()
    {
        return antall;
    }

    @Override
    public boolean tom()
    {
        return antall == 0;
    }

    @Override
    public boolean leggInn(T verdi)
    {

        Objects.requireNonNull(verdi, "Ikke tillat med null-verdier!");

        if(tom()) {
            hode = hale = new Node<>(verdi, null, null);
        } else {
            hale = hale.neste = new Node<>(verdi, hale, null);
        }
        antall++;
        endringer++;
        return true;
    }

    @Override
    public void leggInn(int indeks, T verdi)
    {
        Objects.requireNonNull(verdi, "Ikke tillatt med null-verdier");

        indeksKontroll(indeks, true);

        if(tom()) {
            hode = hale = new Node<>(verdi, null, null);
        } else if (indeks == 0) {
            hode = hode.forrige = new Node<>(verdi, null, hode);
        } else if(indeks == antall) {
            hale = hale.neste = new Node<>(verdi, hale, null);
        } else {
            Node<T> p = hode;

            for(int i = 0; i < indeks; i++) p = p.neste;

            p = new Node<>(verdi, p, p.neste);
            p.neste.forrige = p;
            p.forrige.neste = p;
        }
        antall++;
        endringer++;
    }

    @Override
    public boolean inneholder(T verdi)
    {
        return indeksTil(verdi) != -1;
    }

    @Override
    public T hent(int indeks)
    {
        indeksKontroll(indeks, false);
        return finnNode(indeks).verdi;
    }

    @Override
    public int indeksTil(T verdi)
    {
        if (verdi == null) return -1;

        Node<T> p = hode;

        for (int indeks = 0; indeks < antall ; indeks++)
        {
            if (p.verdi.equals(verdi)) return indeks;
            p = p.neste;
        }
        return -1;
    }

    @Override
    public T oppdater(int indeks, T nyverdi)
    {
        Objects.requireNonNull(nyverdi, "Ikke tillatt med null-verdier!");
        indeksKontroll(indeks, false);
        Node<T> p = finnNode(indeks);
        T gammelVerdi = p.verdi;
        p.verdi = nyverdi;
        return gammelVerdi;
    }

    @Override
    public boolean fjern(T verdi)
    {
        if (verdi == null) {
            return false;
        }

        Node<T> l = hode, k = null;

        while (l != null) {
            if(l.verdi.equals(verdi)) break;
                k = l;
                l = l.neste;
        }

        if (l == null) {
            return false;
        } else if (l == hode) {
            hode = hode.neste;
            if(antall > 1) {
                hode.forrige = null;
            }
        } else if (k == hale) {
            hale = hale.forrige;
            hale.neste = null;
        } else {
            k.forrige.neste = k.neste;
            k.neste.forrige = k.forrige;
        }

        antall--;
        endringer++;
        return true;
    }

    @Override
    public T fjern(int indeks)
    {
        indeksKontroll(indeks, false);

        T temp;

        if (indeks == 0) {
            temp = hode.verdi;
        //    hode = hode.neste;
            if(antall > 1) {
                hode = hode.neste;
                hode.forrige = null;
            } else {
                hale = null;
                hode.neste = null;
            }
        } else if(indeks == antall - 1) {

            temp = hale.verdi;
            Node<T> tempHale = hale;
            hale = hale.forrige;
            tempHale.forrige = null;
            hale.neste = null;
        } else {
            Node<T> k = finnNode(indeks - 1);
            temp = k.verdi;

            k.forrige.neste = k.neste;
            k.neste.forrige = k.forrige;
        }

        antall--;
        endringer++;
        return temp;
    }

    @Override
    public void nullstill()
    {
        throw new UnsupportedOperationException("Ikke laget ennå!");
    }

    @Override
    public String toString()
    {
        StringBuilder s = new StringBuilder();

        s.append('[');

        if (!tom()) {
            Node<T> a = hode;
            s.append(a.verdi);

            a = a.neste;

            while (a != null) {
                s.append(',').append(' ').append(a.verdi);
                a = a.neste;

            }
        }

        s.append(']');

        return s.toString();

    }

    public String omvendtString()
    {
        StringBuilder i = new StringBuilder();

        i.append('[');

        if(!tom()) {
            Node<T> p = hale;
            i.append(p.verdi);

            p = p.forrige;

            while (p != null) {
                i.append(',').append(' ').append(p.verdi);
                p = p.forrige;
            }
        }

        i.append(']');

        return String.valueOf(i);
    }

    public static <T> void sorter(Liste<T> liste, Comparator<? super T> c)
    {
        throw new UnsupportedOperationException("Ikke laget ennå!");
    }

    @Override
    public Iterator<T> iterator()
    {
        throw new UnsupportedOperationException("Ikke laget ennå!");
    }

    public Iterator<T> iterator(int indeks)
    {
        throw new UnsupportedOperationException("Ikke laget ennå!");
    }

    private class DobbeltLenketListeIterator implements Iterator<T>
    {
        private Node<T> denne;
        private boolean fjernOK;
        private int iteratorendringer;

        private DobbeltLenketListeIterator()
        {
            denne = hode;     // denne starter på den første i listen
            fjernOK = false;  // blir sann når next() kalles
            iteratorendringer = endringer;  // teller endringer
        }

        private DobbeltLenketListeIterator(int indeks)
        {
            throw new UnsupportedOperationException("Ikke laget ennå!");
        }

        @Override
        public boolean hasNext()
        {
            return denne != null;  // denne koden skal ikke endres!
        }

        @Override
        public T next()
        {
            throw new UnsupportedOperationException("Ikke laget ennå!");
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException("Ikke laget ennå!");
        }

    } // DobbeltLenketListeIterator

/** //////////////////////////////////////////////////////////////////////////// **/
/*
public static void main(String[] args) {

        // Oppgave 1
        String[] s1 = {}, s2 = {"A"}, s3 = {null,"A",null,"B",null};
        DobbeltLenketListe<String> l1 = new DobbeltLenketListe<>(s1);
        DobbeltLenketListe<String> l2 = new DobbeltLenketListe<>(s2);
        DobbeltLenketListe<String> l3 = new DobbeltLenketListe<>(s3);

        System.out.println(l1.toString() + ""+ l2.toString()+ ""+ l3.toString() + ""+ l1.omvendtString() + ""+ l2.omvendtString() + ""+ l3.omvendtString());

        //Oppgave 3b)

        Character[] c = {'A','B','C','D','E','F','G','H','I','J',};
        DobbeltLenketListe<Character> liste = new DobbeltLenketListe<>(c);
        System.out.println(liste.subliste(3,8));  // [D, E, F, G, H]
        System.out.println(liste.subliste(5,5));  // []
        System.out.println(liste.subliste(8,liste.antall()));  // [I, J]
        // System.out.println(liste.subliste(0,11));  // skal kaste unntak



        }
        } // DobbeltLenketListe
        */