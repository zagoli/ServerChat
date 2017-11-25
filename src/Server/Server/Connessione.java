package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class Connessione implements Runnable {
    private Socket socket;
    private Boolean run = true;
    private Accessi acc;

    public Connessione (Socket s,Accessi accessi){
        this.socket = s;
        this.acc = accessi;
    }

    @Override
    public void run() {
        try {
            BufferedReader lettore = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter scrittore = new PrintWriter(socket.getOutputStream(), true);
            String ip = socket.getRemoteSocketAddress().toString();
            while (run){
                String packet = lettore.readLine();
                String cod = packet.substring(0, 2);
                String value = packet.substring(2);
                switch (cod) {
                    case "00":
                        login(value,scrittore);
                        break;
                    case "10":
                        logout(scrittore);
                        break;
                    case "20":
                        if (acc.isLogged(ip)) {
                            listaUtenti(scrittore);
                        } else {
                            erroreUtNonLoggato(scrittore);
                        }
                        break;
                    case "30":
                        if (acc.isLogged(ip)) {
                            ban(value, scrittore);
                        } else {
                            erroreUtNonLoggato(scrittore);
                        }
                        break;
                    case "31":
                        if (acc.isLogged(ip)) {
                            unban(value, scrittore);
                        } else {
                            erroreUtNonLoggato(scrittore);
                        }
                        break;
                    case "40":
                        if (acc.isLogged(ip)) {

                        } else {
                            erroreUtNonLoggato(scrittore);
                        }
                        break;
                    case "50":
                        if (acc.isLogged(ip)) {

                        } else {
                            erroreUtNonLoggato(scrittore);
                        }
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void login(String value, PrintWriter scrittore) throws IOException {
        if (!value.contains(";")) {
            Utente ut = new Utente(socket.getRemoteSocketAddress().toString(), value);
            acc.addUtente(ut);
            //risposta cercando il nome
            String name = acc.getUtenteByIp(socket.getRemoteSocketAddress().toString()).getNomeUt();
            scrittore.write("01" + name);
            scrittore.flush();
            System.out.println("login:" + socket.getRemoteSocketAddress().toString() + "|" + value);
        } else {
            this.erroreNomeErrato(scrittore);
        }
    }

    private void logout(PrintWriter scrittore) throws IOException {
        String ip = socket.getRemoteSocketAddress().toString();
        Utente toLogout = acc.getUtenteByIp(ip);
        acc.removeUtente(toLogout);
        //risposta di logout
        if (!acc.isLogged(ip)) {
            scrittore.write("11");
            scrittore.flush();
        }
        System.out.println("logout:" + ip);
    }

    private void ban(String value, PrintWriter scrittore) {
        String mioip = socket.getRemoteSocketAddress().toString();
        acc.getUtenteByName(value).addBan(mioip);
        String nomeUtCheBanna = acc.getUtenteByIp(mioip).getNomeUt();
        String ack = "32" + nomeUtCheBanna + ";1";
        //TODO inviare l'ack all'utente bannato
        System.out.println(acc.getUtenteByIp(socket.getRemoteSocketAddress().toString()).getNomeUt() + " ha bannato " + value);
    }

    private void unban(String value, PrintWriter scrittore) {
        String mioip = socket.getRemoteSocketAddress().toString();
        acc.getUtenteByName(value).removeBan(mioip);
        String nomeUtCheSbanna = acc.getUtenteByIp(mioip).getNomeUt();
        String ack = "32" + nomeUtCheSbanna + ";0";
        //TODO inviare l'ack all'utente sbannato
        System.out.println(acc.getUtenteByIp(socket.getRemoteSocketAddress().toString()).getNomeUt() + " ha sbannato " + value);
    }

    private void listaUtenti(PrintWriter scrittore) throws IOException {
        ArrayList<Utente> list = acc.getListautenti();
        String packet = "21";
        for (Utente u : list) {
            packet += u.getNomeUt() + ";";
        }
        scrittore.write(packet);
        scrittore.flush();
        System.out.println("inviata lista utenti a "+ socket.getRemoteSocketAddress().toString());
    }

    private void erroreUtNonLoggato(PrintWriter scrittore) throws IOException {
        scrittore.write("02NON HAI ESEGUITO IL LOGIN. FALLO!");
        scrittore.flush();
        System.out.println("Errore ut non loggato :"+socket.getRemoteSocketAddress().toString());
    }

    private void erroreNomeErrato(PrintWriter scrittore) throws IOException {
        scrittore.write("02IL TUO NOME E' ERRATO. SCEGLINE UNO DIVERSO");
        scrittore.flush();
        System.out.println("Errore nome errato:"+socket.getRemoteSocketAddress().toString());
    }

    public void stop (){
        this.run = false;
    }
}