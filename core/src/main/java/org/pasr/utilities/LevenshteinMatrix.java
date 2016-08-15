package org.pasr.utilities;

import java.util.List;

import static java.lang.Integer.min;


public class LevenshteinMatrix<T extends Comparable<T>> {
    public LevenshteinMatrix(List<T> source, List<T> destination){
        source_ = source;
        destination_ = destination;

        int sourceSize = source_.size();
        int destinationSize = destination_.size();

        // Initialize the Levenshtein matrix.
        matrix_ = new int[destinationSize + 1][sourceSize + 1];
        for (int i = 0; i <= destinationSize; i++) {
            for (int j = 0; j <= sourceSize; j++) {
                if (j == 0 && i == 0) {
                    matrix_[0][0] = 0;
                } else if (i == 0) {
                    matrix_[0][j] = j;
                } else if (j == 0) {
                    matrix_[i][0] = i;
                } else {
                    matrix_[i][j] = 0;
                }
            }
        }

        calculateMatrix();
    }

    private void calculateMatrix(){
        int sourceSize = source_.size();
        int destinationSize = destination_.size();

        // Build the Levenshtein matrix.
        int substitutionCost;
        for (int j = 1; j <= sourceSize; j++) {
            for (int i = 1; i <= destinationSize; i++) {
                if (destination_.get(i - 1).compareTo(source_.get(j - 1)) == 0) {
                    substitutionCost = 0;
                } else {
                    substitutionCost = 1;
                }

                matrix_[i][j] = min(
                    matrix_[i - 1][j] + 1,
                    min(
                        matrix_[i][j - 1] + 1,
                        matrix_[i - 1][j - 1] + substitutionCost
                    )
                );
            }
        }

        distance_ = matrix_[destinationSize][sourceSize];

        // Find the path.
        path_ = new int[distance_][];

        int currentScore = distance_;

        int row = destinationSize;
        int column = sourceSize;

        int previousRow;
        int previousColumn;

        int leftValue;
        int aboveValue;
        int diagonalValue;

        int minValue;
        int pathIndex = 0;
        while (currentScore > 0) {
            if(row == 0 && column == 0){
                break;
            }

            previousRow = row > 0 ? row - 1 : 0;
            previousColumn = column > 0 ? column - 1 : 0;

            leftValue = matrix_[row][previousColumn];
            aboveValue = matrix_[previousRow][column];
            diagonalValue = matrix_[previousRow][previousColumn];

            minValue = min(leftValue, min(aboveValue, diagonalValue));
            if (currentScore != minValue) {
                // Note that in Levenshtein matrix, columns start counting from 1 not zero.
                path_[pathIndex] = new int[] {previousRow, previousColumn};
                pathIndex++;
            }

            if (minValue == diagonalValue && row != previousRow && column != previousColumn) {
                row--;
                column--;
            } else if (minValue == leftValue && column != previousColumn) {
                column--;
            } else {
                row--;
            }

            currentScore = minValue;
        }
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("|" + "  " + "|" + "  ");
        for(Comparable symbol : source_){
            stringBuilder.append(symbol.toString()).append("  ");
        }
        stringBuilder.append("\n");

        stringBuilder.append("|").append("  ");
        for(int i = 0, n = destination_.size();i <= n;i++){
            if(i > 0) {
                stringBuilder.append(destination_.get(i - 1).toString()).append("  ");
            }

            for(int j = 0, m = source_.size();j <= m;j++){
                if(matrix_[i][j] >= 10) {
                    stringBuilder.append(matrix_[i][j]).append(" ");
                }
                else{
                    stringBuilder.append(matrix_[i][j]).append("  ");
                }
            }
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }

    public int[][] getMatrix(){
        return matrix_;
    }

    public int[][] getPath(){
        return path_;
    }

    public int getDistance(){
        return distance_;
    }

    private List<T> source_;
    private List<T> destination_;

    private int[][] matrix_;

    private int[][] path_;

    private int distance_;

}
