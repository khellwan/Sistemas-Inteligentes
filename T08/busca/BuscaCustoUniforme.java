package busca;

import arvore.TreeNode;
import arvore.fnComparator;
import problema.*;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import sistema.Agente;

/**
 *
 * @author THIAGO
 */
public class BuscaCustoUniforme implements Busca{
    private final TreeNode raiz;
    private final Agente agnt;
    
    public BuscaCustoUniforme(Agente agnt){
        this.agnt = agnt;
        this.raiz = new TreeNode();
        this.raiz.setState(this.agnt.sensorPosicao());
        this.raiz.setGnHn(0, 0);
    }
    
    @Override
    public int[] CriarPlano(){
        int i, j;
        List<Integer> solucao = new ArrayList<>();
        int [] plano;
        TreeNode filho;
        TreeNode noAtual = raiz;
        boolean achouSolucao = false;
        boolean estadoVisitado;
        final List<Estado> estadosVisitados = new ArrayList<>();
        int [] acoesPossiveis;
        Estado proxEstado;
        fnComparator comparador = new fnComparator();
        PriorityQueue fronteira = new PriorityQueue(comparador);
        
        //estadosVisitados.add(noAtual.getState());
        fronteira.add(noAtual);
        int iteracoes = 0;
        int count = 1;

        do{
            /* Explora o nó */
            noAtual = (TreeNode) fronteira.remove();
            acoesPossiveis = this.agnt.getProblem().acoesPossiveis(noAtual.getState());
            if (agnt.getProblem().testeObjetivo(noAtual.getState()))
                achouSolucao = true;
            
            estadosVisitados.add(noAtual.getState());
            
            /* Adiciona os nós vizinhos na fronteira */ 
            for (i = 0; i < acoesPossiveis.length; i++){
                
                // Se a Ação é possível, adiciona na fronteira
                if (acoesPossiveis[i] != -1){   
                    proxEstado = this.agnt.getProblem().suc(noAtual.getState(), i);
                    estadoVisitado = false;
                    
                    for (j=0; j < estadosVisitados.size(); j++) {
                        if(proxEstado.igualAo(estadosVisitados.get(j)))
                            estadoVisitado = true;
                    }
                    
                    if (!estadoVisitado){
                        filho = noAtual.addChild();
                        filho.setState(proxEstado);
                        filho.setAction(i);
                        filho.setGnHn(this.agnt.getProblem().obterCustoAcao(noAtual.getState(), i, proxEstado) + noAtual.getGn(), 0);
                        count += 1;
                        fronteira.add(filho);
                    }                        
                }    
            }
            iteracoes += 1;
        }while(!achouSolucao);
        
        while(noAtual.getParent() != null)
        {
            solucao.add(noAtual.getAction());
            noAtual = noAtual.getParent();
        }
        
        plano = new int[solucao.size()];
        for(i  = 0; i < solucao.size(); i++)
            plano[i] = solucao.get(solucao.size()-i-1);
        
        System.out.println("numero de iteracoes na busca uniforme: ");
        System.out.println(iteracoes);
        System.out.println("numero de nós explorados na busca uniforme: ");
        System.out.println(estadosVisitados.size());
        System.out.println("numero de nós gerados na busca uniforme: ");
        System.out.println(count);
        
        return plano;
    }
    
    @Override
    public void PrintarArvore() {
        raiz.printSubTree();
    }   
}
