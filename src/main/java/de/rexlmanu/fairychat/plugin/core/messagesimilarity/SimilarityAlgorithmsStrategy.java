package de.rexlmanu.fairychat.plugin.core.messagesimilarity;

import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.ricecode.similarity.DiceCoefficientStrategy;
import net.ricecode.similarity.JaroStrategy;
import net.ricecode.similarity.JaroWinklerStrategy;
import net.ricecode.similarity.LevenshteinDistanceStrategy;
import net.ricecode.similarity.SimilarityStrategy;

@AllArgsConstructor
@Getter
public enum SimilarityAlgorithmsStrategy {
  LEVENSHTEIN(LevenshteinDistanceStrategy::new),
  JARO_WINKLER(JaroWinklerStrategy::new),
  JARO(JaroStrategy::new),
  DICE_COEFFICIENT(DiceCoefficientStrategy::new);

  private Supplier<SimilarityStrategy> strategy;
}
