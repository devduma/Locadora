import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class TestLocadora {
    public static Integer NUMERO_CARROS = 10;

    public static void main(String[] args) {
        Carro[] carros = new Carro[NUMERO_CARROS];
        cadastrarVeiculo(carros);
        Arrays.sort(carros);

        try (Scanner ler = new Scanner(System.in)) {
            int option;
            String nomeCliente;
            Deque<String> log = new ArrayDeque<>();
            Queue<String> listaEspera = new ArrayDeque<>();
            Queue<Cliente> clientes = new ArrayDeque<>();

            do {
                option = lerOpcaoMenu(ler);

                if (option != 0) {
                    switch (option) {
                        case 1: // Empréstimo
                            ler.nextLine();
                            System.out.println("\nNome do Cliente:");
                            nomeCliente = ler.nextLine();
                            if (!validarClienteCadastrado(nomeCliente, clientes)) {// cliente não cadastrado
                                System.out.println("Cliente não encontrado. Preencha o seu cadastro");
                            }
                            else {

                                if (listaVeiculos(carros) > 0) {
                                    Carro carroEscolhido = emprestarCarro(ler, carros);
                                    System.out.println(carroEscolhido.getMarca() + "/" + carroEscolhido.getModelo());

                                    gravarEmprestimo(carroEscolhido, nomeCliente);
                                    log.push(montarRelatorio(carroEscolhido, nomeCliente, "EMPRESTADO"));

                                } else { // inclui cliente na lista de espera
                                    listaEspera.add(nomeCliente);
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

                                log.push(montarRelatorio(carroDevolvido, nomeCliente, "DEVOLVIDO"));
                                String clienteEspera = listaEspera.poll();
                                if (clienteEspera != null) { // tem lista de espera?
                                    gravarEmprestimo(carroDevolvido, clienteEspera);
                                    log.push(montarRelatorio(carroDevolvido, clienteEspera, "EMPRESTADO"));
                                }
                            }
                            else {
                                System.out.println("Placa não encontrada ou veículo no pátio");
                            }
                            break;

                        case 3: // Cadastrar cliente
                            Cliente clienteCadastrado = cadastrarCliente(ler, carros);
                            clientes.add(clienteCadastrado);
                            break;

                        case 4: // lista veículos disponíveis para locação
                            listaVeiculos(carros);
                            break;

                        case 5: // mostra lista de espera de clientes
                            System.out.println("Lista de Espera");
                            if (listaEspera.isEmpty())
                                System.out.println("\tLista vazia");
                            else
                                listaEspera.forEach(ls -> System.out.println(ls));
                            break;

                        case 6: // relatório de movimentação de veículos (log)
                            log.forEach(ls -> System.out.println(ls));
                            break;
                    }
                }

            } while (option != 0);
        }
    }

    private static int lerOpcaoMenu(Scanner ler){
        int opcao = 0;
        boolean opcaoOk = false;
        while (!opcaoOk){
            System.out.println("\nEscolha uma das opções do menu");
            System.out.println("\n\t 1 - Empréstimo \n\t 2 - Devolução" +
                    "\n\t 3 - Cadastrar Cliente \n\t 4 - Veículos Disponíveis" +
                    " \n\t 5 - Mostra Lista de Espera \n\t 6 - Relatório de Movimentação" +
                    "\n\t 0 - Sair do programa");
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

    private static boolean validarClienteCadastrado(String nomeCliente, Queue clientes){
        boolean clienteCadastrado = false;
        Iterator it = clientes.iterator();
        while (it.hasNext()){
            Cliente cliente = (Cliente) it.next();
            if (cliente.getNome().equals(nomeCliente))
                clienteCadastrado = true;
        }
        return clienteCadastrado;
    }

    private static Carro emprestarCarro(Scanner ler, Carro[] carros){
        List<Carro> carrosDisponiveis;
        carrosDisponiveis = Arrays.stream(carros).filter(x -> x.getPode_Alugar())
                .collect(Collectors.toList());
        System.out.println(carrosDisponiveis.stream().map(x -> x.getMarca() + "/" + x.getModelo() +
                " R$ " + x.getValor()).collect(Collectors.toList()));

        System.out.println("\nEscolha um dos carros disponíveis");
        int numCarroEscolhido = escolherCarro(ler, carrosDisponiveis.size());

        return carrosDisponiveis.get(numCarroEscolhido - 1);
    }

    private static int escolherCarro(Scanner ler, int qdeCarros) {
        boolean carroOk = false;
        int carroEscolhido = 1;
        while (!carroOk) {
            try {
                carroEscolhido = ler.nextInt();
                if (carroEscolhido > 0 && carroEscolhido <= qdeCarros) {
                    carroOk = true;
                } else {
                    System.out.println("\tEscolha um modelo entre 1 e " + qdeCarros);
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
        carroDevolvido = Arrays.stream(carros).filter(x -> x.getPlaca().equals(placa))
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

    private static String montarRelatorio(Carro carro, String cliente, String movimentacao) {
        DateTimeFormatter dataFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm");
        String dataCorrente = dataFormat.format(LocalDateTime.now());

        return String.format(" %s: Carro %s, %s, %s foi %s %s %s %n",
                dataCorrente, carro.getMarca(), carro.getModelo(), carro.getPlaca(),
                movimentacao, (movimentacao.equals("EMPRESTADO"))?"para":"por",cliente);
    }

    private static int listaVeiculos(Carro[] carros) {
        System.out.println("FIFTY CARS\n\nConfira nossos modelos:");
        int numModelo = 0;
        for (Carro c : carros) {
            if (c.getPode_Alugar()) {
                System.out.printf("%nModelo %d%nMarca: %s%nModelo: %s%nCor: %s%nPlaca: %s%n" +
                                "Valor da diária: R$ %.2f%n", ++numModelo, c.getMarca(),
                        c.getModelo(), c.getCor(), c.getPlaca(), c.getValor());
            }
        }
        return numModelo;
    }

    private static Cliente cadastrarCliente(Scanner ler, Carro[] carros){
        List<Carro> carrosLocadora;
        carrosLocadora = Arrays.stream(carros).collect(Collectors.toList());
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
        int numCarroEscolhido = escolherCarro(ler, carrosLocadora.size());

        return new Cliente(nome, endereco, telefone, carrosLocadora.get(numCarroEscolhido - 1));
    }

    private static void cadastrarVeiculo(Carro[] carros) {

        carros[0] = new Carro("abc-1234", "Jipe", "branco", "Renegade",
                500.90, false);
        carros[1] = new Carro("kgf-4523", "Honda", "cinza", "City",
                300.80, true);
        carros[2] = new Carro("plk-2158", "Toyota", "verde", "Corolla",
                800.50, false);
        carros[3] = new Carro("kzf-2473", "Nissan", "vermelho", "Kicks",
                450.00, false);
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
                420.50, false);
    }
}
