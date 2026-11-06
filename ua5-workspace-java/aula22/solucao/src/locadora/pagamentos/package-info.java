/**
 * Módulo PAGAMENTOS — arquitetura MICROKERNEL (plugins).
 *
 * O núcleo (SistemaPagamentos) não conhece nenhuma forma de pagamento. Cada forma é
 * um plugin que implementa PluginPagamento e é DESCOBERTO pelo java.util.ServiceLoader,
 * que lê o arquivo src/META-INF/services/locadora.pagamentos.PluginPagamento
 * (é assim que o Java acha drivers JDBC, por exemplo).
 *
 * Para aceitar boleto: crie plugins/Boleto.java e acrescente UMA linha naquele
 * arquivo — sem alterar uma linha do núcleo (OCP em nível de arquitetura).
 */
package locadora.pagamentos;
