import java.util.LinkedList;
import java.util.Random;

public class SimuladorSubstituicaoPaginas {
    public static void main(String[] args) {
        int[][] matrizSWAP = new int[100][6];
        int[][] matrizRAM = new int[10][6];
        Random random = new Random();

        // Preencher a matriz SWAP
        preencherMatrizSWAP(matrizSWAP, random);

        // Preencher a matriz RAM
        preencherMatrizRAM(matrizSWAP, matrizRAM, random);

        // Loop para executar cada algoritmo de substituição de página
        String[] algoritmos = {"NRU", "FIFO", "FIFO-SC", "RELÓGIO", "WS-CLOCK"};

        for (String algoritmo : algoritmos) {
            LinkedList<Integer> fifoQueue = new LinkedList<>();
            LinkedList<Integer> secondChanceQueue = new LinkedList<>();
            LinkedList<Integer> relogio = new LinkedList<>();
            int EP = random.nextInt(9900) + 100; // Valor de EP na faixa de 100 a 9999

            // Imprimir MATRIZ RAM no início
            System.out.println("MATRIZ RAM no início para o algoritmo " + algoritmo + ":");
            imprimirMatrizRAM(matrizRAM);

            for (int instrucao = 1; instrucao <= 1000; instrucao++) {
                int instrucaoRequisitada = random.nextInt(100) + 1;

                // Verificar se a página está na RAM
                int pagina = -1;
                for (int i = 0; i < 10; i++) {
                    if (matrizRAM[i][1] == instrucaoRequisitada) {
                        pagina = i;
                        break;
                    }
                }

                if (pagina != -1) {
                    // A página está na RAM
                    matrizRAM[pagina][3] = 1; // Bit de Acesso R

                    if (random.nextInt(10) < 3) {
                        // 30% de chance de modificação
                        matrizRAM[pagina][2] += 1; // Atualizar Dado (D)
                        matrizRAM[pagina][4] = 1; // Bit de Modificação M
                    }
                } else {
                    // A página não está na RAM, usar algoritmo de substituição
                    if (algoritmo.equals("NRU")) {
                        substituirPaginaNRU(matrizRAM, matrizSWAP, random);
                    } else if (algoritmo.equals("FIFO")) {
                        substituirPaginaFIFO(matrizRAM, fifoQueue, matrizSWAP, random);
                    } else if (algoritmo.equals("FIFO-SC")) {
                        substituirPaginaFIFOSC(matrizRAM, fifoQueue, secondChanceQueue, matrizSWAP, random);
                    } else if (algoritmo.equals("RELÓGIO")) {
                        substituirPaginaRelogio(matrizRAM, relogio, matrizSWAP, random);
                    } else if (algoritmo.equals("WS-CLOCK")) {
                        substituirPaginaWSClock(matrizRAM, relogio, random, EP, matrizSWAP);
                    }
                }

                if (instrucao % 10 == 0) {
                    // Zerar o Bit de Acesso R para todas as páginas na RAM
                    for (int i = 0; i < 10; i++) {
                        matrizRAM[i][3] = 0; // Bit de Acesso R
                    }
                }

                if (instrucao % 100 == 0) {
                    // Imprimir MATRIZ RAM a cada 100 instruções
                    System.out.println("MATRIZ RAM após " + instrucao + " instruções para o algoritmo " + algoritmo + ":");
                    imprimirMatrizRAM(matrizRAM);
                }
            }

            // Imprimir MATRIZ RAM no final da execução
            System.out.println("MATRIZ RAM ao final da execução do algoritmo " + algoritmo + ":");
            imprimirMatrizRAM(matrizRAM);
        }
    }

    private static void preencherMatrizSWAP(int[][] matrizSWAP, Random random) {
        for (int i = 0; i < 100; i++) {
            matrizSWAP[i][0] = i; // Número de Página (N)
            matrizSWAP[i][1] = i + 1; // Instrução (I)
            matrizSWAP[i][2] = random.nextInt(50) + 1; // Dado (D) aleatório
            matrizSWAP[i][3] = 0; // Bit de Acesso R
            matrizSWAP[i][4] = 0; // Bit de Modificação M
            matrizSWAP[i][5] = random.nextInt(9900) + 100; // Tempo de Envelhecimento (T)
        }
    }

