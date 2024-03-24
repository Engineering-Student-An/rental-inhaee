package an.rentalinhaee.controller;


import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.*;

@RestController
public class ExcelController {


    public static void role() throws IOException, InterruptedException {
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path path = Paths.get("src/main/resources/static");
        path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

        WatchKey key;
        while ((key = watchService.take()) != null) {
            for (WatchEvent<?> event : key.pollEvents()) {
                System.out.println("Event kind:" + event.kind() + ". File affected: " + event.context() + ".");
            }
            key.reset();
        }
    }
//    private static final String path;
//
//    static {
//        try {
//            path = String.valueOf(new ClassPathResource("static").getFile().toPath());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private WatchKey watchKey;
//
//    @PostConstruct
//    public void init() throws IOException {
//
//        // watchService 생성
//        WatchService watchService = FileSystems.getDefault().newWatchService();
//        // 경로 생성
//        Path path = Paths.get(ExcelController.path);
//        // 해당 디렉토리 경로에 와치서비스와 이벤트 등록
//        path.register(watchService,
//                ENTRY_CREATE,
//                ENTRY_MODIFY,
//                OVERFLOW);
//
//
//        Thread thread = new Thread(() -> {
//            while (true) {
//                try {
//                    watchKey = watchService.take(); // 이벤트가 오길 대기(Blocking)
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                    System.out.println("WatchService interrupted");
//                    break;
//                }
//                List<WatchEvent<?>> events = watchKey.pollEvents(); // 이벤트들을 가져옴
//                for (WatchEvent<?> event : events) {
//                    // 이벤트 종류
//                    WatchEvent.Kind<?> kind = event.kind();
//                    // 경로
//                    Path eventPath = (Path) event.context();
//                    Path fullPath = path.resolve(eventPath);
//
//                    // 엑셀 파일인지 확인
//                    if (fullPath.toString().endsWith(".xlsx")) {
//                        System.out.println(fullPath.toAbsolutePath()); // 파일의 절대 경로 출력
//                        if(kind.equals(ENTRY_CREATE)) {
//                            System.out.println("CREATED");
//                        } else if (kind.equals(ENTRY_MODIFY)) {
//                            System.out.println("Modified an Excel file in directory");
//                        }
//                    }
//                }
//                boolean valid = watchKey.reset();
//                if (!valid) {
//                    break;
//                }
//            }
//        });
//        thread.start();
//    }

}
