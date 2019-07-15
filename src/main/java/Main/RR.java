/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import Main.BCP;
import java.util.ArrayList;
import org.apache.commons.lang3.ArrayUtils;
import java.util.List;

/**
 *
 * @author Torres
 */
public class RR {

    List<int[]> processes;
    int quantum;
    Long tempoTotalEspera; //Tempo total de espera
    Long tempoMediaEspera; //Tempo médio de espera
    int tempoInicio;
    int tempoAtual;
    int flagBloqueio;

    public RR(List<int[]> processes, int quantum) {
        this.processes = processes;
        this.quantum = quantum;

    }

    public void runRR() {
        BCP bcp = new BCP();

//      Separa os processos sem a fila de io no 'procs' e as filas de io de cada processo no 'io'
        List<int[]> procs = bcp.createProcessIO(processes);
        List<int[]> io = bcp.createIO(processes);

        //Tempo total de espera e tempo medio de espera
        tempoTotalEspera = 0L;
        tempoMediaEspera = 0L;

        // Inicializa o tempo atual com o instante de chegada do primeiro processo
        tempoInicio = processes.get(0)[3];
        tempoAtual = tempoInicio;

        //flag utilizada para o tempo de bloqueio
        flagBloqueio = 0;

//      Inicia escalonamento 
        while (!procs.isEmpty()) {// Enquanto a fila não estiver vazia
            while (true) {
//                #Busca o processo que chegou ao sistema no tempo atual
                if ((procs.get(0)[3] < tempoAtual) || (procs.get(0)[3] == tempoInicio)) {
                    break;
                } else {
//                  Coloca o processo para o fim da fila
                    procs = bcp.insertEndOfQueue(procs, 0);
                    io = bcp.insertEndOfQueue(io, 0);
                    break;

                }
            }

            if (procs.get(0)[4] <= 0) {//Se o processo nao esta bloqueado
                if (io.get(0)[0] > 0) {
                    for (int i = 1; i < quantum + 1; i++) {
                        if (((processes.get(procs.get(0)[0])[1] - procs.get(0)[1]) + i) == io.get(0)[0]) {//Se o processo atual e o processo de io, bloquear 
                            io.set(0, ArrayUtils.remove(io.get(0), 0));
                            flagBloqueio = i;
                            break;
                        }

                    }

                }
                if (flagBloqueio > 0) {//Se flag de bloqueio for positiva 
                    //Atualiza o tempo total e decrementa o que foi executado
                    tempoAtual += flagBloqueio;
                    procs.get(0)[1] -= flagBloqueio;

                    for (int i = 1; i < procs.size(); i++) {//Decrementar o tempo de bloqueio de todos os processos io que estao bloqueados 
                        if (procs.get(i)[4] > 0) {
                            procs.get(i)[4] -= flagBloqueio;
                        }
                    }

                    for (int i = 0; i < flagBloqueio; i++) {//Print do diagrama de Gantt 
                        printGant(procs.get(0)[0]);
                    }

                    tempoTotalEspera += (flagBloqueio * (procs.size() - 1));//Atualiza o tempo total de espera
                    //Volta a flag e coloca o tempo de bloqueio
                    flagBloqueio = 0;
                    procs.get(0)[4] = 4;

                    //Coloca o processo no fim da fila 
                    procs = bcp.insertEndOfQueue(procs, 0);
                    io = bcp.insertEndOfQueue(io, 0);

                } else {
                    //Verifica se e a ultima execucao do processo e o quantum e maior que o restante do processo
                    if (procs.get(0)[1] <= quantum) {
                        //Soma o tempo da ultima execucao do processo para ao tempo atual
                        tempoAtual += procs.get(0)[1];
                        //Soma o tempo restante do processo ao tempo total de espera
                        tempoTotalEspera += (procs.get(0)[1] * (procs.size() - 1));

                        for (int i = 0; i < procs.get(0)[1]; i++) {//Print do diagrama de Gantt
                            printGant(procs.get(0)[0]);
                        }

                        for (int i = 1; i < procs.size(); i++) {//Reduzir o tempo de bloqueio de todos os processos io
                            if (procs.get(i)[4] > 0) {
                                procs.get(i)[4] -= procs.get(0)[1];
                            }
                        }

                        procs.remove(0);//Remove o processo da lista
                        io.remove(0);//Remove o processo da lista de io

                    } else {//Se der para executar o quantum
                        procs.get(0)[1] -= quantum;//Reduz o quantum da duracao do processo
                        //Atualiza o tempo de espera total e tempo atual
                        tempoTotalEspera += quantum * (procs.size() - 1);
                        tempoAtual += quantum;

                        //Print do diagrama de Gantt
                        for (int i = 0; i < quantum; i++) {
                            printGant(procs.get(0)[0]);
                        }

                        //Reduz o quantum do tempo de bloqueio de todos os processos de io 
                        for (int i = 1; i < procs.size(); i++) {
                            if (procs.get(i)[4] > 0) {
                                procs.get(i)[4] -= quantum;
                            }
                        }
                        //Coloca o processo para o fim da lista
                        procs = bcp.insertEndOfQueue(procs, 0);
                        io = bcp.insertEndOfQueue(io, 0);

                    }

                }
            } else {//Se o processo atual esta bloqueado 
                if (bcp.verifyAllBlockeds(procs)) {//Verifica se todos os processo estao bloqueados 
                    tempoAtual += 1;//Incrementa o tempo atual
                    for (int i = 0; i < procs.size(); i++) {//Decrementa o tempo de bloqueio dos processos
                        if (procs.get(i)[4] > 0) {
                            procs.get(i)[4] -= 1;
                        }
                    }
                    tempoTotalEspera += procs.size();//Atualiza o tempo de espera

//                    System.out.println("--------------------------");
                }
                //Coloca o processo para o fim da fila
                procs = bcp.insertEndOfQueue(procs, 0);
                io = bcp.insertEndOfQueue(io, 0);
            }
        }

        for (int i = 0; i < processes.size(); i++) {//Subtrai o tempo de entrada dos processos	
            tempoTotalEspera -= processes.get(i)[3];
        }
        tempoMediaEspera = tempoTotalEspera / processes.size();//Calculo do tempo medio

        System.out.println("Tempo total de espera : " + tempoTotalEspera);
        System.out.println("Tempo médio de espera : " + tempoMediaEspera);

    }

    public void printGant(int element) {
        System.out.print("Process : ");
        for (int i = 0; i < element; i++) {

            System.out.print("---");
        }
        System.out.println(" " + element);
    }

    public void createOutputFile() {
    }

}
