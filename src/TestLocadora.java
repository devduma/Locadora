import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.*;

/**
 * Locação de Veículos numa empresa com filiais
 *
 * Os cadastros de veículos e clientes são consolidados na empresa
 * Carregue o cadastro de clientes e associe os veículos às filiais.  A partir de então, é possivel
 *      realizar as operações de Empréstimo e de Devolução de Veículos
 * O sistema possui uma função para trocar a filial corrente. As operações são realizadas para esta filial
 *
 * Quando a filial tem uma lista de espera, o primeiro carro disponível será locado para o primeiro
 * cliente na fila
 */
public class TestLocadora {
    public static Integer NUMERO_CARROS = 10;

    public static void main(String[] args) {
        Carro[] carros = new Carro[NUMERO_CARROS];
        cadastrarVeiculo(carros);
        sort(carros);

        // cadastro das filiais
        List<Filial> filiais = new ArrayList<>();
        filiais.add(new Filial(1, "Matriz"));
        filiais.add(new Filial(20, "Salvador"));
        filiais.add(new Filial(30, "Porto Alegre"));
        filiais.add(new Filial(40, "São Paulo"));

        try (Scanner ler = new Scanner(System.in)) {
            int option;
            Filial filial;
            String nomeCliente;
            Deque<String> log = new ArrayDeque<>();
            // Queue<String> listaEspera = new ArrayDeque<>();
            Queue<Cliente> clientes = new ArrayDeque<>();

            filial = lerFilial(ler, filiais); // determina a filial corrente do sistema
            do {
                option = lerOpcaoMenu(ler, filial.getNumero());
                switch (option) {

                    case 1: // Empréstimo
                        ler.nextLine();
                        List<Carro> carrosDisponiveis =  filial.listaVeiculosDisponiveisFilial();
                        nomeCliente = filial.acessaPrimeiroDaFila(); // verifica lista de espera
                        // Solicita o nome do cliente se lista de espera vazia
                        // ou se não tiver carro (acrescenta na lista de espera)
                            if (nomeCliente == null || carrosDisponiveis.size() == 0) {
                                System.out.println("\nNome do Cliente:");
                                nomeCliente = ler.nextLine();
                            }

                            if (!validarClienteCadastrado(nomeCliente, clientes)) {// cliente não cadastrado
                                System.out.println("Cliente não encontrado. Preencha o seu cadastro");
                            }
                            else {
                                if (carrosDisponiveis.size() > 0) {
                                    System.out.println(nomeCliente + ", escolha um dos carros disponíveis: 1 a " + carrosDisponiveis.size());
                                    int numCarroEscolhido = escolherCarro(ler, carrosDisponiveis.size(), 1);
                                    Carro carroEscolhido = carrosDisponiveis.get(numCarroEscolhido - 1);

                                    System.out.println(carroEscolhido.getMarca() + "/" + carroEscolhido.getModelo());

                                    filial.retiraClientelistaEspera();  // lista de espera tem prioridade
                                    gravarEmprestimo(carroEscolhido, nomeCliente);
                                    log.push(montarRelatorio(carroEscolhido, nomeCliente, "EMPRESTADO", filial));

                                } else { // inclui cliente na lista de espera qdo não há carro disponível
                                    filial.incluiListaEspera(nomeCliente);
                                    System.out.printf("Cliente %s incluído(a) na lista de espera", nomeCliente);
                                }
                            }
                            break;

                    case 2: // Devolução
                        System.out.println("\nPlaca do veículo:");
                        String placa = ler.next();
                        Carro carroDevolvido = verificarPlaca(placa, carros);
                        if (carroDevolvido != null){
                            // placa encontrada; trata devolução
                            nomeCliente = carroDevolvido.getCliente();
                            carroDevolvido.setPode_Alugar(true);
                            carroDevolvido.setCliente("");

                            log.push(montarRelatorio(carroDevolvido, nomeCliente, "DEVOLVIDO", filial));
                        }
                        else {
                            System.out.println("Placa não encontrada ou veículo no pátio");
                        }
                        break;

                    case 3: // Cadastrar cliente
                        Cliente clienteCadastrado = cadastrarCliente(ler, carros);
                        clientes.add(clienteCadastrado);
                        break;

                    case 4: // associa o veículo a uma filial
                        Carro carro;
                        do {
                            if (listaVeiculos(carros) > 0) { // filtra veículos sem filial
                                carro = indicarVeiculo(ler, carros);
                                if (carro != null) {
                                    carro.setFilial(filial.getNumero());
                                    filial.associaVeiculos(carro);
                                }
                            }
                            else // não há mais carros sem uma filial
                                carro = null;

                        } while(carro != null) ;
                        break;

                    case 5: // lista todos os veículos da filial, os disponíveis para locação,
                        // o valor total das locações vigentes e a lista de espera
                        System.out.println("Número de carros na filial: " + filial.listaVeiculosFilial());
                        System.out.println();
                        carrosDisponiveis = filial.listaVeiculosDisponiveisFilial();
                        System.out.println("Número de carros disponíveis para locação: " + carrosDisponiveis.size());
                        System.out.println("\tValor Total da Locação: R$ " +
                                filial.valorTotalCarrosAlugados());
                        System.out.println();
                        System.out.println("Lista de Espera");
                        filial.mostralistaEsperaFilial();
                        break;

                    case 6: // relatório de movimentação de veículos (log)
                        log.forEach(System.out::println);
                        // substitui log.forEach(ls -> System.out.println(ls));
                        break;

                    case 7: // mudar a filial corrente
                        filial = lerFilial(ler, filiais);
                        break;
                }
            }while (option != 0);
        }
    }

