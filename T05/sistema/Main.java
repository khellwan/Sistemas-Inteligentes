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
        Model model = new Model(5, 9);
        model.labir.porParedeVertical(3, 4, 1);
        model.labir.porParedeVertical(1, 3, 3);
        model.labir.porParedeVertical(1, 3, 4);
        model.labir.porParedeVertical(1, 3, 5);
        model.labir.porParedeVertical(2, 2, 6);
        model.labir.porParedeHorizontal(0, 1, 1);
        model.labir.porParedeHorizontal(7, 8, 0);
        
        // seta a posição inicial do agente no ambiente - nao interfere no 
        // raciocinio do agente, somente no amibente simulado
        model.setPos(4, 0);
        model.setObj(2, 8);
        
        // Cria um agente
        Agente ag = new Agente(model);
        
        // Ciclo de execucao do sistema
        // desenha labirinto
        model.desenhar(); 
        
        // agente escolhe proxima açao e a executa no ambiente (modificando
        // o estado do labirinto porque ocupa passa a ocupar nova posicao)
        while(ag.getVetCaminhos().size() <= 6){
            ag.redefineAgente();
            System.out.println("\n*** Inicio do ciclo de raciocinio do agente ***\n");
            ag.execucao++;
            while (ag.deliberar() != -1) {  
                model.desenhar(); 
            }
            System.out.println("\n***** Caminho = " + ag.busca.getCaminho());
            System.out.println("\n***** Razão de Competitividade = " + ag.getRazao());
            System.out.println("\n***** Número de Execuções = " + ag.getExecucao());
            System.out.println("\n*_*_*_*_* Caminhos ótimos encontrados = " + ag.getVetCaminhos().size());
        }
        System.out.println("\n*_*_*_*_* Caminhos ótimos encontrados = " + ag.getVetCaminhos().size());
        for (int i = 0; i < ag.getVetCaminhos().size()-1; i++)
            System.out.println("\n Caminho " + (i+1) + " : " + ag.getVetCaminhos().get(i));
    }
}
