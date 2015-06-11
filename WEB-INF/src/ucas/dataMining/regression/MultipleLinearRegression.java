package ucas.dataMining.regression;

import Jama.Matrix;
import Jama.QRDecomposition;

public class MultipleLinearRegression {
    private final int N;        // ��Ŀ���� 
    private final int p;        // ��������
    private final Matrix beta;  // �ع����
    private double SSE;         // sum of squared
    private double SST;         // sum of squared

    public MultipleLinearRegression(double[][] x, double[] y) {
        if (x.length != y.length) throw new RuntimeException("dimensions don't agree");
        N = y.length;
        p = x[0].length;

        Matrix X = new Matrix(x);

        // ��������������
        Matrix Y = new Matrix(y, N);

        // �Ȼ�þ����QR�ֽ⣬Ȼ������С���˷����ع�ϵ��
        QRDecomposition qr = new QRDecomposition(X);
        beta = qr.solve(Y);


        // mean of y[] values
        double sum = 0.0;
        for (int i = 0; i < N; i++)
            sum += y[i];
        double mean = sum / N;

        // total variation to be accounted for
        for (int i = 0; i < N; i++) {
            double dev = y[i] - mean;
            SST += dev*dev;
        }

        // variation not accounted for
        Matrix residuals = X.times(beta).minus(Y);
        SSE = residuals.norm2() * residuals.norm2();

    }

    public double beta(int j) {
        return beta.get(j, 0);
    }

    public double R2() {
        return 1.0 - SSE/SST;
    }

    public static void main(String[] args) {
        double[][] x = { {  1,  10,  20 },
                         {  1,  20,  40 },
                         {  1,  40,  15 },
                         {  1,  80, 100 },
                         {  1, 160,  23 },
                         {  1, 200,  18 } };
        double[] y = { 243, 483, 508, 1503, 1764, 2129 };
        MultipleLinearRegression regression = new MultipleLinearRegression(x, y);

        String result = String.format("%.2f + %.2f beta1 + %.2f beta2  (R^2 = %.2f)\n",
                      regression.beta(0), regression.beta(1), regression.beta(2), regression.R2());
        
        //System.out.println(result);
    }
}

