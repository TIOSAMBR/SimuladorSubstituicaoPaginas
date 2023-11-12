import java.util.ArrayList;
import java.util.Random;

class Page {
    int pageNumber;
    int instruction;
    int data;
    int accessBit;
    int modifyBit;
    int agingTime;

    public Page(int pageNumber, int instruction, int data, int agingTime) {
        this.pageNumber = pageNumber;
        this.instruction = instruction;
        this.data = data;
        this.accessBit = 0;
        this.modifyBit = 0;
        this.agingTime = agingTime;
    }
}

public class PageReplacementSimulator {
    public static void main(String[] args) {
        ArrayList<Page> ram = new ArrayList<>();
        ArrayList<Page> swap = new ArrayList<>();

        // Inicialização das matrizes SWAP e RAM
        initializeSwap(swap);
        initializeRam(ram, swap);

        // Simular a execução de 1000 instruções
        for (int i = 0; i < 1000; i++) {
            int pageToAccess = new Random().nextInt(100) + 1;
            boolean found = false;

            // Verificar se a página está na RAM
            for (Page page : ram) {
                if (page.instruction == pageToAccess) {
                    page.accessBit = 1;

                    if (Math.random() <= 0.3) {
                        page.data += 1;
                        page.modifyBit = 1;
                    }

                    found = true;
                    break;
                }
            }

            if (!found) {
                // Página não está na RAM, escolher um algoritmo de substituição
                int algorithmToUse = new Random().nextInt(5); // Escolha aleatoriamente entre 0 a 4
            
                switch (algorithmToUse) {
                    case 0:
                        nruAlgorithm(ram, swap, pageToAccess);
                        break;
                    case 1:
                        fifoAlgorithm(ram, swap, pageToAccess);
                        break;
                    case 2:
                        fifoScAlgorithm(ram, swap, pageToAccess);
                        break;
                    case 3:
                        relogioAlgorithm(ram, swap, pageToAccess);
                        break;
                    case 4:
                        wsClockAlgorithm(ram, swap, pageToAccess);
                        break;
                    // Adicione mais casos se tiver mais algoritmos
                }
            }
            

            if (i % 10 == 9) {
                // A cada 10 instruções, resetar o bit R
                resetAccessBit(ram);
            }
        }

        // Salvar páginas modificadas em SWAP
        saveModifiedPages(ram, swap);

        // Imprimir as matrizes no final
        printMatrices(ram, swap);
    }

    // Implemente os algoritmos de substituição de página (NRU, FIFO, FIFO-SC, RELÓGIO, WS-CLOCK) aqui
// Algoritmo NRU
private static void nruAlgorithm(ArrayList<Page> ram, ArrayList<Page> swap, int pageToAccess) {
    // Selecionar as páginas em quatro classes com base no bit de acesso e no bit de modificação
    ArrayList<Page> class0 = new ArrayList<>();
    ArrayList<Page> class1 = new ArrayList<>();
    ArrayList<Page> class2 = new ArrayList<>();
    ArrayList<Page> class3 = new ArrayList<>();

    for (Page page : ram) {
        int classBits = (page.accessBit << 1) | page.modifyBit;
        switch (classBits) {
            case 0:
                class0.add(page);
                break;
            case 1:
                class1.add(page);
                break;
            case 2:
                class2.add(page);
                break;
            case 3:
                class3.add(page);
                break;
        }
    }

    // Selecionar uma página para substituição com base nas classes
    // Implemente a lógica de seleção, por exemplo, aleatoriamente
    ArrayList<Page> selectedClass = new ArrayList<>();
    selectedClass.addAll(class0);
    selectedClass.addAll(class1);
    selectedClass.addAll(class2);
    selectedClass.addAll(class3);

    if (!selectedClass.isEmpty()) {
        int randomIndex = new Random().nextInt(selectedClass.size());
        Page pageToReplace = selectedClass.get(randomIndex);

        // Verificar se a página a ser substituída está modificada e, se for o caso, salvá-la em SWAP
        if (pageToReplace.modifyBit == 1) {
            swap.add(new Page(pageToReplace.pageNumber, pageToReplace.instruction, pageToReplace.data, pageToReplace.agingTime));
        }

        // Carregar a nova página
        if (!swap.isEmpty()) {
            int randomSwapIndex = new Random().nextInt(swap.size());
            Page newPage = swap.get(randomSwapIndex);
            newPage.accessBit = 1;
            ram.add(newPage);
            swap.remove(randomSwapIndex);
        } else {
            // Lógica para lidar com o caso em que a lista swap está vazia
        }

        // Remover a página substituída da classe selecionada
        selectedClass.remove(pageToReplace);
    }
}


