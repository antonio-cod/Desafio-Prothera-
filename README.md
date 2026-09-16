# Desafio Iniflex — Gestão de Funcionários

Projeto desenvolvido como solução para o **Teste Prático de Programação da Iniflex**.

A aplicação implementa o gerenciamento de funcionários de uma indústria, aplicando conceitos de **Programação Orientada a Objetos**, herança, coleções, persistência de dados, manipulação de datas, cálculos monetários, ordenação, agrupamento e consultas.

## Tecnologias

- Java 17
- Spring Boot
- Spring Data JPA
- H2 Database
- Maven
- Maven Wrapper
- Visual Studio Code

## Sobre o desafio

O desafio utiliza os dados de dez funcionários e solicita a execução de diferentes operações sobre essa lista.

Entre as funcionalidades implementadas estão:

- Cadastro dos funcionários;
- Remoção do funcionário **João**;
- Exibição dos funcionários com formatação de datas e valores monetários;
- Aplicação de **10% de aumento salarial**;
- Agrupamento dos funcionários por função;
- Consulta dos aniversariantes dos meses solicitados;
- Identificação do funcionário com maior idade;
- Ordenação alfabética dos funcionários;
- Cálculo do total dos salários;
- Cálculo da quantidade de salários mínimos correspondente ao salário de cada funcionário.

Os resultados são apresentados no terminal seguindo a numeração dos requisitos definidos no teste.

## Modelagem

A aplicação possui duas classes principais de domínio:

### Person

Representa uma pessoa e contém:

- `name` — nome;
- `birthDate` — data de nascimento.

### Employee

Representa um funcionário e herda os atributos de `Person`, adicionando:

- `salary` — salário;
- `role` — função;
- `insertionOrder` — posição original do funcionário na tabela do desafio.

A classe `Person` utiliza `@MappedSuperclass`, permitindo que seus atributos sejam persistidos diretamente na tabela `employee`.

Para representar os dados são utilizados:

- `LocalDate` para datas;
- `BigDecimal` para valores monetários;
- `Map<String, List<Employee>>` para agrupamento dos funcionários por função.

## Arquitetura e organização

O projeto separa responsabilidades entre domínio, persistência, casos de uso e apresentação.

| Componente                | Responsabilidade                                                                                 |
| ------------------------- | ------------------------------------------------------------------------------------------------ |
| `TesteIniflexApplication` | Ponto de entrada da aplicação e inicialização do Spring Boot.                                    |
| `Principal`               | Implementa `CommandLineRunner` e inicia automaticamente a execução do desafio.                   |
| `entity`                  | Contém as entidades `Person` e `Employee`.                                                       |
| `repository`              | Contém o `EmployeeRepository` para acesso aos dados com Spring Data JPA.                         |
| `useCases`                | Contém as regras responsáveis pelo cadastro, atualização e consultas dos funcionários.           |
| `presentation`            | Contém `EmployeePrinter`, responsável pela apresentação e formatação dos resultados no terminal. |

As dependências entre os componentes são fornecidas pelo Spring através de **injeção de dependências via construtor**.

### Estrutura do projeto

