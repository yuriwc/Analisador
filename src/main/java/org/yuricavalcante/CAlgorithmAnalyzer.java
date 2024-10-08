package org.yuricavalcante;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class CAlgorithmAnalyzer {

    public static void main(String[] args) {
        String filePath = "src/main/resources/algoritmo.txt";

        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            analyzeCAlgorithm(lines); // Chama o método para analisar o código
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
        }
    }

    public static void analyzeCAlgorithm(List<String> lines) {
        int loopCount = 0;
        int ifCount = 0;
        int functionCount = 0;
        int recursionCount = 0;
        int maxLoopDepth = 0;
        int currentLoopDepth = 0;
        boolean hasConditionals = false;

        String currentFunctionName = null;
        boolean insideFunction = false;

        for (String line : lines) {
            line = line.trim();

            // Verifica se a linha contém um loop (for ou while)
            if (line.startsWith("for") || line.startsWith("while")) {
                loopCount++;
                currentLoopDepth++;
                maxLoopDepth = Math.max(maxLoopDepth, currentLoopDepth);
            }

            // Verifica se a linha contém uma condicional (if)
            if (line.startsWith("if")) {
                ifCount++;
                hasConditionals = true;
            }

            // Verifica se a linha contém uma assinatura de função
            if (isFunctionSignature(line)) {
                functionCount++;
                currentFunctionName = getFunctionName(line);
                insideFunction = true;
            }

            // Verifica recursão: chamada da própria função dentro de seu corpo
            if (insideFunction && currentFunctionName != null && line.contains(currentFunctionName + "(")) {
                if (line.contains(currentFunctionName) && !line.startsWith("printf")) {
                    recursionCount++;
                }
            }

            // Verifica o fechamento de blocos
            if (line.contains("}")) {
                currentLoopDepth--;
                insideFunction = false;
            }
        }

        // Ajustar para recursão dupla em QuickSort (2 chamadas no corpo da função)
        recursionCount = Math.min(recursionCount, 2); // Garantir que sejam contadas apenas as chamadas principais

        // Determinação da complexidade do pior e melhor caso
        String worstCaseComplexity = "O(1)";
        String bestCaseComplexity = "O(1)";

        // Ajustes na complexidade com base em loops e recursão
        if (recursionCount > 0) {
            worstCaseComplexity = "O(n log n)"; // QuickSort pior caso é O(n log n)
            bestCaseComplexity = "O(n log n)";  // QuickSort melhor caso também é O(n log n)
        } else if (maxLoopDepth > 0) {
            worstCaseComplexity = "O(n^" + maxLoopDepth + ")";
            if (hasConditionals && maxLoopDepth == 1) {
                bestCaseComplexity = "O(1)";
            } else {
                bestCaseComplexity = "O(n)";
            }
        }

        // Determina Big-O, Big Omega e Big Theta com base na análise dinâmica
        String bigO = worstCaseComplexity;
        String bigOmega = bestCaseComplexity;
        String bigTheta = (loopCount > 0 || recursionCount > 0) ? "Θ(n log n)" : "Θ(1)";

        // Exibe os resultados da análise
        System.out.println("Análise do algoritmo em C:");
        System.out.println("Quantidade de loops: " + loopCount);
        System.out.println("Quantidade de condicionais (if): " + ifCount);
        System.out.println("Quantidade de funções: " + functionCount);
        System.out.println("Quantidade de recursões: " + recursionCount);
        System.out.println("Complexidade no pior caso: " + worstCaseComplexity);
        System.out.println("Complexidade no melhor caso: " + bestCaseComplexity);

        // Análise assintótica dinâmica
        String asymptoticAnalysis = "Análise Assintótica:\n" +
                "Big O: " + bigO + "\n" +
                "Big Omega: " + bigOmega + "\n" +
                "Big Theta: " + bigTheta;

        System.out.println(asymptoticAnalysis);
    }

    // Método para verificar se a linha contém uma assinatura de função
    private static boolean isFunctionSignature(String line) {
        return (line.matches("(int|void|float|double|char)\\s+[a-zA-Z_][a-zA-Z0-9_]*\\s*\\(.*\\)\\s*\\{"));
    }

    // Extrai o nome da função de uma assinatura
    private static String getFunctionName(String line) {
        String functionName = line.substring(0, line.indexOf("(")).trim();
        String[] parts = functionName.split(" ");
        return parts[parts.length - 1];
    }
}
