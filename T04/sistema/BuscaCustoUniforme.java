package sistema;

import arvore.TreeNode;
import arvore.fnComparator;
import problema.*;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 *
 * @author THIAGO
 */
public class BuscaCustoUniforme {
    private final TreeNode raiz;
    private final Agente agnt;
    
    public BuscaCustoUniforme(Agente agnt){
        this.agnt = agnt;
        this.raiz = new TreeNode();
        this.raiz.setState(this.agnt.sensorPosicao());
        this.raiz.setGnHn(0, 0);
    }
    
    public int[] CriarPlano(){
        int i, j;
        List<Integer> solucao = new ArrayList<>();
        int [] plano;
        TreeNode filho;
        TreeNode noAtual = raiz;
        boolean achouSolucao = false;
        boolean estadoVisitado = false;
        final List<Estado> estadosVisitados = new ArrayList<>();
        int [] acoesPossiveis = this.agnt.prob.acoesPossiveis(noAtual.getState());
        Estado proxEstado;
        fnComparator comparador = new fnComparator();
        PriorityQueue fronteira = new PriorityQueue(comparador);
        
        //estadosVisitados.add(noAtual.getState());
        fronteira.add(noAtual);

        do{
            /* Explora o nó */
            noAtual = (TreeNode) fronteira.remove();
            acoesPossiveis = this.agnt.prob.acoesPossiveis(noAtual.getState());
            if (agnt.prob.testeObjetivo(noAtual.getState()))
                achouSolucao = true;
            
            estadosVisitados.add(noAtual.getState());
            
            /* Adiciona os nós vizinhos na fronteira */ 
            for (i = 0; i < acoesPossiveis.length; i++){
                
                // Se a Ação é possível, adiciona na fronteira
                if (acoesPossiveis[i] != -1){   
                    proxEstado = this.agnt.prob.suc(noAtual.getState(), i);
                    estadoVisitado = false;
                    
                    for (j=0; j < estadosVisitados.size(); j++) {
                        if(proxEstado.igualAo(estadosVisitados.get(j)))
                            estadoVisitado = true;
                    }
                    
                    if (!estadoVisitado){
                        filho = noAtual.addChild();
                        filho.setState(proxEstado);
                        filho.setAction(i);
                        filho.setGnHn(noAtual.getGn() + this.agnt.prob.obterCustoAcao(noAtual.getState(), i, proxEstado), 0);
                        fronteira.add(filho);
                    }                        
                }    
            }
        }while(!achouSolucao);
        
        while(noAtual.getParent() != null)
        {
            solucao.add(noAtual.getAction());
            noAtual = noAtual.getParent();
        }
        
        plano = new int[solucao.size()];
        for(i  = 0; i < solucao.size(); i++)
            plano[i] = solucao.get(solucao.size()-i-1);
        
        return plano;
    }
    
    public void PrintarArvore() {
        raiz.printSubTree();
    }   
}
