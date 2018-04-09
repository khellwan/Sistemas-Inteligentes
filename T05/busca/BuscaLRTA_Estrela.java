/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package busca;

import arvore.TreeNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
    private List<TreeNode> fronteira;
    private List<Integer> caminho;
    float Hn[][];
    private final static Random random = new Random(System.currentTimeMillis());;
    
    public BuscaLRTA_Estrela(Agente agnt){
        int i, j;
        this.agnt = agnt;
        this.raiz = new TreeNode();
        this.raiz.setState(this.agnt.sensorPosicao());
        this.raiz.setGnHn(0, 0);
        this.caminho = new ArrayList<>();
        this.fronteira = new ArrayList<>();
        this.noAtual = raiz;
        /*  Define uma matriz com as heurísticas */
        Hn = new float [this.agnt.getProblem().crencaLabir.getMaxLin()][this.agnt.getProblem().crencaLabir.getMaxCol()];
        for (i = 0; i < this.agnt.getProblem().crencaLabir.getMaxLin(); i++){
            for (j = 0; j < this.agnt.getProblem().crencaLabir.getMaxCol(); j++){
                //this.Hn[i][j] = (float) Math.abs(j - this.agnt.getProblem().estObj.getCol()); // Distância horizontal
                this.Hn[i][j] = (float) (Math.sqrt(Math.pow(j - this.agnt.getProblem().estObj.getCol(), 2) +
                            Math.pow(i - this.agnt.getProblem().estObj.getLin(), 2))); //distância euclidiana
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
                noVizinho.setGnHn(this.agnt.getProblem().obterCustoAcao(noAtual.getState(), proxAcao, estadoVizinho), Hn[estadoVizinho.getLin()][estadoVizinho.getCol()]);
                fronteira.add(noVizinho);
                System.out.println("\nCusto (heuristica) para ir na direção " + proxAcao + " é de " + noVizinho.getHn());
            }    
        }
    }
    
    public int EscolheAcao(){
        /* Variáveis */
        int proxAcao;
        float menorCusto = Float.POSITIVE_INFINITY; // Infinito
        List <TreeNode> MenorCustoList = new ArrayList<>();
        TreeNode proxNo;
        
        /* Atualiza os custos das fronteiras */
        AtualizarCustos();
        
        /* Percorre a fronteira e pega o menor custo */
        for(int i = 0; i < fronteira.size(); i++) {
            proxNo = (TreeNode) fronteira.get(i);
            if (proxNo.getFn() < menorCusto)
                menorCusto = proxNo.getFn();
        }
        
        /* Adiciona a lista de nós com menores custos os nós da fronteira que possuem o menor custo */
        for(int i = 0; i < fronteira.size(); i++) {
            proxNo = (TreeNode) fronteira.get(i);
            if (proxNo.getFn() == menorCusto){
                MenorCustoList.add(proxNo);
            }
        }
        fronteira.clear();
        this.noAtual = MenorCustoList.get(random.nextInt(MenorCustoList.size()));
        proxAcao = this.noAtual.getAction();
        
        /* Atualiza o H(n) do nó em que o agente está com o menor F(n) das fronteiras */
        this.Hn[this.agnt.sensorPosicao().getLin()][this.agnt.sensorPosicao().getCol()] = menorCusto;
        
        this.caminho.add(proxAcao);
        return proxAcao;
    }
    
    public void PrintarArvore() {
        raiz.printSubTree();
    }
    
    public List<Integer> getCaminho(){
        return this.caminho;
    }
    
    public void RedefineBusca(){
        this.noAtual = this.raiz;
        this.caminho.clear();
    }
}
