package br.infonet.flavio.AutScript.gerador;

import br.infonet.flavio.AutScript.RecolhimentoObject.Recolhimento;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

public class AutScript {

    private final ArrayList<Recolhimento> recolhimentos;
    private final ArrayList<Recolhimento> rec_diferenca;
    private String ag, pab, cx;

    public AutScript() {
        recolhimentos = new ArrayList<>();
        rec_diferenca = new ArrayList<>();
    }

    private void getRecolhimentos(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("Recolhimento de notas") && line.contains("-")) {
                    String l = br.readLine();
                    ag = l.substring(46, 48);
                    pab = l.substring(49, 51);
                    cx = l.substring(53, 56);
                }
                if (line.contains("Saldo a Recolher:")
                        && line.contains("Saldo Sistema:")) {
                    int saldoRecolher, saldoSistema;
                    String hora, data;
                    data = line.substring(line.indexOf(" ") + 1, line.indexOf(" ") + 11);
                    hora = line.substring(18, 26);
                    saldoRecolher = Integer.parseInt(line.substring(line.indexOf("Recolher:") + 10,
                            line.indexOf(",", line.indexOf("Recolher:") + 11)).replace(".", ""));
                    saldoSistema = Integer.parseInt(line.substring(line.indexOf("Sistema:") + 9,
                            line.indexOf(",", line.indexOf("Sistema:") + 9)).replace(".", ""));
                    recolhimentos.add(new Recolhimento(data, hora, saldoRecolher, saldoSistema, ag, pab, cx));

                }

            }

        } catch (IOException e) {
            // TODO Bloco catch gerado automaticamente

        }

    }

    private ArrayList<Recolhimento> groupByDate() {
        ArrayList<Recolhimento> aux = new ArrayList<>();
        for (int i = 0; i < recolhimentos.size(); i++) {
            if (i + 1 == recolhimentos.size()) {
                aux.add(recolhimentos.get(i));
            } else if (!recolhimentos.get(i).isSameDate(recolhimentos.get(i + 1))) {
                aux.add(recolhimentos.get(i));
            }
        }
        return aux;
    }

    private void addToDif() {
        int p = 0;
        Collections.sort(recolhimentos);
        for (int i = 0; i < recolhimentos.size(); i++) {
            if (recolhimentos.get(i).getValorAlteracao() != 0) {
                p = i;
                break;
            }
        }
        if (recolhimentos.get(p).getValorAlteracao() != 0)
            rec_diferenca.add(recolhimentos.get(p));
        Recolhimento rOld, rNew;
        if(!rec_diferenca.isEmpty())
            for (int i = p + 1; i < recolhimentos.size(); i++) {
                rOld = rec_diferenca.get(rec_diferenca.size() - 1);
                rNew = recolhimentos.get(i);
                if(rNew.getDiferenca() != rOld.getDiferenca()){
                    rNew.setValorAlteracao(rOld.getDiferenca() -rNew.getDiferenca());
                    if (rNew.isSameDate(rOld)) {
                            rNew.setValorAlteracao(rNew.getValorAlteracao() + rOld.getValorAlteracao());
                            rec_diferenca.remove(rOld);
                        }
                    rec_diferenca.add(rNew);
                }

            }

    }

    public ArrayList<Recolhimento> getDiferencas() {
        Collections.sort(rec_diferenca);
        return rec_diferenca;
    }

    private String getRecolhimentosToString() {

        String recs = "==============================================\n";
        if (recolhimentos.isEmpty()) {
            return "Nenhum recolhimento foi encontrado";
        } else {
            for (Recolhimento rec : recolhimentos) {
                recs = recs + rec.toString()
                        + "\n==============================================\n";
            }
            return recs;
        }
    }

    private String criarScript(Recolhimento r) {
        String script = "" + String.format("--Diferença referente ao recolhimento do dia %s às %s\n", r.getDataString(),r.getData().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        //escolher o menor recolhimento com diferença;
        if (r.getValorAlteracao() > 0) {
            script = script
                    + String.format("UPDATE USR_IBANKING.TB_CXA_CAIXA SET CXA_VL_SALDO=CXA_VL_SALDO+%d"
                            + "\n\tWHERE CXA_DT_REFERENCIA>=TO_DATE('%s', 'yyyy/mm/dd') "
                            + "AND CXA_CD_AGENCIA=%s AND CXA_CD_PAB=%s AND CXA_ID_CAIXA=20%s;\n\n"
                            + "UPDATE USR_IBANKING.TB_CXA_CAIXA SET CXA_VL_SALDO_ABERTURA=CXA_VL_SALDO_ABERTURA+%d"
                            + "\n\tWHERE CXA_DT_REFERENCIA>=TO_DATE('%s', 'yyyy/mm/dd') "
                            + "AND CXA_CD_AGENCIA=%s AND CXA_CD_PAB=%s AND CXA_ID_CAIXA=20%s;\n\n"
                            + "UPDATE USR_IBANKING.TB_ACM_ACUMULADOR SET ACM_VL_DINHEIRO=ACM_VL_DINHEIRO-%d"
                            + "\n\tWHERE CXA_DT_REFERENCIA=TO_DATE('%s', 'yyyy/mm/dd') "
                            + "AND CXA_CD_AGENCIA=%s AND CXA_CD_PAB=%s AND CXA_ID_CAIXA=20%s AND TAC_CD_ACUMULADOR='RCX';\n\n",
                            r.getValorAlteracao(), r.getData().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
                            r.getAg(), r.getPab(), r.getCx(),
                            r.getValorAlteracao(), r.getData().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
                            r.getAg(), r.getPab(), r.getCx(),
                            r.getValorAlteracao(), r.getData().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
                            r.getAg(), r.getPab(), r.getCx());
        } else {
            script = script
                    + String.format("UPDATE USR_IBANKING.TB_CXA_CAIXA SET CXA_VL_SALDO=CXA_VL_SALDO-%d"
                            + "\n\tWHERE CXA_DT_REFERENCIA>=TO_DATE('%s', 'yyyy/mm/dd') "
                            + "AND CXA_CD_AGENCIA=%s AND CXA_CD_PAB=%s AND CXA_ID_CAIXA=20%s;\n\n"
                            + "UPDATE USR_IBANKING.TB_CXA_CAIXA SET CXA_VL_SALDO_ABERTURA=CXA_VL_SALDO_ABERTURA-%d"
                            + "\n\tWHERE CXA_DT_REFERENCIA>=TO_DATE('%s', 'yyyy/mm/dd') "
                            + "AND CXA_CD_AGENCIA=%s AND CXA_CD_PAB=%s AND CXA_ID_CAIXA=20%s;\n\n"
                            + "UPDATE USR_IBANKING.TB_ACM_ACUMULADOR SET ACM_VL_DINHEIRO=ACM_VL_DINHEIRO+%d"
                            + "\n\tWHERE CXA_DT_REFERENCIA=TO_DATE('%s', 'yyyy/mm/dd') "
                            + "AND CXA_CD_AGENCIA=%s AND CXA_CD_PAB=%s AND CXA_ID_CAIXA=20%s AND TAC_CD_ACUMULADOR='RCX';\n\n",
                            r.getValorAlteracao() * -1, r.getData().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
                            r.getAg(), r.getPab(), r.getCx(),
                            r.getValorAlteracao() * -1, r.getData().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
                            r.getAg(), r.getPab(), r.getCx(),
                            r.getValorAlteracao() * -1, r.getData().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
                            r.getAg(), r.getPab(), r.getCx());
        }
        return script;
    }

    public String criarScript(String caminho) {
        String script = "--SCRIPT BY AutScript v.1.0.0.0\n--GERADO AUTOMATICAMENTE EM: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "\n\n";
        for (Recolhimento r : rec_diferenca) {
            script = script + criarScript(r);
        }
        script = script + "\nCOMMIT;";
        String nomeArq = "script_AG0" + ag + pab + "-H" + cx + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy_HH.mm.ss")) + ".sql";
        String diretorio = caminho + "\\" + nomeArq;
        File file = new File(diretorio);

        try {
            file.createNewFile();
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(script);
            }
            return nomeArq;
        } catch (IOException e) {
            // TODO Bloco catch gerado automaticamente

        }
        return null;
    }

    public String lerBob(String caminho) {
        File dir = new File(caminho);
        File[] foundFiles;
        foundFiles = dir.listFiles((File dir1, String name) -> (name.startsWith("Bobina") || name.startsWith("bobina"))
                && name.endsWith(".txt"));
        if (foundFiles == null) {
            return "Caminho Inválido! Favor verificar se existem bobinas no diretório informado.";
        }
        for (File file : foundFiles) {
            getRecolhimentos(file);
        }
        if (recolhimentos.isEmpty()) {
            return "Não foi encontrar recolhimentos na bobina.\n"
                    + "Favor verificar se os recolhimentos estão no formato padrão.";
        }
        Collections.sort(recolhimentos);
        addToDif();

        return getRecolhimentosToString();
    }

}
