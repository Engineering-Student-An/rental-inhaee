package an.rentalinhaee.service;

import an.rentalinhaee.domain.dto.ParserDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
//@RequiredArgsConstructor
public class InhaEEService {

    private final RestTemplate restTemplate;
    private final String url;
    private final ResponseEntity<String> response;


    public InhaEEService() {
        // RestTemplate 인스턴스 생성
        this.restTemplate = new RestTemplate();

        // 요청 URL
        this.url = "https://ee.inha.ac.kr/ee/784/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGZWUlMkYyMTglMkZhcnRjbExpc3QuZG8lM0Y%3D";

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Host", "ee.inha.ac.kr");
        headers.set("Sec-Fetch-Site", "same-origin");
        headers.set("Sec-Fetch-Dest", "document");
        headers.set("Connection", "keep-alive");
        headers.set("Sec-Fetch-Mode", "navigate");
        headers.set("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        headers.set("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 17_5_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.5 Mobile/15E148 Safari/604.1");
        headers.set("Accept-Language", "ko-KR,ko;q=0.9");
        headers.set("Referer", "https://ee.inha.ac.kr/ee/index.do");
        headers.set("Cookie", "JSESSIONID=1E64DC971F9363159AEE2A7DD82A8354");

        // HttpEntity 생성
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // GET 요청 보내기
        response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    }

    public List<ParserDto> importantPostParser() {

        String html = response.toString();

        List<ParserDto> result = new ArrayList<>();

        // HTML 파싱
        Document document = Jsoup.parse(html);

        // headline 클래스의 tr 요소 찾기
        Elements headlines = document.select("tr.headline");

        for (Element headline : headlines) {
            // _artclTdTitle에서 strong 태그의 텍스트 추출
            String title = headline.select("td._artclTdTitle strong").text();
            // a 태그의 href 속성 추출
            String link = headline.select("td._artclTdTitle a").attr("href");
            String date = headline.select("td._artclTdRdate").text();

            ParserDto parserDto = new ParserDto(title, link, date);
            result.add(parserDto);
        }
        
        return result;
    }

    public List<ParserDto> recentPostParser() {

        String html = response.toString();

        List<ParserDto> result = new ArrayList<>();

        // HTML 파싱
        Document document = Jsoup.parse(html);

        // headline 클래스의 tr 요소 찾기
        Elements headlines = document.select("tr");

        // 클래스가 없는 tr 요소 필터링
        Elements normalRows = new Elements();
        for (Element row : headlines) {
            if (!row.hasClass("headline")) {
                normalRows.add(row);
            }
        }

        for (Element headline : normalRows) {
            // _artclTdTitle에서 strong 태그의 텍스트 추출
            String title = headline.select("td._artclTdTitle strong").text();
            // a 태그의 href 속성 추출
            String link = headline.select("td._artclTdTitle a").attr("href");
            String date = headline.select("td._artclTdRdate").text();

            if(title.isEmpty()) continue;

            ParserDto parserDto = new ParserDto(title, link, date);
            result.add(parserDto);

            if(result.size() == 5) break;
        }

        return result;
    }
}
