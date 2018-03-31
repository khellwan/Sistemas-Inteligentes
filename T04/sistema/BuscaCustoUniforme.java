package sistema;

import arvore.TreeNode;
import problema.Estado;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author THIAGO
 */
public class BuscaCustoUniforme {
    private TreeNode raiz;
    private final List<Estado> estadosVisitados = new ArrayList<>();
    private final Agente agnt;
    
    public BuscaCustoUniforme(Agente agnt){
        this.agnt = agnt;
    }
    
    public void CriarArvore(TreeNode noAtual)
    {
        int i;
        float custo;
        TreeNode filho;
        int [] acoesPossiveis = this.agnt.prob.acoesPossiveis(noAtual.getState());
        Estado proxEstado;
       
        this.estadosVisitados.add(noAtual.getState());
        
        for (i = 0; i < acoesPossiveis.length; i++){
            if (acoesPossiveis[i] != -1){   
                proxEstado = this.agnt.prob.suc(noAtual.getState(), i);
                
                if (!estadosVisitados.contains(proxEstado))
                    continue;
                    
                filho = noAtual.addChild();
                filho.setState(proxEstado);
                custo = noAtual.getGn() + this.agnt.prob.obterCustoAcao(noAtual.getState(), i, proxEstado);
                filho.setGnHn(custo, 0);
                
                this.CriarArvore(filho);
                
            }
        }
    }
    
    //public int[] getPlano(TreeNode arvore) {
        
    //}
    
    public void PrintarArvore() {
        raiz.printSubTree();
    }   
}