    // Algoritmo FIFO
    private static void fifoAlgorithm(ArrayList<Page> ram, ArrayList<Page> swap, int pageToAccess) {
        if (!ram.isEmpty()) {
            // Encontre a página mais antiga na RAM para substituição (a primeira inserida)
            Page pageToReplace = ram.get(0);
    
            // Verifique se a página a ser substituída está modificada e, se for o caso, salve-a em SWAP
            if (pageToReplace.modifyBit == 1) {
                swap.add(new Page(pageToReplace.pageNumber, pageToReplace.instruction, pageToReplace.data, pageToReplace.agingTime));
            }
    
            // Restante do código para substituir a página e carregar a nova página
        } else {
            // Lógica para lidar com o caso em que a lista ram está vazia
        }
    }
    


    // Algoritmo FIFO-SC
    private static void fifoScAlgorithm(ArrayList<Page> ram, ArrayList<Page> swap, int pageToAccess) {
        if (!ram.isEmpty()) {
            // Encontre a página mais antiga na RAM para substituição (a primeira inserida)
            Page pageToReplace = ram.get(0);
    
            // Verifique o bit de acesso (segunda chance)
            while (pageToReplace.accessBit == 1) {
                // Dê uma segunda chance, redefina o bit de acesso e mova a página para o final
                pageToReplace.accessBit = 0;
                ram.remove(0);
                ram.add(pageToReplace);
    
                // Se não for a página desejada, continue a busca
                pageToReplace = ram.get(0);
            }
    
            // Verificar se a página a ser substituída está modificada e, se for o caso, salve-a em SWAP
            if (pageToReplace.modifyBit == 1) {
                swap.add(new Page(pageToReplace.pageNumber, pageToReplace.instruction, pageToReplace.data, pageToReplace.agingTime));
            }
    
            // Verifique se a lista SWAP não está vazia antes de gerar um índice aleatório
            if (!swap.isEmpty()) {
                // Carregue a nova página a partir de SWAP
                int randomSwapIndex = new Random().nextInt(swap.size());
                Page newPage = swap.get(randomSwapIndex);
                newPage.accessBit = 1;
                ram.set(0, newPage);
                swap.remove(randomSwapIndex);
            } else {
                // Lógica para lidar com o caso em que a lista swap está vazia
            }
        } else {
            // Lógica para lidar com o caso em que a lista ram está vazia
        }
    }
    


    // Algoritmo RELÓGIO
    private static void relogioAlgorithm(ArrayList<Page> ram, ArrayList<Page> swap, int pageToAccess) {
        // Verifique se a lista 'ram' não está vazia
        if (!ram.isEmpty()) {
            // Encontre a página mais antiga com bit de acesso 0
            Page pageToReplace = null;
            int currentIndex = 0;
    
            while (pageToReplace == null) {
                Page currentPage = ram.get(currentIndex);
    
                if (currentPage.accessBit == 0) {
                    pageToReplace = currentPage;
                } else {
                    currentPage.accessBit = 0;
                    currentIndex = (currentIndex + 1) % ram.size();
                }
            }
    
            // Verifique se a página a ser substituída está modificada e, se for o caso, salve-a em SWAP
            if (pageToReplace.modifyBit == 1) {
                swap.add(new Page(pageToReplace.pageNumber, pageToReplace.instruction, pageToReplace.data, pageToReplace.agingTime));
            }
    
            // Carregue a nova página a partir de SWAP
            if (!swap.isEmpty()) {
                int randomSwapIndex = new Random().nextInt(swap.size());
                Page newPage = swap.get(randomSwapIndex);
                newPage.accessBit = 1;
                ram.set(currentIndex, newPage);
                swap.remove(randomSwapIndex);
            } else {
                // Lógica para lidar com o caso em que a lista swap está vazia
            }
        }
    }
    


