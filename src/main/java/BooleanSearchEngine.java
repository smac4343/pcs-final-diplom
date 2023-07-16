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

        for (File pdf : Objects.requireNonNull(pdfsDir.listFiles())) {
            processFile(pdf);
        }

        for (List<PageEntry> pages : wordsMap.values()) {
            Collections.sort(pages, Collections.reverseOrder());
        }
    }

    private void processFile(File pdf) throws IOException {
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
                wordsMap.computeIfAbsent(entry.getKey(), k -> new ArrayList<>())
                        .add(new PageEntry(pdf.getName(), pageNum, entry.getValue()));
            }
        }
    }

    @Override
    public List<PageEntry> search(String word) {
        return wordsMap.getOrDefault(word.toLowerCase(), new ArrayList<>());
    }
}
