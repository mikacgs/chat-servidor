/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Mensagem;
import model.Mensagem.Acao;

/**
 *
 *
 * @author Michael
 */
public class ServicoServidor {

    private ServerSocket serverSocket;
    private Socket socket;
    private Map<String, ObjectOutputStream> pessoasOnline = new HashMap<String, ObjectOutputStream>();

    public ServicoServidor() {
        try {
            serverSocket = new ServerSocket(5555);
        } catch (IOException ex) {
            Logger.getLogger(ServicoServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Servidor ativo!");
        aceitaConexoes();
    }

    /**
     * Metodo que fica escutando e aceitando conexoes com novos usuarios
     */
    private void aceitaConexoes() {
        try {
            while (true) {
                socket = serverSocket.accept();
                new Thread(new Recebedor(socket, this)).start();
            }
        } catch (IOException ex) {
            Logger.getLogger(ServicoServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void conectar(Mensagem mensagem, ObjectOutputStream saida) {
        boolean aceitar;
        if (pessoasOnline.isEmpty()) {
            mensagem.setAcao(Acao.CONECTAR);
            envia(mensagem, saida);
            aceitar = true;
        } else {
            if (pessoasOnline.containsKey(mensagem.getOrigem())) {
                mensagem.setAcao(Acao.DESCONECTAR);
                envia(mensagem, saida);
                aceitar = false;
            } else if (!mensagem.getOrigem().isEmpty()) {
                mensagem.setAcao(Acao.CONECTAR);
                envia(mensagem, saida);
                aceitar = true;
            } else {
                aceitar = false;
            }
        }
        if (aceitar) {
            pessoasOnline.put(mensagem.getOrigem(), saida);
            atualizaOnlines();
            System.out.println(mensagem.getOrigem() + " está se juntando à sala");
        }
    }

    private void desconectar(Mensagem mensagem) {
        mensagem.setNomeReservado(mensagem.getOrigem());
        enviaParaUm(mensagem);
        pessoasOnline.remove(mensagem.getOrigem());

        mensagem.setConteudo(" até logo!");
        mensagem.setAcao(Acao.ENVIAR);
        enviaParaTodos(mensagem);

        System.out.println(mensagem.getOrigem() + " está saindo da sala");

        atualizaOnlines();
    }

    private void envia(Mensagem mensagem, ObjectOutputStream saida) {
        try {
            saida.writeObject(mensagem);
        } catch (IOException ex) {
            Logger.getLogger(ServicoServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void enviaParaUm(Mensagem mensagem) {
        for (Map.Entry<String, ObjectOutputStream> entrada : pessoasOnline.entrySet()) {
            if (entrada.getKey().equals(mensagem.getNomeReservado())) {
                envia(mensagem, entrada.getValue());
            }
        }
    }

    private void enviaParaTodos(Mensagem mensagem) {
        for (Map.Entry<String, ObjectOutputStream> entrada : pessoasOnline.entrySet()) {
            if (!entrada.getKey().equals(mensagem.getOrigem())) {
                envia(mensagem, entrada.getValue());
            }
        }
    }

    private void atualizaOnlines() {
        Set<String> nomes = new HashSet<String>();
        for (Map.Entry<String, ObjectOutputStream> entrada : pessoasOnline.entrySet()) {
            nomes.add(entrada.getKey());
        }

        Mensagem mensagem = new Mensagem();
        mensagem.setAcao(Acao.USUARIOS_ONLINE);
        mensagem.setPessoasOnline(nomes);

        for (Map.Entry<String, ObjectOutputStream> entrada : pessoasOnline.entrySet()) {
            mensagem.setOrigem(entrada.getKey());

            envia(mensagem, entrada.getValue());

        }
    }

    /**
     * Método responsável por analisar a mensagem e decidir o que será feita com
     * cada uma.
     *
     * @param mensagem deve conter um {@link Acao} em seu conteudo, veja
     * {@link Mensagem}
     * @param output usado apenas em caso de conexão, para ser armazenado na
     * lista de clientes ativos
     */
    public synchronized void encaminha(Mensagem mensagem, ObjectOutputStream output) {
        Acao acao = mensagem.getAcao();
        switch (acao) {
            case CONECTAR:
                conectar(mensagem, output);
                break;
            case DESCONECTAR:
                desconectar(mensagem);
                return;
            case SUSSURRAR:
                enviaParaUm(mensagem);
                break;
            case ENVIAR:
                enviaParaTodos(mensagem);
                break;
            default:
                break;
        }
    }

}
