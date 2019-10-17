package br.infonet.flavio.AutScript.RecolhimentoObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Recolhimento implements Comparable<Recolhimento>{
    

    protected LocalDateTime data;
    protected String ag,pab,cx;
    protected int saldoRecolher,saldoSistema,diferenca,valorAlteracao;
    
    public Recolhimento(String data,String hora, int saldoRecolher,int saldoSistema,String ag,String pab,String cx){
        this.data = LocalDateTime.parse(data.substring(6,10)+"-"+data.substring(3,5)+"-"+data.substring(0, 2)+"T"+hora);
        this.diferenca = saldoSistema - saldoRecolher;
        this.saldoRecolher = saldoRecolher;
        this.saldoSistema = saldoSistema;
        valorAlteracao = -1*diferenca;
        this.ag = ag;
        this.pab = pab;
        this.cx = cx;
    }
    @Override
    public String toString(){
        String recolhimento;
        recolhimento = String.format("Recolhimento Agência %s pab %s cash %s\n"
                + "Data/Hora = %s\n"
                + "Saldo a Recolher: %d\n"
                + "Saldo do Sistema: %d\n"
                + "Diferença de Caixa: %d ",
                ag,pab,cx,
                data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                saldoRecolher,saldoSistema,diferenca);
        return recolhimento;
    }

    @Override
    public int compareTo(Recolhimento o) {
        return this.data.compareTo(o.data);
    }
    public int getValorAlteracao(){
        return valorAlteracao;
    }
    public void setValorAlteracao(int alteracao){
        this.valorAlteracao = alteracao;
    }
    public String getDataString(){
       return data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
    public boolean isSameDate(Recolhimento r){
       return this.data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")).
               equals(r.data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

    public String getAg() {
        return ag;
    }

    public String getPab() {
        return pab;
    }

    public String getCx() {
        return cx;
    }

    public int getDiferenca() {
        return diferenca;
    }

    public LocalDateTime getData() {
        return data;
    }


}
