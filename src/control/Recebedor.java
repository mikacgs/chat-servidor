/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Mensagem;
import model.Mensagem.Acao;

/**
 * Classe responsável por receber as mensagens e encaminhar ao controlador, e
 * tambem fazer autenticação de novos usuarios
 *
 * @author Michael
 */
public class Recebedor implements Runnable {

    private Socket socket;
    private ObjectOutputStream saida;
    private ObjectInputStream entrada;
    private ServicoServidor servidor;
    private boolean sair = false;

    /**
     * Inicia uma thread que fica escutando mensagens
     *
     * @param socket conexao provedora das mensagens
     * @param servidor objeto utilizado para encaminhar as mensagens
     */
    public Recebedor(Socket socket, ServicoServidor servidor) {
        this.servidor = servidor;
        this.socket = socket;
        try {
            this.saida = new ObjectOutputStream(socket.getOutputStream());
            this.entrada = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(ServicoServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        Mensagem mensagem = null;
        try {
            while ((mensagem = (Mensagem) entrada.readObject()) != null) {
                if (mensagem.getAcao() == Acao.DESCONECTAR) {
                    servidor.encaminha(mensagem, saida);
                    entrada.close();
                    saida.close();
                    return;
                } else {
                    servidor.encaminha(mensagem, saida);
                }
            }
        } catch (IOException ex) {
            if (mensagem != null) {
                Mensagem msgTemp = new Mensagem();
                msgTemp.setOrigem(mensagem.getOrigem());
                msgTemp.setAcao(Acao.DESCONECTAR);
                servidor.encaminha(msgTemp, saida);
                System.out.println(mensagem.getOrigem() + " passou por problemas e teve que deixar o chat");
                System.out.println("Problema: " + ex.getMessage());
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServicoServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
