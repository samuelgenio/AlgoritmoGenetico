/*
 * Copyright (C) 2020 Asconn
 *
 * This file is part of AlgoritmoGenetico.
 * AlgoritmoGenetico is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * AlgoritmoGenetico is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>
 */
package br.com.samuka.algoritmogenetico.classes;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author 'Samuel José Eugênio - https://github.com/samuelgenio'
 */
public class Genetica {

    private final int QTD_CELULA = 36;

    /**
     * Quantidade de gerações.
     */
    private int qtdGeracao = 10;

    /**
     * Quantidade de individuos da população.
     */
    private int qtdGenotipos = 12;

    /**
     * Ponto de corte
     */
    private int indexCut = 12;

    /**
     * Genotipos utilizados.
     */
    Integer[][] genotipos;

    /**
     * Fenotipos dos genotipos atuais.
     */
    Integer[] fenotipos;

    File file = new File("result.txt");

    FileWriter writer;

    private int geracaoAtual;

    private boolean isElitismo;

    List<Integer> listIndexesRoleta;

    private double qtdIndividuosMutacao;

    /**
     *
     * @param qtdGenotipos Quantidade de indivíduos que a população terá.
     * @param qtdGeracao Quantidade de derações serão processadas.
     * @param isElitismo Caso true, método aplicado será Elitismo, false para
     * Proporcional a Aptidão
     * @param perMutacao Percentual de mutação de indivíduos de cada geração.
     */
    public Genetica(int qtdGenotipos, int qtdGeracao, boolean isElitismo, double perMutacao) {
        this.qtdGenotipos = qtdGenotipos;
        this.isElitismo = isElitismo;
        this.qtdIndividuosMutacao = qtdGenotipos * perMutacao / 100;
        if (qtdGeracao != -1) {
            this.qtdGeracao = qtdGeracao;
        }
        try {
            writer = new FileWriter(file);
        } catch (IOException ex) {
        }

    }

    /**
     * Executa o processo da criação da geração.
     */
    public void execute() {

        generatePopulation();

        geracaoAtual = 0;

        while (geracaoAtual < qtdGeracao - 1) {

            nextGeneration();

            geracaoAtual++;
        }

        calculateFenotipos();

        int i = 0;

        try {

            String resultLine = "";

            for (Integer[] genotipo : genotipos) {
                resultLine += (String.valueOf(fenotipos[i]).length() == 1) ? "0" + fenotipos[i] : fenotipos[i];

                if (i + 1 < genotipos.length) {
                    resultLine += " - ";
                }

                i++;
            }

            writer.write((geracaoAtual + 1) + "° Geração -> ");
            writer.write(resultLine);
            writer.flush();
        } catch (IOException ex) {
        }

    }

    private void nextGeneration() {

        calculateFenotipos();

        QuickSort.genotipo = genotipos;

        int i = 0;

        try {

            String resultLine = "";

            for (Integer[] genotipo : genotipos) {

                resultLine += (String.valueOf(fenotipos[i]).length() == 1) ? "0" + fenotipos[i] : fenotipos[i];

                if (i + 1 < genotipos.length) {
                    resultLine += " - ";
                }

                i++;
            }

            writer.write((geracaoAtual + 1) + "º Geração -> ");
            writer.write(resultLine + "\r\n");
        } catch (IOException ex) {
        }

        QuickSort.order(fenotipos, 0, fenotipos.length - 1);

        genotipos = QuickSort.genotipo;

        Integer[][] nextGeneration = new Integer[qtdGenotipos / 2][QTD_CELULA];

        if (isElitismo) {
            getGenotiposElitismo(nextGeneration);
        } else {
            getGenotiposProporcionalAptidao(nextGeneration);
        }

        genotipos = new Integer[genotipos.length][QTD_CELULA];

        double qtdMutacaoAtual = qtdIndividuosMutacao;

        i = 0;
        int indexGenotiposAdded = 0;
        while (i < nextGeneration.length) {

            boolean isMutacao = false;

            if (qtdMutacaoAtual > 0) {

                if (qtdMutacaoAtual < 1) {
                    isMutacao = Math.random() * 1 < qtdMutacaoAtual;
                    qtdMutacaoAtual = isMutacao ? - 1 : -0;
                } else {
                    isMutacao = true;
                    qtdMutacaoAtual--;
                }

            }

            Integer[][] sons = getSon(nextGeneration[i], nextGeneration[i + 1], isMutacao);

            genotipos[indexGenotiposAdded++] = nextGeneration[i];
            genotipos[indexGenotiposAdded++] = nextGeneration[i + 1];
            genotipos[indexGenotiposAdded++] = sons[0];
            genotipos[indexGenotiposAdded++] = sons[1];

            i = i + 2;
        }
    }

    /**
     * Produz a próxima geração.
     *
     * @param ancient1 Ancestral 1
     * @param ancient2 Ancestral 2
     * @param isMutacao Indica se os filhos sofreram mutação.
     * @return Integer[][] com os dois filhos gerados
     */
    private Integer[][] getSon(Integer[] ancient1, Integer[] ancient2, boolean isMutacao) {

        Integer[] son1 = new Integer[QTD_CELULA];
        Integer[] son2 = new Integer[QTD_CELULA];

        int localCut = indexCut;

        int j = 0;
        while (j < ancient1.length) {

            if (j % 12 == 0) {
                localCut = localCut * 2;
                Integer[] ancientTroca = ancient1.clone();
                ancient1 = ancient2.clone();
                ancient2 = ancientTroca.clone();
            }
            son1[j] = ancient1[j];
            son2[j] = ancient2[j];
            j++;
        }

        if (isMutacao) {

            int index = new Random().nextInt(son1.length - 1) + 1;

            if (Math.random() * 1 > 0.5) {
                son1[index] = son1[index] > 0 ? 0 : 1;
            } else {
                son2[index] = son2[index] > 0 ? 0 : 1;
            }
        }

        return new Integer[][]{son1, son2};
    }

