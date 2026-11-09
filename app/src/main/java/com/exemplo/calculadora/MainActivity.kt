package com.exemplo.calculadora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.math.BigDecimal
import java.math.MathContext
import kotlin.math.sqrt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { TelaCalculadora() } }
    }
}

private val precisao = MathContext(12)

// Remove zeros desnecessários, como 5.000 -> 5.
private fun formatar(numero: BigDecimal): String =
    numero.stripTrailingZeros().toPlainString()

// Devolve null se a operação não puder ser feita.
private fun calcular(a: BigDecimal, b: BigDecimal, operacao: String): BigDecimal? =
    when (operacao) {
        "+" -> a.add(b, precisao)
        "−" -> a.subtract(b, precisao)
        "×" -> a.multiply(b, precisao)
        "÷" -> if (b.compareTo(BigDecimal.ZERO) == 0) null else a.divide(b, precisao)
        else -> null
    }

@Composable
fun TelaCalculadora() {
    var visor by rememberSaveable { mutableStateOf("0") }
    var primeiro by rememberSaveable { mutableStateOf("") }
    var operacao by rememberSaveable { mutableStateOf("") }
    var novoNumero by rememberSaveable { mutableStateOf(false) }
    var historico by rememberSaveable { mutableStateOf("") }

    fun limpar() {
        visor = "0"
        primeiro = ""
        operacao = ""
        novoNumero = false
    }

    fun digitar(numero: String) {
        if (visor == "Erro") limpar()
        if (novoNumero) {
            visor = if (numero == ".") "0." else numero
            novoNumero = false
        } else if (numero == ".") {
            if (!visor.contains(".")) visor += "."
        } else if (visor.length < 16) {
            visor = if (visor == "0") numero else visor + numero
        }
    }

    fun apagar() {
        if (visor == "Erro") {
            limpar()
        } else if (novoNumero) {
            visor = "0"
            novoNumero = false
        } else {
            visor = visor.dropLast(1).ifEmpty { "0" }
            if (visor == "-") visor = "0"
        }
    }

    fun adicionarHistorico(texto: String) {
        historico = (listOf(texto) + historico.lines().filter { it.isNotBlank() })
            .take(8).joinToString("\n")
    }

    fun escolherOperacao(op: String) {
        if (visor == "Erro") return
        if (operacao.isNotEmpty() && !novoNumero) {
            val resultado = calcular(primeiro.toBigDecimal(), visor.toBigDecimal(), operacao)
            if (resultado == null) {
                visor = "Erro"
                primeiro = ""
                operacao = ""
                return
            }
            visor = formatar(resultado)
        }
        primeiro = visor
        operacao = op
        novoNumero = true
    }

    fun igual() {
        if (operacao.isEmpty() || visor == "Erro") return
        val a = primeiro.toBigDecimal()
        val b = visor.toBigDecimal()
        val resultado = calcular(a, b, operacao)
        if (resultado == null) {
            visor = "Erro"
        } else {
            val resposta = formatar(resultado)
            adicionarHistorico("${formatar(a)} $operacao ${formatar(b)} = $resposta")
            visor = resposta
        }
        primeiro = ""
        operacao = ""
        novoNumero = true
    }

    fun porcentagem() {
        if (visor == "Erro") return
        val anterior = visor
        visor = formatar(visor.toBigDecimal().divide(BigDecimal("100"), precisao))
        adicionarHistorico("$anterior% = $visor")
    }

    fun raiz() {
        if (visor == "Erro") return
        val numero = visor.toDouble()
        if (numero < 0) {
            visor = "Erro"
        } else {
            val anterior = visor
            visor = formatar(BigDecimal.valueOf(sqrt(numero)).round(precisao))
            adicionarHistorico("√($anterior) = $visor")
        }
        primeiro = ""
        operacao = ""
        novoNumero = true
    }

    val fundo = Color(0xFF101827)
    val corNumero = Color(0xFF263447)
    val corOperacao = Color(0xFF335B87)
    val corEspecial = Color(0xFF3D4657)

    Surface(color = fundo, modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Calculadora", color = Color.White, fontSize = 24.sp,
                fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 12.dp))

            Text("Histórico (últimos 8)", color = Color.LightGray, fontSize = 14.sp)
            Text(
                text = historico.ifBlank { "Nenhum cálculo ainda" },
                color = Color(0xFFBAC9DC), fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth().height(100.dp).verticalScroll(rememberScrollState())
            )
            Text(
                text = if (operacao.isEmpty()) " " else "$primeiro $operacao",
                color = Color.LightGray, textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = visor,
                color = Color.White, fontSize = 44.sp,
                fontWeight = FontWeight.Bold, textAlign = TextAlign.End,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            val linhas = listOf(
                listOf("C", "⌫", "%", "÷"),
                listOf("7", "8", "9", "×"),
                listOf("4", "5", "6", "−"),
                listOf("1", "2", "3", "+"),
                listOf("√", "0", ".", "=")
            )

            linhas.forEach { linha ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    linha.forEach { simbolo ->
                        val cor = when (simbolo) {
                            "÷", "×", "−", "+", "=" -> corOperacao
                            "C", "⌫", "%", "√" -> corEspecial
                            else -> corNumero
                        }
                        Button(
                            onClick = {
                                when (simbolo) {
                                    "C" -> limpar()
                                    "⌫" -> apagar()
                                    "%" -> porcentagem()
                                    "√" -> raiz()
                                    "÷", "×", "−", "+" -> escolherOperacao(simbolo)
                                    "=" -> igual()
                                    else -> digitar(simbolo)
                                }
                            },
                            modifier = Modifier.weight(1f).height(70.dp),
                            shape = RoundedCornerShape(14.dp),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = cor)
                        ) {
                            Text(simbolo, fontSize = 25.sp, color = Color.White)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            Text("Toque em C para limpar ou ⌫ para apagar um dígito.",
                color = Color.LightGray, fontSize = 12.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp))
        }
    }
}
