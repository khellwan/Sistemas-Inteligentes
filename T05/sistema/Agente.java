package sistema;

import ambiente.*;
import problema.*;
import comuns.*;
import static comuns.PontosCardeais.*;
import busca.*;
/**
 *
 * @author tacla
 */
public class Agente implements PontosCardeais {
    /* referência ao ambiente para poder atuar no mesmo*/
    Model model;
    Problema prob;
    Estado estAtu; // guarda o estado atual (posição atual do agente)
    BuscaLRTA_Estrela busca;
    double razao;
    double custo;
    static int ct = -1;
    int execucao = 0;
           
    public Agente(Model m) {
        this.model = m;
        prob = new Problema();
        prob.criarLabirinto(5, 9);

        prob.crencaLabir.porParedeVertical(3, 4, 1);
        prob.crencaLabir.porParedeVertical(1, 3, 3);
        prob.crencaLabir.porParedeVertical(1, 3, 4);
        prob.crencaLabir.porParedeVertical(1, 3, 5);
        prob.crencaLabir.porParedeVertical(2, 2, 6);
        prob.crencaLabir.porParedeHorizontal(0, 1, 1);
        prob.crencaLabir.porParedeHorizontal(7, 8, 0);
        
        // Estado inicial, objetivo e atual
        prob.defEstIni(4, 0);
        prob.defEstObj(2, 8);
        this.estAtu = prob.estIni;
        this.custo = 0;
        
        // Busca
        this.busca = new BuscaLRTA_Estrela(this);
        this.razao = 50;
    }
    
    /**Escolhe qual ação (UMA E SOMENTE UMA) será executada em um ciclo de raciocínio
     * @return 1 enquanto o plano não acabar; -1 quando acabar
     */
    public int deliberar() {
        //ct++;
        int ap[];
        ap = prob.acoesPossiveis(estAtu);
        int proxAcao = busca.EscolheAcao();
        
        // nao atingiu objetivo e ha acoesPossiveis a serem executadas no plano
        if (!prob.testeObjetivo(estAtu)) {
           System.out.println("estado atual: " + estAtu.getLin() + "," + estAtu.getCol());
           System.out.print("açoes possiveis: {");
           for (int i=0;i<ap.length;i++) {
               if (ap[i]!=-1)
                   System.out.print(acao[i]+" ");
           }

           executarIr(proxAcao);
           
           // atualiza custo
           if (proxAcao % 2 == 0 ) // acoes pares = N, L, S, O
               custo = custo + 1;
           else
               custo = custo + 1.5;
           
           this.razao = custo/12; // 12 é o custo ótimo
            //System.out.println("}\nct = "+ ct + " de " + (plan.length-1) + " ação escolhida=" + acao[plan[ct]]);
           System.out.println("custo ate o momento: " + custo);
           System.out.println("**************************\n\n");
           
           // atualiza estado atual - sabendo que o ambiente eh deterministico
           estAtu = prob.suc(estAtu, proxAcao);
                      
        }
        else
            return (-1);
        
        return 1;
    }
    
    /**Funciona como um driver ou um atuador: envia o comando para
     * agente físico ou simulado (no nosso caso, simulado)
     * @param direcao N NE S SE ...
     * @return 1 se ok ou -1 se falha
     */
    public int executarIr(int direcao) {
        model.ir(direcao);
        return 1; 
    }   
    
    // Sensor
        public Estado sensorPosicao() {
        int pos[];
        pos = model.lerPos();
        return new Estado(pos[0], pos[1]);
    }
        
     public Problema getProblem(){
         return this.prob;
     }
     
     public double getRazao(){
         return this.razao;
     }
     
     public double getExecucao(){
         return this.execucao;
     }
     
     public void redefineAgente(){
        this.estAtu = prob.estIni;
        this.custo = 0;
        this.model.setPos(4, 0);
        this.model.setObj(2, 8);
        busca.RedefineBusca();
     }
}
    

