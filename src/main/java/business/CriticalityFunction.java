package business;

public class CriticalityFunction {
	private Double inf;
    private Double sup;
    private Double epsilon;
    private Double epsilon2;
 
    // Optional Parameters
    private Double eta;
    private Double eta2;
    private Double cMin = 0d;
    private Double cMax = 100d;
 
    private Double gamma;
    private Double gamma2;
    private Double delta;
    private Double delta2;
 
    public CriticalityFunction(Double inf, Double sup, Double epsilon,
            Double epsilon2) {
        super();
        this.inf = inf;
        this.sup = sup;
        this.epsilon = epsilon;
        this.epsilon2 = epsilon2;
        initVariables();
        checkValidity();
    }
    
    public void setParameters(Double inf, Double sup, Double epsilon, Double epsilon2) {
        this.inf = inf;
        this.sup = sup;
        this.epsilon = epsilon;
        this.epsilon2 = epsilon2;
        initVariables();
        checkValidity();
    }
    
    
    
 
    private void checkValidity() {
        if (!(inf <= eta && eta <= epsilon && epsilon <= epsilon2
                && epsilon2 <= eta2 && eta2 <= sup))
            throw new RuntimeException(
                    "Criticality Function creation parameters inconsistent");
    }
     
    private void initVariables() {
        eta = (epsilon - inf) / 3 + inf;
        eta2 = sup - (sup - epsilon2) / 3;
 
        gamma = -2 * cMax / (epsilon - inf);
        gamma2 = -2 * cMax / (sup - epsilon2);
 
        delta = -1 * gamma * ((epsilon) - (eta)) / 2;
        delta2 = -1 * gamma2 * (eta2 - epsilon2) / 2;
    }

    
    // renvoie négatif si à gauche de la criticite nulle.
    public Double compute(Double x) {
 
    	if (x < inf)
            return -cMax;
 
        if (x < eta)
            return - (gamma * (x - (eta)) * (x - (eta)) / (2 * (eta - inf))
                    + gamma * (x - (eta - 0)) + delta);
 
        if (x < epsilon)
            return - (-1 * gamma * (x - eta) * (x - eta) / (2 * (epsilon - eta))
                    + gamma * (x - eta) + delta);
 
        
        if (x < epsilon2)
            return cMin;
 
        if (x < eta2)
            return -1 * gamma2 * (eta2 - x) * (eta2 - x)
                    / (2 * (eta2 - epsilon2)) + gamma2 * (eta2 - x) + delta2;
 
        if (x < sup)
            return gamma2 * (eta2 - x) * (eta2 - x) / (2 * (sup - eta2))
                    + gamma2 * (eta2 - x) + delta2;
        else
            return cMax;
 
    }
 
    // Uncomment to use the main to visualize a function
    // You need JFreeChart in your path
    // ctrl + shift + o for Auto-import >:O
/*
    public static void main(String[] args) {
        Double inf = -3d;
        Double sup = 3d;
        Double epsilon = -1d;
        Double epsilon2 = 0d;
 
        Double displayStep = 0.1d;
        CriticalityFunction crit = new CriticalityFunction(inf, sup, epsilon,
                epsilon2);
 
        JFrame frame = new JFrame("Criticality Function Visualization");
        JPanel panel = new JPanel();
 
        XYSeries series = new XYSeries("XYGraph");
 
        for (Double x = inf; x <= sup; x += displayStep) {
            series.add(x, crit.compute(x));
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
 
        JFreeChart chart = ChartFactory.createXYLineChart("+", "Value",
                "Criticality", dataset, PlotOrientation.VERTICAL, false, true,
                false);
        ChartPanel chartPanel = new ChartPanel(chart);
        frame.add(chartPanel);
        frame.pack();
        frame.setVisible(true);
    }
    */
}
