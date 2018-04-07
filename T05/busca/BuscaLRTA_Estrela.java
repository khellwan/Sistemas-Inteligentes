/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package busca;

import arvore.TreeNode;
import arvore.fnComparator;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import sistema.Agente;
import problema.*;

/**
 *
 * @author THIAGO
 */
public class BuscaLRTA_Estrela {
 
    private final TreeNode raiz;
    private TreeNode noAtual;
    private final Agente agnt;
    private final PriorityQueue fronteira;
    float Hn[][];
    
    public BuscaLRTA_Estrela(Agente agnt){
        int i, j;
        this.agnt = agnt;
        this.raiz = new TreeNode();
        this.raiz.setState(this.agnt.sensorPosicao());
        this.raiz.setGnHn(0, 0);
        this.noAtual = raiz;
        fnComparator comparador = new fnComparator();
        this.fronteira = new PriorityQueue(comparador);
        /*  Define uma matriz com as heurísticas */
        Hn = new float [this.agnt.getProblem().crencaLabir.getMaxLin()][this.agnt.getProblem().crencaLabir.getMaxCol()];
        for (i = 0; i < this.agnt.getProblem().crencaLabir.getMaxLin(); i++){
            for (j = 0; j < this.agnt.getProblem().crencaLabir.getMaxCol(); j++){
                this.Hn[i][j] = (float)  Math.abs(j - this.agnt.getProblem().estObj.getCol()); // Distância horizontal
            }
        }
        
    }
    
    private void AtualizarCustos(){
        /* Variáveis */
        int proxAcao;
        int [] acoesPossiveis;
        TreeNode noVizinho;
        Estado estadoVizinho;
        
        /* Define o conjunto de ações possíveis */
        acoesPossiveis = this.agnt.getProblem().acoesPossiveis(noAtual.getState());

        /* Adiciona os nós vizinhos na fronteira */ 
        for (proxAcao = 0; proxAcao < acoesPossiveis.length; proxAcao++){

            // Se a ação é possível, adiciona na fronteira
            if (acoesPossiveis[proxAcao] != -1){   
                estadoVizinho = this.agnt.getProblem().suc(noAtual.getState(), proxAcao);
                noVizinho = noAtual.addChild();
                noVizinho.setState(estadoVizinho);
                noVizinho.setAction(proxAcao);
                noVizinho.setGnHn(this.agnt.getProblem().obterCustoAcao(noAtual.getState(), proxAcao, estadoVizinho) + noAtual.getGn(), Hn[estadoVizinho.getLin()][estadoVizinho.getCol()]);
                fronteira.add(noVizinho);
                //System.out.println("\nCusto (heuristica) para ir na direção " + proxAcao + " é de " + noVizinho.getHn());
            }    
        }
    }
    
    public int EscolheAcao(){
        /* Variáveis */
        int proxAcao = -1;
        float menorCusto = Float.POSITIVE_INFINITY; // Infinito
        TreeNode proxNo;
        
        /* Atualiza os custos das fronteiras */
        AtualizarCustos();
        
        /* Escolhe a fronteira de menor F(n) para ser o próximo estado */
        while(!fronteira.isEmpty()) {
            proxNo = (TreeNode) fronteira.remove();
            if (proxNo.getFn() < menorCusto){
                menorCusto = proxNo.getFn();
                proxAcao = proxNo.getAction();
                this.noAtual = proxNo;
            }
        }
        
        /* Atualiza o H(n) do nó em que o agente está com o menor F(n) das fronteiras se não for o estado objetivo*/
        if (!this.agnt.getProblem().testeObjetivo(this.agnt.sensorPosicao())){
            this.Hn[this.agnt.sensorPosicao().getLin()][this.agnt.sensorPosicao().getCol()] = menorCusto;
        }
        
        return proxAcao;
    }
    
    public void PrintarArvore() {
        raiz.printSubTree();
    }
    public void RedefineBusca(){
        this.noAtual = this.raiz;
    }
}
