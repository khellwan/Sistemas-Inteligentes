/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistema;

import java.util.Random;
import problema.*;

/**
 *
 * @author tacla
 */
public class Agente {
    
    private static final int MAX_EXECUCOES = 1000; 
    /* Tamanho da população deve ser par, pois o cruzamento é em pares */
    private static final int TAM_POP = 4;
    private static final int MAX_GERACOES = 1000;
    private static final float PROB_CROSSOVER = (float) 0.8;
    private static final float PROB_MUTACAO = (float) 0.05;
    
    /* População stuff */
    private final Mochila pais_e_filhos[] = new Mochila[2*TAM_POP];
    private final int[] fitness = new int[TAM_POP];
    private static final int PENALIZACAO = 1; // 0 -> penaliza , 1-> repara
    
    Agente(){
        for (int i = 0; i < TAM_POP; i++) {
            pais_e_filhos[i] = new Mochila();
            pais_e_filhos[i].encherMochila();
        }
    }
    
    public static int[] selecionarPorRoleta(int fitness[], int qtdCromossomos) {
        int selecao[] = new int[qtdCromossomos];
        float fitnessRel[] = new float[fitness.length];
        float soma = 0;
        Random gerador = new Random();
        float r;
        int j, i;

        for (i = 0; i < fitness.length; i++) {
           soma += (float) fitness[i];
           selecao[i] = 0;
        }
        for (i = 0; i < fitness.length; i++) {
                fitnessRel[i] = (float) fitness[i] / (float) soma;
        }
        for (i = 0; i < qtdCromossomos; i++) {
            float acumulado = 0;
            r = gerador.nextFloat(); // gera um número (float) entre 0.0 e 1.0
            for (j = 0; j < qtdCromossomos; j++){
                acumulado += fitnessRel[j];
                if (acumulado >= r){
                    selecao[i] = j;
                    break;
                }
            }
            if (j == qtdCromossomos){
                selecao[i] = j-1;
            }
        }
        return selecao;
    }
    
    public void crossover (Mochila cromo_1, Mochila cromo_2, float probCross){
        Random gerador = new Random();
        int pontoCrossover;
        Mochila aux = Mochila.clonarMochila(cromo_2);
        
        if(gerador.nextFloat() <= probCross){
            pontoCrossover = gerador.nextInt(Mochila.QTD_ITENS_TOTAL);
            
            //cromo_1 -> aux
            for(int i = pontoCrossover; i < Mochila.QTD_ITENS_TOTAL; i++){
                if(cromo_1.getToNumTo()[i])
                    aux.colocarItem(i);
                else
                    aux.retirarItem(i);
            }
            
            // cromo_2 -> cromo_1
            for(int i = pontoCrossover; i < Mochila.QTD_ITENS_TOTAL; i++){
                if(cromo_2.getToNumTo()[i])
                    cromo_1.colocarItem(i);
                else
                    cromo_1.retirarItem(i);
            }
            
            // aux -> cromo_2
            for(int i = pontoCrossover; i < Mochila.QTD_ITENS_TOTAL; i++){
                if(aux.getToNumTo()[i])
                    cromo_2.colocarItem(i);
                else
                    cromo_2.retirarItem(i);
            }
        }
    }
    
    
    public static void mutacao(Mochila bag, float probMut){
        Random gerador = new Random();
        
        for(int i = 0; i < Mochila.QTD_ITENS_TOTAL; i++){
            if(gerador.nextFloat() <= probMut){
                if(bag.getToNumTo()[i])
                    bag.retirarItem(i);
                else
                    bag.colocarItem(i);
            }
        }
    }

