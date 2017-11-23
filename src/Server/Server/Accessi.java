package Server;

import java.util.ArrayList;

public class  Accessi{
    private ArrayList<Utente> listautenti;

    public Accessi() {
        listautenti = new ArrayList<>();
    }

    public ArrayList<Utente> getListautenti() {
        return listautenti;
    }

    public void addUtente (Utente u){
        listautenti.add(u);
    }

    public void removeUtente (Utente u){
        listautenti.remove(u);
    }

    public Utente getUtenteByIp (String ip){
        Utente ut = null;
        for (Utente u:listautenti) {
            if (u.getIp().equals(ip)){
                ut = u;
                break;
            }
        }
        return ut;
    }

    public Utente getUtenteByName (String name){
        Utente ut = null;
        for (Utente u:listautenti) {
            if (u.getNomeUt().equals(name)){
                ut = u;
                break;
            }
        }
        return ut;
    }
}

