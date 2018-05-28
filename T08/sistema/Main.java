/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistema;

import ambiente.*;

/**
 *
 * @author tacla
 */
public class Main {
    public static void main(String args[]) {
        

        // Cria o ambiente (modelo) = labirinto com suas paredes
        Model model = new Model(9, 9);
        model.labir.porParedeVertical(6, 8, 1);
        model.labir.porParedeVertical(6, 7, 4);
        model.labir.porParedeVertical(5, 6, 5);
        model.labir.porParedeVertical(5, 7, 7);
        model.labir.porParedeHorizontal(0, 1, 0);
        model.labir.porParedeHorizontal(4, 7, 0);
        model.labir.porParedeHorizontal(0, 0, 1);
        model.labir.porParedeHorizontal(3, 5, 2);
        model.labir.porParedeHorizontal(3, 6, 3);
        model.labir.porParedeHorizontal(2, 2, 5);
        model.labir.porParedeHorizontal(2, 2, 8);
        
        // seta a posição inicial do agente no ambiente - nao interfere no 
        // raciocinio do agente, somente no amibente simulado
        model.setPos(8, 0);
        model.setObj(2, 6);
        
        // Cria um agente
        Agente ag = new Agente(model);
        
        // Ciclo de execucao do sistema
        // desenha labirinto
        model.desenhar(); 
        
        // agente escolhe proxima açao e a executa no ambiente (modificando
        // o estado do labirinto porque ocupa passa a ocupar nova posicao)
        while(ag.getVetCaminhos().size() <= 7){
            ag.redefineAgente();
//            System.out.println("\n*** Inicio do ciclo de raciocinio do agente ***\n");
            ag.execucao++;
            while (ag.deliberar(true,false) != -1) {  //1º parametro -> comer é aleatorio ou não, 2º parametro -> ir pelo caminho ótimo
//                model.desenhar(); 
            }
//            System.out.println("\n***** Caminho = " + ag.busca.getCaminho());
//            System.out.println("\n***** Razão de Competitividade = " + ag.getRazao());
//            System.out.println("\n***** Número de Execuções = " + ag.getExecucao());
            //System.out.println("\n*_*_*_*_* Caminhos ótimos encontrados = " + (ag.getVetCaminhos().size()-1));
        }
        System.out.println("\n*_*_*_*_* Caminhos ótimos encontrados = " + (ag.getVetCaminhos().size()-1));
        for (int i = 0; i < ag.getVetCaminhos().size()-1; i++)
            System.out.println("\n Caminho " + (i+1) + " : " + ag.getVetCaminhos().get(i));
        System.out.println("\n*** 100 execuções aleatorias ***\n");
        for (int i = 0; i < 100; i++){
            ag.redefineAgente();
//            System.out.println("\n*** Inicio do ciclo de raciocinio do agente ***\n");
            ag.execucao++;
            while (ag.deliberar(true,true) != -1) {  //1º parametro -> comer é aleatorio ou não, 2º parametro -> ir pelo caminho ótimo
//                model.desenhar(); 
            }
//            System.out.println("\n***** rand Razão de Competitividade = " + ag.getRazao());
//            System.out.println("\n***** Número de Execuções = " + ag.getExecucao());
        }
        System.out.println("\n*** 100 execuções com algoritmo de decisao ***\n");
        for (int i = 0; i < 100; i++){
            ag.redefineAgente();
//            System.out.println("\n*** Inicio do ciclo de raciocinio do agente ***\n");
            ag.execucao++;
            while (ag.deliberar(false,true) != -1) {  //1º parametro -> comer é aleatorio ou não, 2º parametro -> ir pelo caminho ótimo
//                model.desenhar(); 
            }
//            System.out.println("\n***** alg Razão de Competitividade = " + ag.getRazao());
//            System.out.println("\n***** Número de Execuções = " + ag.getExecucao());
        }
    }
}
