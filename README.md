# Playwright Java com Maven e JUnit 5

Estrutura inicial para testes de UI com Java 17, Maven, JUnit 5 e Playwright Java.

## Pré-requisitos

- JDK 17 ou superior
- Maven 3.9 ou superior
- Node.js, usado pelo instalador dos navegadores do Playwright

## Instalação

Baixe os navegadores do Playwright uma vez na máquina:

```bash
mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install"
```

## Executar os testes

```bash
mvn test
```

Para executar somente o teste smoke:

```bash
mvn -Dtest=SmokeTest test
```

Para executar o teste exploratorio do campo de texto:

```bash
mvn -Dtest=SimpleInputTest test
```

## Visualizar o relatorio Allure

Depois da execucao dos testes, gere e abra o relatorio:

```bash
npx allure-commandline generate target/allure-results --clean -o target/allure-report
npx allure-commandline open target/allure-report
```

## Configurar evidencias

Altere `src/main/resources/test.properties`:

```properties
evidence.mode=ALL_ACTIONS
```

Modos disponíveis:

- `ALL_ACTIONS`: captura após cada ação e validação, além da tela final.
- `FINAL_SCREEN`: captura somente a tela final de cada teste.
- `FAILURE_ONLY`: captura somente a primeira evidência quando o teste falhar.

Para sobrescrever o modo temporariamente sem alterar o arquivo:

```bash
mvn -Devidence.mode=FAILURE_ONLY test
```

O teste usa Chromium em modo headless por padrão. Para depurar visualmente, altere `setHeadless(false)` em `PlaywrightTestBase`.