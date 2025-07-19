import javax.swing.text.html.HTMLDocument;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Random;

public class Main {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static int[][] H,P,G;
    public static int k,r,n;
    public static void printAlphabet(String[] a){
        for (String str : a) {
            Blueprint("\t"+str+"        ");
        }
        Blueprint("?");
    }

    public static void Blueprint(String text){
        System.out.print(ANSI_BLUE + text + ANSI_RESET);
    }

    public static double sumInString(double[][] arr, int index, int len){
        double sum = 0;
        for (int stolb = 0; stolb < len; stolb++) {
            sum += arr[index][stolb];
        }
        return sum;
    }

    // округялем до 5 знаков
    public static double returnNormal(double value){
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(5, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }

    public static void printCanalMatrix(double[][] kmi, int len, String[] a, String name){
        System.out.println("\n\n\t\t\t\t\t\t\t\t\t\t\t"+ name);
        printAlphabet(a);
        System.out.print("\n"+"-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------" + "\n");
        boolean flag;
        for (int i = 0; i < len; i++) {
            flag = true;
            for (int j = 0; j <= len; j++) {
                if(flag){
                    Blueprint(a[i]);
                    System.out.print("\t" + String.format("%.5f",kmi[i][j]) + "   ");
                    flag = false;
                }
                else{
                    System.out.print("\t" + String.format("%.5f",kmi[i][j]) + "   ");
                }

            }
            System.out.println();
        }
        System.out.print("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"+"\n");
    }

    // априорная КМИ
    public static void getKMIApr(String[] coding, double[] prop, String[] a){
        int iter;           // итератор для кодированной 2 байтами буквы
        double multiply;    // сумматор перемножения
        char[] let1, let2;  // 0001
        System.out.println();
        double[][] kmiArr = new double[a.length][a.length+1];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a.length; j++) {
                // берем для буквы ее сопоставление другой буквы
                let1 = coding[i].toCharArray();
                let2 = coding[j].toCharArray();
                multiply = 1;
                for (int l = 0; l < 4; l++) {
                    if(let1[l]=='0' && let2[l]=='0'){            // 0->0
                        multiply*=prop[0]; // 0,66
                    }
                    else if(let1[l]=='0' && let2[l]=='1'){       // 0->1
                        multiply*=prop[1]; // 0,34
                    }
                    else if(let1[l]=='1' && let2[l]=='1'){       // 1->1
                        multiply*=prop[2]; // 0,65
                    }
                    else if(let1[l]=='1' && let2[l]=='0'){       // 1->0
                        multiply*=prop[3]; // 0,35
                    }
                }
                kmiArr[i][j] = returnNormal(multiply);
            }
            // неизвестный символёнок
            kmiArr[i][a.length] = returnNormal(1 -  sumInString(kmiArr, i, a.length));
        }
        printCanalMatrix(kmiArr, a.length, a, "АПРИОРНАЯ КАНАЛЬНАЯ МАТРИЦА");
    }

    // генерация вероятностей от 0 до 1 с 5ю знаками
    public static double generate0to1() {
        Random random = new Random();
        return random.nextDouble();
    }

    // заполнение словаря символов в зависимости от выпавшей вероятности
    public static boolean fillDict(HashMap<String, Double> symbolDict, HashMap<String, Double> generateDict, double r, String let1, String let2){
        if(r < symbolDict.get("a")) {
            generateDict.put("a", generateDict.get("a")+1.0);
            return  true;
        }
        else if (r > symbolDict.get(let1) && r < symbolDict.get(let2)) {
            generateDict.put(let2, generateDict.get(let2)+1.0);
            return true;
        }
        return false;
    }

    public static int findSubstring(String[] strings, String substr){
        for (int i = 0; i < strings.length; i++) {
            if(strings[i].equals(substr)){
                return i;
            }
        }
        return strings.length;
    }

    public static double sumInStr(double[][] arr, int index, int len){
        double sum = 0;
        for (int i = 0; i < len; i++) {
            sum+=arr[index][i];
        }
        return sum;
    }

    public static HashMap<String, Double>  getDictSD(String[] s, Double[] d){ // String double dict
        HashMap<String, Double> dict = new HashMap<>();
        for (int i = 0; i < s.length; i++) {
            dict.put(s[i], d[i]);
        }
        return  dict;
    }

    public static HashMap<String, String> getDictSS(String[] s1, String[] s2){ // String String dict
        HashMap<String, String> dict = new HashMap<>();
        for (int i = 0; i < s1.length; i++) {
            dict.put(s1[i], s2[i]);
        }
        return dict;
    }

    public static String HammingEncrypt(String m){
        k = m.length();
        r = Hamming.findR(k,1);
        n = k+r;
        H = Hamming.createH(n, r);
        P = Hamming.createP(k,H,r);
        P = Hamming.matrixTurnClockwise(P, r,k);
        G = Hamming.createG(P,n,k);
        // ---------------------------- получение U ---------------------------- \\
        String u = Hamming.getU(m,G,n,k);
        return u;
    }
    public static int iterator = 0;
    public static String HammingDecrypt(String _u){
        String m = "";
        String S = Hamming.getSyndrom(H, _u, n, r); // получить синдром;
        String SourceM = "";
        if(Hamming.convertToDecimal(S) == 0) {
            iterator++; // считаем кол-во правильно переданных
            SourceM = Hamming.getM(_u,n);
        }
        else{
            String uFixed = Hamming.uFix(Hamming.convertToDecimal(S), _u,n);
            SourceM = Hamming.getM(uFixed,n);
        }
        return SourceM;
    }

    public static void createAposteriorKmi(double[][] kmi, HashMap<String, Double> generateDict, String[] coding, HashMap<String, String> startDict){
        String code;
        double r;
        int iter = 0;
        for (String key : generateDict.keySet()) {
            double freq = generateDict.get(key); // получаем частоту символа
            for (int i = 0; i < freq; i++) { // бежим по кол-ву iой буквы
                code = startDict.get(key); // получим 7битный зашифрованный по хеммингу код по ключу
                String res = "";
                // пропускаем через помехи да
                for (int j = 0; j < code.length(); j++) {
                    r = generate0to1();
                    if((code.charAt(j) == '0')){
                        if(r<0.66){
                            res+= "0";
                        }
                        else{
                            res+= "1";
                        }
                    }
                    if((code.charAt(j) == '1')){
                        if(r<0.65){
                            res+= "1";
                        }
                        else{
                            res+= "0";
                        }
                    }
                }
//                res = "1101001";
                res = HammingDecrypt(res);
                kmi[iter][findSubstring(coding, res.toString())]+=1;
            }
            iter++;
        }

    }


    public static void smsGenerate(String[] alphabet, HashMap<String, Double> generateDict, double[][] kmi){
        //---------------СМОДЕЛИРОВАТЬ ГЕНЕРАЦИЮ К СООБЩЕНИЙ---------------
        int k = 300; // кол-во передаваемых символов
        int len = alphabet.length;
        // вероятность каждого символа из alphabet, если r входит в промежуток а то сгенерирован символ а
        Double[] symbolProp = {0.0621, 0.1234, 0.1927, 0.2516, 0.3214, 0.3911, 0.4612,
                               0.5431, 0.5999, 0.6741, 0.7592, 0.8123, 0.9011, 1.0};
        HashMap<String, Double> symbolDict = getDictSD(alphabet, symbolProp);
        // заполняем сначала нулями, потом при каждой встрече сивола будем увеличивать
        for (String symbol : alphabet) {
            generateDict.put(symbol, 0.0);
        }
        // генерируем
        double r;
        // {"a","e","i","o","n","r","l","t","s","c","d","u","v","m",}; // алфавит
        for (int i = 0; i < k; i++) {
            // заполнить словарь
            r = generate0to1();
            boolean find = false;
            int j = 0;
            while(!find && j<len-1){
                 find = fillDict(symbolDict,generateDict, r, alphabet[j], alphabet[j+1]);
                 j++;
            }
        }
    }

    public static boolean Compare(String s1, String s2){
        int counter = 0;
        for (int i = 0; i < Math.min(s1.length(),s2.length()) && counter!=2; i++) {
            if(s1.charAt(i)!=s2.charAt(i))
                counter++;
        }
        return counter==1;
    }

    public static void Write1t(){
        String[] coding1 ={"0001", "0010", "0011", "0100",  "0101", "0110", "0111", "1000",  "1001", "1010", "1011", "1100",  "1101", "1110"}; // кодовая последовательность
        String[] coding2 ={"0001", "0010", "0011", "0100",  "0101", "0110", "0111", "1000",  "1001", "1010", "1011", "1100",  "1101", "1110", "1111", "0000"}; // кодовая последовательность
        System.out.println("Однократная ошибка:");
        for(String c1: coding1){
            Blueprint("Для " + c1 + " \n");
            for(String c2: coding2) {
                if(Compare(c1,c2))
                    System.out.print(c2 + "  ");
            }
            System.out.println();
        }
    }

    public static void generateAprKMO(String[] alphabet, HashMap<String, Double> generateDict, double[][] kmi, double[][] kmo){
        int len = alphabet.length;
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len+1; j++) {
                kmo[i][j] = kmi[i][j]*generateDict.get(alphabet[i]);
            }
        }
    }

    public static double apostEntropyU(HashMap<String, Double> puDict, String[] alphabet){
        double result = 0;
        double pui;
        for (String s : alphabet) {
            pui = puDict.get(s);
            result += (pui * (Math.log(pui) / Math.log(2)));
        }
        return -result;
    }
    public static double apostEntropyV(HashMap<String, Double> pvDict, String[] alphabet){
        double result = 0;
        double pvi;
        for (String s : alphabet) {
            pvi = pvDict.get(s);
            result += (pvi * (Math.log(pvi) / Math.log(2)));
        }
        pvi = pvDict.get("?");
        result+=(pvi * Math.log(pvi)/Math.log(2));
        return -result;
    }
    public static double noiseEntropy(double[][] kmi, HashMap<String, Double> puDict, String[] alphabet){
        double result = 0;
        double[] hvui = new double[alphabet.length];
        int len = alphabet.length;
        for (int i = 0; i < len; i++) {
            for (int j = 0; j <= len; j++) {
                if(kmi[i][j]!=0){
                    hvui[i]+= (-1)*(kmi[i][j] * (Math.log(kmi[i][j])/Math.log(2)));
                }
            }
        }
        for (int i = 0; i < len; i++) {
            result+=(puDict.get(alphabet[i]) * hvui[i]);
        }
        return result;
    }

    public static void main(String[] args) {
        //Write1t();


                String[] coding ={"0001", "0010", "0011", "0100",  "0101", "0110", "0111", "1000",  "1001", "1010", "1011", "1100",  "1101", "1110"}; // кодовая последовательность
        String[] hammingCoding = new String[coding.length];
        // получить кодирование Хемминга
        System.out.println("Закодированный алфавит по Хеммингу");
        for (int i = 0; i < coding.length; i++) {
            hammingCoding[i] = HammingEncrypt(coding[i]);
            System.out.print(hammingCoding[i] + " ");
        }

        String[] alphabet = {"a","e","i","o","n","r","l","t","s","c","d","u","v","m"}; // алфавит
        double[] prop = {0.66, 0.34, 0.65, 0.35}; // 00 01 11 10
         getKMIApr(coding, prop, alphabet);
        // апостериорная кми
        // сначала
        // -------------------------Pu-------------------------
        HashMap<String, Double> puDict = new HashMap<>();
        int len = alphabet.length;
        double[][] kmi = new double[len][len+1];
        smsGenerate(alphabet, puDict, kmi);

        //---------------ГЕНЕРИРУЕМ АПОСТЕРИОРНУЮ КМИ---------------
        HashMap<String, String> startDict = getDictSS(alphabet, hammingCoding); // получим сначала словарь для удобства из кодов и букв
        createAposteriorKmi(kmi,puDict, coding, startDict);

        System.out.println("Матрица переходов");
        for (String str : alphabet) {
            Blueprint("\t"+str);
        }
        Blueprint("\t?\n");
        for (int i = 0; i < coding.length; i++) {
            Blueprint(alphabet[i] + "\t");
            for (int j = 0; j <= coding.length; j++) {
                System.out.print(String.format("%.0f",kmi[i][j])  + "\t");
            }
            System.out.println();
        }



        System.out.println("Без ошибок передалось " + iterator + " раз");
        double sum = 0;
        double sumInstr;
        // получаем вже вероятность
        for (int i = 0; i < len; i++) {
            sumInstr = sumInStr(kmi, i, len+1);
            for (int j = 0; j < len+1; j++) {
                kmi[i][j]/=sumInstr;
            }
        }


        printCanalMatrix(kmi, len, alphabet, "АПОСТЕРИОРНАЯ КАНАЛЬНАЯ МАТРИЦА");

        System.out.println("\n"  + "P(Ui):");
        for (String s : alphabet) {
            puDict.put(s, puDict.get(s) / 300);
        }
        for (String str : alphabet) {
            System.out.print(str+ ") " + String.format("%.5f",puDict.get(str)) + "\t");
        }

        // получим КМО
        double[][] kmo = new double[len][len+1];
        generateAprKMO(alphabet, puDict, kmi, kmo);
        printCanalMatrix(kmo, len, alphabet, "КАНАЛЬНАЯ МАТРИЦА ОБЬЕДИНЕНИЯ");
        // -------------------------Pv-------------------------
        HashMap<String, Double> pvDict = new HashMap<>();
        for (int i = 0; i <= len; i++) {
            sum = 0;
            for (int j = 0; j < len; j++) {
                sum+=kmo[j][i];
            }
            if(i==len){
                pvDict.put("?",sum);
            }
            else{
                pvDict.put(alphabet[i],sum);
            }
        }
        System.out.println("\n"  + "P(Vj):");
        for (String str : alphabet) {
            System.out.print(str+ ") " + String.format("%.5f",pvDict.get(str)) + "\t");
        }
        System.out.print("?) " + String.format("%.5f",pvDict.get("?")));
        // вычислим апостериорные характеристики
        double hu  =  apostEntropyU(puDict, alphabet);
        double hv = apostEntropyV(pvDict, alphabet);
        double hvu = noiseEntropy(kmi, puDict, alphabet);
        double i = hv - hvu;
        double huv = hu - i;
        double t0 = 0.0003;
        double j = i/(t0*4);
        System.out.println("\n" + "Энтропия источника:     " + String.format("%.5f", hu));
        System.out.println("Энтропия приёмника:     " + String.format("%.5f", hv));
        System.out.println("Энтропия шума:          " + String.format("%.5f", hvu));
        System.out.println("Полезная информация:    " + String.format("%.5f", i));
        System.out.println("Ненадежность канала:    " + String.format("%.5f", huv));
        System.out.println("Скорость передачи:      " + String.format("%.5f", j) + " бит/сек");
    }
}
---------------------------------------------
Код с классом по коду Хемминга:
public class Hamming {
    // ------------------------ Вспомогательные ------------------------ \\
    /**
     * Вывод матрицы
     */
    public static void printMatrix(int[][] M, int rows, int columns){
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                System.out.print(M[i][j] + "\t");
            }
            System.out.println();
        }
    }

    /**
     * Перевод в 2сс, возвращает строку
     * дополняет нулями для размерности rank
     */
    public static String convertToBinary(int number, int rank) {
        if (number == 0) {
            return "0";
        }

        StringBuilder binary = new StringBuilder();
        while (number > 0) {
            int remainder = number % 2;
            binary.insert(0, remainder); // Вставляем остаток в начало строки
            number /= 2;
        }
        while(binary.length() != rank){
            binary.insert(0,0);
        }

        return binary.toString();
    }

    public static int convertToDecimal(String binary){
        int number=0;
        for (int i = 0; i < binary.length(); i++) {
            char a = binary.charAt(i);
            if(binary.charAt(i)=='1'){
                number+=Math.pow(2,binary.length()-1-i);
            }
        }
        return number;
    }

    /**
     * Проверка, что число не степень двойки
     */
    public static boolean powerOf2(int number){
        if (number == 1) return true; // 0вая степень
        while(number>1){
            if(number%2!=0) return false;
            number/=2;
        }
        return true;
    }

    public static int factorial(int n){
        int factorial = 1;
        for (int i = 1; i <= n; i++) {
            factorial*=i;
        }
        return factorial;
    }

    public static int combinations(int n, int k){
        return factorial(n)/(factorial(k)*factorial(n-k));
    }

    public static boolean numberIsBinary(String str){
        for (int i = 0; i < str.length(); i++) {
            if(str.charAt(i)!='0' && str.charAt(i)!='1')
                return false;
        }
        return true;
    }

    public static int compareU(String u1, String u2){

        int countNotCompare = 0;
        int len = Math.min(u1.length(), u2.length());
        for (int i = 0; i < len ; i++) {
            if(u1.charAt(i)!=u2.charAt(i))
                countNotCompare++;
        }
        return countNotCompare;
    }

    // ------------------------------------------------------------------------ \\


    public static int[][] createP(int k, int[][] H, int rank){

        int[][] P = new int[rank][k];
        // заполним not2Pow k не степенями 2
        int[] not2Pow = new int[k];
        int candidate = 3;
        int index = 0;
        while(index<k)
        {
            if(!powerOf2(candidate)){
                not2Pow[index] = candidate;
                index++;
                //System.out.print(candidate + "\t");
            }
            candidate++;
        }
        // заполняем подматрицу P - k числами не степенями 2
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < rank; j++) {
                P[j][i] = H[j][not2Pow[i]-1];
            }
        }
        return P;
    }

    public static int[][] createH(int n, int r){
        int[][] H = new int[r][n];
        String binaryString;
        for (int i = 0; i < n; i++) {
            binaryString = convertToBinary((i+1),r);
            for (int j = 0; j < r; j++) {
                H[j][i] = Integer.parseInt(String.valueOf(binaryString.charAt(j)));
            }
        }
        return H;
    }

    public static int[][] createG(int[][] P, int n, int k){
        int[][] G = new int[k][n]; //  k = 5, n = 9
        // на степени двойки ставим P`, на остальные единичную матрицу
        int EIterate = 0; // итератор единичной матрицы
        int PIterateColumn = 0; // итератор единичной матрицы
        for (int column = 0; column < n; column++) {
            for (int row = 0; row < k; row++) {
                if(powerOf2(column+1)){ // заполняем матрицей P`
                    G[row][column] = P[row][PIterateColumn];
                }
                else{
                    if(row == EIterate)
                        G[row][column] = 1;
                    else
                        G[row][column] = 0;
                }
            }
            // если колонка была не степенью 2 то значит
            // мы уже заполнили столбец и итератор увеличиваем
            if(!powerOf2(column+1))
                EIterate++;
                // наоборот если была
            else
                PIterateColumn++;
        }
        return G;
    }

    public static String getU(String m, int[][] G, int n, int r) {
        StringBuilder u = new StringBuilder();
        int counter;
        // реализуем xor
        for (int i = 0; i < n; i++) {
            counter = 0;
            for (int j = 0; j < r; j++) { // прошлись по столбцу
                if(m.charAt(j) == '1' && G[j][i]==1){
                    counter++;
                }
            }
            if(counter%2==0)
                u.append("0");
            else
                u.append("1");
        }
        return u.toString();
    }

    public static String getSyndrom(int[][] H, String _u, int n, int r){
        int counter;
        StringBuilder S = new StringBuilder();
        // идём по строке наконец-то
        for (int i = 0; i < r; i++) {
            counter = 0;
            for (int j = 0; j < n; j++) {
                if(H[i][j] == 1 && _u.charAt(j) == '1'){
                    counter++;
                }
            }
            if(counter%2==0)
                S.append("0");
            else
                S.append("1");
        }
        return S.toString();
    }

    public static String uFix(int errorRank, String u, int n){
        // вектор ошибки
        StringBuilder e = new StringBuilder();
        StringBuilder uFixed = new StringBuilder();
        for (int i = 0; i < n-1; i++) {
            e.append("0");
        }
        e.insert(errorRank-1,'1');
        //System.out.println("e = " + e);
        for (int i = 0; i < n; i++) {
            int eBit = Integer.parseInt(String.valueOf(e.charAt(i)));
            int uBit = Integer.parseInt(String.valueOf(u.charAt(i)));
            uFixed.append(eBit ^ uBit);
        }
        return uFixed.toString();
    }

    /**
     * Поворот матрицы по часовой стрелке
     */
    public  static int[][] matrixTurnClockwise(int[][] M, int rows, int columns){
        int[][] rotateMatrix = new int[columns][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                rotateMatrix[j][i] = M[rows-i-1][j];
            }
        }
        return rotateMatrix;
    }



    public static String getM(String u, int n){
        StringBuilder m = new StringBuilder();
        for (int i = 1; i < n; i++) {
            if(!powerOf2(i+1))
                m.append(u.charAt(i));
        }
        return m.toString();
    }


    public static int findR(int k,  int t){
        // по границе Хеминга
        int r=1;
        int n;
        int sum;
        while(true){
            n=k+r;
            sum=0;
            for (int i = 0; i <= t; i++) {
                sum+=combinations(n,i);
            }
            if(r>= Math.log(sum)/Math.log(2))
                return r;
            r++;
        }
    }
}