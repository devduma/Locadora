public class Carro implements Comparable<Carro>{
    private final String placa;
    private final String marca;
    private final String cor;
    private final String modelo;
    private final double valor;
    private boolean pode_alugar;
    private String cliente;

    public Carro(String placa, String marca, String cor, String modelo,
                 double valor, boolean pode_alugar){
        this.placa = placa;
        this.marca = marca;
        this.cor = cor;
        this.modelo = modelo;
        this.valor = valor;
        this.pode_alugar = pode_alugar;
        cliente = "";
    }
     public String getPlaca(){
        return placa;
     }
     public String getMarca(){
        return marca;
     }
     public String getCor(){
        return cor;
     }
     public String getModelo(){
        return modelo;
     }
     public double getValor(){
        return valor;
     }
     public boolean getPode_Alugar(){
        return pode_alugar;
    }
     public void setPode_Alugar(boolean isAlugado){
        this.pode_alugar = isAlugado;
    }
    public String getCliente(){return cliente;}
    public void setCliente(String cliente){
        this.cliente = cliente;
    }

    @Override
    public int compareTo(Carro carro) {
        return Double.compare(this.valor, carro.valor);
    }
}
