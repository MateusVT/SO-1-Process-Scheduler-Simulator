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
public class PQ {

    List<int[]> processes;
    int quantum;
    Long tempoTotalEspera; //Tempo total de espera
    Long tempoMediaEspera; //Tempo médio de espera
    int tempoInicio;
    int tempoAtual;
    int flagBloqueio;
    int aux;
    Integer nextP;

    public PQ(List<int[]> processes, int quantum) {
        this.processes = processes;
        this.quantum = quantum;

    }

    public void runPQ() {
        BCP bcp = new BCP();

        //Divide os processos, os processos sem a fila de io no 'procs' e as filas de io de cada processo no 'io'
        List<int[]> procs = bcp.createProcessIO(processes);
        List<int[]> io = bcp.createIO(processes);

//      Alterar a prioridade de todos os processos para 1
        for (int i = 0; i < procs.size(); i++) {
            procs.get(i)[2] = 1;
        }

        tempoAtual = 0;
        tempoTotalEspera = 0L;
        tempoMediaEspera = 0L;

        flagBloqueio = 0;
        aux = -1;

//      Inicia escalonamento 
        while (!procs.isEmpty()) {// Enquanto a fila não estiver vazia

            int next = bcp.findNextProcessByPriority(procs, tempoAtual);//Pegar o proximo processo a ser executado 

            if (next > -1) {//correncia de vacuo, nao ha processo com tempo de chegada <= ao tempo atual
                for (int i = 1; i < quantum; i++) {
                    nextP = bcp.findNextProcessByPriority(procs, tempoAtual + i);//Verifica se durante o quantum ha preempcao
                    if (nextP != next) {//Aloca o tempo no aux
                        aux = i;
                    }

                }

                if (nextP != next) {//Se o processo foi preemptado
                    //Atualiza o tempo atual e o tempo total de espera com o aux 
                    tempoAtual += aux;
                    tempoTotalEspera += aux * (procs.size() - 1);
                    //ecrementa o tempo de execucao do processo preemptado 
                    procs.get(next)[1] -= aux;

                    for (int i = 0; i < aux; i++) {
                        printGant(procs.get(next)[0]);

                    }
                    for (int i = 0; i < procs.size(); i++) {//Decrementa o tempo de bloqueio dos processos
                        if (i != next) {
                            if (procs.get(i)[4] > 0) {
                                procs.get(i)[4] -= aux;
                            }
                        }
                    }
                } else { //Se o processo nao esta bloqueado 

                    if (io.get(next)[0] > 0) {//Se o processo tem io	
                        for (int i = 1; i < quantum + 1; i++) {
                            if (((processes.get(procs.get(next)[0])[1] - procs.get(next)[1]) + i) == io.get(next)[0]) {
                                flagBloqueio = i;
                                io.set(next, ArrayUtils.remove(io.get(next), 0));
                                break;
                            }
                        }
                    }
                    if (flagBloqueio > 0) {//Se ainda resta tempo de bloqueio

                        tempoAtual += flagBloqueio;
                        procs.get(next)[1] -= flagBloqueio;//Decrementa o tempo de execucao

                        for (int i = 0; i < procs.size(); i++) {//Decrementa o tempo de bloqueio dos processos 
                            if (i != next) {
                                if (procs.get(i)[4] > 0) {
                                    procs.get(i)[4] -= flagBloqueio;
                                }

                            }
                        }

                        for (int i = 0; i < flagBloqueio; i++) {
                            printGant(procs.get(next)[0]);
                        }

                        tempoTotalEspera += (flagBloqueio * (procs.size() - 1));//Atualiza o tempo total de espera	

                        flagBloqueio = 0;//Volta a flag para 0

                        procs.get(next)[4] = 4;//Coloca o tempo de bloqueio
                        procs.get(next)[3] = 0;//Altera a prioridade para menor

                        //Coloca os processos para o final da fila
                        procs = bcp.insertEndOfQueue(procs, next);
                        io = bcp.insertEndOfQueue(io, next);

                    } else {//Se o tempo restante do processo e menor que o quantum 
                        if (procs.get(next)[1] < quantum) {//Atualiza o tempo atual e o tempo total de espera com o tempo restante
                            tempoAtual += procs.get(next)[1];
                            tempoTotalEspera += procs.get(next)[1] * (procs.size() - 1);
                            for (int i = 0; i < procs.get(next)[1]; i++) {
                                printGant(procs.get(next)[0]);

                            }
                            for (int i = 0; i < procs.size(); i++) {//Decrementa o tempo de bloqueio dos processos
                                if (i != next) {
                                    if (procs.get(i)[4] > 0) {
                                        procs.get(i)[4] -= procs.get(next)[1];
                                    }
                                }
                            }

                            //Retira o processo que terminou 
                            procs.remove(next);
                            io.remove(next);

                        } else {

                            //Atualiza o tempo atual e o tempo total de espera com o quantum 
                            tempoAtual += quantum;
                            tempoTotalEspera += (quantum * (procs.size() - 1));

                            procs.get(next)[1] -= quantum;//Decrementa o tempo de execucao

                            for (int i = 0; i < quantum; i++) {//Decrementa o tempo de espera dos processos
                                printGant(procs.get(next)[0]);

                            }

                            for (int i = 0; i < procs.size(); i++) {
                                if (i != next) {
                                    if (procs.get(i)[4] > 0) {
                                        procs.get(i)[4] -= quantum;
                                    }
                                }
                            }
                        }

                    }

                }
            } else {

                if (bcp.verifyAllBlockeds(procs)) {//Verifica se todos os processos estao bloqueados
                    tempoAtual += 1;
                    tempoTotalEspera += procs.size();

                    for (int i = 0; i < procs.size(); i++) {//Decrementa o tempo de bloqueio dos processos
                        if (procs.get(i)[4] > 0) {
                            procs.get(i)[4] -= 1;
                        }
                    }

                    System.out.println("-------");
                }

            }
        }

        for (int i = 0; i < processes.size(); i++) {
            tempoTotalEspera -= processes.get(i)[3];
        }
        tempoMediaEspera = tempoTotalEspera / processes.size();

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
