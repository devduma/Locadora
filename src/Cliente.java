public class Cliente {
    public final String nome;
    public final String endereco;
    public final String telefone;
    public final Carro carroFavorito;

    public String getNome() {
        return nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getCarroFavorito() {
        return carroFavorito.getMarca() + "/" + carroFavorito.getModelo();
    }

    public Cliente(String nome, String endereco, String telefone, Carro carroFavorito) {
        this.nome = nome;
        this.endereco = endereco;
        this.telefone = telefone;
        this.carroFavorito = carroFavorito;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "nome='" + getNome() + '\'' +
                ", endereco='" + getEndereco() + '\'' +
                ", telefone='" + getTelefone() + '\'' +
                ", carro favorito='" + getCarroFavorito() + '\'' +
                '}';
    }
}
