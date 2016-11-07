package net.himadri.passwordmanager.dto;

/**
 * Created by Kávai on 2016.09.26..
 */
public class RecommendedSettings {
    private final int recommendedIterations;
    private final String recommendedPbkdf2Algorithm;
    private final String recommendedMasterPasswordHashAlgorithm;

    public RecommendedSettings(int recommendedIterations, String recommendedPbkdf2Algorithm, String recommendedMasterPasswordHashAlgorithm) {
        this.recommendedIterations = recommendedIterations;
        this.recommendedPbkdf2Algorithm = recommendedPbkdf2Algorithm;
        this.recommendedMasterPasswordHashAlgorithm = recommendedMasterPasswordHashAlgorithm;
    }

    public int getRecommendedIterations() {
        return recommendedIterations;
    }

    public String getRecommendedPbkdf2Algorithm() {
        return recommendedPbkdf2Algorithm;
    }

    public String getRecommendedMasterPasswordHashAlgorithm() {
        return recommendedMasterPasswordHashAlgorithm;
    }
}
