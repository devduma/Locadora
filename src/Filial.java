import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Filial {
    private final int numero;
    private final String nome;
    LinkedList<Carro> carrosFilial;
    LinkedList<String> listaEspera;

    public Filial(int numero, String nome) {
        this.numero = numero;
        this.nome = nome;
        carrosFilial = new LinkedList<>();
        listaEspera = new LinkedList<>();
    }

    public int getNumero() {
        return numero;
    }

    public String getNome() {
        return nome;
    }

    public String acessaPrimeiroDaFila(){
        if (!listaEspera.isEmpty())
            return listaEspera.getFirst();
        else
            return null;
    }

    public void incluiListaEspera(String nomeCliente){
        listaEspera.add(nomeCliente);
    }

    public String retiraClientelistaEspera(){
        if (!listaEspera.isEmpty())
            return listaEspera.poll();
        else
            return null;
    }

    public void mostralistaEsperaFilial(){
        if (listaEspera.isEmpty()){
            System.out.println("\tLista vazia");
        }
        else{
            listaEspera.forEach(System.out::println);
            System.out.println("\tLista de espera composta por " + listaEspera.size() + " cliente(s)");
        }
    }

    public void associaVeiculos(Carro carro){
        carrosFilial.add(carro);
    }

    public int listaVeiculosFilial(){
        System.out.println("Veículos da filial " + numero);
        System.out.println(carrosFilial.stream().map(x -> x.getMarca() + "/" + x.getModelo() +
                " R$ " + x.getValor() + (x.getPode_Alugar()?" (Disponível)":" (Alugado)")).collect(Collectors.toList()));
        return carrosFilial.size();
    }

    public List<Carro> listaVeiculosDisponiveisFilial(){
        List<Carro> carrosDisponiveis;
        carrosDisponiveis = carrosFilial.stream().filter(Carro::getPode_Alugar).collect(Collectors.toList());
        System.out.println(carrosDisponiveis.stream().map(x -> x.getMarca() + "/" + x.getModelo() +
                " R$ " + x.getValor()).collect(Collectors.toList()));
        return carrosDisponiveis;
    }

    public double valorTotalCarrosAlugados(){
        List<Carro> carrosAlugados;
        double valorTotalLocacao;
        carrosAlugados = carrosFilial.stream().filter(x-> !x.getPode_Alugar()).collect(Collectors.toList());
        valorTotalLocacao = carrosAlugados.stream().mapToDouble(Carro::getValor).sum();
        return valorTotalLocacao;
    }
}
