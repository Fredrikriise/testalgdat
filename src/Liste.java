import java.util.Iterator;
import java.util.Objects;
import java.util.function.Predicate;

public interface Liste<T>
{
    public boolean leggInn(T verdi);           // Nytt element bakerst
    public void leggInn(int indeks, T verdi);  // Nytt element på plass indeks
    public boolean inneholder(T verdi);        // Er verdi i listen?
    public T hent(int indeks);                 // Hent element på plass indeks
    public int indeksTil(T verdi);             // Hvor ligger verdi?
    public T oppdater(int indeks, T verdi);    // Oppdater på plass indeks
    public boolean fjern(T verdi);             // Fjern objektet verdi
    public T fjern(int indeks);                // Fjern elementet på plass indeks
    public int antall();                       // Antallet i listen
    public boolean tom();                      // Er listen tom?
    public void nullstill();                   // Listen nullstilles (og tømmes)
    public Iterator<T> iterator();             // En iterator

    default boolean fjernHvis(Predicate<? super T> p)  // betingelsesfjerning
    {
        Objects.requireNonNull(p);                       // kaster unntak

        boolean fjernet = false;
        for (Iterator<T> i = iterator(); i.hasNext(); )  // løkke
        {
            if (p.test(i.next()))                          // betingelsen
            {
                i.remove(); fjernet = true;                  // fjerner
            }
        }
        return fjernet;
    }

    public default String melding(int indeks)  // Unntaksmelding
    {
        return "Indeks: " + indeks + ", Antall: " + antall();
    }

    public default void indeksKontroll(int indeks, boolean leggInn)
    {
        if (indeks < 0 ? true : (leggInn ? indeks > antall() : indeks >= antall()))
            throw new IndexOutOfBoundsException(melding(indeks));
    }
}  // Liste