    private static void preencherMatrizRAM(int[][] matrizSWAP, int[][] matrizRAM, Random random) {
        for (int i = 0; i < 10; i++) {
            int pagina = random.nextInt(100);
            for (int j = 0; j < 6; j++) {
                matrizRAM[i][j] = matrizSWAP[pagina][j];
            }
        }
    }

    private static void imprimirMatrizRAM(int[][] matrizRAM) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 6; j++) {
                System.out.print(matrizRAM[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private static void substituirPaginaNRU(int[][] matrizRAM, int[][] matrizSWAP, Random random) {
        int classeR0M0 = -1;
        int classeR0M1 = -1;
        int classeR1M0 = -1;
        int classeR1M1 = -1;

        // Identificar páginas em cada classe
        for (int i = 0; i < 10; i++) {
            int bitR = matrizRAM[i][3]; // Bit de Acesso R
            int bitM = matrizRAM[i][4]; // Bit de Modificação M

            if (bitR == 0 && bitM == 0) {
                classeR0M0 = i;
            } else if (bitR == 0 && bitM == 1) {
                classeR0M1 = i;
            } else if (bitR == 1 && bitM == 0) {
                classeR1M0 = i;
            } else if (bitR == 1 && bitM == 1) {
                classeR1M1 = i;
            }
        }

        // Prioridade de seleção das classes: R=0,M=0 > R=0,M=1 > R=1,M=0 > R=1,M=1
        if (classeR0M0 != -1) {
            realizarSubstituicao(matrizRAM, classeR0M0, matrizSWAP, random);
        } else if (classeR0M1 != -1) {
            realizarSubstituicao(matrizRAM, classeR0M1, matrizSWAP, random);
        } else if (classeR1M0 != -1) {
            realizarSubstituicao(matrizRAM, classeR1M0, matrizSWAP, random);
        } else if (classeR1M1 != -1) {
            realizarSubstituicao(matrizRAM, classeR1M1, matrizSWAP, random);
        }
    }

    private static void realizarSubstituicao(int[][] matrizRAM, int paginaASubstituir, int[][] matrizSWAP, Random random) {
        // Remover a página da RAM
        for (int i = 0; i < 10; i++) {
            if (matrizRAM[i][0] == paginaASubstituir) {
                // Você encontrou a página a ser substituída na RAM

                // Substituir a página por outra da SWAP (por exemplo, a primeira página na SWAP)
                int paginaNaSwap = matrizSWAP[0][0];
                for (int j = 0; j < 6; j++) {
                    matrizRAM[i][j] = matrizSWAP[paginaNaSwap][j];
                }

                // Atualizar a fila FIFO, removendo a página substituída e adicionando a nova página
                // Adicione a nova página à fila FIFO
                // Não é necessário adicionar a página substituída de volta à fila, pois a NRU já cuida da escolha da vítima

                break; // Saia do loop, pois a página foi encontrada e substituída
            }
        }
    }

    private static void substituirPaginaFIFO(int[][] matrizRAM, LinkedList<Integer> fifoQueue, int[][] matrizSWAP, Random random) {
        if (!fifoQueue.isEmpty()) {
            Integer paginaMaisAntiga = fifoQueue.poll();
            if (paginaMaisAntiga != null) {
                // Encontre a página a ser substituída na RAM (por exemplo, a primeira página da fila FIFO)
                int paginaASubstituir = paginaMaisAntiga;

                // Substitua a página por outra da SWAP (por exemplo, a primeira página na SWAP)
                int paginaNaSwap = matrizSWAP[0][0];
                for (int i = 0; i < 6; i++) {
                    matrizRAM[paginaASubstituir][i] = matrizSWAP[paginaNaSwap][i];
                }

                // Atualize a fila FIFO, adicionando a nova página à fila FIFO
                fifoQueue.offer(paginaNaSwap);
            } else {
                // A fila estava vazia, faça o tratamento apropriado, se necessário
                // Neste exemplo, você poderia escolher a próxima página da SWAP, mas isso depende de como você deseja lidar com isso.
            }
        }
    }

    private static void substituirPaginaFIFOSC(int[][] matrizRAM, LinkedList<Integer> fifoQueue, LinkedList<Integer> secondChanceQueue, int[][] matrizSWAP, Random random) {
        while (!fifoQueue.isEmpty()) {
            Integer paginaMaisAntiga = fifoQueue.poll();
            Integer segundaChance = secondChanceQueue.poll();
            
            if (paginaMaisAntiga != null && segundaChance != null) {
                if (segundaChance == 0) {
                    // Encontre a página a ser substituída na RAM (por exemplo, a primeira página da fila FIFO)
                    int paginaASubstituir = paginaMaisAntiga;

                    // Substitua a página por outra da SWAP (por exemplo, a primeira página na SWAP)
                    int paginaNaSwap = matrizSWAP[0][0];
                    for (int i = 0; i < 6; i++) {
                        matrizRAM[paginaASubstituir][i] = matrizSWAP[paginaNaSwap][i];
                    }

                    // Atualize a fila FIFO, adicionando a nova página à fila FIFO
                    fifoQueue.offer(paginaNaSwap);
                } else {
                    // Dê uma segunda chance para esta página
                    fifoQueue.offer(paginaMaisAntiga);
                    secondChanceQueue.offer(0);
                }
            } else {
                // A fila estava vazia, faça o tratamento apropriado, se necessário
                // Neste exemplo, você poderia escolher a próxima página da SWAP, mas isso depende de como você deseja lidar com isso.
            }
        }
    }

    private static void substituirPaginaRelogio(int[][] matrizRAM, LinkedList<Integer> relogio, int[][] matrizSWAP, Random random) {
        while (!relogio.isEmpty()) {
            Integer pagina = relogio.poll();
            if (pagina != null) {
                int bitR = matrizRAM[pagina][3]; // Bit de Acesso R

                if (bitR == 0) {
                    // Encontre a página a ser substituída na RAM (por exemplo, a próxima página no relógio)
                    int paginaASubstituir = pagina;

                    // Substitua a página por outra da SWAP (por exemplo, a primeira página na SWAP)
                    int paginaNaSwap = matrizSWAP[0][0];
                    for (int i = 0; i < 6; i++) {
                        matrizRAM[paginaASubstituir][i] = matrizSWAP[paginaNaSwap][i];
                    }

                    // Atualize o relógio, movendo para a próxima posição
                    relogio.offer((pagina + 1) % 10);
                    return;
                } else {
                    // Define o Bit R de volta para 0
                    matrizRAM[pagina][3] = 0;
                }
            } else {
                // A fila estava vazia, faça o tratamento apropriado, se necessário
                // Neste exemplo, você poderia escolher a próxima página da SWAP, mas isso depende de como você deseja lidar com isso.
            }
        }
    }

    private static void substituirPaginaWSClock(int[][] matrizRAM, LinkedList<Integer> relogio, Random random, int EP, int[][] matrizSWAP) {
        while (!relogio.isEmpty()) {
            Integer pagina = relogio.poll();
            if (pagina != null) {
                int bitR = matrizRAM[pagina][3]; // Bit de Acesso R
                int tempoEnvelhecimento = matrizRAM[pagina][5]; // Tempo de Envelhecimento T

                if (bitR == 0 && tempoEnvelhecimento <= EP) {
                    // Encontre a página a ser substituída na RAM (por exemplo, a próxima página no relógio)
                    int paginaASubstituir = pagina;

                    // Substitua a página por outra da SWAP (por exemplo, a primeira página na SWAP)
                    int paginaNaSwap = matrizSWAP[0][0];
                    for (int i = 0; i < 6; i++) {
                        matrizRAM[paginaASubstituir][i] = matrizSWAP[paginaNaSwap][i];
                    }

                    // Atualize o relógio, movendo para a próxima posição
                    relogio.offer((pagina + 1) % 10);
                    return;
                } else {
                    // Define o Bit R de volta para 0
                    matrizRAM[pagina][3] = 0;
                }
            } else {
                // A fila estava vazia, faça o tratamento apropriado, se necessário
                // Neste exemplo, você poderia escolher a próxima página da SWAP, mas isso depende de como você deseja lidar com isso.
            }
        }
    }
}