import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class WebCrawler {

    private Set<String> links;
    private int linkCounter;
    private int fileCounter;

    public WebCrawler() {
        links = new HashSet<>();
        linkCounter = 0;
        fileCounter = 1;
    }

    public void startCrawling(String url) {
        crawlPage(url);
    }

    private void crawlPage(String url) {
        if (!links.contains(url) && linkCounter < 10) {
            try {
                // Add the URL to the index
                links.add(url);
                linkCounter++;
                System.out.println(url);

                // Fetch the HTML code
                Document document = Jsoup.connect(url).get();

                // Save the data to a file
                saveDataToFile(url, document.html());

                // Parse the HTML to extract links
                Elements linksOnPage = document.select("a[href]");

                // For each extracted URL, go back to crawlPage
                for (Element page : linksOnPage) {
                    if (linkCounter < 10) {
                        String extractedUrl = page.attr("abs:href");
                        crawlPage(extractedUrl);
                    } else {
                        break;
                    }
                }
            } catch (IOException e) {
                System.err.println("Error crawling '" + url + "': " + e.getMessage());
            }
        }
    }

    private void saveDataToFile(String url, String data) {
        try {
            String filename = "file" + fileCounter + ".txt";
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            writer.write("URL: " + url);
            writer.newLine();
            writer.write("Data: ");
            writer.newLine();
            writer.write(data);
            writer.close();
            fileCounter++;
        } catch (IOException e) {
            System.err.println("Error saving data to file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the starting URL: ");
        String startingUrl = scanner.nextLine();

        WebCrawler crawler = new WebCrawler();
        crawler.startCrawling(startingUrl);
    }
}
