package sistema;

import ambiente.*;
import problema.*;
import comuns.*;
import static comuns.PontosCardeais.*;
import busca.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

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
    static final int VALOR_MELHOR_CAMINHO = 12;
    int execucao = 0;
    int pathNumber = 0;
    double energia;
    int step;
    int caminhoAtual = 0;
    char[] mapaFrutas[][];
    List<List<Integer>> VetCaminhos;
           
    public Agente(Model m) {
        this.model = m;
        prob = new Problema();
        prob.criarLabirinto(9, 9);

        prob.crencaLabir.porParedeVertical(6, 8, 1);
        prob.crencaLabir.porParedeVertical(6, 7, 4);
        prob.crencaLabir.porParedeVertical(5, 6, 5);
        prob.crencaLabir.porParedeVertical(5, 7, 7);
        prob.crencaLabir.porParedeHorizontal(0, 1, 0);
        prob.crencaLabir.porParedeHorizontal(4, 7, 0);
        prob.crencaLabir.porParedeHorizontal(0, 0, 1);
        prob.crencaLabir.porParedeHorizontal(3, 5, 2);
        prob.crencaLabir.porParedeHorizontal(3, 6, 3);
        prob.crencaLabir.porParedeHorizontal(2, 2, 5);
        prob.crencaLabir.porParedeHorizontal(2, 2, 8);
        
        // Estado inicial, objetivo e atual
        prob.defEstIni(8, 0);
        prob.defEstObj(2, 6);
        this.estAtu = prob.estIni;
        this.custo = 0;
        
        // Busca
        this.busca = new BuscaLRTA_Estrela(this);
        this.razao = 50;
        VetCaminhos = new ArrayList<>();
        VetCaminhos.add(new ArrayList<>());
    }
    
    /**Escolhe qual ação (UMA E SOMENTE UMA) será executada em um ciclo de raciocínio
     * @return 1 enquanto o plano não acabar; -1 quando acabar
     */
    public int deliberar(boolean random, boolean caminhoOtimo) {
        //ct++;
        int ap[];
        ap = prob.acoesPossiveis(estAtu);
        int proxAcao;
//        System.out.println("Energia: " + this.energia);
        
        // nao atingiu objetivo e ha acoesPossiveis a serem executadas no plano
        if (!prob.testeObjetivo(estAtu)) {
           if (caminhoOtimo){
//               System.out.println("\n Caminho escolhido:" + this.VetCaminhos.get(this.caminhoAtual) + "step: " + this.step);
               proxAcao = this.VetCaminhos.get(this.caminhoAtual).get(this.step);
               step++;
           }else{
               proxAcao = busca.EscolheAcao();
           }
//           System.out.println("estado atual: " + estAtu.getLin() + "," + estAtu.getCol());
//           System.out.print("açoes possiveis: {");
           for (int i=0;i<ap.length;i++) {
               if (ap[i]!=-1){
//                   System.out.print(acao[i]+" ");
               }
           }
//           System.out.println("}");
           executarIr(proxAcao);
           boolean comer = comoNumComo(random, proxAcao);
           if (comer)
               this.energia += calculaEnergia(mapaFrutas[estAtu.getLin()][estAtu.getCol()]);
           
           // atualiza custo
           if (proxAcao % 2 == 0 ){ // acoes pares = N, L, S, O
               custo = custo + 1;
               this.energia -= 1;
           }
           else{
               custo = custo + 1.5;
               this.energia -= 1.5;
           }
           
           this.razao = custo/VALOR_MELHOR_CAMINHO;
            //System.out.println("}\nct = "+ ct + " de " + (plan.length-1) + " ação escolhida=" + acao[plan[ct]]);
//           System.out.println("custo ate o momento: " + custo);
//           System.out.println("**************************\n\n");
           
           // atualiza estado atual - sabendo que o ambiente eh deterministico
           estAtu = prob.suc(estAtu, proxAcao);
                      
        }
        else{
            // Se o caminho ótimo ainda não existir no vetor de caminhos, adiciona ele:
            if(custo == VALOR_MELHOR_CAMINHO){
                if(!checkRepeatedPath(busca.getCaminho())) {
                    this.VetCaminhos.get(pathNumber).addAll(busca.getCaminho());
                    pathNumber+=1;
                    VetCaminhos.add(new ArrayList<>());
                }
            }
            if (this.energia < 0){
                this.energia -= 100;
//                System.out.println("Faltou energia");
            }
            else {
                this.energia = this.energia * (-1);
//                System.out.println("Sobrou energia");
            }
            System.out.println(this.execucao + "," + this.energia);
            return (-1);
        }
        return 1;
    }
    
    private boolean checkRepeatedPath(List<Integer> testList) {
        List<Integer> curOptimalList;
        boolean repeated;
        for(int i = 0; i < this.VetCaminhos.size(); i++){
            repeated = true;
            curOptimalList = this.VetCaminhos.get(i);
            if(curOptimalList.isEmpty()){
                continue;
            }
            for(int j = 0; j < Math.min(curOptimalList.size(), testList.size());j++){
                if(!Objects.equals(curOptimalList.get(j), testList.get(j)))
                    repeated = false;
            }
            if(repeated == true)
                return true;
        }
        return false;
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
        
    public final Problema getProblem(){
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
        this.energia = 3;
        this.model.setPos(8, 0);
        this.model.setObj(2, 6);
        // Energia
	this.energia = 3;
        this.step = 0;
        if (this.VetCaminhos.size() > 2){
            Random rand = new Random();
            this.caminhoAtual = rand.nextInt(this.VetCaminhos.size()-1); 
        }
	this.mapaFrutas = new char[this.getProblem().crencaLabir.getMaxLin()][this.getProblem().crencaLabir.getMaxCol()][5];
	geraMapaFrutas();
        
        busca.RedefineBusca();
     }
     
    public List<List<Integer>> getVetCaminhos(){
         return this.VetCaminhos;
     }
     
    private  boolean equalLists(List<Integer> one, List<Integer> two){
        if (one == null && two == null){
            return true;
        }

        if((one == null && two != null) 
          || one != null && two == null
          || one.size() != two.size()){
            return false;
        }

        //to avoid messing the order of the lists we will use a copy
        //as noted in comments by A. R. S.
        one = new ArrayList<>(one); 
        two = new ArrayList<>(two);

        //Collections.sort(one);
        //Collections.sort(two);
        return one.equals(two);
    }
    
    // Gera frutas aleatórias
    public final void geraMapaFrutas() {
	int maxLinhas = this.getProblem().crencaLabir.getMaxLin();
	int maxColunas = this.getProblem().crencaLabir.getMaxCol();
	char[] fruta = new char[5];
	char[] cores1_2_e_3 = {'R', 'G', 'B'};
	char[] cores0_e_4 = {'K', 'W'};
	Random randFruta = new Random();

	for (int i = 0; i < maxLinhas; i++) {
	    for (int j = 0; j < maxColunas; j++) {
		fruta[0] = cores0_e_4[randFruta.nextInt(2)];
		fruta[1] = cores1_2_e_3[randFruta.nextInt(3)];
		fruta[2] = cores1_2_e_3[randFruta.nextInt(3)];
		fruta[3] = cores1_2_e_3[randFruta.nextInt(3)];
		fruta[4] = cores0_e_4[randFruta.nextInt(2)];
		this.mapaFrutas[i][j] = fruta;
	    }
	}
    }
    
    /* Função que retorna a quantidade de energia de uma fruta baseada
       na árvore de decisão gerada pelo Weka */
    public int calculaEnergia (char cor[]){
        if (cor.length != 5)
            return -9999;
	
	/* Árvore */
	
	// Ramo 1
	if (cor[1] == 'R') {
	    if (cor[3] == 'R') {
		if (cor[2] == 'R')
		    return 4;
		if (cor[2] == 'G')
		    return 2;
		if (cor[2] == 'B')
		    return 2;
	    }
	    else if (cor[3] == 'G') {
		if (cor[2] == 'R')
		    return 2;
		if (cor[2] == 'G')
		    return 2;
		if (cor[2] == 'B')
		    return 0;
	    }
	    else if (cor[3] == 'B') {
		if (cor[2] == 'R')
		    return 2;
		if (cor[2] == 'G')
		    return 4;
		if (cor[2] == 'B')
		    return 2;
	    } 
	}
	
	// Ramo 2
	else if(cor[1] == 'G') {
	    if (cor[0] == 'K'){
		if (cor[3] == 'R') {
		    if (cor[2] == 'R')
			return 2;
		    if (cor[2] == 'G')
			return 2;
		    if (cor[2] == 'B')
			return 0;
		}
		else if (cor[3] == 'G') {
		    if (cor[2] == 'R')
			return 2;
		    if (cor[2] == 'G')
			return 4;
		    if (cor[2] == 'B')
			return 2;
		}
		else if (cor[3] == 'B') {
		    if (cor[2] == 'R')
			return 0;
		    if (cor[2] == 'G')
			return 2;
		    if (cor[2] == 'B')
			return 2;
		} 
	    }
	    else if (cor[0] == 'W')
		return 0;
	}
	
	// Ramo 3
	else if (cor[1] == 'B') {
	    if (cor[2] == 'R') {
		if (cor[3] == 'R')
		    return 2;
		if (cor[3] == 'G')
		    return 0;
		if (cor[3] == 'B')
		    return 2;
	    }
	    else if (cor[2] == 'G') {
		if (cor[3] == 'R')
		    return 0;
		if (cor[3] == 'G')
		    return 2;
		if (cor[3] == 'B')
		    return 2;
	    }
	    else if (cor[2] == 'B') {
		if (cor[3] == 'R')
		    return 2;
		if (cor[3] == 'G')
		    return 2;
		if (cor[3] == 'B')
		    return 4;
	    } 
	}
	
	// Qualquer outro caso
        return -9999;
    }
    
    public boolean comoNumComo (boolean random, int acao){
        if(random == true){
            Random num = new Random();
            if(num.nextInt(2) == 1){
//                System.out.println("Comeu no random");
                return true;
            }
            else{
//                System.out.println("Não Comeu no random");
                return false;
            }
        }
        int val = calculaEnergia(mapaFrutas[estAtu.getLin()][estAtu.getCol()]);
        double bar = (Math.abs(prob.estObj.getCol() - estAtu.getCol()) + Math.abs(prob.estObj.getLin() - estAtu.getLin()));
//        System.out.println("energia: " + this.energia + " delta: " + bar + "valor: " + val);
        if (acao%2 == 1){
            bar = bar - 1;
        }
        double foo =  this.energia - bar;
        if (foo < 0){
//            System.out.println("Comeu");
            return true;
        }
        else{
//            System.out.println("Não Comeu");
            return false;
        }
    }
    
}
    

