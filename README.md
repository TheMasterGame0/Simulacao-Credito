# Simulador de Credito

## Como Rodar a Aplicação

### Docker
Para rodar a aplicação em Docker é preciso ter o Docker Desktop instalado. 

A aplicação será rodada utilizando WSL que pode ser obtida seguindo o tutorial da Microsoft, disponível no link https://learn.microsoft.com/pt-br/windows/wsl/install

Para rodar a aplicação é preciso:
- Entrar nas configurações do Docker e habilitar a distribuição que tenha instalado;
- Entrar pelo terminal até a pasta raiz do projeto (Simulacao-Credito);
- Criar o arquivo .env com os valores da senha do banco mssql e do link de acesso ao eventhub. O aqruivo ficcara similar a:

EVENTHUB_CONNECTION_STRING=link_Conexao
HACK_PASSWORD=senha_banco

- Executar o comando:
```shell script
docker compose up -d --build
```

## Informações sobre o projeto

Esse projeto tem como objetivo produzir uma API capaz de realizar simulações de crédito para as taxas SAC e PRICE, baseado em dados obtidos em um banco externo com taxas, prazos e valores. Os dados das simulações realizadas são guardados em um banco local utilizando H2.

A API apresenta 4 enpoints que são, com suas respectivas funções:

- **api/simular**: Por ele que as simulações deverão ser realizadas, retornando a simulação pela tabela PRICE e SAC.

- **api/simulacoes**: Por ele que é obtida a lista de simulações já realizadas, paginadas e com o tamanho da página determinada na chamada.

- **api/simulacoes-por-dia/{data}**: Por ele é possível obter um resumo de todas as simulações realizadas em um dia, divididas por produto.

- **api/telemetria/{data}**: Por essa chamada será possível obter as métricas coletadas ao longo do dia de cada um dos endpoints.




Recebe um JSON com o valor desejado e o número de parcelas. Com essas informações, são retornadas o id da simulação, o Produto utilizado com suas informações e a simulação pelo sistema SAC e PRICE. **Observação**: Dada as especificações, apenas um valor toral seria utilizado, portnato foi definido que o menor valor seria o utilizado, partindo do princípio que o usuário buscará pagar a menor quantidade de juros.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:


> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/code-with-quarkus-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Related Guides

- REST ([guide](https://quarkus.io/guides/rest)): A Jakarta REST implementation utilizing build time processing and Vert.x. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it.
- REST Jackson ([guide](https://quarkus.io/guides/rest#json-serialisation)): Jackson serialization support for Quarkus REST. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it
- Hibernate ORM with Panache ([guide](https://quarkus.io/guides/hibernate-orm-panache)): Simplify your persistence code for Hibernate ORM via the active record or the repository pattern
- JDBC Driver - MySQL ([guide](https://quarkus.io/guides/datasource)): Connect to the MySQL database via JDBC

## Provided Code

### Hibernate ORM

Create your first JPA entity

[Related guide section...](https://quarkus.io/guides/hibernate-orm)

[Related Hibernate with Panache section...](https://quarkus.io/guides/hibernate-orm-panache)


### REST

Easily start your REST Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)
