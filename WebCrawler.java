import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class WebCrawler {
    private Set<String> links = new HashSet<>();

    public static void main(String[] args) {
        // Create an instance of the WebCrawler class
        WebCrawler webCrawler = new WebCrawler();

        // Start crawling from a specified URL
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the starting URL: ");
        String startingURL = scanner.nextLine();
        scanner.close();

        webCrawler.crawl(startingURL);
    }

    public void crawl(String url) {
        // Check if the URL has already been crawled
        if (!links.contains(url)) {
            try {
                // Add the URL to the set of crawled links
                links.add(url);

                // Print the URL
                System.out.println(url);

                // Fetch the HTML code
                Document document = Jsoup.connect(url).get();

                // Parse the HTML to extract links to other URLs
                Elements linksOnPage = document.select("a[href]");

                // For each extracted URL, recursively crawl it
                for (Element page : linksOnPage) {
                    String nextUrl = page.attr("abs:href");
                    crawl(nextUrl);
                }
            } catch (IOException e) {
                System.err.println("Error crawling URL: " + url);
                e.printStackTrace();
            }
        }
    }
}
