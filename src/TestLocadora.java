import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


public class TestLocadora {
    public static Integer NUMERO_CARROS = 10;

    public static void main(String[] args) {
        Carro[] carros = new Carro[NUMERO_CARROS];

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

        // Opção para cadastrar veículos
        /* try (Scanner ler = new Scanner(System.in)) {
            System.out.println("Complete o cadastro com 3 novos veículos");
            int i = 1;
            do {
                carros[i + 6] = cadastrarVeiculo(ler, i);
                i++;
            } while (i <= 3);
        } */

        Arrays.sort(carros);

        try (Scanner ler = new Scanner(System.in)) {
            int option;
            String cliente;
            StringBuilder log = new StringBuilder();
            Queue<String> listaEspera = new ArrayDeque<>();
            do {
                System.out.println("\n\nEscolha uma das opções do menu");
                System.out.println("\n\t 1 - Empréstimo \n\t 2 - Devolução" +
                        " \n\t 3 - Veículos Disponíveis \n\t 4 - Lista de Espera" +
                        "\n\t 5 - Relatório \n\t 0 - Sair do programa");
                option = ler.nextInt();

                if (option != 0) {
                    switch (option) {
                        case 1: // Empréstimo
                            System.out.println("\nNome do Cliente:");
                            cliente = ler.next();
                            if (listaVeiculos(carros) > 0) {
                                Carro carroEscolhido = emprestarCarro(ler, carros);
                                System.out.println(carroEscolhido.getMarca() + "/" + carroEscolhido.getModelo());

                                gravarEmprestimo(carroEscolhido, cliente);
                                log.append(montaRelatorio(carroEscolhido, cliente, "EMPRESTADO"));

                                System.out.printf("%nRelatório de movimentação: %n%s ", log);
                            } else { // inclui cliente na lista de espera
                                listaEspera.add(cliente);
                                System.out.printf("Cliente %s incluído(a) na lista de espera", cliente);
                            }
                            break;

                        case 2: // Devolução
                            System.out.println("\nPlaca do veículo:");
                            String placa = ler.next();
                            Carro carroDevolvido = verificaPlaca(placa, carros);
                            if (carroDevolvido != null){
                                // placa encontrada; trata devolução
                                cliente = carroDevolvido.getCliente();
                                carroDevolvido.setPode_Alugar(true);
                                carroDevolvido.setCliente("");

                                log.append(montaRelatorio(carroDevolvido, cliente, "DEVOLVIDO"));
                                String clienteEspera = listaEspera.poll();
                                if (clienteEspera != null) { // tem lista de espera?
                                    gravarEmprestimo(carroDevolvido, clienteEspera);
                                    log.append(montaRelatorio(carroDevolvido, clienteEspera, "EMPRESTADO"));
                                }
                            }
                            else {
                                System.out.println("Placa não encontrada ou veículo no pátio");
                            }
                            System.out.printf("%nRelatório de movimentação: %n%s ", log);
                            break;

                        case 3: // lista veículos disponíveis para locação
                            listaVeiculos(carros);
                            break;

                        case 4: // mostra lista de espera de clientes
                            System.out.println("Lista de Espera");
                            if (listaEspera.isEmpty())
                                System.out.println("\tLista vazia");
                            else
                                listaEspera.forEach(ls -> System.out.println(ls));
                            break;

                        case 5: // relatório de movimentação de veículos (log)
                            System.out.printf("%nRelatório de movimentação: %n%s ", log);
                            break;
                    }
                }

            } while (option != 0);
        }

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

    private static Carro emprestarCarro(Scanner ler, Carro[] carros){
        List<Carro> carrosDisponiveis;
        carrosDisponiveis = Arrays.stream(carros).filter(x -> x.getPode_Alugar())
                .collect(Collectors.toList());
        System.out.println(carrosDisponiveis.stream().map(x -> x.getMarca() + "/" + x.getModelo() +
                " R$ " + x.getValor()).collect(Collectors.toList()));

        int numCarroEscolhido = escolherCarro(ler, carrosDisponiveis.size());

        return carrosDisponiveis.get(numCarroEscolhido - 1);
    }

    private static int escolherCarro(Scanner ler, int qdeCarros) {
        boolean carroOk = false;
        int carroEscolhido = 1;
        while (!carroOk) {
            System.out.println("\nEscolha um dos carros disponíveis");
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
    private static Carro verificaPlaca(String placa, Carro[] carros){
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

    private static String montaRelatorio(Carro carro, String cliente, String movimentacao) {
        DateTimeFormatter dataFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm");
        String dataCorrente = dataFormat.format(LocalDateTime.now());
        // Se não entrou 'Entrada do veículo de placa: <placa>'

        return String.format(" %s: Carro %s, %s, %s foi %s %s %s %n",
                dataCorrente, carro.getMarca(), carro.getModelo(), carro.getPlaca(),
                movimentacao, (movimentacao.equals("EMPRESTADO"))?"para":"por",cliente);
    }

    private static Carro cadastrarVeiculo(Scanner ler, int i) {
        System.out.println();
        System.out.println("Informe o número de placa do carro " + i);
        String placa = ler.next();

        System.out.println("Informe a marca do carro");
        String marca = ler.next();

        System.out.println("Informe o modelo");
        String modelo = ler.next();

        System.out.println("Informe a cor");
        String cor = ler.next();

        double valor = 0;
        boolean isDouble = true;
        while (isDouble) {
            System.out.println("Informe o valor da diária");
            try {
                valor = ler.nextDouble();
                isDouble = false;
            } catch (InputMismatchException e) {
                System.out.println("Valor inválido! Digite um valor numérico.");
                ler.nextLine();
            }
        }
        return new Carro(placa, marca, cor, modelo, valor, true);
    }
}
