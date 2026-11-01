package com.example.springbootwithangular.service;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.example.springbootwithangular.entity.Product;

@Service
public class ProductRecommendationService {
	private static final Pattern MARKS = Pattern.compile("\\p{M}+");
	private static final Pattern WORDS = Pattern.compile("[^\\p{L}\\p{N}]+");

	public List<Product> recommend(List<Product> products, String query) {
		String normalizedQuery = normalize(query == null ? "" : query);
		if (normalizedQuery.isBlank()) {
			return products.stream()
					.sorted(Comparator.comparing(Product::getId, Comparator.nullsLast(Comparator.reverseOrder())))
					.toList();
		}

		List<String> queryWords = WORDS.splitAsStream(normalizedQuery)
				.filter(word -> !word.isBlank())
				.toList();
		List<ScoredProduct> scored = new ArrayList<>();
		for (Product product : products) {
			String name = normalize(product.getName());
			String category = normalize(product.getCategory());
			String description = normalize(product.getDescription());
			double score = 0;
			for (String word : queryWords) {
				score += 0.60 * tokenScore(word, name)
						+ 0.25 * tokenScore(word, category)
						+ 0.15 * tokenScore(word, description);
			}
			score /= queryWords.size();
			if (score >= 0.28) {
				scored.add(new ScoredProduct(product, score));
			}
		}
		return scored.stream()
				.sorted(Comparator.comparingDouble(ScoredProduct::score).reversed())
				.limit(20)
				.map(ScoredProduct::product)
				.toList();
	}

	private double tokenScore(String queryWord, String text) {
		if (text.isBlank()) {
			return 0;
		}
		double best = 0;
		for (String word : WORDS.split(text)) {
			if (word.isBlank()) {
				continue;
			}
			if (word.equals(queryWord)) {
				return 1;
			}
			if (word.startsWith(queryWord) || queryWord.startsWith(word)) {
				best = Math.max(best, 0.82);
				continue;
			}
			int longest = Math.max(word.length(), queryWord.length());
			if (longest >= 3) {
				double similarity = 1.0 - (double) levenshtein(word, queryWord) / longest;
				best = Math.max(best, similarity);
			}
		}
		return best;
	}

	private String normalize(String value) {
		if (value == null) {
			return "";
		}
		String decomposed = Normalizer.normalize(value, Normalizer.Form.NFD);
		return MARKS.matcher(decomposed).replaceAll("").toLowerCase(Locale.ROOT);
	}

	private int levenshtein(String left, String right) {
		int[] previous = new int[right.length() + 1];
		for (int j = 0; j <= right.length(); j++) {
			previous[j] = j;
		}
		for (int i = 1; i <= left.length(); i++) {
			int[] current = new int[right.length() + 1];
			current[0] = i;
			for (int j = 1; j <= right.length(); j++) {
				int substitution = previous[j - 1] + (left.charAt(i - 1) == right.charAt(j - 1) ? 0 : 1);
				current[j] = Math.min(Math.min(previous[j] + 1, current[j - 1] + 1), substitution);
			}
			previous = current;
		}
		return previous[right.length()];
	}

	private record ScoredProduct(Product product, double score) {
	}
}
