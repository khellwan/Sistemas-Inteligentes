/** Representação de um problema a ser resolvido por um algoritmo de busca clássica.
 * A formulação do problema - instância desta classe - reside na 'mente' do agente.
 *
 * @author Thiago
 */
package problema;

import java.util.Random;

/**
 *
 * @author Thiago
 */
public class Mochila {
    /* Capacidade maxima da mochila */
    public static final int CAPACIDADE_MOCHILA = 113;
    /* Quantidade de itens a ser analizada */
    public static final int QTD_ITENS_TOTAL = 42;
    /* Peso de cada item */
    private final int[] pesoItem = {3,  8, 12,  2,  8,  4,  4,  5,  1,  1,  8,  6,  4,  3, 
                      3,  5,  7,  3,  5,  7,  4,  3,  7,  2,  3,  5,  4,  3,
                      7, 19, 20, 21, 11, 24, 13, 17, 18,  6, 15, 25, 12, 19};
    /* Valor de cada item */
    private final int[] valorItem = {1,  3,  1,  8,  9,  3,  2,  8,  5,  1,  1,  6,  3,  2, 
                      5,  2,  3,  8,  9,  3,  2,  4,  5,  4,  3,  1,  3,  2,
                     14, 32, 20, 19, 15, 37, 18, 13, 19, 10, 15, 40, 17, 39};
    
    private int pesoAtual ;
    private int qtdItensNaMochila;
    /* Fitness */
    public int valorAtual;
    private Random randItem = new Random();
    
    private final boolean[] toNumTo  = new boolean[QTD_ITENS_TOTAL];
    
    public Mochila(){
        this.pesoAtual = 0;
        this.qtdItensNaMochila = 0;
        this.valorAtual = 0;
        for(int i = 0; i < QTD_ITENS_TOTAL; i++){
            this.toNumTo[i] = false;
        }
    }
    
    public void imprimirMochila() {
        System.out.printf("Mochila,  peso, valor\n");
        System.out.printf("-------------------\n");
        for (int i = 0; i < QTD_ITENS_TOTAL; i++) {
            if (toNumTo[i] == true) {
                System.out.printf("item[%2d], %4d, %5d\n", i+1, pesoItem[i], valorItem[i]);
            }
        }
        System.out.printf("---------------------\n");
        System.out.printf("Mochila com %3d ITENS\n", qtdItensNaMochila);
        System.out.printf("Mochila com %3d KG\n", pesoAtual);
        System.out.printf("Mochila com %3d VALOR\n", valorAtual);
        System.out.printf("---------------------\n");

     /*   System.out.printf("%3d", qtdItensNaMochila);
        System.out.printf(",");
        System.out.printf("%3d", pesoAtual);
        System.out.printf(",");
        System.out.printf("%3d", valorAtual);
        System.out.printf("\n");*/

    }
    
    public int calcularFitness(int penalizar){
        if(this.pesoAtual <= CAPACIDADE_MOCHILA)
            return(this.valorAtual);
        else{
            switch(penalizar){
                case 0:
                    penaliza();
                    break;
                case 1:
                    repara();
                    break;
            }
            return(this.valorAtual);
        }
    }
    
    private void penaliza(){
        this.valorAtual = valorAtual/2;
    }
    
    private void repara(){
        this.randItem = new Random();
        while(this.pesoAtual > CAPACIDADE_MOCHILA){
            retirarItem(randItem.nextInt(QTD_ITENS_TOTAL));
        }
    }
    
    // Se o item não estiver na mochila, coloca
    public void colocarItem(int item){
        if(!this.toNumTo[item]){
            this.toNumTo[item] = true;
            this.valorAtual += this.valorItem[item];
            this.pesoAtual += this.pesoItem[item];
            this.qtdItensNaMochila++;
        }
    }
    
    // Se o item estiver na mochila, retira
    public void retirarItem(int item){
        if(this.toNumTo[item]){
            this.toNumTo[item] = false;
            this.valorAtual -= this.valorItem[item];
            this.pesoAtual -= this.pesoItem[item];
            this.qtdItensNaMochila--;
        }
    }
    
    public void encherMochila(){
        while(this.pesoAtual < CAPACIDADE_MOCHILA){
            colocarItem(randItem.nextInt(QTD_ITENS_TOTAL));
        }
    }
    
    public void esvaziarMochila(){
        for (int i = 0; i < QTD_ITENS_TOTAL; i++){
            retirarItem(i);
        }
    }
    
    public static Mochila clonarMochila(Mochila bag){
        Mochila dolly = new Mochila();
        for (int i = 0; i < QTD_ITENS_TOTAL; i++){
            if(bag.toNumTo[i]){
                dolly.colocarItem(i);
            }
        }
        return(dolly);
    }
    
    public int getValorAtual(){
        return(this.valorAtual);
    }
    
    public boolean[] getToNumTo(){
        return(this.toNumTo);
    }
    
    public int getPesoAtual(){
        return(this.pesoAtual);
    }
    
    public int getQtdItensAtual(){
        return(this.qtdItensNaMochila);
    }
}
