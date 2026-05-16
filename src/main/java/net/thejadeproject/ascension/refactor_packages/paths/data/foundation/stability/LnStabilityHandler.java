package net.thejadeproject.ascension.refactor_packages.paths.data.foundation.stability;

public class LnStabilityHandler extends GenericCalculatedStabilityHandler{

    public LnStabilityHandler(){
        super(input->Math.log(6*input+1),0,10,5000);

    }
}