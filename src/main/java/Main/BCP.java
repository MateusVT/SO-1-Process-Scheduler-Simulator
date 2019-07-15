/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Torres
 */
public class BCP {

//  Verifica se ocorre bloqueio em todos os processos
    public boolean verifyAllBlockeds(List<int[]> processes) {
        boolean blocked = true;
        for (int i = 0; i < processes.size(); i++) {
            if (processes.get(i)[4] <= 0) {
                blocked = false;
            }
        }
        return blocked;
    }

//Insere o processo[index] no final da lista
    public List<int[]> insertEndOfQueue(List<int[]> queue, int index) {
        int[] element = queue.get(index);
        queue.remove(index);
        queue.add(element);
//        System.out.println(Arrays.toString(element));
//        printProcesses(queue);

        return queue;

    }

//Retorna o index do proximo processo a ser executado pela maior prioridade
    public int findNextProcessByPriority(List<int[]> procs, int tempoAtual) {

        Integer arrive = null;
        int bestIndex = -1;

        for (int i = 0; i < procs.size(); i++) {

            if (procs.get(i)[3] <= tempoAtual && procs.get(i)[4] <= 0) {

                if (procs.get(i)[2] == 1) {

                    if (arrive == null) {
                        arrive = procs.get(i)[3];
                        bestIndex = i;
                    } else if (procs.get(i)[3] < arrive) {
                        arrive = procs.get(i)[3];
                        bestIndex = i;

                    }
                } else if (procs.get(i)[2] == 0) {

                    if (arrive == null) {
                        arrive = procs.get(i)[3];
                        bestIndex = i;
                    } else if (procs.get(i)[3] < 3) {
                        arrive = procs.get(i)[3];
                        bestIndex = i;
                    }
                }
            }
        }
        return bestIndex;
    }
//Retorna o index do próximo processo a ser executado

    public int findNextProcess(List<int[]> procs, int tempoAtual) {
        int bestIndex = -1;
        Integer shorterDuration = null;

        for (int i = 0; i < procs.size(); i++) {

            if (procs.get(i)[3] <= tempoAtual) {//Verifica se o processo já entrou no sistema  

                if (shorterDuration == null) {//Verifica se é o primeiro menor que o tempo atual 
                    shorterDuration = procs.get(i)[1];
                    bestIndex = i;
                } else {

                    if (procs.get(i)[1] < shorterDuration) {//Se encontrar o tempo de duracao menor que o tempo do melhor até então, atualiza o melhor 
                        shorterDuration = procs.get(i)[1];
                        bestIndex = i;
                        //Em caso de empate
                        //Se o tempo de duração é igual ao melhor até o momento, verifica o instante de chegada e o primeiro a ter chegado é escolhido
                    } else if (procs.get(i)[1] == shorterDuration) {
                        if (procs.get(i)[3] < procs.get(bestIndex)[3]) {
                            bestIndex = i;
                            shorterDuration = procs.get(i)[1];
                        }
                    }
                }
            }
        }
        return bestIndex;
    }

//    Encontra o index do próximo a executar que não esteja bloqueado
    public int findNextBlockProcess(List<int[]> procs, int tempoAtual) {
//    public int findNextBlockProcess(List<int[]> procs, int tempoAtual, boolean block) {
        Integer shorterDuration = null;
        int bestIndex = -1;

        for (int i = 0; i < procs.size(); i++) {

//            if (procs.get(i)[3] <= tempoAtual && (block && procs.get(i)[4] <= 0 || !block)) {
            if (procs.get(i)[3] <= tempoAtual && procs.get(i)[4] <= 0) {  //Verifica se o processo já entrou no sistema  

                if (shorterDuration == null) { //Verifica se é o primeiro menor que o tempo atual 
                    shorterDuration = procs.get(i)[1];
                    bestIndex = i;
                } else {

                    if (procs.get(i)[1] < shorterDuration) {//Se encontrar o tempo de duracao menor que o tempo do melhor até então, atualiza o melhor  
                        shorterDuration = procs.get(i)[1];
                        bestIndex = i;
                        //Em caso de empate
                        //Se o tempo de duração é igual ao melhor até o momento, verifica o instante de chegada e o primeiro a ter chegado é escolhido
                    } else if (procs.get(i)[1] == shorterDuration) {
                        if (procs.get(i)[3] < procs.get(bestIndex)[3]) {
                            bestIndex = i;
                            shorterDuration = procs.get(i)[1];
                        }
                    }
                }
            }
        }
        return bestIndex;
    }

//Printa os processos formatados
    public void printProcesses(List<int[]> queue) {
        for (int i = 0; i < queue.size(); i++) {
            String print = null;
            print += "[";
            for (int vect : queue.get(i)) {
                print += vect + ", ";
            }
            print += "]";
            System.out.println(print.replace(", ]", "]"));
            System.out.print(", ");
        }

    }

//  Aloca o restante do processo sem as I/O em outra lista
    public List<int[]> createProcessIO(List<int[]> processes) {
        List<int[]> proc = new ArrayList();
        for (int i = 0; i < processes.size(); i++) {
            proc.add(new int[5]);
            for (int j = 0; j < 4; j++) {
                proc.get(i)[j] = processes.get(i)[j];
            }
            proc.get(i)[4] = 0;
        }
        return proc;

    }

// Separa as I/O em uma lista, cada processo possui uma lista de I/O, caso não tenha é colocado -1
    public List<int[]> createIO(List<int[]> processes) {
        List<int[]> io = new ArrayList();
        for (int i = 0; i < processes.size(); i++) {
            io.add(new int[processes.get(i).length - 3]);//Precisa ser -3 para alocar o último elemento

            if (processes.get(i).length > 4) {
                for (int j = 4, k = 0; j < processes.get(i).length; j++, k++) {
                    io.get(i)[k] = processes.get(i)[j];
                }
            }
            io.get(i)[io.get(i).length - 1] = -1;
        }
        return io;

    }

}
