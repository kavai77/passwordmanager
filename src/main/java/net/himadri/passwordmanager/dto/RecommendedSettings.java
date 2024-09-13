package net.himadri.passwordmanager.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class RecommendedSettings {
    int recommendedIterations;
    String recommendedPbkdf2Algorithm;
    String recommendedMasterPasswordHashAlgorithm;
}