    // Algoritmo WS-CLOCK
private static void wsClockAlgorithm(ArrayList<Page> ram, ArrayList<Page> swap, int pageToAccess) {
    // Selecionar aleatoriamente uma página da RAM
    int randomIndex = new Random().nextInt(ram.size());
    Page currentPage = ram.get(randomIndex);

    // Verificar o envelhecimento (EP) da página
    if (currentPage.agingTime <= 0) {
        // Página não faz parte do conjunto de trabalho, substitua
        // Implemente a lógica de seleção da página a ser substituída, por exemplo, com base no envelhecimento
        Page pageToReplace = currentPage; // Substitua esta linha pela lógica de seleção

        // Verifique se a página a ser substituída está modificada e, se for o caso, salve-a em SWAP
        if (pageToReplace.modifyBit == 1) {
            swap.add(new Page(pageToReplace.pageNumber, pageToReplace.instruction, pageToReplace.data, pageToReplace.agingTime));
        }

        // Carregue a nova página a partir de SWAP
        int randomSwapIndex = new Random().nextInt(swap.size());
        Page newPage = swap.get(randomSwapIndex);
        newPage.accessBit = 1;
        ram.set(randomIndex, newPage);
        swap.remove(randomSwapIndex);
    } else {
        // A página faz parte do conjunto de trabalho, atualize seu tempo de envelhecimento
        currentPage.agingTime--;
    }
}


    // Função auxiliar para inicializar a matriz SWAP
    private static void initializeSwap(ArrayList<Page> swap) {
        for (int i = 0; i < 100; i++) {
            int pageNumber = i;
            int instruction = i + 1;
            int data = new Random().nextInt(50) + 1;
            int agingTime = new Random().nextInt(9900) + 100;
            swap.add(new Page(pageNumber, instruction, data, agingTime));
        }
    }

    // Função auxiliar para inicializar a matriz RAM
    private static void initializeRam(ArrayList<Page> ram, ArrayList<Page> swap) {
        for (int i = 0; i < 10; i++) {
            int randomIndex = new Random().nextInt(swap.size());
            Page page = swap.get(randomIndex);
            page.accessBit = 1;
            ram.add(page);
            swap.remove(randomIndex);
        }
    }

    // Função auxiliar para imprimir as matrizes RAM e SWAP
    private static void printMatrices(ArrayList<Page> ram, ArrayList<Page> swap) {
        System.out.println("MATRIZ RAM:");
        for (Page page : ram) {
            System.out.println("N: " + page.pageNumber + " I: " + page.instruction + " D: " + page.data +
                    " R: " + page.accessBit + " M: " + page.modifyBit + " T: " + page.agingTime);
        }

        System.out.println("\nMATRIZ SWAP:");
        for (Page page : swap) {
            System.out.println("N: " + page.pageNumber + " I: " + page.instruction + " D: " + page.data +
                    " R: " + page.accessBit + " M: " + page.modifyBit + " T: " + page.agingTime);
        }
    }

    // Função auxiliar para resetar o bit de acesso (R) em todas as páginas
    private static void resetAccessBit(ArrayList<Page> ram) {
        for (Page page : ram) {
            page.accessBit = 0;
        }
    }

    // Função auxiliar para salvar páginas modificadas em SWAP
    private static void saveModifiedPages(ArrayList<Page> ram, ArrayList<Page> swap) {
        for (Page page : ram) {
            if (page.modifyBit == 1) {
                swap.add(new Page(page.pageNumber, page.instruction, page.data, page.agingTime));
                page.modifyBit = 0;
            }
        }
    }
    }