    // Esquerda = começo. Direita = fim.
    public static void quickSort(Mochila v[], int esquerda, int direita) {
        int esq = esquerda;
        int dir = direita;
        Mochila troca = new Mochila();
        Mochila pivo = v[(esq + dir) / 2];
        while (esq <= dir) {
                while (v[esq].valorAtual > pivo.valorAtual) {
                    esq++;
                }
                while (v[dir].valorAtual < pivo.valorAtual) {
                    dir--;
                }
                if (esq <= dir) {
                        troca = v[esq];
                        v[esq] = v[dir];
                        v[dir] = troca;
                        esq++;
                        dir--;
                }
        }
        if (dir > esquerda)
                quickSort(v, esquerda, dir);
        if (esq < direita)
                quickSort(v, esq, direita);
    }
    
    public Mochila executar(){
        int geracao = 0;
        int[] selecao;
        selecao = new int[TAM_POP];
        int melhorFitness = 0;
        Mochila bestBag = null;
        
        while(geracao < MAX_GERACOES){
//            for(int i = 0; i < TAM_POP; i++){
//                System.out.printf("(valor:" + pais_e_filhos[i].getValorAtual() + " peso:"+ pais_e_filhos[i].getPesoAtual()+"), ");
//            }
//            System.out.printf("\nComeço\n ");
            
            for(int i = 0; i < TAM_POP; i++)
                this.fitness[i] = pais_e_filhos[i].getValorAtual();

            selecao = selecionarPorRoleta(this.fitness, TAM_POP);
            
            // Clona os pais para chamar de filhos
            for (int i = 0; i < TAM_POP; i++){
                pais_e_filhos[TAM_POP+i] = Mochila.clonarMochila(pais_e_filhos[selecao[i]]);
            }
//            for(int i = 0; i < 2*TAM_POP; i++){
//                System.out.printf("" + pais_e_filhos[i].getValorAtual() + ", ");
//            }
//            System.out.printf("\n ");
            // Realiza o cruzamento entre filhos
            for(int i = TAM_POP; i < 2*TAM_POP-1; i+=2){
                crossover(pais_e_filhos[i], pais_e_filhos[i+1], Agente.PROB_CROSSOVER);
                mutacao(pais_e_filhos[i], Agente.PROB_MUTACAO);
                mutacao(pais_e_filhos[i+1], Agente.PROB_MUTACAO);
            }   
            
//            System.out.printf("\n Antes: \n");
//            
//            for(int i = 0; i < 2*TAM_POP; i++)
//                System.out.printf("" + pais_e_filhos[i].getValorAtual() + ", ");
            
            quickSort(pais_e_filhos, 0, 2*TAM_POP-1);
//            System.out.printf("\n Depois: \n");
//            
//            for(int i = 0; i < 2*TAM_POP; i++)
//                System.out.printf("" + pais_e_filhos[i].getValorAtual() + ", ");
            for(int i = 1; i < 2*TAM_POP; i++){
                pais_e_filhos[i].calcularFitness(PENALIZACAO);
            }
            int fit = pais_e_filhos[0].calcularFitness(PENALIZACAO);
            if( fit > melhorFitness)
            {
                melhorFitness = fit;
                bestBag = pais_e_filhos[0];
            }
            geracao++;
//            for(int i = 0; i < 2*TAM_POP; i++){
//                System.out.printf("(valor:" + pais_e_filhos[i].getValorAtual() + " peso:"+ pais_e_filhos[i].getPesoAtual()+"), ");
//            }
//            System.out.printf("\nFinal\n ");
        }
        return(bestBag);
    }
    
    public static void main(String args[]) {
        Mochila best;
        int exec = 0;
        Mochila melhor_valor = new Mochila();
//        Mochila teste = new Mochila();
        System.out.printf("Qtd,  Peso, Valor\n");
        while (exec < MAX_EXECUCOES){
            Agente zerozerosete = new Agente();
            best = zerozerosete.executar();
            if (best != null) {
                best.imprimirMochila();
                if (best.getValorAtual() > melhor_valor.getValorAtual())
                    melhor_valor = best;
            }
            exec++;
        } 
        System.out.println("\n ----- Melhor mochila:\n");
        melhor_valor.imprimirMochila();
//        teste = Mochila.clonarMochila(melhor_valor);
//        System.out.println("\n ----- teste mochila:\n");
//        teste.imprimirMochila();
    }
}
