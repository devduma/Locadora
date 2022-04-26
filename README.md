# Locadora
Devido ao aumento de clientes, a loja não tinha veículos suficientes para atender a todos e com isso foi criada uma lista de espera. Incialmente a lista era feita em um papel, mas um dos atendentes perdeu essas folhas e não foi possível entrar em contato com os clientes. Pensando em evitar que isso volte a acontecer, o gerente priorizou para nossa próxima melhoria os seguintes itens:
### Requisitos
1. Preciso de uma forma estruturada para armazenar informações dos clientes:
* Nome
* Telefone
* Endereço
* Carro desejado
2. Gostaria de ter uma forma de organizar a espera dos clientes respeitando a ordem de entrada deles, 
ou seja, o primeiro cliente a entrar na espera, deverá ser o primeiro a ser atendido. Observação: neste momento não precisamos nos preocupar em validar qual veículo está livre, nossos atendentes foram treinados para oferecer outro veículo disponível, mesmo que não seja o desejado nos dados do cliente.
3. Como o fluxo de movimentação dos carros cresceu muito, eu gostaria de ter uma forma de auditar as mudanças de disponibilidade e quem foi o cliente que usou o veículo. Por exemplo: 
    1. 10/04/2022 às 08:50:00: Carro Hilux (marca), SW4 (modelo), ABC-1234 (placa) foi DEVOLVIDO por Frangolino da Silva (nome do cliente).
    2. 08/04/2022 às 19:00: Carro Hilux (marca), SW4 (modelo), ABC-1234 (placa) foi EMPRESTADO para Frangolino da Silva (nome do cliente).
    3. 07/04/2022 às 10:50:00: Carro VW (marca), Fox (modelo), XYZ-9876 (placa) foi DEVOLVIDO por Juvenal Moreira (nome do cliente).
    4. 04/04/2022 às 10:50:00: Carro VW (marca), Fox (modelo), XYZ-9876 (placa) foi EMPRESTADO por Juvenal Moreira (nome do cliente). 
4. Agora que a lista de espera e nosso controle de trocas está automatizado, será necessário uma forma de imprimir na tela essas informações, portanto gostaria de conseguir visualizar a lista de espera com todas as informações armazenadas, bem como todos os registros de alteração de status dos carros.
### Etapa 2
Depois da contratação do novo gerente e da implementação do sistema que você construiu, as lojas passaram a faturar muito e o controle de informações ficou muito melhor para a gestão. Com todo esse sucesso, o gerente decidiu abrir algumas filiais, mas sem perder o controle que implementamos até aqui, por isso surgiram alguns novos requisitos:

1. O gerente precisa ter uma forma de controle de todas filiais;
2. Todas as filiais deverão ter as mesmas características e funções que a matriz;
3. Como a empresa está aumentando e o gerente não consegue estar em todos os lugares, ele precisa de uma forma de visualizar todas as filiais; 
4.  Além de visualizar todas as filiais, o gerente deve poder escolher uma filial que terá todos os seus detalhes:
    1. Quantidade de carros (total);
    2. Quantidade de carros alugados;
    3. Quantidade de clientes na lista de espera;
    4. Valor total (em diárias) dos carros alugados;