```text
.
├── data/
│   └── iniflex.mv.db
│
├── .mvn/
│   └── wrapper/
│
├── src/
│   ├── main/
│   │   ├── java/br/com/prothera/teste_Iniflex/
│   │   │   ├── TesteIniflexApplication.java
│   │   │   ├── Principal.java
│   │   │   │
│   │   │   ├── entity/
│   │   │   │   ├── Person.java
│   │   │   │   └── Employee.java
│   │   │   │
│   │   │   ├── repository/
│   │   │   │   └── EmployeeRepository.java
│   │   │   │
│   │   │   ├── useCases/
│   │   │   │   ├── ExecuteChallengeUseCase.java
│   │   │   │   ├── InitializeEmployeesUseCase.java
│   │   │   │   ├── UpdateEmployeesUseCase.java
│   │   │   │   └── EmployeeReportsUseCase.java
│   │   │   │
│   │   │   └── presentation/
│   │   │       └── EmployeePrinter.java
│   │   │
│   │   └── resources/
│   │       └── application.properties
│   │
│   └── test/
│       └── java/br/com/prothera/teste_Iniflex/
│           └── TesteIniflexApplicationTests.java
│
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

## Persistência de dados

Os funcionários são armazenados em um banco de dados **H2 em arquivo**, permitindo que os registros sejam preservados após o encerramento da aplicação.

O arquivo é armazenado em:

```text
data/iniflex.mv.db
```

A configuração:

```properties
spring.jpa.hibernate.ddl-auto=update
```

permite que o Hibernate crie ou atualize a estrutura necessária sem recriar o banco a cada inicialização.

Quando a tabela `employee` está vazia, a aplicação:

1. Cadastra os dez funcionários;
2. Remove o funcionário João;
3. Aplica o aumento salarial de 10%;
4. Persiste as alterações.

Nos próximos reinícios, os dados já existentes são utilizados, evitando duplicação de registros e aplicação repetida do aumento salarial.

## Como executar

### Pré-requisitos

É necessário possuir o **JDK 17** instalado.

Para verificar:

```bash
java -version
```

O projeto possui **Maven Wrapper**, portanto não é necessário instalar o Maven manualmente.

### Linux / macOS

Na raiz do projeto, execute:

```bash
./mvnw spring-boot:run
```

Caso o arquivo não possua permissão de execução:

```bash
chmod +x mvnw
./mvnw spring-boot:run
```

Também é possível executar:

```bash
bash mvnw spring-boot:run
```

### Windows — PowerShell

```powershell
.\mvnw.cmd spring-boot:run
```

Também é possível executar diretamente o método `main` da classe:

```text
TesteIniflexApplication.java
```

utilizando uma IDE ou editor com suporte ao Java, como **VS Code**, IntelliJ IDEA ou Eclipse.

## Execução do desafio

Após a inicialização do Spring Boot, a classe `Principal`, através da implementação de `CommandLineRunner`, executa automaticamente as operações solicitadas no desafio.

Os resultados são apresentados diretamente no terminal.

A aplicação permanece disponível na porta:

```text
http://localhost:8080
```

Para encerrar a execução pelo terminal:

```text
Ctrl + C
```

## Console do H2

Com a aplicação em execução, o console do H2 pode ser acessado em:

```text
http://localhost:8080/h2-console
```

Utilize as seguintes configurações:

| Campo        | Valor                         |
| ------------ | ----------------------------- |
| Driver Class | `org.h2.Driver`               |
| JDBC URL     | `jdbc:h2:file:./data/iniflex` |
| User Name    | `sa`                          |
| Password     | deixar vazio                  |

Depois, clique em **Connect**.

Para consultar os funcionários seguindo a ordem original do desafio:

```sql
SELECT *
FROM employee
ORDER BY insertion_order;
```

> A URL JDBC utiliza o caminho do banco sem a extensão `.mv.db`.

## Formatação dos resultados

A saída segue as regras solicitadas pelo desafio.

As datas são exibidas no formato brasileiro:

```text
dd/MM/yyyy
```

Exemplo:

```text
18/10/1990
```

Os valores monetários utilizam separador de milhar por ponto e casas decimais por vírgula:

```text
1.234,56
```

Essas responsabilidades ficam concentradas na camada `presentation`, através da classe `EmployeePrinter`.

## Decisões de implementação

Algumas decisões foram adotadas para manter o código organizado e facilitar sua manutenção:

- Uso de `BigDecimal` para cálculos monetários;
- Uso de `LocalDate` para datas de nascimento;
- Separação das regras em casos de uso;
- Uso de Spring Data JPA para abstrair o acesso aos dados;
- Persistência em H2 para permitir consulta dos registros após a execução;
- Injeção de dependências via construtor;
- Uso de `@MappedSuperclass` para representar a herança entre `Person` e `Employee`;
- Separação da apresentação dos resultados através de `EmployeePrinter`;
- Execução automática do desafio através de `CommandLineRunner`.

## Autor

**Antônio Carlos da Cruz**

Projeto desenvolvido como solução para o desafio técnico de programação da **Prothera **.