    /**
     * Obtém somente os genotipos mais fortes.
     */
    private void getGenotiposElitismo(Integer[][] nextGeneration) {

        int half = genotipos.length / 2;

        int count = 0;
        while (half > 0) {
            nextGeneration[count] = genotipos[half--];
            count++;
        }
    }

    /**
     * Obtém os genotipos por meio do método da roleta.
     */
    private void getGenotiposProporcionalAptidao(Integer[][] nextGeneration) {

        int half = genotipos.length / 2;

        listIndexesRoleta = new ArrayList<>();

        int count = 0;
        while (half > count) {

            int index = getIndexRoleta();
            nextGeneration[count] = genotipos[index];
            count++;
        }

    }

    private int getIndexRoleta() {

        Integer retorno = new Random().nextInt(genotipos.length - 1) + 1;

        if (listIndexesRoleta.contains(retorno)) {
            return getIndexRoleta();
        }

        listIndexesRoleta.add(retorno);

        return retorno;
    }

    /**
     * Calcula os fenotipos dos genotipos.
     */
    private void calculateFenotipos() {

        fenotipos = new Integer[genotipos.length];

        int i = 0;
        for (Integer[] genotipo : genotipos) {

            int fenotipo = 9;

            fenotipo += genotipo[1] * genotipo[4];
            fenotipo -= genotipo[22] * genotipo[13];
            fenotipo += genotipo[23] * genotipo[3];
            fenotipo -= genotipo[20] * genotipo[9];
            fenotipo += genotipo[35] * genotipo[14];
            fenotipo -= genotipo[10] * genotipo[25];
            fenotipo += genotipo[15] * genotipo[0];
            fenotipo += genotipo[2] * genotipo[32];
            fenotipo += genotipo[27] * genotipo[18];
            fenotipo += genotipo[11] * genotipo[33];
            fenotipo -= genotipo[30] * genotipo[31];
            fenotipo -= genotipo[21] * genotipo[24];
            fenotipo += genotipo[34] * genotipo[26];
            fenotipo -= genotipo[28] * genotipo[6];
            fenotipo += genotipo[7] * genotipo[12];
            fenotipo -= genotipo[5] * genotipo[8];
            fenotipo += genotipo[17] * genotipo[19];
            fenotipo -= genotipo[0] * genotipo[29];
            fenotipo += genotipo[22] * genotipo[3];
            fenotipo += genotipo[20] * genotipo[14];
            fenotipo += genotipo[25] * genotipo[15];
            fenotipo += genotipo[30] * genotipo[11];
            fenotipo += genotipo[24] * genotipo[18];
            fenotipo += genotipo[6] * genotipo[7];
            fenotipo += genotipo[8] * genotipo[17];
            fenotipo += genotipo[0] * genotipo[32];

            fenotipos[i] = fenotipo;
            i++;
        }
    }

    /**
     * Cria a população.
     */
    private void generatePopulation() {

        genotipos = new Integer[qtdGenotipos][QTD_CELULA];

        int i = 0;
        while (i < qtdGenotipos) {

            genotipos[i] = new Integer[]{
                Math.random() * 1 > 0.5 ? 1 : 0,
                Math.random() * 1 > 0.5 ? 1 : 0,
                Math.random() * 1 > 0.5 ? 1 : 0,
                Math.random() * 1 > 0.5 ? 1 : 0,
                Math.random() * 1 > 0.5 ? 1 : 0,
                Math.random() * 1 > 0.5 ? 1 : 0,
                Math.random() * 1 > 0.5 ? 1 : 0,
                Math.random() * 1 > 0.5 ? 1 : 0,
                Math.random() * 1 > 0.5 ? 1 : 0,
                Math.random() * 1 > 0.5 ? 1 : 0,
                Math.random() * 1 > 0.5 ? 1 : 0,
                Math.random() * 1 > 0.5 ? 1 : 0,
                Math.random() * 1 > 0.5 ? 1 : 0,
                Math.random() * 1 > 0.5 ? 1 : 0,
                Math.random() * 1 > 0.5 ? 1 : 0,
                Math.random() * 1 > 0.5 ? 1 : 0,
                Math.random() * 1 > 0.5 ? 1 : 0,
                Math.random() * 1 > 0.5 ? 1 : 0,
                Math.random() * 1 > 0.5 ? 1 : 0,
                Math.random() * 1 > 0.5 ? 1 : 0,
                Math.random() * 1 > 0.5 ? 1 : 0,
                Math.random() * 1 > 0.5 ? 1 : 0,
                Math.random() * 1 > 0.5 ? 1 : 0,
                Math.random() * 1 > 0.5 ? 1 : 0,
                Math.random() * 1 > 0.5 ? 1 : 0,
                Math.random() * 1 > 0.5 ? 1 : 0,
                Math.random() * 1 > 0.5 ? 1 : 0,
                Math.random() * 1 > 0.5 ? 1 : 0,
                Math.random() * 1 > 0.5 ? 1 : 0,
                Math.random() * 1 > 0.5 ? 1 : 0,
                Math.random() * 1 > 0.5 ? 1 : 0,
                Math.random() * 1 > 0.5 ? 1 : 0,
                Math.random() * 1 > 0.5 ? 1 : 0,
                Math.random() * 1 > 0.5 ? 1 : 0,
                Math.random() * 1 > 0.5 ? 1 : 0,
                Math.random() * 1 > 0.5 ? 1 : 0,};
            i++;
        }
    }

}
