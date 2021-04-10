/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Michael
 */
public class Mensagem implements Serializable {

    /**
     * Nome de origem da mensagem
     */
    private String origem;
    /**
     * conteudo da mensagem
     */
    private String conteudo;
    /**
     * Nome que pode ser utilizado para mandar uma mensagem privada
     */
    private String nomeReservado;
    /**
     * Conjunto de pessoas que está online no servidor
     */
    private Set<String> pessoasOnline = new HashSet<String>();
    /**
     * Tipo da mensagem que está sendo enviada
     */
    private Acao acao;

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    /**
     * pega o destinatario da mensagem, usado para destinar a mensagem a um
     * usuario especifico veja {@link #nomeReservado}
     *
     * @return
     */
    public String getNomeReservado() {
        return nomeReservado;
    }

    /**
     * seta um destinatario dn mensagem, usado para destinar a mensagem a um
     * usuario especifico veja {@link #nomeReservado} Algumas funções podem não
     * fazer uso desse parametro na sua leitura *
     */
    public void setNomeReservado(String nomeReservado) {
        this.nomeReservado = nomeReservado;
    }

    /**
     * Metodo para atualização da lista de pessoas online
     *
     * @return uma espécie de lista com o nome dos usuarios conectados
     */
    public Set<String> getPessoasOnline() {
        return pessoasOnline;
    }

    /**
     * Usado pelo servidor para encaminhar os usuarios que estão online
     *
     * @param pessoasOnline uma lista com as pessoas online
     */
    public void setPessoasOnline(Set<String> pessoasOnline) {
        this.pessoasOnline = pessoasOnline;
    }

    public Acao getAcao() {
        return acao;
    }

    public void setAcao(Acao acao) {
        this.acao = acao;
    }

    /**
     * Identificador de mensagem
     */
    public enum Acao {
        CONECTAR, DESCONECTAR, SUSSURRAR, ENVIAR, USUARIOS_ONLINE
    }
}
