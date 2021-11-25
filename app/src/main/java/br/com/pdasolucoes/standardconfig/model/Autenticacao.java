package br.com.pdasolucoes.standardconfig.model;

import java.util.Date;
import java.util.List;

public class Autenticacao {

    private boolean authenticated;
    private Date created;
    private Date expiration;
    private String accessToken;
    private String refreshToken;
    private List<Erros> erros;

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public List<Erros> getErros() {
        return erros;
    }

    public void setErros(List<Erros> erros) {
        this.erros = erros;
    }
}
