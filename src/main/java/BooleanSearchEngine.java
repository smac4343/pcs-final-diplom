import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    private Map<String, List<PageEntry>> wordsMap;

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        wordsMap = new HashMap<>();

        for (File pdf : pdfsDir.listFiles()) {
            var doc = new PdfDocument(new PdfReader(pdf));
            for (int pageNum = 1; pageNum <= doc.getNumberOfPages(); pageNum++) {
                PdfPage page = doc.getPage(pageNum);
                String text = PdfTextExtractor.getTextFromPage(page);
                String[] words = text.split("\\P{IsAlphabetic}+");

                Map<String, Integer> freqs = new HashMap<>();

                for (var word : words) {
                    if (word.isEmpty()) {
                        continue;
                    }
                    word = word.toLowerCase();
                    freqs.put(word, freqs.getOrDefault(word, 0) + 1);
                }

                for (var entry : freqs.entrySet()) {
                    List<PageEntry> searchingResult;
                    if (!wordsMap.containsKey(entry.getKey())) {
                        searchingResult = new ArrayList<>();

                    } else {
                        searchingResult = wordsMap.get(entry.getKey());
                    }
                    searchingResult.add(new PageEntry(pdf.getName(), pageNum, entry.getValue()));
                    Collections.sort(searchingResult, Collections.reverseOrder());
                    wordsMap.put(entry.getKey(), searchingResult);
                }
            }

        }
    }

    @Override
    public List<PageEntry> search(String word) {

        return wordsMap.get(word.toLowerCase());
    }
}