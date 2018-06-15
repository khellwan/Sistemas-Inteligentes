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
 * @author EVERTON
 */
public class BuscaInformada implements Busca{
    private final TreeNode raiz;
    private final Agente agnt;
    private final boolean h;
    
    public BuscaInformada(Agente agnt, boolean h){
        this.agnt = agnt;
        this.raiz = new TreeNode();
        this.raiz.setState(this.agnt.sensorPosicao());
        this.raiz.setGnHn(0, 0);
        this.h = h;
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
                        float hn;
                        if (h){
                            hn = (float) ( Math.sqrt(Math.pow(noAtual.getState().getCol() - this.agnt.getProblem().estObj.getCol(), 2) +
                            Math.pow(noAtual.getState().getLin() - this.agnt.getProblem().estObj.getLin(), 2))); //distância pitágoras * custo de andar na diagonal
                        }else{
                            hn = (float) Math.abs(noAtual.getState().getLin() - this.agnt.getProblem().estObj.getLin()); //distância vertical
                        }
                        filho.setGnHn(this.agnt.getProblem().obterCustoAcao(noAtual.getState(), i, proxEstado) + noAtual.getGn(), hn);
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
        
        System.out.println("numero de iteracoes no A*: ");
        System.out.println(iteracoes);
        System.out.println("numero de nós explorados na A*: ");
        System.out.println(estadosVisitados.size());
        System.out.println("numero de nós gerados na A*: ");
        System.out.println(count);
        
        return plano;
    }
    
    @Override
    public void PrintarArvore() {
        raiz.printSubTree();
    }   
}
