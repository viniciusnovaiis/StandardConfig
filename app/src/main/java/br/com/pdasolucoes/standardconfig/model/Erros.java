package br.com.pdasolucoes.standardconfig.model;

public class Erros {

    private String campo;
    private String mensagem;
    private String valor;
    private int tipoErro;

    public String getCampo() {
        return campo;
    }

    public void setCampo(String campo) {
        this.campo = campo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public int getTipoErro() {
        return tipoErro;
    }

    public void setTipoErro(int tipoErro) {
        this.tipoErro = tipoErro;
    }
}
