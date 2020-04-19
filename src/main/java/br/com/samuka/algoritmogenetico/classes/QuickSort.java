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

/**
 *
 * @author 'Samuel José Eugênio - https://github.com/samuelgenio'
 */
public class QuickSort {

    public static Integer[][] genotipo;
    
    public static void order(Integer[] vetor, int inicio, int fim) {
        if (inicio < fim) {
            int posicaoPivo = separar(vetor, inicio, fim);
            order(vetor, inicio, posicaoPivo - 1);
            order(vetor, posicaoPivo + 1, fim);
        }
    }

    private static int separar(Integer[] vetor, int inicio, int fim) {
        int pivo = vetor[inicio];
        Integer[] pivoGenotipo = genotipo[inicio];
        
        int i = inicio + 1, f = fim;
        while (i <= f) {
            if (vetor[i] >= pivo) {
                i++;
            } else if (pivo > vetor[f]) {
                f--;
            } else {
                int troca = vetor[i];
                vetor[i] = vetor[f];
                vetor[f] = troca;
                
                Integer[] trocaGenotipo = genotipo[i];
                genotipo[i] = genotipo[f];
                genotipo[f] = trocaGenotipo;
                
                i++;
                f--;
            }
        }
        vetor[inicio] = vetor[f];
        vetor[f] = pivo;
        
        genotipo[inicio] = genotipo[f];
        genotipo[f] = pivoGenotipo;
        
        return f;
    }

}