    private static Filial lerFilial(Scanner ler, List<Filial> filiais) {
        Filial filial= null;
        int filialDigitada;
        boolean filialOk = false;

        System.out.println("Informe o número da filial para operar o sistema");
        for (Filial cadastroFilial:filiais) System.out.print("\t" + cadastroFilial.getNumero() + " - " +
                cadastroFilial.getNome());
        System.out.println();

        while (!filialOk) {
            try {
                filialDigitada = ler.nextInt();
                for (Filial it_filial: filiais) {
                    if (it_filial.getNumero() == filialDigitada) {
                        filialOk = true;
                        filial = it_filial;
                        break;
                    }
                }
                if (!filialOk){
                    System.out.println("\tFilial não cadastrada");
                }
            } catch (InputMismatchException e) {
                System.out.println("\tValor inválido! Digite um valor numérico.");
                ler.nextLine();
            }
        }
        return filial;
    }

    private static int lerOpcaoMenu(Scanner ler, int filial){
        int opcao = 0;
        boolean opcaoOk = false;
        while (!opcaoOk){
            System.out.println("\nEscolha uma das opções do menu. 0 para Sair");
            System.out.println("\t 1 - Empréstimo \n\t 2 - Devolução" +
                    "\n\t 3 - Cadastrar Cliente " +
                    "\n\t 4 - Associar Veículo à Filial \n\t 5 - Veículos e Lista de Espera da Filial" +
                    " \n\t 6 - Relatório de Movimentação" +
                    "\n\t 7 - Mudar Filial (atual " + filial + ")\n\t 0 - Sair do programa");
            try {
                opcao = ler.nextInt();
                opcaoOk = true;
                }
            catch (InputMismatchException e) {
                System.out.println("\tValor inválido! Digite uma das opções do menu");
                ler.nextLine();
            }
        }
        return opcao;
    }

    /**
     * Valida existência de um cliente no cadastro a partir do seu nome
     * @param nomeCliente objeto de pesquisa
     * @param clientes Lista dos clientes da FIFTY
     * @return true/false indicando se encontrou o cliente no cadastro (Objeto Queue)
     */
    private static boolean validarClienteCadastrado(String nomeCliente, Queue<Cliente> clientes){
        boolean clienteCadastrado = false;
        for (Cliente itemCliente : clientes){
            if (itemCliente.getNome().equals(nomeCliente)){
                clienteCadastrado = true;
                break;
            }
        }
        /* Forma 2
        Iterator<Cliente> it = clientes.iterator();
        while (it.hasNext()){
            Cliente cliente = it.next();
            if (cliente.getNome().equals(nomeCliente))
                clienteCadastrado = true;
        }
        Forma 3 (não valida, recomendável para imprimir ou tomar uma ação
         clientes.iterator().forEachRemaining(clientes->{
            System.out.println(clientes.getNome());
            System.out.println(clientes.getEndereco());
         }

        Forma 4: stream
        filaespera.stream.forEach(cliente -> System.out.println(cliente.getNome());

        Forma 5: stream / filter
        List<Cliente> c1 = clientes.stream().filter(cliente -> cliente.getNome().equals(nomeCliente));
         */
        return clienteCadastrado;
    }

    /**
     * Esta função é usada para a escolha do modelo no Empréstimo e tb quando os
     * veículos são atrelados a uma filial (chamada por indicarVeiculo())
     * @param ler variável de leitura
     * @param qdeCarros quantidade de carros da lista de escolha
     * @param inicio permite habilitar o 0 (zero) para sair da função
     * @return carroEscolhido índice da lista referente ao carro selecionado para locação
     */
    private static int escolherCarro(Scanner ler, int qdeCarros, int inicio) {
        boolean carroOk = false;
        int carroEscolhido = 1;
        while (!carroOk) {
            try {
                carroEscolhido = ler.nextInt();
                if (carroEscolhido >= inicio && carroEscolhido <= qdeCarros) {
                    carroOk = true;
                } else {
                    System.out.println("\tEscolha um modelo entre " + inicio + " e " + qdeCarros);
                }
            } catch (InputMismatchException e) {
                System.out.println("\tValor inválido! Digite um valor numérico.");
                ler.nextLine();
            }
        }
        return carroEscolhido;
    }

