# Simulador de Credito

## Como Rodar a Aplicação

### Docker
Para rodar a aplicação em Docker é preciso ter o Docker Desktop instalado. 

A aplicação será rodada utilizando WSL que pode ser a própria utilizada pelo docker. A recomendação do Docker é utilizar alguma disytribuição extra, que pode ser obtida seguindo o tutorial da Microsoft, disponível no link https://learn.microsoft.com/pt-br/windows/wsl/install

Para rodar a aplicação é preciso:
- Entrar nas configurações do Docker e habilitar a distribuição que tenha instalado;
- Entrar pelo terminal até a pasta raiz do projeto (Simulacao-Credito);
- Criar o arquivo .env com os valores da senha do banco mssql e do link de acesso ao eventhub. O arquivo ficará similar a:

EVENTHUB_CONNECTION_STRING=link_Conexao

HACK_PASSWORD=senha_banco

- Executar o comando:
```shell script
docker compose up -d --build
```
- Com isso as dependências serão baixadas e a aplicação iniciará no localhost pela porta 8080. Alguns links uteis são: 

  -  **Swagger**: http://localhost:8080/q/swagger-ui
  - **Health Checker**: http://localhost:8080/q/health-ui

### Localmente

Caso queira rodar a aplicação localmente, é necessário ter o Java 17 e o Maven instalados. Nesse caso para rodar a aplicação é preciso:

- Entrar pelo terminal até a pasta raiz do projeto (Simulacao-Credito);
- Criar o arquivo .env com os valores da senha do banco mssql e do link de acesso ao eventhub. O arquivo ficará similar a:

EVENTHUB_CONNECTION_STRING=link_Conexao

HACK_PASSWORD=senha_banco

- Executar os comandos:
```shell script
./mvnw clean install
export $(cat .env | xargs)
./mnvw quarkus:dev
```

ou

```shell script
mvn clean install
export $(cat .env | xargs)
mvn quarkus:dev
```

dependendo da maneira como foi instalado o maven.

- Com isso as dependências serão baixadas e a aplicação iniciará no localhost pela porta 8080. Alguns links uteis são:

    -  **Swagger**: http://localhost:8080/q/swagger-ui
    - **Health Checker**: http://localhost:8080/q/health-ui


## Informações sobre o projeto

Esse projeto tem como objetivo produzir uma API capaz de realizar simulações de crédito para as taxas SAC e PRICE, baseado em dados obtidos em um banco externo com taxas, prazos e valores. Os dados das simulações realizadas são guardados em um banco local utilizando H2.

A API apresenta 4 enpoints que são:

- **api/simular**: Por ele que as simulações deverão ser realizadas, retornando a simulação pela tabela PRICE e SAC.

  - Recebe um JSON com o valor desejado e o número de parcelas. 
  - Retorna o id da simulação, o Produto utilizado com suas informações e a simulação pelo sistema SAC e PRICE. 
  - **Observação**: Dada as especificações, apenas um valor toral seria utilizado, portanto foi definido que o menor valor seria o utilizado, partindo do princípio que o usuário buscará pagar a menor quantidade de juros.

- **api/simulacoes**: Por ele que é obtida a lista de simulações já realizadas, paginadas e com o tamanho da página determinada na chamada.

- **api/simulacoes-por-dia/{data}**: Por ele é possível obter um resumo de todas as simulações realizadas em um dia, divididas por produto.

- **api/telemetria/{data}**: Por essa chamada será possível obter as métricas coletadas ao longo do dia de cada um dos endpoints.

### REST

Easily start your REST Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)
