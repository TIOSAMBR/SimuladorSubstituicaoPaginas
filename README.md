# SimuladorSubstituicaoPaginas

O código é um simulador de substituição de páginas de memória que implementa vários algoritmos de substituição de página. Ele simula o comportamento de um sistema de gerenciamento de memória, onde as páginas são movidas entre a RAM e a área de SWAP.

O programa segue os seguintes passos:

1.Inicializa matrizes representando a RAM e a SWAP com dados fictícios.
2.Executa um loop que simula a execução de 1000 instruções.
3.Em cada iteração do loop, uma instrução é requisitada, e o programa verifica se a página correspondente à instrução está na RAM.
4.Se a página não estiver na RAM, um algoritmo de substituição é acionado com base no algoritmo atual (NRU, FIFO, FIFO-SC, RELÓGIO, WS-CLOCK).
5.O algoritmo de substituição determina qual página da RAM será substituída pela página da SWAP, se necessário.
6.A cada 10 instruções, o bit de acesso R é zerado para todas as páginas na RAM.
7.A cada 100 instruções, a matriz RAM é impressa para observar seu estado.
8.O processo continua até que as 1000 instruções sejam simuladas para cada algoritmo de substituição.

No final, o estado final da matriz RAM é impresso para cada algoritmo de substituição.
Em resumo, o código demonstra como diferentes algoritmos de substituição de página (NRU, FIFO, FIFO-SC, RELÓGIO, WS-CLOCK) lidam com a movimentação de páginas entre a RAM e a SWAP durante a execução de um programa, mostrando como as decisões de substituição são tomadas e como as páginas são gerenciadas ao longo do tempo.