    private static void gravarEmprestimo(Carro carro, String cliente){
        carro.setPode_Alugar(false);
        carro.setCliente(cliente);
    }

    private static Carro verificarPlaca(String placa, Carro[] carros){
        List<Carro> carroDevolvido;
        carroDevolvido = stream(carros).filter(x -> x.getPlaca().equals(placa))
                .collect(Collectors.toList());
        if (carroDevolvido.size() > 0) {
            if (!carroDevolvido.get(0).getPode_Alugar()) // carro emprestado
                return carroDevolvido.get(0);
            else
                return null; // carro no pátio
        }
        else // placa não encontrada
            return null;
    }

    private static Carro indicarVeiculo(Scanner ler, Carro[] carros) {
        List<Carro> carrosCadastrados;

        carrosCadastrados = stream(carros).filter(x -> x.getFilial() == 0)
                    .collect(Collectors.toList());

        System.out.println("\nInforme um dos carros do cadastro.  Digite 0 para sair");
        int numCarroEscolhido = escolherCarro(ler, carrosCadastrados.size(), 0);

        if (numCarroEscolhido > 0)
            return carrosCadastrados.get(numCarroEscolhido - 1);
        else
            return null; // opção digitada = 0 (nenhuma escolha)
    }

    private static String montarRelatorio(Carro carro, String cliente, String movimentacao, Filial filial) {
        DateTimeFormatter dataFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm");
        String dataCorrente = dataFormat.format(LocalDateTime.now());

        return String.format(" %s: Filial %d - Carro %s, %s, %s foi %s %s %s",
                dataCorrente, filial.getNumero(), carro.getMarca(), carro.getModelo(), carro.getPlaca(),
                movimentacao, (movimentacao.equals("EMPRESTADO"))?"para":"por",cliente);
    }

    /**
     * Apresenta na console a lista de veículos cadastrados na empresa
     * @param carros array de carros cadastrados
     * @return numModelo número de modelos encontrados na pesquisa
     */
    private static int listaVeiculos(Carro[] carros) {
        System.out.println("FIFTY CARS\n\nModelos:");
        int numModelo = 0;
        for (Carro c : carros) {
            if (c.getFilial() == 0) { // veículo ainda não associado a uma filial
                System.out.printf("%nModelo %d%nMarca: %s%nModelo: %s%nCor: %s%nPlaca: %s%n" +
                                "Valor da diária: R$ %.2f%n", ++numModelo, c.getMarca(),
                        c.getModelo(), c.getCor(), c.getPlaca(), c.getValor());
            }
        }
        return numModelo;
    }

    private static Cliente cadastrarCliente(Scanner ler, Carro[] carros){
        List<Carro> carrosLocadora;
        carrosLocadora = stream(carros).collect(Collectors.toList());
        ler.nextLine();

        System.out.println("Cadastro de Cliente:");
        System.out.println("\tNome do Cliente ");
        String nome = ler.nextLine();

        System.out.println("\tEndereço");
        String endereco = ler.nextLine();

        System.out.println("\tTelefone");
        String telefone = ler.next();

        System.out.println("Carros da Locadora");
        System.out.println(carrosLocadora.stream().map(x -> x.getMarca() + "/" + x.getModelo() +
                " R$ " + x.getValor()).collect(Collectors.toList()));

        System.out.println("\nEscolha seu carro favorito da locadora");
        int numCarroEscolhido = escolherCarro(ler, carrosLocadora.size(), 1);

        return new Cliente(nome, endereco, telefone, carrosLocadora.get(numCarroEscolhido - 1));
    }

    private static void cadastrarVeiculo(Carro[] carros) {

        carros[0] = new Carro("abc-1234", "Jipe", "branco", "Renegade",
                500.90, true);
        carros[1] = new Carro("kgf-4523", "Honda", "cinza", "City",
                300.80, true);
        carros[2] = new Carro("plk-2158", "Toyota", "verde", "Corolla",
                800.50, false);
        carros[3] = new Carro("kzf-2473", "Nissan", "vermelho", "Kicks",
                450.00, true);
        carros[4] = new Carro("dcn-0606", "Honda", "branco", "Fit",
                600.10, true);
        carros[5] = new Carro("los-6783", "Fiat", "preto", "Uno",
                150.00, false);
        carros[6] = new Carro("smp-2709", "Ford", "cinza", "KA",
                120.50, true);
        carros[7] = new Carro("fks-1209", "Volks", "prata", "Gol",
                110.50, false);
        carros[8] = new Carro("acn-3004", "Audi", "preto", "A3",
                870.50, true);
        carros[9] = new Carro("len-3012", "Peugeot", "verde", "405",
                420.50, true);
    }
}
