# Calculadora em Kotlin + Jetpack Compose

Projeto para abrir no **Android Studio**.

## Como abrir
1. Extraia o arquivo ZIP.
2. No Android Studio, clique em **Open** e selecione a pasta `CalculadoraCompose`.
3. Aguarde a sincronização do Gradle (é necessário internet no primeiro uso).
4. Se necessário, selecione **JDK 17** em *Settings > Build, Execution, Deployment > Build Tools > Gradle*.
5. Execute no emulador ou no celular usando **Run ▶**.

## Arquivo principal
`app/src/main/java/com/exemplo/calculadora/MainActivity.kt`

## Recursos
- Adição, subtração, multiplicação e divisão.
- Números inteiros e decimais.
- Visor, limpar (`C`) e apagar (`⌫`).
- Tratamento de divisão por zero (mensagem `Erro`).
- Porcentagem (`%` divide o valor exibido por 100).
- Raiz quadrada (`√`) e tratamento de número negativo.
- Histórico dos últimos 8 cálculos, preservado na rotação da tela.

## Tecnologias
Kotlin, Jetpack Compose (Button e Text), `rememberSaveable`, `mutableStateOf` e `BigDecimal`.

Observação: o histórico fica apenas no estado da tela e não é salvo permanentemente após fechar o aplicativo.
